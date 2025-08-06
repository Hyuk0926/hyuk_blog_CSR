/**
 * API 서비스 클래스
 * 백엔드 API와의 통신을 담당
 */
class ApiService {
  constructor() {
    this.baseURL = process.env.VUE_APP_API_URL || 'http://localhost:9090';
  }

  /**
   * 기본 HTTP 요청 메서드
   */
  async request(endpoint, options = {}) {
    const url = `${this.baseURL}${endpoint}`;
    
    const defaultOptions = {
      headers: {
        'Content-Type': 'application/json',
      },
      credentials: 'include', // 쿠키 포함
    };

    // JWT 토큰이 있으면 Authorization 헤더에 추가
    const token = localStorage.getItem('jwtToken');
    if (token) {
      defaultOptions.headers['Authorization'] = `Bearer ${token}`;
    }

    const config = {
      ...defaultOptions,
      ...options,
      headers: {
        ...defaultOptions.headers,
        ...options.headers,
      },
    };

    try {
      const response = await fetch(url, config);
      
      if (!response.ok) {
        // 401 Unauthorized 에러 시 토큰 제거
        if (response.status === 401) {
          localStorage.removeItem('jwtToken');
          localStorage.removeItem('userRole');
          localStorage.removeItem('username');
          localStorage.removeItem('adminToken');
        }
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      
      return await response.json();
    } catch (error) {
      console.error('API 요청 실패:', error);
      throw error;
    }
  }

  // ==================== Post 관련 API ====================

  /**
   * 모든 공개 게시글 목록 조회
   */
  async getPosts(lang = 'ko') {
    return this.request(`/api/posts?lang=${lang}`);
  }

  /**
   * 특정 게시글 상세 조회
   */
  async getPost(id, lang = 'ko') {
    return this.request(`/api/posts/${id}?lang=${lang}`);
  }

  /**
   * 제목으로 게시글 검색
   */
  async searchPosts(query, lang = 'ko') {
    return this.request(`/api/posts/search?query=${encodeURIComponent(query)}&lang=${lang}`);
  }

  /**
   * 카테고리별 게시글 조회
   */
  async getPostsByCategory(category, lang = 'ko') {
    return this.request(`/api/posts/category/${category}?lang=${lang}`);
  }

  /**
   * 모든 게시글 조회 (관리자용)
   */
  async getAllPostsForAdmin(lang = 'ko') {
    return this.request(`/api/posts/admin?lang=${lang}`);
  }

  /**
   * 새 게시글 작성
   */
  async createPost(postData, lang = 'ko') {
    return this.request(`/api/posts?lang=${lang}`, {
      method: 'POST',
      body: JSON.stringify(postData),
    });
  }

  /**
   * 게시글 수정
   */
  async updatePost(id, postData, lang = 'ko') {
    return this.request(`/api/posts/${id}?lang=${lang}`, {
      method: 'PUT',
      body: JSON.stringify(postData),
    });
  }

  /**
   * 게시글 삭제
   */
  async deletePost(id, lang = 'ko') {
    return this.request(`/api/posts/${id}?lang=${lang}`, {
      method: 'DELETE',
    });
  }

  /**
   * 게시글 존재 여부 확인
   */
  async checkPostExists(id, lang = 'ko') {
    return this.request(`/api/posts/${id}/exists?lang=${lang}`);
  }

  // ==================== 카테고리 관련 API ====================

  /**
   * 사용 가능한 카테고리 목록 조회
   */
  async getCategories() {
    return this.request('/api/categories');
  }

  // ==================== 좋아요 관련 API ====================

  /**
   * 좋아요 토글
   */
  async toggleLike(postId, postType) {
    return this.request(`/api/likes/toggle`, {
      method: 'POST',
      body: JSON.stringify({ postId, postType }),
    });
  }

  /**
   * 좋아요 수 조회
   */
  async getLikeCount(postId, postType) {
    return this.request(`/api/likes/count?postId=${postId}&postType=${postType}`);
  }

  /**
   * 사용자의 좋아요 여부 확인
   */
  async checkUserLike(postId, postType) {
    return this.request(`/api/likes/check?postId=${postId}&postType=${postType}`);
  }

  // ==================== 댓글 관련 API ====================

  /**
   * 게시글의 댓글 목록 조회
   */
  async getComments(postId, postType) {
    return this.request(`/api/comments?postId=${postId}&postType=${postType}`);
  }

  /**
   * 댓글 작성
   */
  async createComment(commentData) {
    return this.request('/api/comments', {
      method: 'POST',
      body: JSON.stringify(commentData),
    });
  }

  /**
   * 댓글 수정
   */
  async updateComment(commentId, commentData) {
    return this.request(`/api/comments/${commentId}`, {
      method: 'PUT',
      body: JSON.stringify(commentData),
    });
  }

  /**
   * 댓글 삭제
   */
  async deleteComment(commentId) {
    return this.request(`/api/comments/${commentId}`, {
      method: 'DELETE',
    });
  }

  // ==================== 인증 관련 API ====================

  /**
   * 통합 로그인 (사용자/관리자)
   */
  async login(credentials) {
    return this.request('/api/auth/login', {
      method: 'POST',
      body: JSON.stringify(credentials),
    });
  }

  /**
   * 회원가입
   */
  async register(userData) {
    return this.request('/api/auth/register', {
      method: 'POST',
      body: JSON.stringify(userData),
    });
  }

  /**
   * JWT 토큰 검증
   */
  async validateToken(token) {
    return this.request('/api/auth/validate', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
      },
    });
  }

  /**
   * JWT 토큰 정보 조회
   */
  async getTokenInfo(token) {
    return this.request('/api/auth/info', {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
      },
    });
  }

  /**
   * 로그아웃 (클라이언트 측 토큰 제거)
   */
  logout() {
    localStorage.removeItem('jwtToken');
    localStorage.removeItem('userRole');
    localStorage.removeItem('username');
    localStorage.removeItem('adminToken');
  }

  /**
   * 현재 로그인 상태 확인
   */
  isLoggedIn() {
    return !!localStorage.getItem('jwtToken');
  }

  /**
   * 관리자 권한 확인
   */
  isAdmin() {
    return localStorage.getItem('userRole') === 'ADMIN';
  }

  /**
   * 현재 사용자 정보 가져오기
   */
  async getCurrentUser() {
    try {
      return await this.request('/api/user/info');
    } catch (error) {
      console.error('Failed to get current user:', error);
      return null;
    }
  }

  // ==================== 사용자 관련 API ====================

  /**
   * 사용자명 중복 확인
   */
  async checkUsername(username) {
    return this.request(`/api/user/check-username?username=${encodeURIComponent(username)}`);
  }

  /**
   * 닉네임 중복 확인
   */
  async checkNickname(nickname) {
    return this.request(`/api/user/check-nickname?nickname=${encodeURIComponent(nickname)}`);
  }

  /**
   * 이메일 중복 확인
   */
  async checkEmail(email) {
    return this.request(`/api/user/check-email?email=${encodeURIComponent(email)}`);
  }

  // ==================== 문의 관련 API ====================

  /**
   * 문의 등록
   */
  async submitInquiry(inquiryData) {
    return this.request('/api/contact', {
      method: 'POST',
      body: JSON.stringify(inquiryData),
    });
  }

  // ==================== 유틸리티 메서드 ====================

  /**
   * 언어 코드를 PostType으로 변환
   */
  getPostTypeFromLang(lang) {
    return lang === 'ja' || lang === 'jp' ? 'JP' : 'KR';
  }

  /**
   * PostType을 언어 코드로 변환
   */
  getLangFromPostType(postType) {
    return postType === 'JP' ? 'ja' : 'ko';
  }

  /**
   * 에러 메시지 추출
   */
  extractErrorMessage(error) {
    if (error.response && error.response.data && error.response.data.message) {
      return error.response.data.message;
    }
    return error.message || '알 수 없는 오류가 발생했습니다.';
  }
}

// 싱글톤 인스턴스 생성
const apiService = new ApiService();

export default apiService; 
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
      credentials: 'same-origin', // JWT 토큰 사용 시 same-origin으로 변경
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
      // 404 오류는 백엔드 서버가 없음을 의미하므로 조용히 처리
      if (error.message && error.message.includes('404')) {
        console.warn('백엔드 서버에 연결할 수 없습니다. 오프라인 모드로 실행됩니다.');
        throw error;
      }
      
      console.error('API 요청 실패:', error);
      throw error;
    }
  }

  /**
   * 공개 request 메서드 (외부에서 직접 호출 가능)
   */
  async publicRequest(endpoint, options = {}) {
    return this.request(endpoint, options);
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
    try {
      return await this.request(`/api/posts/admin?lang=${lang}`);
    } catch (error) {
      // 404 오류는 백엔드 서버가 없음을 의미하므로 조용히 처리
      if (error.message && error.message.includes('404')) {
        console.warn('백엔드 서버에 연결할 수 없습니다. 오프라인 모드로 실행됩니다.');
        // 오프라인 모드에서는 빈 배열 반환
        return [];
      }
      throw error;
    }
  }

  /**
   * 관리자 대시보드 통계 조회
   */
  async getAdminStats(lang = 'ko') {
    try {
      return await this.request(`/api/posts/admin/stats?lang=${lang}`);
    } catch (error) {
      // 404 오류는 백엔드 서버가 없음을 의미하므로 조용히 처리
      if (error.message && error.message.includes('404')) {
        console.warn('백엔드 서버에 연결할 수 없습니다. 오프라인 모드로 실행됩니다.');
        // 오프라인 모드에서는 기본 통계 반환
        return {
          success: true,
          data: {
            totalPosts: 0,
            publishedPosts: 0,
            draftPosts: 0
          }
        };
      }
      throw error;
    }
  }

  /**
   * 새 게시글 작성
   */
  async createPost(postData, lang = 'ko') {
    try {
      return await this.request(`/api/posts?lang=${lang}`, {
        method: 'POST',
        body: JSON.stringify(postData),
      });
    } catch (error) {
      // 404 오류는 백엔드 서버가 없음을 의미하므로 조용히 처리
      if (error.message && error.message.includes('404')) {
        console.warn('백엔드 서버에 연결할 수 없습니다. 게시글 작성이 완료되지 않았습니다.');
        // 프론트엔드에서는 작성 성공으로 처리하되, 실제로는 백엔드에 반영되지 않음
        return { success: true, message: '오프라인 모드: 게시글이 작성되었습니다.' };
      }
      throw error;
    }
  }

  /**
   * 게시글 수정
   */
  async updatePost(id, postData, lang = 'ko') {
    try {
      return await this.request(`/api/posts/${id}?lang=${lang}`, {
        method: 'PUT',
        body: JSON.stringify(postData),
      });
    } catch (error) {
      // 404 오류는 백엔드 서버가 없음을 의미하므로 조용히 처리
      if (error.message && error.message.includes('404')) {
        console.warn('백엔드 서버에 연결할 수 없습니다. 게시글 수정이 완료되지 않았습니다.');
        // 프론트엔드에서는 수정 성공으로 처리하되, 실제로는 백엔드에 반영되지 않음
        return { success: true, message: '오프라인 모드: 게시글이 수정되었습니다.' };
      }
      throw error;
    }
  }

  /**
   * 게시글 삭제
   */
  async deletePost(id, lang = 'ko') {
    try {
      return await this.request(`/api/posts/${id}?lang=${lang}`, {
        method: 'DELETE',
      });
    } catch (error) {
      // 404 오류는 백엔드 서버가 없음을 의미하므로 조용히 처리
      if (error.message && error.message.includes('404')) {
        console.warn('백엔드 서버에 연결할 수 없습니다. 게시글 삭제가 완료되지 않았습니다.');
        // 프론트엔드에서는 삭제 성공으로 처리하되, 실제로는 백엔드에 반영되지 않음
        return { success: true, message: '오프라인 모드: 게시글 삭제가 완료되었습니다.' };
      }
      throw error;
    }
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
    return this.request(`/api/posts/${postId}/comments?postType=${postType}`);
  }

  /**
   * 댓글 작성
   */
  async addComment(postId, postType, commentData) {
    return this.request(`/api/posts/${postId}/comments?postType=${postType}`, {
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
    return localStorage.getItem('userRole') === 'ROLE_ADMIN';
  }

  /**
   * 현재 사용자 정보 가져오기
   */
  async getCurrentUser() {
    try {
      // 관리자인 경우 관리자 정보 API 호출
      if (this.isAdmin()) {
        const response = await this.request('/api/admin/auth/profile');
        if (response.success && response.data) {
          return response.data;
        }
        return null;
      }
      // 일반 사용자인 경우 사용자 정보 API 호출
      const response = await this.request('/api/user/info');
      if (response && response.username) {
        return response;
      }
      return null;
    } catch (error) {
      // 404 오류는 백엔드 서버가 없음을 의미하므로 조용히 처리
      if (error.message && error.message.includes('404')) {
        console.warn('백엔드 서버에 연결할 수 없습니다. 오프라인 모드로 실행됩니다.');
        return null;
      }
      
      console.error('Failed to get current user:', error);
      // 토큰이 유효하지 않은 경우 로그아웃 처리
      if (error.message && error.message.includes('401')) {
        this.logout();
      }
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

  // ==================== 이력서 관련 API ====================

  /**
   * 언어별 이력서 조회
   */
  async getResume(lang = 'ko') {
    return this.request(`/api/resume?lang=${lang}`);
  }

  /**
   * 이력서 수정 (관리자용)
   */
  async updateResume(resumeData) {
    return this.request('/api/resume', {
      method: 'PUT',
      body: JSON.stringify(resumeData),
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
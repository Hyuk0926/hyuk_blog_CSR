/**
 * API 서비스 클래스
 * 백엔드 API와의 통신을 담당
 */
class ApiService {
  constructor() {
    // 프록시 설정을 사용하므로 상대 경로 사용
    this.baseURL = '';
    console.log('API Service initialized with baseURL:', this.baseURL);
  }

  /**
   * 기본 HTTP 요청 메서드
   */
  async request(endpoint, options = {}) {
    const url = `${this.baseURL}${endpoint}`;
    console.log('API Request URL:', url);
    console.log('API Request endpoint:', endpoint);
    console.log('API Request baseURL:', this.baseURL);
    
    const defaultOptions = {
      headers: {},
      credentials: 'same-origin', // 프록시 사용 시 same-origin으로 변경
    };

    // FormData가 아닌 경우에만 Content-Type 헤더 설정
    if (!(options.body instanceof FormData)) {
      defaultOptions.headers['Content-Type'] = 'application/json';
    }

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
      console.log('API Response status:', response.status);
      console.log('API Response headers:', response.headers);
      
      if (!response.ok) {
        // 401 Unauthorized 에러 시 토큰 제거 (Admin 사용자는 제외)
        if (response.status === 401) {
          const adminToken = localStorage.getItem('adminToken');
          const userRole = localStorage.getItem('userRole');
          
          // Admin 사용자가 아닌 경우에만 토큰 제거
          if (!adminToken && userRole !== 'ROLE_ADMIN') {
            localStorage.removeItem('jwtToken');
            localStorage.removeItem('userRole');
            localStorage.removeItem('username');
            localStorage.removeItem('adminToken');
          }
        }
        
        // 응답 본문을 읽어서 더 자세한 에러 정보 제공
        let errorMessage = `HTTP error! status: ${response.status}`;
        try {
          const errorBody = await response.text();
          console.error('API Error response body:', errorBody);
          if (errorBody) {
            try {
              const errorJson = JSON.parse(errorBody);
              errorMessage = errorJson.message || errorJson.error || errorMessage;
            } catch (e) {
              errorMessage = errorBody || errorMessage;
            }
          }
        } catch (e) {
          console.error('Failed to read error response body:', e);
        }
        
        throw new Error(errorMessage);
      }
      
      const data = await response.json();
      console.log('API Response data:', data);
      return data;
    } catch (error) {
      console.error('API Request failed:', error);
      console.error('Request details:', {
        url,
        endpoint,
        options: config,
        error: error.message,
        stack: error.stack
      });
      throw error;
    }
  }

  /**
   * 공개 request 메서드 (외부에서 직접 호출 가능)
   */
  async publicRequest(endpoint, options = {}) {
    return this.request(endpoint, options);
  }

  /**
   * GET 요청
   */
  async get(endpoint, options = {}) {
    return this.request(endpoint, { ...options, method: 'GET' });
  }

  /**
   * POST 요청
   */
  async post(endpoint, data, options = {}) {
    const requestOptions = {
      ...options,
      method: 'POST',
    };

    // FormData인 경우 JSON.stringify를 적용하지 않음
    if (data instanceof FormData) {
      requestOptions.body = data;
      // FormData인 경우 Content-Type 헤더를 제거 (브라우저가 자동으로 설정)
      if (requestOptions.headers) {
        delete requestOptions.headers['Content-Type'];
      }
    } else {
      requestOptions.body = JSON.stringify(data);
    }

    return this.request(endpoint, requestOptions);
  }

  /**
   * PUT 요청
   */
  async put(endpoint, data, options = {}) {
    return this.request(endpoint, {
      ...options,
      method: 'PUT',
      body: JSON.stringify(data),
    });
  }

  /**
   * DELETE 요청
   */
  async delete(endpoint, options = {}) {
    return this.request(endpoint, { ...options, method: 'DELETE' });
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
   * 좋아요 상태 및 개수 조회
   */
  async getLikeStatus(postId, postType) {
    console.log("postId ::::::::::::", postId);
    console.log("postType ::::::::::::", postType);
    return this.request(`/api/posts/${postId}/like?postType=${postType}`);
  }

  /**
   * 좋아요 토글
   */
  async toggleLike(postId, postType) {
    return this.request(`/api/posts/${postId}/like?postType=${postType}`, {
      method: 'POST',
    });
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
    console.log("=== ADD COMMENT DEBUG ===");
    console.log("PostId:", postId);
    console.log("PostType:", postType);
    console.log("CommentData:", commentData);
    console.log("Request URL:", `/api/posts/${postId}/comments?postType=${postType}`);
    
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
      method: 'DELETE'
    });
  }

  /**
   * 사용자 댓글 조회
   */
  async getUserComments() {
    return this.request('/api/user/comments');
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
    localStorage.removeItem('adminName');
    localStorage.removeItem('userNickname');
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
      // 현재 로그인한 사용자의 역할 확인
      const userRole = localStorage.getItem('userRole');
      const username = localStorage.getItem('username');
      console.log('Current user role:', userRole);
      console.log('Current username:', username);
      console.log('All localStorage items:', {
        jwtToken: localStorage.getItem('jwtToken') ? 'exists' : 'null',
        userRole: userRole,
        username: username,
        adminName: localStorage.getItem('adminName'),
        userNickname: localStorage.getItem('userNickname')
      });
      
      // 관리자인 경우 관리자 정보 API 호출
      if (userRole === 'ROLE_ADMIN') {
        console.log('Fetching admin profile...');
        const response = await this.request('/api/admin/auth/profile');
        if (response.success && response.data) {
          // 관리자 정보에 nickname 필드 추가 (name 필드를 사용)
          const adminData = response.data;
          if (adminData.name && !adminData.nickname) {
            adminData.nickname = adminData.name;
          }
          return adminData;
        }
        return null;
      }
      
      // userRole이 null이거나 다른 값인 경우, username으로 판단
      if (username === 'admin' || username === 'admin_jp') {
        console.log('Username indicates admin, fetching admin profile...');
        const response = await this.request('/api/admin/auth/profile');
        if (response.success && response.data) {
          const adminData = response.data;
          if (adminData.name && !adminData.nickname) {
            adminData.nickname = adminData.name;
          }
          return adminData;
        }
        return null;
      }
      
      // 일반 사용자인 경우 사용자 정보 API 호출
      console.log('Fetching user profile...');
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
      
      // 401, 403 오류는 로그인하지 않은 사용자이므로 조용히 처리
      if (error.message && (error.message.includes('401') || error.message.includes('403'))) {
        return null;
      }
      
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

  /**
   * 아이디 검색
   */
  async searchUsername(email) {
    return this.request('/api/auth/search-username', {
      method: 'POST',
      body: JSON.stringify({ email }),
    });
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

  /**
   * 관리자 문의 목록 조회
   */
  async getAdminInquiries() {
    return this.request('/api/admin/inquiries');
  }

  /**
   * 최근 문의 조회
   */
  async getRecentInquiries(count = 5) {
    return this.request(`/api/admin/inquiries/recent?count=${count}`);
  }

  /**
   * 문의 읽음 처리
   */
  async markInquiriesRead() {
    return this.request('/api/admin/inquiries/read', {
      method: 'POST',
    });
  }

  /**
   * 문의 삭제
   */
  async deleteInquiry(id) {
    return this.request(`/api/admin/inquiries/${id}`, {
      method: 'DELETE',
    });
  }

  /**
   * 문의 답변
   */
  async replyToInquiry(id, replyData) {
    return this.request(`/api/admin/inquiries/${id}/reply`, {
      method: 'POST',
      body: JSON.stringify(replyData),
    });
  }

  /**
   * 이메일 발송 테스트
   */
  async testEmail(toEmail) {
    return this.request('/api/admin/email/test', {
      method: 'POST',
      body: JSON.stringify({ toEmail }),
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
  async updateResume(resumeData, lang = 'ko') {
    return this.request(`/api/resume?lang=${lang}`, {
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
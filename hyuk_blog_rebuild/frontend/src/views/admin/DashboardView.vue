<template>
  <div class="admin-container">
    <div class="admin-header">
      <h1 class="admin-title">관리자 대시보드</h1>
      <div class="admin-info">
                 <div class="admin-welcome">
           안녕하세요, <span class="admin-name">{{ adminInfo.name || adminInfo.username || '관리자' }}</span>님
         </div>
        <button @click="goToNewPost" class="admin-btn btn-primary">새 게시글</button>
        <button @click="goToResumeManagement" class="admin-btn btn-secondary">이력서 관리</button>
        <button @click="goToInquiryManagement" class="admin-btn btn-secondary">문의 관리</button>
        <button @click="logout" class="admin-btn btn-danger">로그아웃</button>
      </div>
    </div>
    
    <div class="stats-container">
      <div class="stat-card">
        <div class="stat-number">{{ stats.totalPosts }}</div>
        <div class="stat-label">전체 게시글</div>
      </div>
      <div class="stat-card">
        <div class="stat-number">{{ stats.publishedPosts }}</div>
        <div class="stat-label">공개 게시글</div>
      </div>
      <div class="stat-card">
        <div class="stat-number">{{ stats.draftPosts }}</div>
        <div class="stat-label">임시저장</div>
      </div>
    </div>
    
    <div v-if="posts.length === 0" class="no-posts">
      <h3>등록된 게시글이 없습니다.</h3>
      <p>새 게시글을 작성해보세요!</p>
    </div>
    
    <table v-if="posts.length > 0" class="posts-table">
      <thead>
        <tr>
          <th>ID</th>
          <th>제목</th>
          <th>요약</th>
          <th>상태</th>
          <th>작성일</th>
          <th>수정일</th>
          <th>작업</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="post in posts" :key="post.id">
          <td>{{ post.id }}</td>
          <td>{{ lang === 'ja' ? post.titleJa : post.titleKo }}</td>
          <td>{{ lang === 'ja' ? truncateText(post.summaryJa, 50) : truncateText(post.summaryKo, 50) }}</td>
          <td>
            <span :class="post.published ? 'status-badge status-published' : 'status-badge status-draft'">
              {{ post.published ? '공개' : '임시저장' }}
            </span>
          </td>
          <td>{{ formatDate(post.createdAt) }}</td>
          <td>{{ formatDate(post.updatedAt) }}</td>
          <td>
            <div class="action-buttons">
              <button @click="editPost(post.id)" class="btn-action btn-edit">수정</button>
              <button @click="previewPost(post.id)" class="btn-action btn-preview">미리보기</button>
              <button @click="deletePost(post.id)" class="btn-action btn-delete">삭제</button>
            </div>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
import apiService from '@/services/api.js';

export default {
  name: 'DashboardView',
  data() {
    return {
      lang: 'ko',
      adminInfo: {
        username: '',
        name: '',
        email: ''
      },
      stats: {
        totalPosts: 0,
        publishedPosts: 0,
        draftPosts: 0
      },
      posts: []
    }
  },
  mounted() {
    this.checkAuth();
    this.loadAdminInfo();
    this.loadDashboardData();
    this.loadPosts();
  },
  methods: {
    checkAuth() {
      const token = localStorage.getItem('jwtToken');
      const userRole = localStorage.getItem('userRole');
      
      if (!token || userRole !== 'ROLE_ADMIN') {
        localStorage.removeItem('jwtToken');
        localStorage.removeItem('userRole');
        localStorage.removeItem('username');
        localStorage.removeItem('adminToken');
        this.$router.push('/admin/login');
        return;
      }
      
      // JWT 토큰 형식 검증 (eyJ로 시작하는지 확인)
      if (!token.startsWith('eyJ')) {
        localStorage.removeItem('jwtToken');
        localStorage.removeItem('userRole');
        localStorage.removeItem('username');
        localStorage.removeItem('adminToken');
        this.$router.push('/admin/login');
        return;
      }
      
      // 실제 백엔드 토큰 검증 (추후 활성화)
      /*
      try {
        const response = await fetch('/api/auth/validate', {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });
        
        if (!response.ok) {
          localStorage.removeItem('jwtToken');
          localStorage.removeItem('userRole');
          localStorage.removeItem('username');
          localStorage.removeItem('adminToken');
          this.$router.push('/admin/login');
        }
      } catch (error) {
        console.error('토큰 검증 실패:', error);
        localStorage.removeItem('jwtToken');
        localStorage.removeItem('userRole');
        localStorage.removeItem('username');
        localStorage.removeItem('adminToken');
        this.$router.push('/admin/login');
      }
             */
     },
     async loadAdminInfo() {
       try {
         // 로컬 스토리지에서 기본 정보 가져오기
         const username = localStorage.getItem('username');
         this.adminInfo.username = username || '';
         
         // 백엔드에서 상세 정보 가져오기
         const response = await fetch('/api/admin/auth/profile', {
           headers: {
             'Authorization': `Bearer ${localStorage.getItem('jwtToken')}`
           }
         });
         
         if (response.ok) {
           const data = await response.json();
           if (data.success && data.data) {
             this.adminInfo = {
               username: data.data.username,
               name: data.data.name,
               email: data.data.email
             };
           }
         }
       } catch (error) {
         console.error('관리자 정보 로드 실패:', error);
         // 로컬 스토리지 정보만 사용
         const username = localStorage.getItem('username');
         this.adminInfo.username = username || '';
       }
     },
     async loadDashboardData() {
      try {
        const response = await apiService.getAdminStats(this.lang);
        if (response.success) {
          this.stats = response.data;
        } else {
          console.error('대시보드 통계 로드 실패:', response.message);
        }
      } catch (error) {
        console.error('대시보드 데이터 로드 실패:', error);
        // 기본값 설정
        this.stats = {
          totalPosts: 0,
          publishedPosts: 0,
          draftPosts: 0
        };
      }
    },
    logout() {
      localStorage.removeItem('jwtToken');
      localStorage.removeItem('userRole');
      localStorage.removeItem('username');
      localStorage.removeItem('adminToken');
      this.$router.push('/admin/login');
    },
    goToPostManagement() {
      this.$router.push('/admin/posts');
    },
    goToNewPost() {
      this.$router.push('/admin/posts/new');
    },
    goToResumeManagement() {
      this.$router.push('/admin/resume');
    },
    goToInquiryManagement() {
      this.$router.push('/admin/inquiries');
    },
    goToSettings() {
      this.$router.push('/admin/settings');
    },
    async loadPosts() {
      try {
        const response = await apiService.getAllPostsForAdmin(this.lang);
        if (response.success) {
          this.posts = response.data;
        } else {
          console.error('게시글 로드 실패:', response.message);
          this.posts = [];
        }
      } catch (error) {
        console.error('게시글 로드 실패:', error);
        alert('게시글 목록을 불러오는데 실패했습니다.');
        this.posts = [];
      }
    },
    truncateText(text, maxLength) {
      if (!text) return '';
      return text.length > maxLength ? text.substring(0, maxLength) + '...' : text;
    },
    formatDate(dateString) {
      if (!dateString) return '';
      const date = new Date(dateString);
      return date.toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      });
    },
    editPost(postId) {
      this.$router.push(`/admin/posts/edit/${postId}`);
    },
    previewPost(postId) {
      this.$router.push(`/admin/posts/preview/${postId}`);
    },
    async deletePost(postId) {
      if (confirm('정말 삭제하시겠습니까?')) {
        try {
          const response = await apiService.deletePost(postId, this.lang);
          if (response.success) {
            this.posts = this.posts.filter(post => post.id !== postId);
            this.loadDashboardData(); // 통계 업데이트
            alert('게시글이 삭제되었습니다.');
          } else {
            alert('게시글 삭제에 실패했습니다: ' + response.message);
          }
        } catch (error) {
          console.error('게시글 삭제 실패:', error);
          // 에러 상세 정보 출력
          if (error.response) {
            console.error('Response status:', error.response.status);
            console.error('Response data:', error.response.data);
          }
          alert('게시글 삭제에 실패했습니다. 관리자에게 문의하세요.');
        }
      }
    }
  }
}
</script>

<style scoped>
.admin-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 32px 24px;
  background: #f8f9fa;
  min-height: 100vh;
}

.admin-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;
  padding: 24px;
  background: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  border: 1px solid #e9ecef;
}

.admin-title {
  font-size: 1.75rem;
  color: #2c3e50;
  font-weight: 700;
}

.admin-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.admin-welcome {
  color: #5c6a7a;
  font-size: 0.95rem;
}

.admin-name {
  font-weight: 600;
  color: #2c3e50;
}

.admin-btn {
  padding: 8px 16px;
  text-decoration: none;
  border: none;
  border-radius: 6px;
  font-size: 0.9rem;
  font-weight: 500;
  transition: all 0.2s ease;
  cursor: pointer;
}

.btn-primary {
  background: #495057;
  color: white;
  border: none;
  border-radius: 6px;
  padding: 8px 16px;
  font-size: 0.9rem;
  font-weight: 500;
  transition: all 0.2s ease;
  cursor: pointer;
}

.btn-primary:hover {
  background: #343a40;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.btn-secondary {
  background: #f8f9fa;
  color: #495057;
  border: 1px solid #dee2e6;
  border-radius: 6px;
  padding: 8px 16px;
  font-size: 0.9rem;
  font-weight: 500;
  transition: all 0.2s ease;
  cursor: pointer;
}

.btn-secondary:hover {
  background: #e9ecef;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.btn-danger {
  background: #dc3545;
  color: white;
  border: none;
  border-radius: 6px;
  padding: 8px 16px;
  font-size: 0.9rem;
  font-weight: 500;
  transition: all 0.2s ease;
  cursor: pointer;
}

.btn-danger:hover {
  background: #c82333;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(220, 53, 69, 0.3);
}

.stats-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 24px;
  margin-bottom: 40px;
}

.stat-card {
  background: white;
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
  border: 1px solid #edf2f7;
}

.stat-number {
  font-size: 1.75rem;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 8px;
}

.stat-label {
  color: #718096;
  font-size: 0.9rem;
  font-weight: 500;
}

.posts-table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 24px;
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
}

.posts-table th,
.posts-table td {
  padding: 14px 16px;
  text-align: left;
  border-bottom: 1px solid #edf2f7;
  font-size: 0.95rem;
}

.posts-table th {
  background-color: #f8fafc;
  font-weight: 600;
  color: #2c3e50;
}

.posts-table tr:hover {
  background-color: #f8fafc;
}

.status-badge {
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 500;
}

.status-published {
  background-color: #ebf5ee;
  color: #2f855a;
}

.status-draft {
  background-color: #f7f0ea;
  color: #c05621;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.btn-action {
  padding: 6px 12px;
  text-decoration: none;
  border: none;
  border-radius: 4px;
  font-size: 0.85rem;
  font-weight: 500;
  transition: all 0.2s ease;
  cursor: pointer;
}

.btn-edit {
  background: #f8f9fa;
  color: #495057;
  border: 1px solid #e9ecef;
}

.btn-edit:hover {
  background: #e9ecef;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.btn-delete {
  background: #fff5f5;
  color: #dc3545;
  border: 1px solid #fed7d7;
}

.btn-delete:hover {
  background: #fed7d7;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(220, 53, 69, 0.2);
}

.btn-preview {
  background: #f8f9fa;
  color: #495057;
  border: 1px solid #e9ecef;
}

.btn-preview:hover {
  background: #e9ecef;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.no-posts {
  text-align: center;
  padding: 60px 20px;
  color: #718096;
  background: white;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
}

/* 다크모드 지원 */
body.dark-mode .admin-container {
  background: #1a202c;
}

body.dark-mode .admin-title,
body.dark-mode .admin-name {
  color: #f7fafc;
}

body.dark-mode .admin-welcome {
  color: #a0aec0;
}

body.dark-mode .posts-table {
  background: #2d3748;
  box-shadow: 0 1px 3px rgba(0,0,0,0.2);
}

body.dark-mode .posts-table th {
  background: #2d3748;
  color: #f7fafc;
  border-bottom: 1px solid #4a5568;
}

body.dark-mode .posts-table td {
  border-bottom: 1px solid #4a5568;
  color: #e2e8f0;
}

body.dark-mode .posts-table tr:hover {
  background: #323c4e;
}

body.dark-mode .stat-card {
  background: #2d3748;
  border-color: #4a5568;
}

body.dark-mode .stat-number {
  color: #f7fafc;
}

body.dark-mode .stat-label {
  color: #a0aec0;
}

body.dark-mode .btn-secondary {
  background: #2d3748;
  border-color: #4a5568;
  color: #f7fafc;
}

body.dark-mode .btn-secondary:hover {
  background: #4a5568;
}

body.dark-mode .no-posts {
  background: #2d3748;
  color: #a0aec0;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .admin-container {
    padding: 16px;
  }
  
  .admin-header {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }
  
  .admin-info {
    flex-wrap: wrap;
    justify-content: center;
  }
  
  .stats-container {
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 16px;
  }
  
  .posts-table {
    font-size: 0.85rem;
  }
  
  .posts-table th,
  .posts-table td {
    padding: 10px 12px;
  }
  
  .action-buttons {
    flex-direction: column;
    gap: 4px;
  }
}
</style> 
<template>
  <div class="post-form-container">
    <div class="form-header">
      <div class="header-left">
        <h1 class="form-title">{{ isEdit ? '게시글 수정' : '새 게시글 작성' }}</h1>
        <div class="admin-info">
          <span class="admin-welcome">
            <span class="admin-name">관리자</span>님
          </span>
        </div>
      </div>
      <div class="header-right">
        <button @click="goToDashboard" class="btn-secondary">대시보드</button>
        <button @click="logout" class="btn-danger">로그아웃</button>
      </div>
    </div>

    <div class="form-content">
      <div class="form-main">
        <div class="title-section">
          <input 
            v-model="formData.titleKo" 
            type="text" 
            class="title-input" 
            placeholder="제목을 입력하세요..."
            required
          >
        </div>

        <div class="meta-section">
          <div class="meta-row">
            <div class="meta-item">
              <label class="meta-label">카테고리</label>
              <select v-model="formData.category" class="meta-select" required>
                <option value="">카테고리 선택</option>
                <option value="programming">프로그래밍</option>
                <option value="design">디자인</option>
                <option value="life">일상</option>
                <option value="tech">기술</option>
                <option value="review">리뷰</option>
              </select>
            </div>
            <div class="meta-item">
              <label class="meta-label">상태</label>
              <div class="publish-toggle">
                <input 
                  v-model="formData.published" 
                  type="checkbox" 
                  id="published" 
                  class="publish-checkbox"
                >
                <label for="published" class="publish-label">
                  {{ formData.published ? '공개' : '임시저장' }}
                </label>
              </div>
            </div>
          </div>
        </div>

        <div class="content-section">
          <div class="content-tabs">
            <button 
              @click="activeTab = 'write'" 
              :class="['tab-btn', { active: activeTab === 'write' }]"
            >
              작성
            </button>
            <button 
              @click="activeTab = 'preview'" 
              :class="['tab-btn', { active: activeTab === 'preview' }]"
            >
              미리보기
            </button>
          </div>

          <div v-if="activeTab === 'write'" class="write-area">
            <div class="summary-section">
              <textarea 
                v-model="formData.summaryKo" 
                class="summary-input" 
                placeholder="게시글 요약을 입력하세요... (선택사항)"
                rows="3"
              ></textarea>
            </div>
            
            <div class="content-editor">
              <textarea 
                v-model="formData.contentKo" 
                class="content-input" 
                placeholder="내용을 입력하세요..."
                required
              ></textarea>
            </div>
          </div>

          <div v-if="activeTab === 'preview'" class="preview-area">
            <div class="preview-content">
              <h1 class="preview-title">{{ formData.titleKo || '제목 없음' }}</h1>
              <div class="preview-meta">
                <span class="preview-category">{{ getCategoryName(formData.category) }}</span>
                <span class="preview-status" :class="formData.published ? 'published' : 'draft'">
                  {{ formData.published ? '공개' : '임시저장' }}
                </span>
              </div>
              <div v-if="formData.summaryKo" class="preview-summary">
                {{ formData.summaryKo }}
              </div>
              <div class="preview-body" v-html="formData.contentKo"></div>
            </div>
          </div>
        </div>
      </div>

      <div class="form-sidebar">
        <div class="sidebar-section">
          <h3 class="sidebar-title">게시 설정</h3>
          <div class="setting-item">
            <label class="setting-label">대표 이미지</label>
            <input 
              v-model="formData.imageUrl" 
              type="url" 
              class="setting-input" 
              placeholder="이미지 URL 입력..."
            >
          </div>
          <div class="setting-item">
            <label class="setting-label">태그</label>
            <input 
              v-model="formData.tags" 
              type="text" 
              class="setting-input" 
              placeholder="태그1, 태그2, 태그3..."
            >
            <div class="help-text">쉼표로 구분하여 입력하세요</div>
          </div>
        </div>

        <div class="sidebar-section">
          <h3 class="sidebar-title">HTML 도움말</h3>
          <div class="help-list">
            <div class="help-item">
              <code>&lt;h2&gt;</code> - 제목
            </div>
            <div class="help-item">
              <code>&lt;p&gt;</code> - 단락
            </div>
            <div class="help-item">
              <code>&lt;ul&gt;</code> - 순서없는 목록
            </div>
            <div class="help-item">
              <code>&lt;ol&gt;</code> - 순서있는 목록
            </div>
            <div class="help-item">
              <code>&lt;code&gt;</code> - 인라인 코드
            </div>
            <div class="help-item">
              <code>&lt;pre&gt;</code> - 코드 블록
            </div>
            <div class="help-item">
              <code>&lt;strong&gt;</code> - 굵은 글씨
            </div>
            <div class="help-item">
              <code>&lt;em&gt;</code> - 기울임 글씨
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="form-footer">
      <div class="footer-right">
        <button @click="goToDashboard" class="btn-cancel">취소</button>
        <button @click="submitForm" class="btn-primary" :disabled="isSubmitting">
          {{ isSubmitting ? '저장 중...' : (isEdit ? '수정하기' : '발행하기') }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'PostFormView',
  data() {
    return {
      isEdit: false,
      isSubmitting: false,
      activeTab: 'write',
      formData: {
        titleKo: '',
        titleJa: '',
        summaryKo: '',
        summaryJa: '',
        contentKo: '',
        contentJa: '',
        category: '',
        imageUrl: '',
        tags: '',
        published: false
      }
    }
  },
  mounted() {
    this.checkAuth();
    this.loadPostData();
  },
  methods: {
    checkAuth() {
      const token = localStorage.getItem('adminToken');
      if (!token || !token.startsWith('temp_admin_token_')) {
        this.$router.push('/admin/login');
      }
    },
    loadPostData() {
      // 편집 모드인 경우 기존 데이터 로드
      const postId = this.$route.params.id;
      if (postId) {
        this.isEdit = true;
        // 실제로는 API에서 데이터를 가져옴
        // 임시로 샘플 데이터 사용
        this.formData = {
          titleKo: '기존 게시글 제목',
          titleJa: '既存の投稿タイトル',
          summaryKo: '기존 게시글 요약',
          summaryJa: '既存の投稿要約',
          contentKo: '<h2>기존 내용</h2><p>이미 작성된 게시글 내용입니다.</p>',
          contentJa: '<h2>既存の内容</h2><p>既に作成された投稿内容です。</p>',
          category: 'programming',
          imageUrl: 'https://example.com/image.jpg',
          tags: 'Vue.js, JavaScript, 웹개발',
          published: true
        };
      }
    },
    getCategoryName(category) {
      const categories = {
        programming: '프로그래밍',
        design: '디자인',
        life: '일상',
        tech: '기술',
        review: '리뷰'
      };
      return categories[category] || category;
    },

    async submitForm() {
      if (!this.formData.titleKo.trim()) {
        alert('제목을 입력해주세요.');
        return;
      }
      if (!this.formData.category) {
        alert('카테고리를 선택해주세요.');
        return;
      }
      if (!this.formData.contentKo.trim()) {
        alert('내용을 입력해주세요.');
        return;
      }

      this.isSubmitting = true;
      
      try {
        // 실제 백엔드 API 호출
        // const url = this.isEdit ? `/api/admin/posts/${this.$route.params.id}` : '/api/admin/posts';
        // const method = this.isEdit ? 'PUT' : 'POST';
        // 
        // const response = await fetch(url, {
        //   method: method,
        //   headers: {
        //     'Content-Type': 'application/json',
        //     'Authorization': `Bearer ${localStorage.getItem('adminToken')}`
        //   },
        //   body: JSON.stringify(this.formData)
        // });
        // 
        // if (response.ok) {
        //   this.$router.push('/admin/dashboard');
        // }

        // 임시 저장 (실제로는 백엔드에서 처리)
        console.log('게시글 저장:', this.formData);
        alert(this.formData.published ? '게시글이 발행되었습니다.' : '임시저장되었습니다.');
        this.$router.push('/admin/dashboard');
        
      } catch (error) {
        console.error('게시글 저장 실패:', error);
        alert('저장 중 오류가 발생했습니다.');
      } finally {
        this.isSubmitting = false;
      }
    },
    goToDashboard() {
      this.$router.push('/admin/dashboard');
    },
    logout() {
      localStorage.removeItem('adminToken');
      this.$router.push('/admin/login');
    }
  }
}
</script>

<style scoped>
.post-form-container {
  min-height: 100vh;
  background: #f8f9fa;
  display: flex;
  flex-direction: column;
}

.form-header {
  background: #ffffff;
  border-bottom: 1px solid #e9ecef;
  padding: 20px 32px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 24px;
}

.form-title {
  font-size: 1.75rem;
  font-weight: 700;
  color: #2c3e50;
  margin: 0;
}

.admin-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.admin-welcome {
  color: #6c757d;
  font-size: 0.9rem;
}

.admin-name {
  font-weight: 600;
  color: #007bff;
}

.header-right {
  display: flex;
  gap: 12px;
}

.form-content {
  flex: 1;
  display: flex;
  gap: 32px;
  padding: 32px;
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
}

.form-main {
  flex: 1;
  background: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  border: 1px solid #e9ecef;
}

.title-section {
  padding: 32px;
  border-bottom: 1px solid #e9ecef;
  background: #f8f9fa;
}

.title-input {
  width: 100%;
  font-size: 2.25rem;
  font-weight: 800;
  border: none;
  outline: none;
  color: #2c3e50;
  background: transparent;
  transition: all 0.3s ease;
}

.title-input:focus {
  transform: translateY(-2px);
}

.title-input::placeholder {
  color: #adb5bd;
  font-weight: 400;
}

.meta-section {
  padding: 20px 32px;
  border-bottom: 1px solid #e9ecef;
  background: #ffffff;
}

.meta-row {
  display: flex;
  gap: 24px;
  align-items: center;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.meta-label {
  font-size: 0.9rem;
  font-weight: 600;
  color: #495057;
  min-width: 60px;
}

.meta-select {
  padding: 8px 16px;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  font-size: 0.9rem;
  background: white;
  transition: all 0.3s ease;
  cursor: pointer;
}

.meta-select:focus {
  outline: none;
  border-color: #495057;
  box-shadow: 0 0 0 3px rgba(73, 80, 87, 0.1);
}

.publish-toggle {
  display: flex;
  align-items: center;
  gap: 8px;
}

.publish-checkbox {
  width: 20px;
  height: 20px;
  accent-color: #495057;
  cursor: pointer;
}

.publish-label {
  font-size: 0.9rem;
  font-weight: 600;
  color: #495057;
  cursor: pointer;
  transition: color 0.3s ease;
}

.publish-label:hover {
  color: #2c3e50;
}

.content-section {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.content-tabs {
  display: flex;
  border-bottom: 1px solid #e9ecef;
}

.tab-btn {
  flex: 1;
  padding: 16px 24px;
  border: none;
  background: #f8f9fa;
  color: #6c757d;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.tab-btn::before {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  width: 0;
  height: 3px;
  background: #495057;
  transition: all 0.3s ease;
  transform: translateX(-50%);
}

.tab-btn.active {
  background: white;
  color: #495057;
  font-weight: 700;
}

.tab-btn.active::before {
  width: 100%;
}

.tab-btn:hover {
  background: #e9ecef;
  transform: translateY(-1px);
}

.write-area {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.summary-section {
  padding: 16px 24px;
  border-bottom: 1px solid #e9ecef;
}

.summary-input {
  width: 100%;
  border: none;
  outline: none;
  resize: none;
  font-size: 1rem;
  line-height: 1.5;
  color: #495057;
  background: transparent;
}

.summary-input::placeholder {
  color: #adb5bd;
}

.content-editor {
  flex: 1;
  padding: 32px;
}

.content-input {
  width: 100%;
  height: 100%;
  min-height: 500px;
  border: none;
  outline: none;
  resize: none;
  font-size: 1.1rem;
  line-height: 1.8;
  color: #2c3e50;
  background: transparent;
  font-family: 'Noto Sans KR', sans-serif;
  transition: all 0.3s ease;
}

.content-input:focus {
  transform: scale(1.01);
}

.content-input::placeholder {
  color: #adb5bd;
  font-style: italic;
}

.preview-area {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
}

.preview-content {
  max-width: 800px;
  margin: 0 auto;
}

.preview-title {
  font-size: 2.5rem;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 16px;
}

.preview-meta {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e9ecef;
}

.preview-category {
  background: #e9ecef;
  color: #495057;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 500;
}

.preview-status {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 500;
}

.preview-status.published {
  background: #d4edda;
  color: #155724;
}

.preview-status.draft {
  background: #fff3cd;
  color: #856404;
}

.preview-summary {
  font-size: 1.1rem;
  color: #6c757d;
  margin-bottom: 24px;
  padding: 16px;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 4px solid #007bff;
}

.preview-body {
  line-height: 1.8;
  color: #2c3e50;
}

.preview-body h2 {
  font-size: 1.8rem;
  font-weight: 600;
  margin: 32px 0 16px 0;
  color: #2c3e50;
}

.preview-body p {
  margin-bottom: 16px;
}

.preview-body ul, .preview-body ol {
  margin-bottom: 16px;
  padding-left: 24px;
}

.preview-body li {
  margin-bottom: 8px;
}

.preview-body code {
  background: #f8f9fa;
  padding: 2px 6px;
  border-radius: 4px;
  font-family: 'Courier New', monospace;
  font-size: 0.9rem;
}

.preview-body pre {
  background: #f8f9fa;
  padding: 16px;
  border-radius: 8px;
  overflow-x: auto;
  margin: 16px 0;
}

.preview-body pre code {
  background: none;
  padding: 0;
}

.form-sidebar {
  width: 320px;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.sidebar-section {
  background: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  padding: 24px;
  border: 1px solid #e9ecef;
}

.sidebar-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 16px 0;
}

.setting-item {
  margin-bottom: 16px;
}

.setting-label {
  display: block;
  font-size: 0.9rem;
  font-weight: 600;
  color: #495057;
  margin-bottom: 6px;
}

.setting-input {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  font-size: 0.9rem;
  background: white;
  transition: all 0.3s ease;
  box-sizing: border-box;
}

.setting-input:focus {
  outline: none;
  border-color: #495057;
  box-shadow: 0 0 0 3px rgba(73, 80, 87, 0.1);
  transform: translateY(-1px);
}

.help-text {
  font-size: 0.8rem;
  color: #6c757d;
  margin-top: 4px;
}

.help-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.help-item {
  font-size: 0.85rem;
  color: #495057;
}

.help-item code {
  background: #f8f9fa;
  padding: 2px 6px;
  border-radius: 4px;
  font-family: 'Courier New', monospace;
  color: #e83e8c;
}

.form-footer {
  background: #ffffff;
  border-top: 1px solid #e9ecef;
  padding: 20px 32px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  position: sticky;
  bottom: 0;
  z-index: 100;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.08);
}

.footer-left, .footer-right {
  display: flex;
  gap: 12px;
}

.btn-primary, .btn-secondary, .btn-danger, .btn-cancel {
  padding: 12px 24px;
  border: none;
  border-radius: 12px;
  font-size: 0.95rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.btn-primary {
  background: #495057;
  color: white;
  box-shadow: 0 2px 8px rgba(73, 80, 87, 0.2);
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(73, 80, 87, 0.3);
}

.btn-primary:active {
  transform: translateY(0);
}

.btn-primary:disabled {
  background: #6c757d;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.btn-secondary {
  background: #6c757d;
  color: white;
}

.btn-secondary:hover {
  background: #545b62;
}

.btn-danger {
  background: #dc3545;
  color: white;
}

.btn-danger:hover {
  background: #c82333;
}

.btn-cancel {
  background: #f8f9fa;
  color: #6c757d;
  border: 2px solid #e9ecef;
}

.btn-cancel:hover {
  background: #e9ecef;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

/* 다크모드 지원 */
:global(body.dark-mode) .post-form-container {
  background: #1a202c;
}

:global(body.dark-mode) .form-header,
:global(body.dark-mode) .form-main,
:global(body.dark-mode) .sidebar-section,
:global(body.dark-mode) .form-footer {
  background: #2d3748;
  border-color: #4a5568;
}

:global(body.dark-mode) .form-title,
:global(body.dark-mode) .sidebar-title {
  color: #f7fafc;
}

:global(body.dark-mode) .admin-welcome {
  color: #a0aec0;
}

:global(body.dark-mode) .title-input,
:global(body.dark-mode) .summary-input,
:global(body.dark-mode) .content-input {
  background: #1a202c;
  color: #e2e8f0;
  border-color: #4a5568;
}

:global(body.dark-mode) .title-input::placeholder,
:global(body.dark-mode) .summary-input::placeholder,
:global(body.dark-mode) .content-input::placeholder {
  color: #718096;
}

:global(body.dark-mode) .title-input:focus,
:global(body.dark-mode) .summary-input:focus,
:global(body.dark-mode) .content-input:focus {
  border-color: #a0aec0;
  box-shadow: 0 0 0 2px rgba(160,174,192,0.25);
}

:global(body.dark-mode) .meta-section {
  background: #1a202c;
}

:global(body.dark-mode) .meta-label,
:global(body.dark-mode) .publish-label {
  color: #e2e8f0;
}

:global(body.dark-mode) .meta-select,
:global(body.dark-mode) .setting-input {
  background: #1a202c;
  border-color: #4a5568;
  color: #e2e8f0;
}

:global(body.dark-mode) .meta-select:focus,
:global(body.dark-mode) .setting-input:focus {
  border-color: #a0aec0;
  box-shadow: 0 0 0 2px rgba(160,174,192,0.25);
}

:global(body.dark-mode) .tab-btn {
  background: #1a202c;
  color: #a0aec0;
  border-color: #4a5568;
}

:global(body.dark-mode) .tab-btn.active {
  background: #2d3748;
  color: #a0aec0;
  border-color: #a0aec0;
}

:global(body.dark-mode) .preview-title {
  color: #f7fafc;
}

:global(body.dark-mode) .preview-body {
  color: #e2e8f0;
}

:global(body.dark-mode) .preview-summary {
  background: #1a202c;
  color: #a0aec0;
  border-left-color: #a0aec0;
}

:global(body.dark-mode) .help-item {
  color: #e2e8f0;
}

:global(body.dark-mode) .help-item code {
  background: #1a202c;
  color: #fbb6ce;
}

:global(body.dark-mode) .btn-secondary {
  background: #4a5568;
  color: #e2e8f0;
  border-color: #4a5568;
}

:global(body.dark-mode) .btn-secondary:hover {
  background: #718096;
  border-color: #718096;
}

:global(body.dark-mode) .btn-danger {
  background: #c53030;
  color: #fed7d7;
  border-color: #c53030;
}

:global(body.dark-mode) .btn-danger:hover {
  background: #e53e3e;
  border-color: #e53e3e;
}

:global(body.dark-mode) .btn-cancel {
  background: #4a5568;
  color: #e2e8f0;
  border-color: #4a5568;
}

:global(body.dark-mode) .btn-cancel:hover {
  background: #718096;
  border-color: #718096;
}

:global(body.dark-mode) .publish-checkbox {
  accent-color: #a0aec0;
}

:global(body.dark-mode) .content-tabs {
  background: #1a202c;
  border-color: #4a5568;
}

:global(body.dark-mode) .write-area,
:global(body.dark-mode) .preview-area {
  background: #1a202c;
  border-color: #4a5568;
}

/* 반응형 디자인 */
@media (max-width: 1024px) {
  .form-content {
    flex-direction: column;
    gap: 16px;
  }
  
  .form-sidebar {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .form-header {
    padding: 12px 16px;
  }
  
  .header-left {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .form-title {
    font-size: 1.25rem;
  }
  
  .form-content {
    padding: 16px;
  }
  
  .title-input {
    font-size: 1.5rem;
  }
  
  .meta-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .form-footer {
    padding: 12px 16px;
  }
  
  .footer-left, .footer-right {
    flex-direction: column;
    gap: 8px;
  }
}
</style> 
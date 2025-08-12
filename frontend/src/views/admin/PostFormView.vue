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
          <div class="language-tabs">
            <button 
              @click="activeLanguage = 'ko'" 
              :class="['lang-tab', { active: activeLanguage === 'ko' }]"
            >
              한국어
            </button>
            <button 
              @click="activeLanguage = 'ja'" 
              :class="['lang-tab', { active: activeLanguage === 'ja' }]"
            >
              日本語
            </button>
          </div>
          
          <input 
            v-if="activeLanguage === 'ko'"
            v-model="formData.titleKo" 
            type="text" 
            class="title-input" 
            placeholder="한국어 제목을 입력하세요..."
            required
          >
          <input 
            v-if="activeLanguage === 'ja'"
            v-model="formData.titleJa" 
            type="text" 
            class="title-input" 
            placeholder="日本語のタイトルを入力してください..."
            required
          >
        </div>

        <div class="meta-section">
          <div class="meta-row">
            <div class="meta-item">
              <label class="meta-label">카테고리</label>
              <div class="category-input-group">
                <select v-model="formData.category" class="meta-select" required>
                  <option value="">카테고리 선택</option>
                  <option v-for="category in categories" :key="category.value" :value="category.value">
                    {{ category.displayName }}
                  </option>
                  <option value="custom">직접 입력</option>
                </select>
                <input 
                  v-if="formData.category === 'custom'"
                  v-model="customCategory" 
                  type="text" 
                  class="custom-category-input" 
                  placeholder="새 카테고리명 입력..."
                  @input="updateCustomCategory"
                >
              </div>
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
                v-if="activeLanguage === 'ko'"
                v-model="formData.summaryKo" 
                class="summary-input" 
                placeholder="한국어 요약을 입력하세요... (선택사항)"
                rows="3"
              ></textarea>
              <textarea 
                v-if="activeLanguage === 'ja'"
                v-model="formData.summaryJa" 
                class="summary-input" 
                placeholder="日本語の要約を入力してください... (選択項目)"
                rows="3"
              ></textarea>
            </div>
            
                         <div class="content-editor">
               <Editor
                 v-if="activeLanguage === 'ko'"
                 :key="'ko-editor'"
                 v-model="formData.contentKo"
                 :init="editorConfig"
                 :placeholder="'한국어 내용을 입력하세요...'"
                 class="content-input"
               />
               <Editor
                 v-if="activeLanguage === 'ja'"
                 :key="'ja-editor'"
                 v-model="formData.contentJa"
                 :init="editorConfig"
                 :placeholder="'日本語の内容を入力してください...'"
                 class="content-input"
               />
             </div>
          </div>

          <div v-if="activeTab === 'preview'" class="preview-area">
            <div class="preview-content">
              <h1 class="preview-title">{{ getDisplayTitle() }}</h1>
              <div class="preview-meta">
                <span class="preview-category">{{ getCategoryName(formData.category) }}</span>
                <span class="preview-status" :class="formData.published ? 'published' : 'draft'">
                  {{ formData.published ? '공개' : '임시저장' }}
                </span>
              </div>
              <div v-if="getDisplaySummary()" class="preview-summary">
                {{ getDisplaySummary() }}
              </div>
              <div class="preview-body" v-html="getDisplayContent()"></div>
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
           <h3 class="sidebar-title">빠른 가이드</h3>
           <div class="help-list">
             <div class="help-item">
               <strong>서식</strong>
               <p>굵게, 기울임, 색상</p>
             </div>
             <div class="help-item">
               <strong>구조</strong>
               <p>제목, 목록, 정렬</p>
             </div>
             <div class="help-item">
               <strong>미디어</strong>
               <p>링크, 이미지, 코드</p>
             </div>
             <div class="help-item">
               <strong>미리보기</strong>
               <p>실제 결과 확인</p>
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
import apiService from '@/services/api.js';
import Editor from '@tinymce/tinymce-vue';

export default {
  name: 'PostFormView',
  components: {
    Editor
  },
  data() {
    return {
      isEdit: false,
      isSubmitting: false,
      activeTab: 'write',
      activeLanguage: 'ko', // 기본 언어 설정
      lang: 'ko', // API 요청용 언어 설정
      categories: [],
      customCategory: '', // 사용자가 직접 입력한 카테고리 이름
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
      },
      editorConfig: {
        height: 400,
        language: 'ko_KR',
        plugins: [
          'advlist', 'autolink', 'lists', 'link', 'image', 'charmap', 'preview',
          'anchor', 'searchreplace', 'visualblocks', 'code', 'fullscreen',
          'insertdatetime', 'media', 'table', 'help', 'wordcount', 'paste'
        ],
        toolbar: [
          'undo redo | formatselect | bold italic underline strikethrough | forecolor backcolor',
          'alignleft aligncenter alignright alignjustify | bullist numlist outdent indent',
          'link image table | code preview | removeformat help'
        ].join(' | '),
        content_style: `
          body { 
            font-family: 'Noto Sans KR', 'Noto Sans JP', 'Malgun Gothic', 'Hiragino Kaku Gothic ProN', sans-serif; 
            font-size: 14px; 
            line-height: 1.6; 
            color: #2c3e50; 
            margin: 0;
            padding: 16px 20px;
          }
          .mce-content-body[data-mce-placeholder]:not(.mce-visualblocks)::before {
            color: #adb5bd;
            font-style: italic;
          }
          h1, h2, h3, h4, h5, h6 {
            color: #2c3e50;
            margin-top: 24px;
            margin-bottom: 16px;
          }
          p {
            margin-bottom: 16px;
          }
          code {
            background: #f8f9fa;
            padding: 2px 6px;
            border-radius: 4px;
            font-family: 'Courier New', monospace;
            color: #e83e8c;
          }
          pre {
            background: #f8f9fa;
            padding: 16px;
            border-radius: 8px;
            overflow-x: auto;
            border-left: 4px solid #007bff;
          }
          blockquote {
            border-left: 4px solid #007bff;
            padding-left: 16px;
            margin: 16px 0;
            color: #6c757d;
            font-style: italic;
          }
          a {
            color: #007bff;
            text-decoration: none;
          }
          a:hover {
            text-decoration: underline;
          }
        `,
        placeholder: '내용을 입력하세요...',
        menubar: false,
        branding: false,
        elementpath: false,
        statusbar: false,
        resize: false,
        paste_as_text: false,
        paste_enable_default_filters: true,
        paste_word_valid_elements: 'b,strong,i,em,h1,h2,h3,h4,h5,h6',
        paste_retain_style_properties: 'color background-color font-size font-family',
        setup: (editor) => {
          // 언어별 폰트 설정
          editor.on('init', () => {
            const currentLang = this.activeLanguage;
            if (currentLang === 'ja') {
              editor.dom.addStyle(`
                body { 
                  font-family: 'Noto Sans JP', 'Hiragino Kaku Gothic ProN', 'Yu Gothic', sans-serif !important; 
                }
              `);
            } else {
              editor.dom.addStyle(`
                body { 
                  font-family: 'Noto Sans KR', 'Malgun Gothic', sans-serif !important; 
                }
              `);
            }
          });
        }
      }
    }
  },
  mounted() {
    this.checkAuth();
    this.loadCategories();
    this.setLanguage();
    this.loadPostData();
  },
     watch: {
     activeLanguage() {
       this.updateEditorConfig();
     }
   },
   beforeUnmount() {
     // 컴포넌트 언마운트 시 에디터 정리
     this.cleanupEditors();
   },
  methods: {
    async loadCategories() {
      try {
        const categories = await apiService.getCategories();
        this.categories = categories;
      } catch (error) {
        console.error('카테고리 로드 실패:', error);
        // 기본 카테고리 설정
        this.categories = [
          { value: 'HTML', displayName: 'HTML' },
          { value: 'CSS', displayName: 'CSS' },
          { value: 'JAVASCRIPT', displayName: 'JavaScript' },
          { value: 'REACT', displayName: 'React' },
          { value: 'SQL', displayName: 'SQL' },
          { value: 'JAVA', displayName: 'Java' },
          { value: 'PYTHON', displayName: 'Python' },
          { value: 'BLOG_CODING', displayName: 'BlogBuild' }
        ];
      }
    },
    setLanguage() {
      // localStorage에서 저장된 언어 정보 가져오기
      const savedLang = localStorage.getItem('userLang');
      if (savedLang) {
        this.lang = savedLang;
        this.activeLanguage = savedLang;
      } else {
        // username 기반으로 언어 설정
        const username = localStorage.getItem('username');
        if (username === 'admin_jp') {
          this.lang = 'ja';
          this.activeLanguage = 'ja';
        } else {
          this.lang = 'ko';
          this.activeLanguage = 'ko';
        }
      }
      console.log('PostFormView - Language set to:', this.lang);
      this.updateEditorConfig();
    },
    
         updateEditorConfig() {
       // 언어에 따른 에디터 설정 업데이트
       const newConfig = {
         ...this.editorConfig,
         language: this.activeLanguage === 'ja' ? 'ja' : 'ko_KR',
         placeholder: this.activeLanguage === 'ja' ? 
           '内容を入力してください...' : '내용을 입력하세요...'
       };
       
       // 에디터 설정을 완전히 새로 생성
       this.editorConfig = {
         height: 400,
         language: newConfig.language,
         plugins: [
           'advlist', 'autolink', 'lists', 'link', 'image', 'charmap', 'preview',
           'anchor', 'searchreplace', 'visualblocks', 'code', 'fullscreen',
           'insertdatetime', 'media', 'table', 'help', 'wordcount', 'paste'
         ],
         toolbar: [
           'undo redo | formatselect | bold italic underline strikethrough | forecolor backcolor',
           'alignleft aligncenter alignright alignjustify | bullist numlist outdent indent',
           'link image table | code preview | removeformat help'
         ].join(' | '),
         content_style: `
           body { 
             font-family: 'Noto Sans KR', 'Noto Sans JP', 'Malgun Gothic', 'Hiragino Kaku Gothic ProN', sans-serif; 
             font-size: 14px; 
             line-height: 1.6; 
             color: #2c3e50; 
             margin: 0;
             padding: 16px 20px;
           }
           .mce-content-body[data-mce-placeholder]:not(.mce-visualblocks)::before {
             color: #adb5bd;
             font-style: italic;
           }
           h1, h2, h3, h4, h5, h6 {
             color: #2c3e50;
             margin-top: 24px;
             margin-bottom: 16px;
           }
           p {
             margin-bottom: 16px;
           }
           code {
             background: #f8f9fa;
             padding: 2px 6px;
             border-radius: 4px;
             font-family: 'Courier New', monospace;
             color: #e83e8c;
           }
           pre {
             background: #f8f9fa;
             padding: 16px;
             border-radius: 8px;
             overflow-x: auto;
             border-left: 4px solid #007bff;
           }
           blockquote {
             border-left: 4px solid #007bff;
             padding-left: 16px;
             margin: 16px 0;
             color: #6c757d;
             font-style: italic;
           }
           a {
             color: #007bff;
             text-decoration: none;
           }
           a:hover {
             text-decoration: underline;
           }
         `,
         placeholder: newConfig.placeholder,
         menubar: false,
         branding: false,
         elementpath: false,
         statusbar: false,
         resize: false,
         paste_as_text: false,
         paste_enable_default_filters: true,
         paste_word_valid_elements: 'b,strong,i,em,h1,h2,h3,h4,h5,h6',
         paste_retain_style_properties: 'color background-color font-size font-family',
         setup: (editor) => {
           // 언어별 폰트 설정
           editor.on('init', () => {
             const currentLang = this.activeLanguage;
             if (currentLang === 'ja') {
               editor.dom.addStyle(`
                 body { 
                   font-family: 'Noto Sans JP', 'Hiragino Kaku Gothic ProN', 'Yu Gothic', sans-serif !important; 
                 }
               `);
             } else {
               editor.dom.addStyle(`
                 body { 
                   font-family: 'Noto Sans KR', 'Malgun Gothic', sans-serif !important; 
                 }
               `);
             }
           });
         }
       };
     },
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
    },
    async loadPostData() {
      // 편집 모드인 경우 기존 데이터 로드
      const postId = this.$route.params.id;
      console.log('PostFormView - loadPostData called with postId:', postId);
      console.log('PostFormView - current lang:', this.lang);
      
      if (postId) {
        this.isEdit = true;
        try {
          console.log('PostFormView - Fetching post data for ID:', postId, 'with lang:', this.lang);
          const response = await apiService.getPost(postId, this.lang);
          console.log('PostFormView - API response:', response);
          
          if (response.success) {
            const postData = response.data;
            console.log('PostFormView - Post data loaded:', postData);
            this.formData = {
              titleKo: postData.titleKo || '',
              titleJa: postData.titleJa || '',
              summaryKo: postData.summaryKo || '',
              summaryJa: postData.summaryJa || '',
              contentKo: postData.contentKo || '',
              contentJa: postData.contentJa || '',
              category: postData.category || '',
              imageUrl: postData.imageUrl || '',
              tags: postData.tags || '',
              published: postData.published || false
            };
            console.log('PostFormView - Form data set:', this.formData);
          } else {
            console.error('PostFormView - API response not successful:', response);
          }
        } catch (error) {
          console.error('PostFormView - 게시글 데이터 로드 실패:', error);
          alert('게시글 데이터를 불러오는데 실패했습니다.');
        }
      } else {
        console.log('PostFormView - No postId, creating new post');
      }
    },
    getCategoryName(category) {
      const foundCategory = this.categories.find(cat => cat.value === category);
      return foundCategory ? foundCategory.displayName : category;
    },
    updateCustomCategory() {
      // 사용자가 직접 입력한 카테고리가 있으면 해당 값을 사용
      if (this.customCategory.trim()) {
        this.formData.category = this.customCategory.trim();
      } else {
        // 사용자가 입력을 취소하거나 비웠으면 기본 카테고리 선택 옵션으로 변경
        this.formData.category = '';
      }
    },
    getDisplayTitle() {
      if (this.activeLanguage === 'ko') {
        return this.formData.titleKo || '제목 없음';
      } else {
        return this.formData.titleJa || '제목 없음';
      }
    },
    getDisplaySummary() {
      if (this.activeLanguage === 'ko') {
        return this.formData.summaryKo || '';
      } else {
        return this.formData.summaryJa || '';
      }
    },
    getDisplayContent() {
      if (this.activeLanguage === 'ko') {
        return this.formData.contentKo || '';
      } else {
        return this.formData.contentJa || '';
      }
    },

    async submitForm() {
      // 현재 선택된 언어에 따라 필수 필드 검증
      if (this.activeLanguage === 'ko') {
        if (!this.formData.titleKo.trim()) {
          alert('한국어 제목을 입력해주세요.');
          return;
        }
        if (!this.formData.contentKo.trim()) {
          alert('한국어 내용을 입력해주세요.');
          return;
        }
      } else if (this.activeLanguage === 'ja') {
        if (!this.formData.titleJa.trim()) {
          alert('일본어 제목을 입력해주세요.');
          return;
        }
        if (!this.formData.contentJa.trim()) {
          alert('일본어 내용을 입력해주세요.');
          return;
        }
      }
      
      if (!this.formData.category) {
        alert('카테고리를 선택해주세요.');
        return;
      }

      this.isSubmitting = true;
      
      try {
        let response;
        const currentLang = this.activeLanguage; // 현재 선택된 언어 사용
        
        if (this.isEdit) {
          response = await apiService.updatePost(this.$route.params.id, this.formData, currentLang);
        } else {
          response = await apiService.createPost(this.formData, currentLang);
        }
        
        if (response.success) {
          alert(this.formData.published ? '게시글이 발행되었습니다.' : '임시저장되었습니다.');
          this.$router.push('/admin/dashboard');
        } else {
          alert('저장 중 오류가 발생했습니다: ' + response.message);
        }
        
      } catch (error) {
        console.error('게시글 저장 실패:', error);
        alert('저장 중 오류가 발생했습니다.');
      } finally {
        this.isSubmitting = false;
      }
    },
         goToDashboard() {
       // 에디터 정리 후 대시보드로 이동
       this.cleanupEditors();
       this.$router.push('/admin/dashboard');
     },
         logout() {
       // 에디터 정리 후 로그아웃
       this.cleanupEditors();
       localStorage.removeItem('jwtToken');
       localStorage.removeItem('userRole');
       localStorage.removeItem('username');
       localStorage.removeItem('adminToken');
       this.$router.push('/admin/login');
     },
     cleanupEditors() {
       // TinyMCE 에디터 인스턴스 정리
       try {
         const editors = document.querySelectorAll('.tox-tinymce');
         editors.forEach(editor => {
           if (editor && editor._tinymce) {
             editor._tinymce.destroy();
           }
         });
       } catch (error) {
         console.log('에디터 정리 중 오류:', error);
       }
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
  padding: 20px 32px;
  border-bottom: 1px solid #e9ecef;
  background: #ffffff;
}

.title-input {
  width: 100%;
  font-size: 1.5rem;
  font-weight: 600;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  outline: none;
  color: #2c3e50;
  background: #ffffff;
  padding: 10px 14px;
  transition: all 0.3s ease;
}

.title-input:focus {
  border-color: #007bff;
  box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.1);
}

.title-input::placeholder {
  color: #adb5bd;
  font-weight: 400;
}

.language-tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
}

.lang-tab {
  padding: 8px 16px;
  border: 1px solid #dee2e6;
  background: #ffffff;
  color: #6c757d;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.3s ease;
}

.lang-tab:hover {
  background: #f8f9fa;
  border-color: #adb5bd;
}

.lang-tab.active {
  background: #007bff;
  color: #ffffff;
  border-color: #007bff;
}

.meta-section {
  padding: 12px 32px;
  border-bottom: 1px solid #e9ecef;
  background: #f8f9fa;
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
  font-size: 0.85rem;
  font-weight: 600;
  color: #495057;
  min-width: 60px;
}

.category-input-group {
  display: flex;
  align-items: center;
  gap: 8px;
}

.meta-select {
  padding: 6px 12px;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  font-size: 0.85rem;
  background: white;
  transition: all 0.3s ease;
  cursor: pointer;
}

.meta-select:focus {
  outline: none;
  border-color: #495057;
  box-shadow: 0 0 0 3px rgba(73, 80, 87, 0.1);
}

.custom-category-input {
  flex: 1;
  padding: 6px 12px;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  font-size: 0.85rem;
  background: white;
  transition: all 0.3s ease;
}

.custom-category-input:focus {
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
  font-size: 0.85rem;
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
  padding: 12px 32px;
  border-bottom: 1px solid #e9ecef;
  background: #ffffff;
}

.summary-input {
  width: 100%;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  outline: none;
  resize: none;
  font-size: 0.9rem;
  line-height: 1.4;
  color: #495057;
  background: #ffffff;
  padding: 8px 12px;
  transition: all 0.3s ease;
  min-height: 60px;
}

.summary-input:focus {
  border-color: #007bff;
  box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.1);
}

.summary-input::placeholder {
  color: #adb5bd;
}

.content-editor {
  flex: 1;
  padding: 16px 32px;
  background: #ffffff;
}

.content-input {
  width: 100%;
  min-height: 400px;
  border: none;
  outline: none;
  background: transparent;
  transition: all 0.3s ease;
}

/* TinyMCE 에디터 스타일 커스터마이징 */
.content-input {
  border: 1px solid #e9ecef !important;
  border-radius: 8px !important;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.content-input .tox-tinymce {
  border: none !important;
  border-radius: 8px !important;
}

.content-input .tox-toolbar {
  background: #ffffff !important;
  border-bottom: 1px solid #e9ecef !important;
  padding: 8px 12px !important;
}

.content-input .tox-toolbar__group {
  border-right: 1px solid #e9ecef !important;
  padding-right: 8px !important;
  margin-right: 8px !important;
}

.content-input .tox-toolbar__group:last-child {
  border-right: none !important;
  margin-right: 0 !important;
}

.content-input .tox-tbtn {
  border-radius: 4px !important;
  transition: all 0.2s ease !important;
}

.content-input .tox-tbtn:hover {
  background: #f8f9fa !important;
  transform: translateY(-1px) !important;
}

.content-input .tox-tbtn--enabled {
  background: #e9ecef !important;
  color: #495057 !important;
}

.content-input .tox-edit-area {
  background: #ffffff !important;
  border-top: 1px solid #e9ecef !important;
}

.content-input .tox-edit-area__iframe {
  background: #ffffff !important;
  min-height: 350px !important;
}

.content-input .tox-edit-focus {
  border-color: #007bff !important;
  box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.1) !important;
}

/* 에디터 내부 콘텐츠 스타일 */
.content-input .mce-content-body {
  padding: 16px 20px !important;
  font-family: 'Noto Sans KR', 'Noto Sans JP', 'Malgun Gothic', 'Hiragino Kaku Gothic ProN', sans-serif !important;
  font-size: 14px !important;
  line-height: 1.6 !important;
  color: #2c3e50 !important;
}

.content-input .mce-content-body:focus {
  outline: none !important;
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

.preview-body code {
  background: #f8f9fa;
  padding: 2px 6px;
  border-radius: 4px;
  font-family: 'Courier New', monospace;
  font-size: 0.9rem;
  color: #e83e8c;
}

.preview-body pre {
  background: #f8f9fa;
  padding: 16px;
  border-radius: 8px;
  overflow-x: auto;
  margin: 16px 0;
  border-left: 4px solid #007bff;
}

.preview-body blockquote {
  border-left: 4px solid #007bff;
  padding-left: 16px;
  margin: 16px 0;
  color: #6c757d;
  font-style: italic;
}

.preview-body a {
  color: #007bff;
  text-decoration: none;
}

.preview-body a:hover {
  text-decoration: underline;
}

.preview-body hr {
  border: none;
  height: 1px;
  background: #e9ecef;
  margin: 24px 0;
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
  padding: 8px 12px;
  border-radius: 4px;
  background: #f8f9fa;
  margin-bottom: 6px;
  border-left: 2px solid #007bff;
}

.help-item:last-child {
  margin-bottom: 0;
}

.help-item strong {
  display: inline;
  font-weight: 600;
  color: #2c3e50;
  margin-right: 8px;
}

.help-item p {
  display: inline;
  margin: 0;
  color: #6c757d;
  font-size: 0.8rem;
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

:global(body.dark-mode) .title-input {
  background: #2d3748;
  color: #e2e8f0;
  border-color: #4a5568;
}

:global(body.dark-mode) .title-input:focus {
  border-color: #60a5fa;
  box-shadow: 0 0 0 3px rgba(96, 165, 250, 0.2);
}

:global(body.dark-mode) .summary-input {
  background: #2d3748;
  color: #e2e8f0;
  border-color: #4a5568;
}

:global(body.dark-mode) .summary-input:focus {
  border-color: #60a5fa;
  box-shadow: 0 0 0 3px rgba(96, 165, 250, 0.2);
}

:global(body.dark-mode) .content-input {
  background: #1a202c;
  color: #e2e8f0;
  border-color: #4a5568;
}

:global(body.dark-mode) .content-input {
  border-color: #4a5568 !important;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2) !important;
}

:global(body.dark-mode) .content-input .tox-toolbar {
  background: #2d3748 !important;
  border-bottom-color: #4a5568 !important;
}

:global(body.dark-mode) .content-input .tox-toolbar__group {
  border-right-color: #4a5568 !important;
}

:global(body.dark-mode) .content-input .tox-tbtn:hover {
  background: #4a5568 !important;
}

:global(body.dark-mode) .content-input .tox-tbtn--enabled {
  background: #4a5568 !important;
  color: #e2e8f0 !important;
}

:global(body.dark-mode) .content-input .tox-edit-area {
  background: #1a202c !important;
  border-top-color: #4a5568 !important;
}

:global(body.dark-mode) .content-input .tox-edit-area__iframe {
  background: #1a202c !important;
}

:global(body.dark-mode) .content-input .tox-edit-focus {
  border-color: #60a5fa !important;
  box-shadow: 0 0 0 3px rgba(96, 165, 250, 0.2) !important;
}

:global(body.dark-mode) .content-input .mce-content-body {
  color: #e2e8f0 !important;
}

:global(body.dark-mode) .content-input .mce-content-body h1,
:global(body.dark-mode) .content-input .mce-content-body h2,
:global(body.dark-mode) .content-input .mce-content-body h3,
:global(body.dark-mode) .content-input .mce-content-body h4,
:global(body.dark-mode) .content-input .mce-content-body h5,
:global(body.dark-mode) .content-input .mce-content-body h6 {
  color: #f7fafc !important;
}

:global(body.dark-mode) .content-input .mce-content-body code {
  background: #1a202c !important;
  color: #fbb6ce !important;
}

:global(body.dark-mode) .content-input .mce-content-body pre {
  background: #1a202c !important;
  border-left-color: #60a5fa !important;
}

:global(body.dark-mode) .content-input .mce-content-body blockquote {
  border-left-color: #60a5fa !important;
  color: #a0aec0 !important;
}

:global(body.dark-mode) .content-input .mce-content-body a {
  color: #60a5fa !important;
}

:global(body.dark-mode) .content-input .mce-content-body a:hover {
  color: #93c5fd !important;
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

:global(body.dark-mode) .custom-category-input {
  background: #1a202c;
  border-color: #4a5568;
  color: #e2e8f0;
}

:global(body.dark-mode) .custom-category-input:focus {
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
  background: #2d3748;
  border-left-color: #60a5fa;
}

:global(body.dark-mode) .help-item strong {
  color: #f7fafc;
}

:global(body.dark-mode) .help-item p {
  color: #a0aec0;
}

:global(body.dark-mode) .preview-body code {
  background: #1a202c;
  color: #fbb6ce;
}

:global(body.dark-mode) .preview-body pre {
  background: #1a202c;
  border-left-color: #a0aec0;
}

:global(body.dark-mode) .preview-body blockquote {
  border-left-color: #a0aec0;
  color: #a0aec0;
}

:global(body.dark-mode) .preview-body a {
  color: #60a5fa;
}

:global(body.dark-mode) .preview-body a:hover {
  color: #93c5fd;
}

:global(body.dark-mode) .preview-body hr {
  background: #4a5568;
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
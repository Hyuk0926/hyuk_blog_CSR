<template>
  <div class="min-h-screen bg-[#f8f9fa] flex flex-col">
    <div class="bg-white border-b border-[#e9ecef] p-5 px-8 flex justify-between items-center sticky top-0 z-100 shadow-[0_2px_8px_rgba(0,0,0,0.08)]">
      <div class="flex items-center gap-6">
        <h1 class="text-[1.75rem] font-bold text-[#2c3e50] m-0">{{ isEdit ? '게시글 수정' : '새 게시글 작성' }}</h1>
        <div class="flex items-center gap-2">
          <span class="text-[#6c757d] text-[0.9rem]">
            <span class="font-semibold text-[#007bff]">관리자</span>님
          </span>
        </div>
      </div>
      <div class="flex gap-3">
        <button @click="goToDashboard" class="px-6 py-3 bg-[#6c757d] text-white border-none rounded-xl text-[0.95rem] font-semibold cursor-pointer transition-all duration-300 ease-in-out no-underline inline-flex items-center justify-center relative overflow-hidden hover:bg-[#545b62]">대시보드</button>
        <button @click="logout" class="px-6 py-3 bg-[#dc3545] text-white border-none rounded-xl text-[0.95rem] font-semibold cursor-pointer transition-all duration-300 ease-in-out no-underline inline-flex items-center justify-center relative overflow-hidden hover:bg-[#c82333]">로그아웃</button>
      </div>
    </div>

    <div class="flex-1 flex gap-8 p-8 max-w-[1400px] mx-auto w-full">
      <div class="flex-1 bg-white rounded-lg shadow-[0_2px_8px_rgba(0,0,0,0.08)] overflow-hidden border border-[#e9ecef]">
        <div class="p-5 px-8 border-b border-[#e9ecef] bg-white">
          <div class="flex gap-2 mb-4">
            <button 
              @click="activeLanguage = 'ko'" 
              :class="['px-4 py-2 border border-[#dee2e6] bg-white text-[#6c757d] rounded cursor-pointer text-[0.9rem] transition-all duration-300 ease-in-out', { 'bg-[#ced9ea] text-[#2c3e50] border-[#ced9ea] shadow-[0_2px_4px_rgba(206,217,234,0.3)]': activeLanguage === 'ko' }]"
            >
              한국어
            </button>
            <button 
              @click="activeLanguage = 'ja'" 
              :class="['px-4 py-2 border border-[#dee2e6] bg-white text-[#6c757d] rounded cursor-pointer text-[0.9rem] transition-all duration-300 ease-in-out', { 'bg-[#ced9ea] text-[#2c3e50] border-[#ced9ea] shadow-[0_2px_4px_rgba(206,217,234,0.3)]': activeLanguage === 'ja' }]"
            >
              日本語
            </button>
          </div>
          
          <input 
            v-if="activeLanguage === 'ko'"
            v-model="formData.titleKo" 
            type="text" 
            class="w-full text-[1.5rem] font-semibold border border-[#e9ecef] rounded-lg outline-none text-[#2c3e50] bg-white p-2.5 px-3.5 transition-all duration-300 ease-in-out focus:border-[#007bff] focus:shadow-[0_0_0_3px_rgba(0,123,255,0.1)] placeholder:text-[#adb5bd] placeholder:font-normal" 
            placeholder="한국어 제목을 입력하세요..."
            required
          >
          <input 
            v-if="activeLanguage === 'ja'"
            v-model="formData.titleJa" 
            type="text" 
            class="w-full text-[1.5rem] font-semibold border border-[#e9ecef] rounded-lg outline-none text-[#2c3e50] bg-white p-2.5 px-3.5 transition-all duration-300 ease-in-out focus:border-[#007bff] focus:shadow-[0_0_0_3px_rgba(0,123,255,0.1)] placeholder:text-[#adb5bd] placeholder:font-normal" 
            placeholder="日本語のタイトルを入力してください..."
            required
          >
        </div>

        <div class="p-3 px-8 border-b border-[#e9ecef] bg-[#f8f9fa]">
          <div class="flex gap-6 items-center">
            <div class="flex items-center gap-2">
              <label class="text-[0.85rem] font-semibold text-[#495057] min-w-[60px]">카테고리</label>
              <div class="flex items-center gap-2">
                <select v-model="formData.category" class="p-1.5 px-3 border-2 border-[#e9ecef] rounded-lg text-[0.85rem] bg-white transition-all duration-300 ease-in-out cursor-pointer focus:outline-none focus:border-[#495057] focus:shadow-[0_0_0_3px_rgba(73,80,87,0.1)]" required>
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
                  class="flex-1 p-1.5 px-3 border-2 border-[#e9ecef] rounded-lg text-[0.85rem] bg-white transition-all duration-300 ease-in-out focus:outline-none focus:border-[#495057] focus:shadow-[0_0_0_3px_rgba(73,80,87,0.1)]" 
                  placeholder="새 카테고리명 입력..."
                  @input="updateCustomCategory"
                >
              </div>
            </div>
            <div class="flex items-center gap-2">
              <label class="text-[0.85rem] font-semibold text-[#495057] min-w-[60px]">상태</label>
              <div class="flex items-center gap-2">
                <input 
                  v-model="formData.published" 
                  type="checkbox" 
                  id="published" 
                  class="w-5 h-5 accent-[#495057] cursor-pointer"
                >
                <label for="published" class="text-[0.85rem] font-semibold text-[#495057] cursor-pointer transition-colors duration-300 ease-in-out hover:text-[#2c3e50]">
                  {{ formData.published ? '공개' : '임시저장' }}
                </label>
              </div>
            </div>
          </div>
        </div>

                 <div class="flex-1 flex flex-col">
           <div v-if="activeTab === 'write'" class="flex-1 flex flex-col">
            <div class="p-3 px-8 border-b border-[#e9ecef] bg-white">
              <textarea 
                v-if="activeLanguage === 'ko'"
                v-model="formData.summaryKo" 
                class="w-full border border-[#e9ecef] rounded-lg outline-none resize-none text-[0.9rem] leading-[1.4] text-[#495057] bg-white p-2 px-3 transition-all duration-300 ease-in-out min-h-[60px] focus:border-[#007bff] focus:shadow-[0_0_0_3px_rgba(0,123,255,0.1)] placeholder:text-[#adb5bd]" 
                placeholder="한국어 요약을 입력하세요... (선택사항)"
                rows="3"
              ></textarea>
              <textarea 
                v-if="activeLanguage === 'ja'"
                v-model="formData.summaryJa" 
                class="w-full border border-[#e9ecef] rounded-lg outline-none resize-none text-[0.9rem] leading-[1.4] text-[#495057] bg-white p-2 px-3 transition-all duration-300 ease-in-out min-h-[60px] focus:border-[#007bff] focus:shadow-[0_0_0_3px_rgba(0,123,255,0.1)] placeholder:text-[#adb5bd]" 
                placeholder="日本語の要約を入力してください... (選択項目)"
                rows="3"
              ></textarea>
            </div>
            
                         <div class="flex-1 p-4 px-8 bg-white">
               <Editor
                 :key="`editor-${activeLanguage}`"
                 v-model="currentContent"
                 :init="editorConfig"
                 :placeholder="activeLanguage === 'ko' ? '한국어 내용을 입력하세요...' : '日本語の内容を入力してください...'"
                 class="w-full min-h-[400px] border-none outline-none bg-transparent transition-all duration-300 ease-in-out border border-[#e9ecef] rounded-lg overflow-hidden shadow-[0_2px_4px_rgba(0,0,0,0.05)]"
                 @init="onEditorInit"
                 @blur="onEditorBlur"
                 @input="onEditorInput"
               />
             </div>
          </div>

          
        </div>
      </div>

      <div class="w-80 flex flex-col gap-6">
        <div class="bg-white rounded-lg shadow-[0_2px_8px_rgba(0,0,0,0.08)] p-6 border border-[#e9ecef]">
          <h3 class="text-[1.1rem] font-semibold text-[#2c3e50] m-0 mb-4">게시 설정</h3>
          <div class="mb-4">
            <label class="block text-[0.9rem] font-semibold text-[#495057] mb-1.5">대표 이미지</label>
            <input 
              v-model="formData.imageUrl" 
              type="url" 
              class="w-full p-3 px-4 border-2 border-[#e9ecef] rounded-lg text-[0.9rem] bg-white transition-all duration-300 ease-in-out box-border focus:outline-none focus:border-[#495057] focus:shadow-[0_0_0_3px_rgba(73,80,87,0.1)] focus:-translate-y-[1px]" 
              placeholder="이미지 URL 입력..."
            >
          </div>
          <div class="mb-4">
            <label class="block text-[0.9rem] font-semibold text-[#495057] mb-1.5">태그</label>
            <input 
              v-model="formData.tags" 
              type="text" 
              class="w-full p-3 px-4 border-2 border-[#e9ecef] rounded-lg text-[0.9rem] bg-white transition-all duration-300 ease-in-out box-border focus:outline-none focus:border-[#495057] focus:shadow-[0_0_0_3px_rgba(73,80,87,0.1)] focus:-translate-y-[1px]" 
              placeholder="태그1, 태그2, 태그3..."
            >
            <div class="text-[0.8rem] text-[#6c757d] mt-1">쉼표로 구분하여 입력하세요</div>
          </div>
        </div>

        <div class="bg-white rounded-lg shadow-[0_2px_8px_rgba(0,0,0,0.08)] p-6 border border-[#e9ecef]">
          <h3 class="text-[1.1rem] font-semibold text-[#2c3e50] m-0 mb-4">빠른 가이드</h3>
          <div class="flex flex-col gap-2">
            <div class="text-[0.85rem] text-[#495057] p-2 px-3 rounded bg-[#f8f9fa] mb-1.5 border-l-2 border-[#007bff]">
              <strong class="inline font-semibold text-[#2c3e50] mr-2">서식</strong>
              <p class="inline m-0 text-[#6c757d] text-[0.8rem]">굵게, 기울임, 색상</p>
            </div>
            <div class="text-[0.85rem] text-[#495057] p-2 px-3 rounded bg-[#f8f9fa] mb-1.5 border-l-2 border-[#007bff]">
              <strong class="inline font-semibold text-[#2c3e50] mr-2">구조</strong>
              <p class="inline m-0 text-[#6c757d] text-[0.8rem]">제목, 목록, 정렬</p>
            </div>
            <div class="text-[0.85rem] text-[#495057] p-2 px-3 rounded bg-[#f8f9fa] mb-1.5 border-l-2 border-[#007bff]">
              <strong class="inline font-semibold text-[#2c3e50] mr-2">미디어</strong>
              <p class="inline m-0 text-[#6c757d] text-[0.8rem]">링크, 이미지, 코드</p>
            </div>
            <div class="text-[0.85rem] text-[#495057] p-2 px-3 rounded bg-[#f8f9fa] mb-1.5 border-l-2 border-[#007bff]">
              <strong class="inline font-semibold text-[#2c3e50] mr-2">미리보기</strong>
              <p class="inline m-0 text-[#6c757d] text-[0.8rem]">실제 결과 확인</p>
            </div>
          </div>
        </div>
      </div>
    </div>

         <div class="bg-white border-t border-[#e9ecef] p-5 px-8 flex justify-end items-center sticky bottom-0 z-[9999] shadow-[0_-2px_8px_rgba(0,0,0,0.08)]">
      <div class="flex gap-3">
        <button @click="goToDashboard" class="px-6 py-3 bg-[#f8f9fa] text-[#6c757d] border-2 border-[#e9ecef] rounded-xl text-[0.95rem] font-semibold cursor-pointer transition-all duration-300 ease-in-out no-underline inline-flex items-center justify-center relative overflow-hidden hover:bg-[#e9ecef] hover:-translate-y-[1px] hover:shadow-[0_2px_8px_rgba(0,0,0,0.08)]">취소</button>
        <button @click="submitForm" class="px-6 py-3 bg-[#495057] text-white border-none rounded-xl text-[0.95rem] font-semibold cursor-pointer transition-all duration-300 ease-in-out no-underline inline-flex items-center justify-center relative overflow-hidden shadow-[0_2px_8px_rgba(73,80,87,0.2)] hover:-translate-y-[1px] hover:shadow-[0_4px_12px_rgba(73,80,87,0.3)] active:translate-y-0 disabled:bg-[#6c757d] disabled:cursor-not-allowed disabled:transform-none disabled:shadow-none" :disabled="isSubmitting">
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
  computed: {
    currentContent: {
      get() {
        return this.activeLanguage === 'ko' ? this.formData.contentKo : this.formData.contentJa;
      },
      set(value) {
        if (this.activeLanguage === 'ko') {
          this.formData.contentKo = value;
        } else {
          this.formData.contentJa = value;
        }
      }
    }
  },
     watch: {
     activeLanguage() {
       // 언어 변경 시 설정만 업데이트 (에디터는 키 변경으로 자동 재생성)
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
     },
     onEditorInit(editor) {
       // 에디터 초기화 완료 시 호출
       console.log('Editor initialized:', editor);
     },
     onEditorBlur(editor) {
       // 에디터에서 포커스가 벗어날 때 호출
       console.log('Editor blur:', editor);
     },
     onEditorInput(value) {
       // 에디터 내용 변경 시 호출
       if (this.activeLanguage === 'ko') {
         this.formData.contentKo = value;
       } else {
         this.formData.contentJa = value;
       }
     }
  }
}
</script>

<style scoped>
/* TinyMCE 에디터 스타일 커스터마이징 */
.content-input {
  border: 1px solid #e9ecef !important;
  border-radius: 8px !important;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  z-index: 1 !important;
}

.content-input .tox-tinymce {
  border: none !important;
  border-radius: 8px !important;
  z-index: 1 !important;
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
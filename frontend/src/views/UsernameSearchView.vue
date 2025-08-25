<template>
  <div class="min-h-screen flex items-center justify-center p-5 font-['Noto_Sans_KR',sans-serif]">
    <!-- 왼쪽 프로필 영역 -->
    <div class="bg-white/90 backdrop-blur-[10px] rounded-2xl shadow-[0_8px_32px_rgba(0,0,0,0.1)] p-10 w-full max-w-[400px] border border-gray-200 mr-8 flex flex-col items-center text-center">
      <!-- 운영자 안내 문구 -->
      <p class="text-sm text-gray-500 mb-4 font-medium">Blog Owner</p>
      <!-- 프로필 사진 -->
      <img src="/img/profile_plana.jpg" alt="Profile" 
           class="w-32 h-32 rounded-full shadow-md mb-4 object-cover">

      <!-- 이름 -->
      <h2 class="text-2xl font-bold text-gray-800 mb-1">Shka</h2>
      
      <!-- 간단 소개 -->
      <p class="text-gray-600 mb-4">Web Developer</p>
    </div>

    <!-- 아이디 검색 폼 -->
    <div class="bg-white/95 backdrop-blur-[10px] rounded-2xl shadow-[0_8px_32px_rgba(0,0,0,0.1)] p-10 w-full max-w-[400px] border border-gray-200">
      <div class="text-center mb-8">
        <h1 class="text-[1.75rem] text-gray-800 mb-2 font-bold font-['Montserrat',sans-serif]">아이디 찾기</h1>
        <p class="text-gray-600 text-[0.95rem] font-medium">가입 시 등록한 이메일 주소를 입력하세요</p>
      </div>
      
      <div v-if="error" class="bg-red-50 text-red-600 p-4 rounded-xl mb-5 text-center font-medium border border-red-200">
        {{ error }}
      </div>
      
      <div v-if="message" class="bg-green-50 text-green-600 p-4 rounded-xl mb-5 text-center font-medium border border-green-200">
        {{ message }}
      </div>
      
      <form @submit.prevent="handleUsernameSearch" v-if="!searchCompleted">
        <div class="mb-5">
          <label for="email" class="block mb-2 font-semibold text-gray-700 text-[0.9rem]">이메일 주소</label>
          <input 
            type="email" 
            id="email" 
            v-model="formData.email" 
            class="w-full p-[14px] px-4 border-2 border-gray-300 rounded-xl text-[0.95rem] transition-all duration-300 ease-in-out box-border bg-white font-inherit text-gray-800 focus:outline-none focus:border-blue-500 focus:shadow-[0_0_0_3px_rgba(59,130,246,0.1)] focus:-translate-y-[1px] focus:bg-white" 
            required
            placeholder="example@email.com"
          >
        </div>
        
        <button type="submit" class="w-full p-[14px] bg-blue-500 text-white border-none rounded-xl text-[0.95rem] font-semibold cursor-pointer transition-all duration-300 ease-[cubic-bezier(0.4,0,0.2,1)] font-['Montserrat',sans-serif] shadow-[0_4px_12px_rgba(59,130,246,0.3)] hover:-translate-y-[2px] hover:shadow-[0_6px_20px_rgba(59,130,246,0.4)] hover:bg-blue-600 active:translate-y-0 disabled:opacity-60 disabled:cursor-not-allowed disabled:transform-none" :disabled="loading">
          {{ loading ? '검색 중...' : '아이디 찾기' }}
        </button>
      </form>

             <!-- 검색 결과 -->
       <div v-if="searchCompleted" class="text-center">
         <div class="mb-6">
           <svg class="w-16 h-16 text-green-500 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
             <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path>
           </svg>
           <h3 class="text-lg font-semibold text-gray-800 mb-2">아이디 찾기 완료</h3>
                       <div class="bg-gray-50 border border-gray-200 rounded-lg p-4 mb-4">
              <p class="text-sm text-gray-600 mb-2">입력하신 이메일 주소</p>
              <p class="text-gray-800 font-medium">{{ formData.email }}</p>
              <div class="border-t border-gray-200 mt-3 pt-3">
                <p class="text-sm text-gray-600 mb-2">등록된 아이디</p>
                <p class="text-blue-600 font-bold text-lg">{{ foundUsername || '아이디를 찾을 수 없습니다' }}</p>
              </div>
            </div>
         </div>
         
         <button 
           @click="resetForm"
           class="w-full p-[14px] bg-blue-500 text-white border-none rounded-xl text-[0.95rem] font-semibold cursor-pointer transition-all duration-300 ease-[cubic-bezier(0.4,0,0.2,1)] font-['Montserrat',sans-serif] shadow-[0_4px_12px_rgba(59,130,246,0.3)] hover:-translate-y-[2px] hover:shadow-[0_6px_20px_rgba(59,130,246,0.4)] hover:bg-blue-600 active:translate-y-0"
         >
           다시 찾기
         </button>
       </div>
      
      <div class="text-center mt-6 pt-5 border-t border-gray-200">
        <p class="text-gray-600">
          <router-link to="/user/login" class="text-blue-600 no-underline font-semibold transition-colors duration-300 ease-in-out hover:text-blue-800 hover:underline">로그인으로 돌아가기</router-link>
        </p>
      </div>
      
      <div class="text-center mt-4">
        <router-link to="/" class="text-gray-500 no-underline text-[0.9rem] transition-colors duration-300 ease-in-out hover:text-gray-700 hover:underline">홈으로 돌아가기</router-link>
      </div>
    </div>
  </div>
</template>

<script>
import api from '@/services/api'

export default {
  name: 'UsernameSearchView',
  data() {
    return {
      formData: {
        email: ''
      },
      loading: false,
      error: '',
      message: '',
      searchCompleted: false,
      foundUsername: ''
    }
  },
  mounted() {
    this.setBackgroundImage();
    // 다크모드 변경 감지
    this.observer = new MutationObserver(() => {
      this.setBackgroundImage();
    });
    this.observer.observe(document.body, {
      attributes: true,
      attributeFilter: ['class']
    });
  },
  beforeUnmount() {
    this.clearBackgroundImage();
    if (this.observer) {
      this.observer.disconnect();
    }
  },
  beforeRouteLeave(to, from, next) {
    this.clearBackgroundImage();
    if (this.observer) {
      this.observer.disconnect();
    }
    next();
  },
  methods: {
    async handleUsernameSearch() {
      this.loading = true;
      this.error = '';
      this.message = '';
      
      try {
        const response = await api.searchUsername(this.formData.email);
        console.log('Username search response:', response);
        
        if (response && response.success) {
          this.foundUsername = response.username;
          console.log('Found username:', this.foundUsername);
          console.log('Response keys:', Object.keys(response));
          console.log('Full response:', JSON.stringify(response, null, 2));
          this.searchCompleted = true;
        } else {
          this.error = response?.message || '아이디 검색 중 오류가 발생했습니다.';
        }
      } catch (error) {
        console.error('Username search error:', error);
        this.error = '아이디 검색 중 오류가 발생했습니다.';
      } finally {
        this.loading = false;
      }
    },
    
    resetForm() {
      this.formData.email = '';
      this.error = '';
      this.message = '';
      this.searchCompleted = false;
      this.foundUsername = '';
    },
    
    setBackgroundImage() {
      const isDarkMode = document.body.classList.contains('dark-mode');
      const lightBg = '/img/light_mode_bg.jpg';
      const darkBg = '/img/dark_mode_bg.jpg';
      
      // !important를 사용하여 main.css의 스타일을 덮어쓰기
      document.body.style.setProperty('background-image', `url(${isDarkMode ? darkBg : lightBg})`, 'important');
      document.body.style.setProperty('background-size', 'cover', 'important');
      document.body.style.setProperty('background-position', 'center', 'important');
      document.body.style.setProperty('background-repeat', 'no-repeat', 'important');
      document.body.style.setProperty('background-attachment', 'fixed', 'important');
      document.body.style.setProperty('margin', '0', 'important');
      document.body.style.setProperty('padding', '0', 'important');
      document.body.style.setProperty('min-height', '100vh', 'important');
      document.body.style.setProperty('transition', 'background-image 0.5s ease', 'important');
    },
    
    clearBackgroundImage() {
      // !important를 사용하여 스타일 제거
      document.body.style.removeProperty('background-image');
      document.body.style.removeProperty('background-size');
      document.body.style.removeProperty('background-position');
      document.body.style.removeProperty('background-repeat');
      document.body.style.removeProperty('background-attachment');
      document.body.style.removeProperty('margin');
      document.body.style.removeProperty('padding');
      document.body.style.removeProperty('min-height');
      document.body.style.removeProperty('transition');
    }
  }
}
</script>

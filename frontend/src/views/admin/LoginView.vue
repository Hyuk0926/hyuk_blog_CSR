<template>
  <div class="bg-[rgba(45,45,45,0.95)] backdrop-blur-[10px] rounded-2xl shadow-[0_8px_32px_rgba(0,0,0,0.3)] p-10 w-full max-w-[400px] border border-[rgba(255,255,255,0.2)] absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2">
    <div class="text-center mb-8">
      <h1 class="text-[1.75rem] text-white mb-2 font-bold font-['Montserrat',sans-serif]">관리자 로그인</h1>
    </div>
    
    <div v-if="errorMessage" class="bg-[rgba(197,48,48,0.2)] text-[#ff6b6b] p-4 rounded-xl mb-5 text-center font-medium border border-[rgba(197,48,48,0.3)]">
      {{ errorMessage }}
    </div>
    
    <form @submit.prevent="submitLogin">
      <div class="mb-5">
        <label for="username" class="block mb-2 font-semibold text-[#e0e0e0] text-[0.9rem]">아이디</label>
        <input 
          type="text" 
          id="username" 
          v-model="formData.username" 
          class="w-full p-[14px] px-4 border-2 border-[#555555] rounded-xl text-[0.95rem] transition-all duration-300 ease-in-out box-border bg-[rgba(60,60,60,0.8)] font-inherit text-white focus:outline-none focus:border-[#888888] focus:shadow-[0_0_0_3px_rgba(136,136,136,0.2)] focus:-translate-y-[1px] focus:bg-[rgba(70,70,70,0.9)]" 
          required
        >
      </div>
      
      <div class="mb-5">
        <label for="password" class="block mb-2 font-semibold text-[#e0e0e0] text-[0.9rem]">비밀번호</label>
        <div class="relative">
          <input 
            :type="showPassword ? 'text' : 'password'" 
            id="password" 
            v-model="formData.password" 
            class="w-full p-[14px] px-4 pr-10 border-2 border-[#555555] rounded-xl text-[0.95rem] transition-all duration-300 ease-in-out box-border bg-[rgba(60,60,60,0.8)] font-inherit text-white focus:outline-none focus:border-[#888888] focus:shadow-[0_0_0_3px_rgba(136,136,136,0.2)] focus:-translate-y-[1px] focus:bg-[rgba(70,70,70,0.9)]" 
            required 
            autocomplete="current-password" 
            style="ime-mode:disabled;"
            @keydown="checkCapsLock"
          >
          <span 
            @click="togglePassword" 
            title="비밀번호 보기" 
            class="absolute right-3 top-1/2 -translate-y-1/2 cursor-pointer text-[1.3rem] text-[#888] hover:text-white hover:scale-110 transition-all duration-200 ease-in-out"
          >
            {{ showPassword ? '👁️' : '𝄐' }}
          </span>
        </div>
        <div v-if="capsLockOn" class="text-[#dc3545] text-[0.95rem] mt-1.5">
          CapsLock이 켜져 있습니다!
        </div>
      </div>
      
      <button type="submit" class="w-full p-[14px] bg-gradient-to-br from-[#666666] to-[#444444] text-white border-none rounded-xl text-[0.95rem] font-semibold cursor-pointer transition-all duration-300 ease-[cubic-bezier(0.4,0,0.2,1)] font-['Montserrat',sans-serif] shadow-[0_4px_12px_rgba(0,0,0,0.3)] hover:-translate-y-[2px] hover:shadow-[0_6px_20px_rgba(0,0,0,0.4)] hover:bg-gradient-to-br hover:from-[#777777] hover:to-[#555555] active:translate-y-0 disabled:bg-gradient-to-br disabled:from-[#444444] disabled:to-[#333333] disabled:cursor-not-allowed disabled:transform-none" :disabled="isSubmitting">
        {{ isSubmitting ? '로그인 중...' : '로그인' }}
      </button>
    </form>
    
    <div class="text-center mt-6 pt-5 border-t border-[rgba(255,255,255,0.1)]">
      <router-link to="/" class="text-[#aaaaaa] no-underline text-[0.9rem] transition-colors duration-300 ease-in-out hover:text-[#cccccc] hover:underline">← 메인 페이지로 돌아가기</router-link>
    </div>
  </div>
</template>

<script>
import apiService from '@/services/api.js';

export default {
  name: 'LoginView',
  data() {
    return {
      formData: {
        username: '',
        password: ''
      },
      showPassword: false,
      capsLockOn: false,
      errorMessage: '',
      isSubmitting: false
    }
  },
  mounted() {
    // 페이지 진입 시 배경 스타일 설정
    document.body.style.background = 'linear-gradient(135deg, #1a1a1a 0%, #2d2d2d 25%, #404040 50%, #2d2d2d 75%, #1a1a1a 100%)';
    document.body.style.minHeight = '100vh';
    document.body.style.margin = '0';
    document.body.style.padding = '0';
    document.body.style.fontFamily = "'Noto Sans KR', sans-serif";
  },
  beforeUnmount() {
    // 페이지 나갈 때 배경 스타일 초기화
    document.body.style.background = '';
    document.body.style.minHeight = '';
    document.body.style.margin = '';
    document.body.style.padding = '';
    document.body.style.fontFamily = '';
  },
  methods: {
    togglePassword() {
      this.showPassword = !this.showPassword;
    },
    checkCapsLock(event) {
      this.capsLockOn = event.getModifierState('CapsLock');
    },
    async submitLogin() {
      this.isSubmitting = true;
      this.errorMessage = '';
      
      try {
        // 새로운 통합 로그인 API 호출
        const response = await apiService.login(this.formData);
        
        console.log('로그인 응답:', response); // 디버깅 로그 추가
        
        // 관리자 권한 확인
        if (response.role === 'ADMIN') {
          // JWT 토큰을 로컬 스토리지에 저장
          localStorage.setItem('jwtToken', response.token);
          localStorage.setItem('userRole', 'ROLE_ADMIN'); // Spring Security 형식으로 저장
          localStorage.setItem('username', response.username);
          localStorage.setItem('adminToken', response.token); // 기존 호환성 유지
          
          // 관리자 이름 저장 (admin_jp는 일본어, admin은 한국어)
          const adminName = response.username === 'admin_jp' ? 'admin_jp' : 'admin';
          localStorage.setItem('adminName', adminName);
          
          // 사용자 언어 정보 저장 (admin_jp는 일본어, admin은 한국어)
          const userLang = response.username === 'admin_jp' ? 'ja' : 'ko';
          localStorage.setItem('userLang', userLang);
          console.log('사용자 언어 설정:', userLang, 'for username:', response.username);
          
          // 관리자 대시보드로 리다이렉트
          console.log('대시보드로 리다이렉트 시도...'); // 디버깅 로그 추가
          this.$router.push('/admin/dashboard');
        } else {
          this.errorMessage = '관리자 권한이 필요합니다.';
        }
        
      } catch (error) {
        console.error('로그인 오류:', error);
        if (error.message && error.message.includes('Invalid username or password')) {
          this.errorMessage = '아이디 또는 비밀번호가 올바르지 않습니다.';
        } else {
          this.errorMessage = '로그인 처리 중 오류가 발생했습니다.';
        }
      } finally {
        this.isSubmitting = false;
      }
    }
  }
}
</script>

<style scoped>
/* 다크모드 지원 - Tailwind CSS로 대체되었으므로 최소한의 스타일만 유지 */
body.dark-mode .login-container {
  background: #2d3748;
  border-color: #4a5568;
  box-shadow: 0 4px 20px rgba(0,0,0,0.3);
}

body.dark-mode .login-title {
  color: #f7fafc;
}

body.dark-mode .form-label {
  color: #e2e8f0;
}

body.dark-mode .form-input {
  background: #1a202c;
  border-color: #4a5568;
  color: #e2e8f0;
}

body.dark-mode .form-input:focus {
  border-color: #ffe082;
  box-shadow: 0 0 0 3px rgba(255, 224, 130, 0.1);
}

body.dark-mode .back-link {
  border-top-color: #4a5568;
}

body.dark-mode .back-link a {
  color: #a0aec0;
}

body.dark-mode .back-link a:hover {
  color: #e2e8f0;
}

/* 반응형 디자인 */
@media (max-width: 480px) {
  .login-container {
    padding: 30px 20px;
    margin: 10px;
    width: calc(100% - 20px);
    max-width: none;
  }
  
  .login-title {
    font-size: 1.5rem;
  }
}
</style> 
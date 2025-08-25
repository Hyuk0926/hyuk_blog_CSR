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

    <!-- 로그인 폼 -->
    <div class="bg-white/95 backdrop-blur-[10px] rounded-2xl shadow-[0_8px_32px_rgba(0,0,0,0.1)] p-10 w-full max-w-[400px] border border-gray-200">
      <div class="text-center mb-8">
        <h1 class="text-[1.75rem] text-gray-800 mb-2 font-bold font-['Montserrat',sans-serif]">{{ $t('login.title') }}</h1>
        <p class="text-gray-600 text-[0.95rem] font-medium">{{ $t('login.subtitle') }}</p>
      </div>
      
      <div v-if="error" class="bg-red-50 text-red-600 p-4 rounded-xl mb-5 text-center font-medium border border-red-200">
        {{ error }}
      </div>
      
      <div v-if="message" class="bg-green-50 text-green-600 p-4 rounded-xl mb-5 text-center font-medium border border-green-200">
        {{ message }}
      </div>
      
      <form @submit.prevent="handleLogin">
        <input type="hidden" name="redirectUrl" :value="redirectUrl">
        
        <div class="mb-5">
          <label for="username" class="block mb-2 font-semibold text-gray-700 text-[0.9rem]">{{ $t('login.username') }}</label>
          <input 
            type="text" 
            id="username" 
            v-model="formData.username" 
            class="w-full p-[14px] px-4 border-2 border-gray-300 rounded-xl text-[0.95rem] transition-all duration-300 ease-in-out box-border bg-white font-inherit text-gray-800 focus:outline-none focus:border-blue-500 focus:shadow-[0_0_0_3px_rgba(59,130,246,0.1)] focus:-translate-y-[1px] focus:bg-white" 
            required
          >
        </div>
        
        <div class="mb-5">
          <label for="password" class="block mb-2 font-semibold text-gray-700 text-[0.9rem]">{{ $t('login.password') }}</label>
          <div class="relative">
            <input 
              :type="showPassword ? 'text' : 'password'" 
              id="password" 
              v-model="formData.password" 
              class="w-full p-[14px] px-4 pr-10 border-2 border-gray-300 rounded-xl text-[0.95rem] transition-all duration-300 ease-in-out box-border bg-white font-inherit text-gray-800 focus:outline-none focus:border-blue-500 focus:shadow-[0_0_0_3px_rgba(59,130,246,0.1)] focus:-translate-y-[1px] focus:bg-white" 
              required 
              autocomplete="current-password" 
              style="ime-mode:disabled;"
            >
            <span 
              @click="togglePassword" 
              :title="$t('login.passwordToggle')" 
              class="absolute right-3 top-1/2 -translate-y-1/2 cursor-pointer text-[1.3rem] text-gray-500 hover:text-gray-700 hover:scale-110 transition-all duration-200 ease-in-out"
            >
              {{ showPassword ? '👁️' : '𝄐' }}
            </span>
          </div>
          <div 
            id="capsLockMsg" 
            class="text-red-500 text-[0.95rem] mt-[6px] hidden" 
            :class="{ 'block': capsLockOn }"
          >
            {{ $t('login.capsLock') }}
          </div>
          
          <!-- 비밀번호 재설정 링크 -->
          <div class="text-right mt-2">
            <router-link 
              to="/password-reset" 
              class="text-gray-600 no-underline text-[0.85rem] transition-colors duration-300 ease-in-out hover:text-blue-600 hover:underline"
            >
              비밀번호를 잊으셨나요?
            </router-link>
          </div>
        </div>
        
        <button type="submit" class="w-full p-[14px] bg-blue-500 text-white border-none rounded-xl text-[0.95rem] font-semibold cursor-pointer transition-all duration-300 ease-[cubic-bezier(0.4,0,0.2,1)] font-['Montserrat',sans-serif] shadow-[0_4px_12px_rgba(59,130,246,0.3)] hover:-translate-y-[2px] hover:shadow-[0_6px_20px_rgba(59,130,246,0.4)] hover:bg-blue-600 active:translate-y-0 disabled:opacity-60 disabled:cursor-not-allowed disabled:transform-none" :disabled="loading">
          {{ loading ? $t('login.loading') : $t('login.loginButton') }}
        </button>
      </form>
      
      <div class="text-center mt-6 pt-5 border-t border-gray-200">
        <p class="text-gray-600">{{ $t('login.noAccount') }} <router-link to="/user/register" class="text-blue-600 no-underline font-semibold transition-colors duration-300 ease-in-out hover:text-blue-800 hover:underline">{{ $t('login.register') }}</router-link></p>
      </div>
      
             <div class="text-center mt-4">
         <router-link to="/" class="text-gray-500 no-underline text-[0.9rem] transition-colors duration-300 ease-in-out hover:text-gray-700 hover:underline">{{ $t('login.backToHome') }}</router-link>
       </div>
     </div>
   </div>
 </template>

<script>
import { inject } from 'vue'

export default {
  name: 'UserLoginView',
  setup() {
    const auth = inject('auth')
    return {
      login: auth.login
    }
  },
  data() {
    return {
      formData: {
        username: '',
        password: ''
      },
      showPassword: false,
      capsLockOn: false,
      loading: false,
      error: '',
      message: '',
      redirectUrl: ''
    }
  },
  mounted() {
    this.redirectUrl = this.$route.query.redirectUrl || '';
    this.message = this.$route.query.message || '';
    this.setupCapsLockDetection();
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
    async handleLogin() {
      this.loading = true;
      this.error = '';
      
      try {
        // 새로운 인증 시스템 사용
        await this.login(this.formData);
        
        this.message = '로그인 성공!';
        
        // 로그인 성공 후 리다이렉트
        setTimeout(() => {
          if (this.redirectUrl) {
            this.$router.push(this.redirectUrl);
          } else {
            this.$router.push('/');
          }
        }, 1000);
        
      } catch (error) {
        console.error('Login error:', error);
        if (error.message && error.message.includes('Invalid username or password')) {
          this.error = '아이디 또는 비밀번호가 올바르지 않습니다.';
        } else {
          this.error = '로그인 처리 중 오류가 발생했습니다.';
        }
      } finally {
        this.loading = false;
      }
    },
    
    togglePassword() {
      this.showPassword = !this.showPassword;
    },
    
    setupCapsLockDetection() {
      const passwordInput = document.getElementById('password');
      
      if (passwordInput) {
        passwordInput.addEventListener('keydown', (e) => {
          if (e.getModifierState('CapsLock')) {
            this.capsLockOn = true;
          } else {
            this.capsLockOn = false;
          }
        });
        
        passwordInput.addEventListener('keyup', (e) => {
          if (e.getModifierState('CapsLock')) {
            this.capsLockOn = true;
          } else {
            this.capsLockOn = false;
          }
        });
      }
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

<style scoped>
/* 다크모드 지원 - Tailwind CSS로 대체되었으므로 최소한의 스타일만 유지 */
.login-page.dark-mode {
  background: #1a202c;
}

.login-page.dark-mode .login-container {
  background: #2d3748;
  border-color: #4a5568;
  box-shadow: 0 4px 20px rgba(0,0,0,0.3);
}

.login-page.dark-mode .login-title {
  color: #f7fafc;
}

.login-page.dark-mode .login-subtitle {
  color: #a0aec0;
}

.login-page.dark-mode .form-label {
  color: #e2e8f0;
}

.login-page.dark-mode .form-input {
  background: #1a202c;
  border-color: #4a5568;
  color: #e2e8f0;
}

.login-page.dark-mode .form-input:focus {
  border-color: #ffe082;
  box-shadow: 0 0 0 3px rgba(255, 224, 130, 0.1);
}

.login-page.dark-mode .register-link {
  border-top-color: #4a5568;
}

.login-page.dark-mode .register-link a {
  color: #90cdf4;
}

.login-page.dark-mode .register-link a:hover {
  color: #63b3ed;
}

.login-page.dark-mode .back-link a {
  color: #a0aec0;
}

.login-page.dark-mode .back-link a:hover {
  color: #e2e8f0;
}

.login-page.dark-mode #togglePassword {
  color: #a0aec0;
}

.login-page.dark-mode #togglePassword:hover {
  color: #e2e8f0;
}
</style> 
<template>
  <div class="min-h-screen flex items-center justify-center p-5 font-['Noto_Sans_KR',sans-serif] bg-gradient-to-br from-[#1a1a1a] via-[#2d2d2d] via-[#404040] via-[#2d2d2d] to-[#1a1a1a]">
    <div class="bg-[rgba(45,45,45,0.95)] backdrop-blur-[10px] rounded-2xl shadow-[0_8px_32px_rgba(0,0,0,0.3)] p-10 w-full max-w-[400px] border border-[rgba(255,255,255,0.2)]">
      <div class="text-center mb-8">
        <h1 class="text-[1.75rem] text-white mb-2 font-bold font-['Montserrat',sans-serif]">{{ $t('login.title') }}</h1>
        <p class="text-[#b0b0b0] text-[0.95rem] font-medium">{{ $t('login.subtitle') }}</p>
      </div>
      
      <div v-if="error" class="bg-[rgba(197,48,48,0.2)] text-[#ff6b6b] p-4 rounded-xl mb-5 text-center font-medium border border-[rgba(197,48,48,0.3)]">
        {{ error }}
      </div>
      
      <div v-if="message" class="bg-[rgba(47,133,90,0.2)] text-[#68d391] p-4 rounded-xl mb-5 text-center font-medium border border-[rgba(47,133,90,0.3)]">
        {{ message }}
      </div>
      
      <form @submit.prevent="handleLogin">
        <input type="hidden" name="redirectUrl" :value="redirectUrl">
        
        <div class="mb-5">
          <label for="username" class="block mb-2 font-semibold text-[#e0e0e0] text-[0.9rem]">{{ $t('login.username') }}</label>
          <input 
            type="text" 
            id="username" 
            v-model="formData.username" 
            class="w-full p-[14px] px-4 border-2 border-[#555555] rounded-xl text-[0.95rem] transition-all duration-300 ease-in-out box-border bg-[rgba(60,60,60,0.8)] font-inherit text-white focus:outline-none focus:border-[#888888] focus:shadow-[0_0_0_3px_rgba(136,136,136,0.2)] focus:-translate-y-[1px] focus:bg-[rgba(70,70,70,0.9)]" 
            required
          >
        </div>
        
        <div class="mb-5">
          <label for="password" class="block mb-2 font-semibold text-[#e0e0e0] text-[0.9rem]">{{ $t('login.password') }}</label>
          <div class="relative">
            <input 
              :type="showPassword ? 'text' : 'password'" 
              id="password" 
              v-model="formData.password" 
              class="w-full p-[14px] px-4 pr-10 border-2 border-[#555555] rounded-xl text-[0.95rem] transition-all duration-300 ease-in-out box-border bg-[rgba(60,60,60,0.8)] font-inherit text-white focus:outline-none focus:border-[#888888] focus:shadow-[0_0_0_3px_rgba(136,136,136,0.2)] focus:-translate-y-[1px] focus:bg-[rgba(70,70,70,0.9)]" 
              required 
              autocomplete="current-password" 
              style="ime-mode:disabled;"
            >
            <span 
              @click="togglePassword" 
              :title="$t('login.passwordToggle')" 
              class="absolute right-3 top-1/2 -translate-y-1/2 cursor-pointer text-[1.3rem] text-[#888] hover:text-white hover:scale-110 transition-all duration-200 ease-in-out"
            >
              {{ showPassword ? '👁️' : '𝄐' }}
            </span>
          </div>
          <div 
            id="capsLockMsg" 
            class="text-[#dc3545] text-[0.95rem] mt-[6px] hidden" 
            :class="{ 'block': capsLockOn }"
          >
            {{ $t('login.capsLock') }}
          </div>
        </div>
        
        <button type="submit" class="w-full p-[14px] bg-gradient-to-br from-[#666666] to-[#444444] text-white border-none rounded-xl text-[0.95rem] font-semibold cursor-pointer transition-all duration-300 ease-[cubic-bezier(0.4,0,0.2,1)] font-['Montserrat',sans-serif] shadow-[0_4px_12px_rgba(0,0,0,0.3)] hover:-translate-y-[2px] hover:shadow-[0_6px_20px_rgba(0,0,0,0.4)] hover:bg-gradient-to-br hover:from-[#777777] hover:to-[#555555] active:translate-y-0 disabled:opacity-60 disabled:cursor-not-allowed disabled:transform-none" :disabled="loading">
          {{ loading ? $t('login.loading') : $t('login.loginButton') }}
        </button>
      </form>
      
      <div class="text-center mt-6 pt-5 border-t border-[rgba(255,255,255,0.1)]">
        <p class="text-[#cccccc]">{{ $t('login.noAccount') }} <router-link to="/user/register" class="text-[#cccccc] no-underline font-semibold transition-colors duration-300 ease-in-out hover:text-white hover:underline">{{ $t('login.register') }}</router-link></p>
      </div>
      
      <div class="text-center mt-4">
        <router-link to="/" class="text-[#aaaaaa] no-underline text-[0.9rem] transition-colors duration-300 ease-in-out hover:text-[#cccccc] hover:underline">{{ $t('login.backToHome') }}</router-link>
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
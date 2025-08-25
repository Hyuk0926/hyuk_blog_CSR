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

    <!-- 회원가입 폼 -->
    <div class="bg-white/95 backdrop-blur-[10px] rounded-2xl shadow-[0_8px_32px_rgba(0,0,0,0.1)] p-10 w-full max-w-[500px] border border-gray-200">
      <div class="text-center mb-8">
        <h1 class="text-[1.75rem] text-gray-800 mb-2 font-bold font-['Montserrat',sans-serif]">{{ $t('register.title') }}</h1>
        <p class="text-gray-600 text-[0.95rem] font-medium">{{ $t('register.subtitle') }}</p>
      </div>
      
      <div v-if="error" class="bg-red-50 text-red-600 p-4 rounded-xl mb-5 text-center font-medium border border-red-200">
        {{ error }}
      </div>
      
      <form @submit.prevent="handleRegister" id="registerForm">
        <div class="mb-5">
          <label for="username" class="block mb-2 font-semibold text-gray-700 text-[0.9rem]">{{ $t('register.username') }} <span class="text-red-500">{{ $t('register.required') }}</span></label>
          <div class="relative flex items-center">
                         <input 
               type="text" 
               id="username" 
               v-model="formData.username" 
               class="flex-1 p-[14px] px-4 border-2 border-gray-300 rounded-xl rounded-r-none text-[0.95rem] transition-all duration-300 ease-in-out box-border bg-white font-inherit text-gray-800 focus:outline-none focus:border-blue-500 focus:shadow-[0_0_0_3px_rgba(59,130,246,0.1)] focus:-translate-y-[1px] focus:bg-white" 
               required 
               pattern="[a-zA-Z0-9_]{4,20}" 
               :title="$t('register.validation.username.format')"
               :placeholder="$t('register.validation.username.format')"
               @blur="validateUsername"
             >
            <button 
              type="button" 
              class="p-[14px] px-4 bg-blue-500 text-white border-2 border-gray-300 border-l-0 rounded-xl rounded-l-none text-[0.9rem] font-semibold cursor-pointer transition-all duration-300 ease-in-out whitespace-nowrap min-w-[80px] hover:bg-blue-600 hover:-translate-y-[1px] disabled:bg-gray-400 disabled:cursor-not-allowed disabled:opacity-60" 
              id="username-check-btn" 
              @click="checkUsernameDuplicate"
              :disabled="!formData.username || usernameChecked"
            >
              {{ usernameChecked ? $t('register.checkComplete') : $t('register.duplicateCheck') }}
            </button>
          </div>
          <div id="username-validation" class="text-[0.875rem] mt-2 font-medium" :class="usernameValidation.type === 'error' ? 'text-red-500' : usernameValidation.type === 'success' ? 'text-green-600' : ''">
            {{ usernameValidation.message }}
          </div>
        </div>
        
        <div class="mb-5">
          <label for="password" class="block mb-2 font-semibold text-gray-700 text-[0.9rem]">{{ $t('register.password') }} <span class="text-red-500">{{ $t('register.required') }}</span></label>
          <div class="relative">
                         <input 
               :type="showPassword ? 'text' : 'password'" 
               id="password" 
               v-model="formData.password" 
               class="w-full p-[14px] px-4 pr-10 border-2 border-gray-300 rounded-xl text-[0.95rem] transition-all duration-300 ease-in-out box-border bg-white font-inherit text-gray-800 focus:outline-none focus:border-blue-500 focus:shadow-[0_0_0_3px_rgba(59,130,246,0.1)] focus:-translate-y-[1px] focus:bg-white" 
               required 
               minlength="6" 
               :title="$t('register.validation.password.minLength')"
               :placeholder="$t('register.validation.password.minLength')"
               @blur="validatePassword"
             >
            <span 
              @click="showPassword = !showPassword" 
              :title="$t('login.passwordToggle')" 
              class="absolute right-3 top-1/2 -translate-y-1/2 cursor-pointer text-[1.3rem] text-gray-500 hover:text-gray-700 hover:scale-110 transition-all duration-200 ease-in-out"
            >
              {{ showPassword ? '👁️' : '𝄐' }}
            </span>
                         <div 
               class="absolute -top-[25px] right-0 bg-yellow-100 text-yellow-800 p-1 px-2 rounded text-[0.8rem] font-medium hidden z-10" 
               id="password-capslock" 
               :class="{ 'block': capsLockOn }"
             >
               {{ $t('login.capsLock') }}
             </div>
          </div>
          <div id="password-validation" class="text-[0.875rem] mt-2 font-medium" :class="passwordValidation.type === 'error' ? 'text-red-500' : passwordValidation.type === 'success' ? 'text-green-600' : ''">
            {{ passwordValidation.message }}
          </div>
        </div>
        
        <div class="mb-5">
          <label for="confirmPassword" class="block mb-2 font-semibold text-gray-700 text-[0.9rem]">{{ $t('register.confirmPassword') }} <span class="text-red-500">{{ $t('register.required') }}</span></label>
          <div class="relative">
                         <input 
               :type="showConfirmPassword ? 'text' : 'password'" 
               id="confirmPassword" 
               v-model="formData.confirmPassword" 
               class="w-full p-[14px] px-4 pr-10 border-2 border-gray-300 rounded-xl text-[0.95rem] transition-all duration-300 ease-in-out box-border bg-white font-inherit text-gray-800 focus:outline-none focus:border-blue-500 focus:shadow-[0_0_0_3px_rgba(59,130,246,0.1)] focus:-translate-y-[1px] focus:bg-white" 
               required
               :placeholder="$t('register.validation.confirmPassword.required')"
               @blur="validateConfirmPassword"
             >
            <span 
              @click="showConfirmPassword = !showConfirmPassword" 
              :title="$t('login.passwordToggle')" 
              class="absolute right-3 top-1/2 -translate-y-1/2 cursor-pointer text-[1.3rem] text-gray-500 hover:text-gray-700 hover:scale-110 transition-all duration-200 ease-in-out"
            >
              {{ showConfirmPassword ? '👁️' : '𝄐' }}
            </span>
                         <div 
               class="absolute -top-[25px] right-0 bg-yellow-100 text-yellow-800 p-1 px-2 rounded text-[0.8rem] font-medium hidden z-10" 
               id="confirmPassword-capslock" 
               :class="{ 'block': capsLockOn }"
             >
               {{ $t('login.capsLock') }}
             </div>
          </div>
          <div id="confirmPassword-validation" class="text-[0.875rem] mt-2 font-medium" :class="confirmPasswordValidation.type === 'error' ? 'text-red-500' : confirmPasswordValidation.type === 'success' ? 'text-green-600' : ''">
            {{ confirmPasswordValidation.message }}
          </div>
        </div>
        
        <div class="mb-5">
          <label for="nickname" class="block mb-2 font-semibold text-gray-700 text-[0.9rem]">{{ $t('register.nickname') }} <span class="text-red-500">{{ $t('register.required') }}</span></label>
          <div class="relative flex items-center">
                         <input 
               type="text" 
               id="nickname" 
               v-model="formData.nickname" 
               class="flex-1 p-[14px] px-4 border-2 border-gray-300 rounded-xl rounded-r-none text-[0.95rem] transition-all duration-300 ease-in-out box-border bg-white font-inherit text-gray-800 focus:outline-none focus:border-blue-500 focus:shadow-[0_0_0_3px_rgba(59,130,246,0.1)] focus:-translate-y-[1px] focus:bg-white" 
               required 
               minlength="2" 
               maxlength="20" 
               :title="$t('register.validation.nickname.length')"
               :placeholder="$t('register.validation.nickname.length')"
               @blur="validateNickname"
             >
            <button 
              type="button" 
              class="p-[14px] px-4 bg-blue-500 text-white border-2 border-gray-300 border-l-0 rounded-xl rounded-l-none text-[0.9rem] font-semibold cursor-pointer transition-all duration-300 ease-in-out whitespace-nowrap min-w-[80px] hover:bg-blue-600 hover:-translate-y-[1px] disabled:bg-gray-400 disabled:cursor-not-allowed disabled:opacity-60" 
              id="nickname-check-btn" 
              @click="checkNicknameDuplicate"
              :disabled="!formData.nickname || nicknameChecked"
            >
              {{ nicknameChecked ? $t('register.checkComplete') : $t('register.duplicateCheck') }}
            </button>
          </div>
          <div id="nickname-validation" class="text-[0.875rem] mt-2 font-medium" :class="nicknameValidation.type === 'error' ? 'text-red-500' : nicknameValidation.type === 'success' ? 'text-green-600' : ''">
            {{ nicknameValidation.message }}
          </div>
        </div>
        
        <div class="mb-5">
          <label for="email" class="block mb-2 font-semibold text-gray-700 text-[0.9rem]">{{ $t('register.email') }}</label>
          <div class="relative flex items-center">
                         <input 
               type="email" 
               id="email" 
               v-model="formData.email" 
               class="flex-1 p-[14px] px-4 border-2 border-gray-300 rounded-xl rounded-r-none text-[0.95rem] transition-all duration-300 ease-in-out box-border bg-white font-inherit text-gray-800 focus:outline-none focus:border-blue-500 focus:shadow-[0_0_0_3px_rgba(59,130,246,0.1)] focus:-translate-y-[1px] focus:bg-white" 
               :title="$t('register.validation.email.format')"
               placeholder="example@email.com"
               @blur="validateEmail"
             >
            <button 
              type="button" 
              class="p-[14px] px-4 bg-blue-500 text-white border-2 border-gray-300 border-l-0 rounded-xl rounded-l-none text-[0.9rem] font-semibold cursor-pointer transition-all duration-300 ease-in-out whitespace-nowrap min-w-[80px] hover:bg-blue-600 hover:-translate-y-[1px] disabled:bg-gray-400 disabled:cursor-not-allowed disabled:opacity-60" 
              id="email-check-btn" 
              @click="checkEmailDuplicate"
              :disabled="!formData.email || emailChecked"
            >
              {{ emailChecked ? $t('register.checkComplete') : $t('register.duplicateCheck') }}
            </button>
          </div>
          <div id="email-validation" class="text-[0.875rem] mt-2 font-medium" :class="emailValidation.type === 'error' ? 'text-red-500' : emailValidation.type === 'success' ? 'text-green-600' : ''">
            {{ emailValidation.message }}
          </div>
        </div>
        
        <button 
          type="submit" 
          class="w-full p-[14px] bg-blue-500 text-white border-none rounded-xl text-[0.95rem] font-semibold cursor-pointer transition-all duration-300 ease-[cubic-bezier(0.4,0,0.2,1)] font-['Montserrat',sans-serif] shadow-[0_4px_12px_rgba(59,130,246,0.3)] hover:-translate-y-[2px] hover:shadow-[0_6px_20px_rgba(59,130,246,0.4)] hover:bg-blue-600 active:translate-y-0 disabled:bg-gray-400 disabled:cursor-not-allowed disabled:transform-none disabled:opacity-60" 
          id="submitBtn" 
          :disabled="!isFormValid || loading"
        >
          {{ loading ? $t('register.loading') : $t('register.registerButton') }}
        </button>
      </form>
      
      <div class="text-center mt-6 pt-5 border-t border-gray-200">
        <p class="text-gray-600">{{ $t('register.hasAccount') }} <router-link to="/user/login" class="text-blue-600 no-underline font-semibold transition-colors duration-300 ease-in-out hover:text-blue-800 hover:underline">{{ $t('register.login') }}</router-link></p>
      </div>
      
      <div class="text-center mt-4">
        <router-link to="/" class="text-gray-500 no-underline text-[0.9rem] transition-colors duration-300 ease-in-out hover:text-gray-700 hover:underline">{{ $t('login.backToHome') }}</router-link>
      </div>
    </div>
  </div>
</template>

<script>
import apiService from '@/services/api.js';

export default {
  name: 'UserRegisterView',
  data() {
    return {
      formData: {
        username: '',
        password: '',
        confirmPassword: '',
        nickname: '',
        email: ''
      },
      showPassword: false,
      showConfirmPassword: false,
      capsLockOn: false,
      loading: false,
      error: '',
      usernameChecked: false,
      nicknameChecked: false,
      emailChecked: false,
      usernameValidation: { type: '', message: '' },
      passwordValidation: { type: '', message: '' },
      confirmPasswordValidation: { type: '', message: '' },
      nicknameValidation: { type: '', message: '' },
      emailValidation: { type: '', message: '' }
    }
  },
  computed: {
    isFormValid() {
      return this.formData.username && 
             this.formData.password && 
             this.formData.confirmPassword && 
             this.formData.nickname &&
             this.usernameChecked &&
             this.nicknameChecked &&
             this.usernameValidation.type === 'success' &&
             this.passwordValidation.type === 'success' &&
             this.confirmPasswordValidation.type === 'success' &&
             this.nicknameValidation.type === 'success' &&
             (this.formData.email ? this.emailValidation.type === 'success' : true);
    }
  },
  mounted() {
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
    async handleRegister() {
      if (!this.isFormValid) {
        this.error = this.$t('register.formError');
        return;
      }

      this.loading = true;
      this.error = '';
      
      try {
        // 새로운 회원가입 API 호출
        const registerData = {
          username: this.formData.username,
          password: this.formData.password,
          nickname: this.formData.nickname,
          email: this.formData.email || null
        };
        
        await apiService.register(registerData);
        
        // 회원가입 성공 시 로그인 페이지로 이동
        this.$router.push(`/user/login?message=${this.$t('register.success')}`);
        
      } catch (error) {
        console.error('Register error:', error);
        if (error.message && error.message.includes('이미 존재하는')) {
          this.error = error.message;
        } else {
          this.error = this.$t('register.serverError');
        }
      } finally {
        this.loading = false;
      }
    },

    validateUsername() {
      const username = this.formData.username;
      if (!username) {
        this.usernameValidation = { type: 'error', message: this.$t('register.validation.username.required') };
        return;
      }
      if (!/^[a-zA-Z0-9_]{4,20}$/.test(username)) {
        this.usernameValidation = { type: 'error', message: this.$t('register.validation.username.format') };
        return;
      }
      this.usernameValidation = { type: 'success', message: this.$t('register.validation.username.available') };
    },

    async checkUsernameDuplicate() {
      if (!this.formData.username) return;
      
      try {
        const response = await fetch(`/api/user/check-username?username=${this.formData.username}`);
        const data = await response.json();
        
        if (response.ok && !data.exists) {
          this.usernameChecked = true;
          this.usernameValidation = { type: 'success', message: this.$t('register.validation.username.available') };
        } else {
          this.usernameChecked = false;
          this.usernameValidation = { type: 'error', message: this.$t('register.validation.username.duplicate') };
        }
      } catch (error) {
        this.usernameValidation = { type: 'error', message: this.$t('register.validation.username.checkError') };
      }
    },

    validatePassword() {
      const password = this.formData.password;
      if (!password) {
        this.passwordValidation = { type: 'error', message: this.$t('register.validation.password.required') };
        return;
      }
      if (password.length < 6) {
        this.passwordValidation = { type: 'error', message: this.$t('register.validation.password.minLength') };
        return;
      }
      this.passwordValidation = { type: 'success', message: this.$t('register.validation.password.available') };
      this.validateConfirmPassword();
    },

    validateConfirmPassword() {
      const confirmPassword = this.formData.confirmPassword;
      if (!confirmPassword) {
        this.confirmPasswordValidation = { type: 'error', message: this.$t('register.validation.confirmPassword.required') };
        return;
      }
      if (confirmPassword !== this.formData.password) {
        this.confirmPasswordValidation = { type: 'error', message: this.$t('register.validation.confirmPassword.mismatch') };
        return;
      }
      this.confirmPasswordValidation = { type: 'success', message: this.$t('register.validation.confirmPassword.match') };
    },

    validateNickname() {
      const nickname = this.formData.nickname;
      if (!nickname) {
        this.nicknameValidation = { type: 'error', message: this.$t('register.validation.nickname.required') };
        return;
      }
      if (nickname.length < 2 || nickname.length > 20) {
        this.nicknameValidation = { type: 'error', message: this.$t('register.validation.nickname.length') };
        return;
      }
      this.nicknameValidation = { type: 'success', message: this.$t('register.validation.nickname.available') };
    },

    async checkNicknameDuplicate() {
      if (!this.formData.nickname) return;
      
      try {
        const response = await fetch(`/api/user/check-nickname?nickname=${this.formData.nickname}`);
        const data = await response.json();
        
        if (response.ok && !data.exists) {
          this.nicknameChecked = true;
          this.nicknameValidation = { type: 'success', message: this.$t('register.validation.nickname.available') };
        } else {
          this.nicknameChecked = false;
          this.nicknameValidation = { type: 'error', message: this.$t('register.validation.nickname.duplicate') };
        }
      } catch (error) {
        this.nicknameValidation = { type: 'error', message: this.$t('register.validation.nickname.checkError') };
      }
    },

    validateEmail() {
      const email = this.formData.email;
      if (!email) {
        this.emailValidation = { type: '', message: '' };
        return;
      }
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailRegex.test(email)) {
        this.emailValidation = { type: 'error', message: this.$t('register.validation.email.format') };
        return;
      }
      this.emailValidation = { type: 'success', message: this.$t('register.validation.email.available') };
    },

    async checkEmailDuplicate() {
      if (!this.formData.email) return;
      
      try {
        const response = await fetch(`/api/user/check-email?email=${this.formData.email}`);
        const data = await response.json();
        
        if (response.ok && !data.exists) {
          this.emailChecked = true;
          this.emailValidation = { type: 'success', message: this.$t('register.validation.email.available') };
        } else {
          this.emailChecked = false;
          this.emailValidation = { type: 'error', message: this.$t('register.validation.email.duplicate') };
        }
      } catch (error) {
        this.emailValidation = { type: 'error', message: this.$t('register.validation.email.checkError') };
      }
    },

    setupCapsLockDetection() {
      const passwordInput = document.getElementById('password');
      const confirmPasswordInput = document.getElementById('confirmPassword');
      
      [passwordInput, confirmPasswordInput].forEach(input => {
        if (input) {
          input.addEventListener('keydown', (e) => {
            if (e.getModifierState('CapsLock')) {
              this.capsLockOn = true;
            }
          });
          
          input.addEventListener('keyup', (e) => {
            if (e.getModifierState('CapsLock')) {
              this.capsLockOn = true;
            } else {
              this.capsLockOn = false;
            }
          });
        }
      });
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
.register-page.dark-mode {
  background: #1a202c;
}

.register-page.dark-mode .register-container {
  background: #2d3748;
  border-color: #4a5568;
  box-shadow: 0 4px 20px rgba(0,0,0,0.3);
}

.register-page.dark-mode .register-title {
  color: #f7fafc;
}

.register-page.dark-mode .register-subtitle {
  color: #a0aec0;
}

.register-page.dark-mode .form-label {
  color: #e2e8f0;
}

.register-page.dark-mode .form-input {
  background: #1a202c;
  border-color: #4a5568;
  color: #e2e8f0;
}

.register-page.dark-mode .form-input:focus {
  border-color: #ffe082;
  box-shadow: 0 0 0 3px rgba(255, 224, 130, 0.1);
}

.register-page.dark-mode .login-link {
  border-top-color: #4a5568;
}

.register-page.dark-mode .login-link a {
  color: #90cdf4;
}

.register-page.dark-mode .login-link a:hover {
  color: #63b3ed;
}

.register-page.dark-mode .back-link a {
  color: #a0aec0;
}

.register-page.dark-mode .back-link a:hover {
  color: #e2e8f0;
}

.register-page.dark-mode .check-button {
  background: linear-gradient(145deg, #4a5568 0%, #2d3748 100%);
  border-color: #4a5568;
  color: #e2e8f0;
}

.register-page.dark-mode .check-button:hover:not(:disabled) {
  background: linear-gradient(145deg, #5a6a7a 0%, #3d4a5a 100%);
}

.register-page.dark-mode .check-button:disabled {
  background: linear-gradient(145deg, #2d3748 0%, #1a202c 100%);
  opacity: 0.6;
}

.register-page.dark-mode .capslock-warning {
  background: rgba(255, 193, 7, 0.8);
  color: #744210;
}
</style> 
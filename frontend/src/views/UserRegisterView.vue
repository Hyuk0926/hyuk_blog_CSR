<template>
  <div class="min-h-screen flex items-center justify-center p-5 font-['Noto_Sans_KR',sans-serif] bg-gradient-to-br from-[#1a1a1a] via-[#2d2d2d] via-[#404040] via-[#2d2d2d] to-[#1a1a1a]">
    <div class="bg-[rgba(45,45,45,0.95)] backdrop-blur-[10px] rounded-2xl shadow-[0_8px_32px_rgba(0,0,0,0.3)] p-10 w-full max-w-[500px] border border-[rgba(255,255,255,0.2)]">
      <div class="text-center mb-8">
        <h1 class="text-[1.75rem] text-white mb-2 font-bold font-['Montserrat',sans-serif]">{{ $t('register.title') }}</h1>
        <p class="text-[#b0b0b0] text-[0.95rem] font-medium">{{ $t('register.subtitle') }}</p>
      </div>
      
      <div v-if="error" class="bg-[rgba(197,48,48,0.2)] text-[#ff6b6b] p-4 rounded-xl mb-5 text-center font-medium border border-[rgba(197,48,48,0.3)]">
        {{ error }}
      </div>
      
      <form @submit.prevent="handleRegister" id="registerForm">
        <div class="mb-5">
          <label for="username" class="block mb-2 font-semibold text-[#e0e0e0] text-[0.9rem]">{{ $t('register.username') }} <span class="text-[#ff6b6b]">{{ $t('register.required') }}</span></label>
          <div class="relative flex items-center">
                         <input 
               type="text" 
               id="username" 
               v-model="formData.username" 
               class="flex-1 p-[14px] px-4 border-2 border-[#555555] rounded-xl rounded-r-none text-[0.95rem] transition-all duration-300 ease-in-out box-border bg-[rgba(60,60,60,0.8)] font-inherit text-white focus:outline-none focus:border-[#888888] focus:shadow-[0_0_0_3px_rgba(136,136,136,0.2)] focus:-translate-y-[1px] focus:bg-[rgba(70,70,70,0.9)" 
               required 
               pattern="[a-zA-Z0-9_]{4,20}" 
               :title="$t('register.validation.username.format')"
               :placeholder="$t('register.validation.username.format')"
               @blur="validateUsername"
             >
            <button 
              type="button" 
              class="p-[14px] px-4 bg-gradient-to-br from-[#666666] to-[#444444] text-white border-2 border-[#555555] border-l-0 rounded-xl rounded-l-none text-[0.9rem] font-semibold cursor-pointer transition-all duration-300 ease-in-out whitespace-nowrap min-w-[80px] hover:bg-gradient-to-br hover:from-[#777777] hover:to-[#555555] hover:-translate-y-[1px] disabled:bg-gradient-to-br disabled:from-[#555555] disabled:to-[#333333] disabled:cursor-not-allowed disabled:opacity-60" 
              id="username-check-btn" 
              @click="checkUsernameDuplicate"
              :disabled="!formData.username || usernameChecked"
            >
              {{ usernameChecked ? $t('register.checkComplete') : $t('register.duplicateCheck') }}
            </button>
          </div>
          <div id="username-validation" class="text-[0.875rem] mt-2 font-medium" :class="usernameValidation.type === 'error' ? 'text-[#ff6b6b]' : usernameValidation.type === 'success' ? 'text-[#68d391]' : ''">
            {{ usernameValidation.message }}
          </div>
        </div>
        
        <div class="mb-5">
          <label for="password" class="block mb-2 font-semibold text-[#e0e0e0] text-[0.9rem]">{{ $t('register.password') }} <span class="text-[#ff6b6b]">{{ $t('register.required') }}</span></label>
          <div class="relative">
                         <input 
               :type="showPassword ? 'text' : 'password'" 
               id="password" 
               v-model="formData.password" 
               class="w-full p-[14px] px-4 pr-10 border-2 border-[#555555] rounded-xl text-[0.95rem] transition-all duration-300 ease-in-out box-border bg-[rgba(60,60,60,0.8)] font-inherit text-white focus:outline-none focus:border-[#888888] focus:shadow-[0_0_0_3px_rgba(136,136,136,0.2)] focus:-translate-y-[1px] focus:bg-[rgba(70,70,70,0.9)" 
               required 
               minlength="6" 
               :title="$t('register.validation.password.minLength')"
               :placeholder="$t('register.validation.password.minLength')"
               @blur="validatePassword"
             >
            <span 
              @click="showPassword = !showPassword" 
              :title="$t('login.passwordToggle')" 
              class="absolute right-3 top-1/2 -translate-y-1/2 cursor-pointer text-[1.3rem] text-[#888] hover:text-white hover:scale-110 transition-all duration-200 ease-in-out"
            >
              {{ showPassword ? '👁️' : '𝄐' }}
            </span>
                         <div 
               class="absolute -top-[25px] right-0 bg-[rgba(255,193,7,0.9)] text-[#856404] p-1 px-2 rounded text-[0.8rem] font-medium hidden z-10" 
               id="password-capslock" 
               :class="{ 'block': capsLockOn }"
             >
               {{ $t('login.capsLock') }}
             </div>
          </div>
          <div id="password-validation" class="text-[0.875rem] mt-2 font-medium" :class="passwordValidation.type === 'error' ? 'text-[#ff6b6b]' : passwordValidation.type === 'success' ? 'text-[#68d391]' : ''">
            {{ passwordValidation.message }}
          </div>
        </div>
        
        <div class="mb-5">
          <label for="confirmPassword" class="block mb-2 font-semibold text-[#e0e0e0] text-[0.9rem]">{{ $t('register.confirmPassword') }} <span class="text-[#ff6b6b]">{{ $t('register.required') }}</span></label>
          <div class="relative">
                         <input 
               :type="showConfirmPassword ? 'text' : 'password'" 
               id="confirmPassword" 
               v-model="formData.confirmPassword" 
               class="w-full p-[14px] px-4 pr-10 border-2 border-[#555555] rounded-xl text-[0.95rem] transition-all duration-300 ease-in-out box-border bg-[rgba(60,60,60,0.8)] font-inherit text-white focus:outline-none focus:border-[#888888] focus:shadow-[0_0_0_3px_rgba(136,136,136,0.2)] focus:-translate-y-[1px] focus:bg-[rgba(70,70,70,0.9)" 
               required
               :placeholder="$t('register.validation.confirmPassword.required')"
               @blur="validateConfirmPassword"
             >
            <span 
              @click="showConfirmPassword = !showConfirmPassword" 
              :title="$t('login.passwordToggle')" 
              class="absolute right-3 top-1/2 -translate-y-1/2 cursor-pointer text-[1.3rem] text-[#888] hover:text-white hover:scale-110 transition-all duration-200 ease-in-out"
            >
              {{ showConfirmPassword ? '👁️' : '𝄐' }}
            </span>
                         <div 
               class="absolute -top-[25px] right-0 bg-[rgba(255,193,7,0.9)] text-[#856404] p-1 px-2 rounded text-[0.8rem] font-medium hidden z-10" 
               id="confirmPassword-capslock" 
               :class="{ 'block': capsLockOn }"
             >
               {{ $t('login.capsLock') }}
             </div>
          </div>
          <div id="confirmPassword-validation" class="text-[0.875rem] mt-2 font-medium" :class="confirmPasswordValidation.type === 'error' ? 'text-[#ff6b6b]' : confirmPasswordValidation.type === 'success' ? 'text-[#68d391]' : ''">
            {{ confirmPasswordValidation.message }}
          </div>
        </div>
        
        <div class="mb-5">
          <label for="nickname" class="block mb-2 font-semibold text-[#e0e0e0] text-[0.9rem]">{{ $t('register.nickname') }} <span class="text-[#ff6b6b]">{{ $t('register.required') }}</span></label>
          <div class="relative flex items-center">
                         <input 
               type="text" 
               id="nickname" 
               v-model="formData.nickname" 
               class="flex-1 p-[14px] px-4 border-2 border-[#555555] rounded-xl rounded-r-none text-[0.95rem] transition-all duration-300 ease-in-out box-border bg-[rgba(60,60,60,0.8)] font-inherit text-white focus:outline-none focus:border-[#888888] focus:shadow-[0_0_0_3px_rgba(136,136,136,0.2)] focus:-translate-y-[1px] focus:bg-[rgba(70,70,70,0.9)" 
               required 
               minlength="2" 
               maxlength="20" 
               :title="$t('register.validation.nickname.length')"
               :placeholder="$t('register.validation.nickname.length')"
               @blur="validateNickname"
             >
            <button 
              type="button" 
              class="p-[14px] px-4 bg-gradient-to-br from-[#666666] to-[#444444] text-white border-2 border-[#555555] border-l-0 rounded-xl rounded-l-none text-[0.9rem] font-semibold cursor-pointer transition-all duration-300 ease-in-out whitespace-nowrap min-w-[80px] hover:bg-gradient-to-br hover:from-[#777777] hover:to-[#555555] hover:-translate-y-[1px] disabled:bg-gradient-to-br disabled:from-[#555555] disabled:to-[#333333] disabled:cursor-not-allowed disabled:opacity-60" 
              id="nickname-check-btn" 
              @click="checkNicknameDuplicate"
              :disabled="!formData.nickname || nicknameChecked"
            >
              {{ nicknameChecked ? $t('register.checkComplete') : $t('register.duplicateCheck') }}
            </button>
          </div>
          <div id="nickname-validation" class="text-[0.875rem] mt-2 font-medium" :class="nicknameValidation.type === 'error' ? 'text-[#ff6b6b]' : nicknameValidation.type === 'success' ? 'text-[#68d391]' : ''">
            {{ nicknameValidation.message }}
          </div>
        </div>
        
        <div class="mb-5">
          <label for="email" class="block mb-2 font-semibold text-[#e0e0e0] text-[0.9rem]">{{ $t('register.email') }}</label>
          <div class="relative flex items-center">
                         <input 
               type="email" 
               id="email" 
               v-model="formData.email" 
               class="flex-1 p-[14px] px-4 border-2 border-[#555555] rounded-xl rounded-r-none text-[0.95rem] transition-all duration-300 ease-in-out box-border bg-[rgba(60,60,60,0.8)] font-inherit text-white focus:outline-none focus:border-[#888888] focus:shadow-[0_0_0_3px_rgba(136,136,136,0.2)] focus:-translate-y-[1px] focus:bg-[rgba(70,70,70,0.9)" 
               :title="$t('register.validation.email.format')"
               placeholder="example@email.com"
               @blur="validateEmail"
             >
            <button 
              type="button" 
              class="p-[14px] px-4 bg-gradient-to-br from-[#666666] to-[#444444] text-white border-2 border-[#555555] border-l-0 rounded-xl rounded-l-none text-[0.9rem] font-semibold cursor-pointer transition-all duration-300 ease-in-out whitespace-nowrap min-w-[80px] hover:bg-gradient-to-br hover:from-[#777777] hover:to-[#555555] hover:-translate-y-[1px] disabled:bg-gradient-to-br disabled:from-[#555555] disabled:to-[#333333] disabled:cursor-not-allowed disabled:opacity-60" 
              id="email-check-btn" 
              @click="checkEmailDuplicate"
              :disabled="!formData.email || emailChecked"
            >
              {{ emailChecked ? $t('register.checkComplete') : $t('register.duplicateCheck') }}
            </button>
          </div>
          <div id="email-validation" class="text-[0.875rem] mt-2 font-medium" :class="emailValidation.type === 'error' ? 'text-[#ff6b6b]' : emailValidation.type === 'success' ? 'text-[#68d391]' : ''">
            {{ emailValidation.message }}
          </div>
        </div>
        
        <button 
          type="submit" 
          class="w-full p-[14px] bg-gradient-to-br from-[#666666] to-[#444444] text-white border-none rounded-xl text-[0.95rem] font-semibold cursor-pointer transition-all duration-300 ease-[cubic-bezier(0.4,0,0.2,1)] font-['Montserrat',sans-serif] shadow-[0_4px_12px_rgba(0,0,0,0.3)] hover:-translate-y-[2px] hover:shadow-[0_6px_20px_rgba(0,0,0,0.4)] hover:bg-gradient-to-br hover:from-[#777777] hover:to-[#555555] active:translate-y-0 disabled:bg-gradient-to-br disabled:from-[#555555] disabled:to-[#333333] disabled:cursor-not-allowed disabled:transform-none disabled:opacity-60" 
          id="submitBtn" 
          :disabled="!isFormValid || loading"
        >
          {{ loading ? $t('register.loading') : $t('register.registerButton') }}
        </button>
      </form>
      
      <div class="text-center mt-6 pt-5 border-t border-[rgba(255,255,255,0.1)]">
        <p class="text-[#cccccc]">{{ $t('register.hasAccount') }} <router-link to="/user/login" class="text-[#cccccc] no-underline font-semibold transition-colors duration-300 ease-in-out hover:text-white hover:underline">{{ $t('register.login') }}</router-link></p>
      </div>
      
      <div class="text-center mt-4">
        <router-link to="/" class="text-[#aaaaaa] no-underline text-[0.9rem] transition-colors duration-300 ease-in-out hover:text-[#cccccc] hover:underline">{{ $t('login.backToHome') }}</router-link>
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
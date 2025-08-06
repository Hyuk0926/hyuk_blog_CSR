<template>
  <div class="register-page">
    <div class="register-container">
    <div class="register-header">
      <h1 class="register-title">{{ $t('register.title') }}</h1>
      <p class="register-subtitle">{{ $t('register.subtitle') }}</p>
    </div>
    
    <div v-if="error" class="error-message">
      {{ error }}
    </div>
    
    <form @submit.prevent="handleRegister" id="registerForm">
      <div class="form-group">
        <label for="username" class="form-label">{{ $t('register.username') }} <span class="required">{{ $t('register.required') }}</span></label>
        <div class="input-group">
          <input 
            type="text" 
            id="username" 
            v-model="formData.username" 
            class="form-input" 
            required 
            pattern="[a-zA-Z0-9_]{4,20}" 
            title="4-20자의 영문, 숫자, 언더스코어만 사용 가능합니다"
            placeholder="4-20자의 영문, 숫자, 언더스코어"
            @blur="validateUsername"
          >
          <button 
            type="button" 
            class="check-button" 
            id="username-check-btn" 
            @click="checkUsernameDuplicate"
            :disabled="!formData.username || usernameChecked"
          >
            {{ usernameChecked ? $t('register.checkComplete') : $t('register.duplicateCheck') }}
          </button>
        </div>
        <div id="username-validation" class="validation-message" :class="usernameValidation.type">
          {{ usernameValidation.message }}
        </div>
      </div>
      
      <div class="form-group">
        <label for="password" class="form-label">{{ $t('register.password') }} <span class="required">{{ $t('register.required') }}</span></label>
        <div class="input-group" style="position: relative;">
          <input 
            :type="showPassword ? 'text' : 'password'" 
            id="password" 
            v-model="formData.password" 
            class="form-input" 
            required 
            minlength="6" 
            title="최소 6자 이상 입력해주세요"
            placeholder="최소 6자 이상"
            @blur="validatePassword"
          >
                  <span 
          @click="showPassword = !showPassword" 
          :title="$t('login.passwordToggle')" 
          style="position:absolute; right:12px; top:50%; transform:translateY(-50%); cursor:pointer; font-size:1.3rem; color:#888;"
        >
          {{ showPassword ? '👁️' : '𝄐' }}
        </span>
          <div 
            class="capslock-warning" 
            id="password-capslock" 
            :class="{ show: capsLockOn }"
          >
            Caps Lock이 켜져 있습니다
          </div>
        </div>
        <div id="password-validation" class="validation-message" :class="passwordValidation.type">
          {{ passwordValidation.message }}
        </div>
      </div>
      
      <div class="form-group">
        <label for="confirmPassword" class="form-label">{{ $t('register.confirmPassword') }} <span class="required">{{ $t('register.required') }}</span></label>
        <div class="input-group" style="position: relative;">
          <input 
            :type="showConfirmPassword ? 'text' : 'password'" 
            id="confirmPassword" 
            v-model="formData.confirmPassword" 
            class="form-input" 
            required
            placeholder="비밀번호를 다시 입력해주세요"
            @blur="validateConfirmPassword"
          >
                  <span 
          @click="showConfirmPassword = !showConfirmPassword" 
          :title="$t('login.passwordToggle')" 
          style="position:absolute; right:12px; top:50%; transform:translateY(-50%); cursor:pointer; font-size:1.3rem; color:#888;"
        >
          {{ showConfirmPassword ? '👁️' : '𝄐' }}
        </span>
          <div 
            class="capslock-warning" 
            id="confirmPassword-capslock" 
            :class="{ show: capsLockOn }"
          >
            Caps Lock이 켜져 있습니다
          </div>
        </div>
        <div id="confirmPassword-validation" class="validation-message" :class="confirmPasswordValidation.type">
          {{ confirmPasswordValidation.message }}
        </div>
      </div>
      
      <div class="form-group">
        <label for="nickname" class="form-label">{{ $t('register.nickname') }} <span class="required">{{ $t('register.required') }}</span></label>
        <div class="input-group">
          <input 
            type="text" 
            id="nickname" 
            v-model="formData.nickname" 
            class="form-input" 
            required 
            minlength="2" 
            maxlength="20" 
            title="2-20자의 닉네임을 입력해주세요"
            placeholder="2-20자의 닉네임"
            @blur="validateNickname"
          >
          <button 
            type="button" 
            class="check-button" 
            id="nickname-check-btn" 
            @click="checkNicknameDuplicate"
            :disabled="!formData.nickname || nicknameChecked"
          >
            {{ nicknameChecked ? $t('register.checkComplete') : $t('register.duplicateCheck') }}
          </button>
        </div>
        <div id="nickname-validation" class="validation-message" :class="nicknameValidation.type">
          {{ nicknameValidation.message }}
        </div>
      </div>
      
      <div class="form-group">
        <label for="email" class="form-label">{{ $t('register.email') }}</label>
        <div class="input-group">
          <input 
            type="email" 
            id="email" 
            v-model="formData.email" 
            class="form-input" 
            title="올바른 이메일 형식을 입력해주세요"
            placeholder="example@email.com"
            @blur="validateEmail"
          >
          <button 
            type="button" 
            class="check-button" 
            id="email-check-btn" 
            @click="checkEmailDuplicate"
            :disabled="!formData.email || emailChecked"
          >
            {{ emailChecked ? $t('register.checkComplete') : $t('register.duplicateCheck') }}
          </button>
        </div>
        <div id="email-validation" class="validation-message" :class="emailValidation.type">
          {{ emailValidation.message }}
        </div>
      </div>
      
      <button 
        type="submit" 
        class="register-button" 
        id="submitBtn" 
        :disabled="!isFormValid || loading"
      >
        {{ loading ? $t('register.loading') : $t('register.registerButton') }}
      </button>
    </form>
    
    <div class="login-link">
      <p>{{ $t('register.hasAccount') }} <router-link to="/user/login">{{ $t('register.login') }}</router-link></p>
    </div>
    
    <div class="back-link">
      <router-link to="/">{{ $t('login.backToHome') }}</router-link>
    </div>
  </div>
  </div>
</template>

<script>
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
        const response = await fetch('/api/user/register', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(this.formData)
        });
        
        const data = await response.json();
        
        if (response.ok) {
          this.$router.push(`/user/login?message=${this.$t('register.success')}`);
        } else {
          this.error = data.message || this.$t('register.formError');
        }
      } catch (error) {
        this.error = this.$t('register.serverError');
        console.error('Register error:', error);
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
.register-page {
  background: linear-gradient(135deg, #1a1a1a 0%, #2d2d2d 25%, #404040 50%, #2d2d2d 75%, #1a1a1a 100%);
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0;
  padding: 20px;
  font-family: 'Noto Sans KR', sans-serif;
}

.register-container {
  background: rgba(45, 45, 45, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.3);
  padding: 40px;
  width: 100%;
  max-width: 500px;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.register-header {
  text-align: center;
  margin-bottom: 32px;
}

.register-title {
  font-size: 1.75rem;
  color: #ffffff;
  margin-bottom: 8px;
  font-weight: 700;
  font-family: 'Montserrat', sans-serif;
}

.register-subtitle {
  color: #b0b0b0;
  font-size: 0.95rem;
  font-weight: 500;
}

.form-group {
  margin-bottom: 20px;
}

.form-label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: #e0e0e0;
  font-size: 0.9rem;
}

.form-input {
  width: 100%;
  padding: 14px 16px;
  border: 2px solid #555555;
  border-radius: 12px;
  font-size: 0.95rem;
  transition: all 0.3s ease;
  box-sizing: border-box;
  background: rgba(60, 60, 60, 0.8);
  font-family: inherit;
  color: #ffffff;
}

.input-group {
  position: relative;
  display: flex;
  align-items: center;
}

.input-group .form-input {
  flex: 1;
  border-top-right-radius: 0;
  border-bottom-right-radius: 0;
}

.check-button {
  padding: 14px 16px;
  background: linear-gradient(145deg, #666666 0%, #444444 100%);
  color: white;
  border: 2px solid #555555;
  border-left: none;
  border-top-right-radius: 12px;
  border-bottom-right-radius: 12px;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  white-space: nowrap;
  min-width: 80px;
}

.check-button:hover:not(:disabled) {
  background: linear-gradient(145deg, #777777 0%, #555555 100%);
  transform: translateY(-1px);
}

.check-button:disabled {
  background: linear-gradient(145deg, #555555 0%, #333333 100%);
  cursor: not-allowed;
  opacity: 0.6;
}

.capslock-warning {
  position: absolute;
  top: -25px;
  right: 0;
  background: rgba(255, 193, 7, 0.9);
  color: #856404;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 0.8rem;
  font-weight: 500;
  display: none;
  z-index: 10;
}

.capslock-warning.show {
  display: block;
}

.form-input:focus {
  outline: none;
  border-color: #888888;
  box-shadow: 0 0 0 3px rgba(136, 136, 136, 0.2);
  transform: translateY(-1px);
  background: rgba(70, 70, 70, 0.9);
}

.form-input.error {
  border-color: #ff6b6b;
  box-shadow: 0 0 0 3px rgba(255, 107, 107, 0.2);
}

.form-input.success {
  border-color: #68d391;
  box-shadow: 0 0 0 3px rgba(104, 211, 145, 0.2);
}

.validation-message {
  font-size: 0.875rem;
  margin-top: 8px;
  font-weight: 500;
}

.validation-message.error {
  color: #ff6b6b;
}

.validation-message.success {
  color: #68d391;
}

.register-button {
  width: 100%;
  padding: 14px;
  background: linear-gradient(145deg, #666666 0%, #444444 100%);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 0.95rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  font-family: 'Montserrat', sans-serif;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

.register-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.4);
  background: linear-gradient(145deg, #777777 0%, #555555 100%);
}

.register-button:active {
  transform: translateY(0);
}

.register-button:disabled {
  background: linear-gradient(145deg, #555555 0%, #333333 100%);
  cursor: not-allowed;
  transform: none;
  opacity: 0.6;
}

.error-message {
  background: rgba(197, 48, 48, 0.2);
  color: #ff6b6b;
  padding: 16px;
  border-radius: 12px;
  margin-bottom: 20px;
  text-align: center;
  font-weight: 500;
  border: 1px solid rgba(197, 48, 48, 0.3);
}

.login-link {
  text-align: center;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.login-link a {
  color: #cccccc;
  text-decoration: none;
  font-weight: 600;
  transition: color 0.3s ease;
}

.login-link a:hover {
  color: #ffffff;
  text-decoration: underline;
}

.back-link {
  text-align: center;
  margin-top: 16px;
}

.back-link a {
  color: #aaaaaa;
  text-decoration: none;
  font-size: 0.9rem;
  transition: color 0.3s ease;
}

.back-link a:hover {
  color: #cccccc;
  text-decoration: underline;
}

.required {
  color: #ff6b6b;
}

/* 다크모드 지원 */
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
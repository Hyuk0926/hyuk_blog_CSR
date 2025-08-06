<template>
  <div class="login-page">
    <div class="login-container">
    <div class="login-header">
      <h1 class="login-title">{{ $t('login.title') }}</h1>
      <p class="login-subtitle">{{ $t('login.subtitle') }}</p>
    </div>
    
    <div v-if="error" class="error-message">
      {{ error }}
    </div>
    
    <div v-if="message" class="success-message">
      {{ message }}
    </div>
    
    <form @submit.prevent="handleLogin">
      <input type="hidden" name="redirectUrl" :value="redirectUrl">
      
      <div class="form-group">
        <label for="username" class="form-label">{{ $t('login.username') }}</label>
        <input 
          type="text" 
          id="username" 
          v-model="formData.username" 
          class="form-input" 
          required
        >
      </div>
      
      <div class="form-group">
        <label for="password" class="form-label">{{ $t('login.password') }}</label>
        <div style="position:relative;">
          <input 
            :type="showPassword ? 'text' : 'password'" 
            id="password" 
            v-model="formData.password" 
            class="form-input" 
            required 
            autocomplete="current-password" 
            style="padding-right:40px; ime-mode:disabled;"
          >
                  <span 
          @click="togglePassword" 
          :title="$t('login.passwordToggle')" 
          style="position:absolute; right:12px; top:50%; transform:translateY(-50%); cursor:pointer; font-size:1.3rem; color:#888;"
        >
          {{ showPassword ? '👁️' : '𝄐' }}
        </span>
        </div>
        <div 
          id="capsLockMsg" 
          style="color:#dc3545; font-size:0.95rem; margin-top:6px; display:none;" 
          :style="{ display: capsLockOn ? 'block' : 'none' }"
        >
          {{ $t('login.capsLock') }}
        </div>
      </div>
      
      <button type="submit" class="login-button" :disabled="loading">
        {{ loading ? $t('login.loading') : $t('login.loginButton') }}
      </button>
    </form>
    
    <div class="register-link">
      <p>{{ $t('login.noAccount') }} <router-link to="/user/register">{{ $t('login.register') }}</router-link></p>
    </div>
    
    <div class="back-link">
      <router-link to="/">{{ $t('login.backToHome') }}</router-link>
    </div>
  </div>
  </div>
</template>

<script>
import apiService from '@/services/api.js';

export default {
  name: 'UserLoginView',
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
        // 새로운 통합 로그인 API 호출
        const response = await apiService.login(this.formData);
        
        // JWT 토큰을 로컬 스토리지에 저장
        localStorage.setItem('jwtToken', response.token);
        localStorage.setItem('userRole', response.role);
        localStorage.setItem('username', response.username);
        
        this.message = '로그인 성공!';
        
        // 부모 컴포넌트의 사용자 정보 업데이트
        this.$emit('user-logged-in', {
          username: response.username,
          role: response.role,
          token: response.token
        });
        
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
.login-page {
  background: linear-gradient(135deg, #1a1a1a 0%, #2d2d2d 25%, #404040 50%, #2d2d2d 75%, #1a1a1a 100%);
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0;
  padding: 20px;
  font-family: 'Noto Sans KR', sans-serif;
}

.login-container {
  background: rgba(45, 45, 45, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.3);
  padding: 40px;
  width: 100%;
  max-width: 400px;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.login-title {
  font-size: 1.75rem;
  color: #ffffff;
  margin-bottom: 8px;
  font-weight: 700;
  font-family: 'Montserrat', sans-serif;
}

.login-subtitle {
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

.form-input:focus {
  outline: none;
  border-color: #888888;
  box-shadow: 0 0 0 3px rgba(136, 136, 136, 0.2);
  transform: translateY(-1px);
  background: rgba(70, 70, 70, 0.9);
}

.login-button {
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

.login-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.4);
  background: linear-gradient(145deg, #777777 0%, #555555 100%);
}

.login-button:active {
  transform: translateY(0);
}

.login-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
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

.success-message {
  background: rgba(47, 133, 90, 0.2);
  color: #68d391;
  padding: 16px;
  border-radius: 12px;
  margin-bottom: 20px;
  text-align: center;
  font-weight: 500;
  border: 1px solid rgba(47, 133, 90, 0.3);
}

.register-link {
  text-align: center;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.register-link a {
  color: #cccccc;
  text-decoration: none;
  font-weight: 600;
  transition: color 0.3s ease;
}

.register-link a:hover {
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

#togglePassword:hover {
  color: #ffffff !important;
  transform: scale(1.1);
  transition: all 0.2s ease;
}

/* 다크모드 지원 */
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
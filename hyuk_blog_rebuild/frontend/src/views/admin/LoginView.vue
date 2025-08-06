<template>
  <div class="login-container">
    <div class="login-header">
      <h1 class="login-title">관리자 로그인</h1>
    </div>
    
    <div v-if="errorMessage" class="error-message">
      {{ errorMessage }}
    </div>
    
    <form @submit.prevent="submitLogin">
      <div class="form-group">
        <label for="username" class="form-label">아이디</label>
        <input 
          type="text" 
          id="username" 
          v-model="formData.username" 
          class="form-input" 
          required
        >
      </div>
      
      <div class="form-group">
        <label for="password" class="form-label">비밀번호</label>
        <div style="position:relative;">
          <input 
            :type="showPassword ? 'text' : 'password'" 
            id="password" 
            v-model="formData.password" 
            class="form-input" 
            required 
            autocomplete="current-password" 
            style="padding-right:40px; ime-mode:disabled;"
            @keydown="checkCapsLock"
          >
          <span 
            @click="togglePassword" 
            title="비밀번호 보기" 
            style="position:absolute; right:12px; top:50%; transform:translateY(-50%); cursor:pointer; font-size:1.3rem; color:#888;"
          >
            {{ showPassword ? '👁️' : '𝄐' }}
          </span>
        </div>
        <div v-if="capsLockOn" style="color:#dc3545; font-size:0.95rem; margin-top:6px;">
          CapsLock이 켜져 있습니다!
        </div>
      </div>
      
      <button type="submit" class="login-button" :disabled="isSubmitting">
        {{ isSubmitting ? '로그인 중...' : '로그인' }}
      </button>
    </form>
    
    <div class="back-link">
      <router-link to="/">← 메인 페이지로 돌아가기</router-link>
    </div>
  </div>
</template>

<script>
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
      
      // 임시 관리자 계정 (추후 백엔드 연동 시 제거)
      const adminCredentials = {
        username: 'admin',
        password: 'admin123'
      };
      
      // 간단한 로컬 인증 (실제로는 백엔드에서 처리)
      if (this.formData.username === adminCredentials.username && 
          this.formData.password === adminCredentials.password) {
        
        // 로그인 성공 시 임시 토큰 저장
        const tempToken = 'temp_admin_token_' + Date.now();
        localStorage.setItem('adminToken', tempToken);
        
        // 관리자 대시보드로 리다이렉트
        this.$router.push('/admin/dashboard');
      } else {
        this.errorMessage = '아이디 또는 비밀번호가 올바르지 않습니다.';
      }
      
      this.isSubmitting = false;
      
      // 실제 백엔드 API 호출 (추후 활성화)
      /*
      try {
        const response = await fetch('/api/admin/login', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(this.formData)
        });
        
        if (response.ok) {
          const data = await response.json();
          
          // 로그인 성공 시 토큰 저장
          if (data.token) {
            localStorage.setItem('adminToken', data.token);
          }
          
          // 관리자 대시보드로 리다이렉트
          this.$router.push('/admin/dashboard');
        } else {
          const errorData = await response.json();
          this.errorMessage = errorData.message || '로그인에 실패했습니다.';
        }
      } catch (error) {
        console.error('로그인 오류:', error);
        this.errorMessage = '네트워크 오류가 발생했습니다. 다시 시도해주세요.';
      } finally {
        this.isSubmitting = false;
      }
      */
    }
  }
}
</script>

<style scoped>
.login-container {
  background: rgba(45, 45, 45, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.3);
  padding: 40px;
  width: 100%;
  max-width: 400px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
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
  background: linear-gradient(145deg, #444444 0%, #333333 100%);
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

.back-link {
  text-align: center;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
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

/* 다크모드 지원 */
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
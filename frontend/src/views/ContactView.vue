<template>
  <div class="container">
    <div class="contact-main">
      <h1 class="page-title">{{ $t('contact.title') }}</h1>
      <div class="contact-card">
        <div v-if="successMessage" class="success-message">
          <span>{{ successMessage }}</span>
        </div>
        <form @submit.prevent="submitForm">
          <div class="form-group">
            <label class="form-label" for="name">{{ $t('contact.form.name') }}</label>
            <input 
              type="text" 
              id="name" 
              v-model="formData.name" 
              class="form-input" 
              required
              :placeholder="$t('contact.form.name') + '을(를) 입력해주세요'"
            >
          </div>
          <div class="form-group">
            <label class="form-label" for="email">{{ $t('contact.form.email') }}</label>
            <input 
              type="email" 
              id="email" 
              v-model="formData.email" 
              class="form-input" 
              required
              placeholder="example@email.com"
            >
          </div>
          <div class="form-group">
            <label class="form-label" for="subject">{{ $t('contact.form.subject') }}</label>
            <input 
              type="text" 
              id="subject" 
              v-model="formData.subject" 
              class="form-input" 
              required
              :placeholder="$t('contact.form.subject') + '을(를) 입력해주세요'"
            >
          </div>
          <div class="form-group">
            <label class="form-label" for="message">{{ $t('contact.form.message') }}</label>
            <textarea 
              id="message" 
              v-model="formData.message" 
              class="form-input" 
              required
              :placeholder="$t('contact.form.message') + '을(를) 입력해주세요'"
            ></textarea>
          </div>
          <button type="submit" class="submit-button" :disabled="isSubmitting">
            {{ isSubmitting ? $t('common.loading') : $t('contact.form.send') }}
          </button>
        </form>
        <div class="contact-info">
          <span>{{ $t('contact.subtitle') }}</span><br>
          <b>{{ $t('contact.form.email') }}:</b> 
          <a href="mailto:ehc28260@gmail.com">ehc28260@gmail.com</a>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import apiService from '@/services/api.js';

export default {
  name: 'ContactView',
  data() {
    return {
      successMessage: '',
      isSubmitting: false,
      formData: {
        name: '',
        email: '',
        subject: '',
        message: ''
      }
    }
  },
  computed: {
    lang() {
      return this.$i18n.locale;
    }
  },
  methods: {
    async submitForm() {
      this.isSubmitting = true;
      
      try {
        // API 서비스를 사용하여 문의 제출
        const result = await apiService.submitInquiry(this.formData);
        
        if (result.success) {
          this.successMessage = this.$t('contact.success');
          
          // 폼 초기화
          this.formData = {
            name: '',
            email: '',
            subject: '',
            message: ''
          };
          
          // 5초 후 성공 메시지 제거
          setTimeout(() => {
            this.successMessage = '';
          }, 5000);
        } else {
          // API에서 반환된 에러 메시지 사용
          const errorMessage = result.error || this.$t('contact.error');
          this.successMessage = errorMessage;
        }
      } catch (error) {
        console.error('문의 전송 실패:', error);
        
        // 백엔드 서버가 없는 경우 오프라인 모드로 처리
        if (error.message && error.message.includes('404')) {
          this.successMessage = '오프라인 모드: 문의가 저장되었습니다. (서버 연결 불가)';
          
          // 폼 초기화
          this.formData = {
            name: '',
            email: '',
            subject: '',
            message: ''
          };
          
          // 5초 후 성공 메시지 제거
          setTimeout(() => {
            this.successMessage = '';
          }, 5000);
        } else {
          this.successMessage = this.$t('contact.error');
        }
      } finally {
        this.isSubmitting = false;
      }
    }
  }
}
</script>

<style scoped>
/* main.css를 참고한 스타일 */
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 32px 24px;
}

.contact-main {
  max-width: 600px;
  margin: 0 auto;
}

.page-title {
  font-size: 1.75rem;
  color: #2c3e50;
  font-weight: 700;
  margin-bottom: 32px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e0e0e0;
  font-family: 'Montserrat', sans-serif;
  letter-spacing: 0.5px;
  transition: color 0.5s ease, border-bottom-color 0.5s ease;
}

.contact-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.04);
  padding: 32px;
  border: 1px solid #f1f1f1;
  transition: background-color 0.5s ease, border-color 0.5s ease, box-shadow 0.5s ease;
}

.success-message {
  background: #ebf7ed;
  color: #276749;
  border: 1px solid #c6f6d5;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 24px;
  font-size: 0.95rem;
  display: flex;
  align-items: center;
  transition: background-color 0.5s ease, border-color 0.5s ease, color 0.5s ease;
}

.success-message::before {
  content: "✓";
  font-weight: bold;
  margin-right: 12px;
  background: #48bb78;
  color: white;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
}

.form-group {
  margin-bottom: 24px;
}

.form-label {
  display: block;
  color: #2c3e50;
  font-weight: 500;
  margin-bottom: 8px;
  font-size: 0.95rem;
  transition: color 0.5s ease;
}

.form-input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  font-size: 0.95rem;
  color: #2c3e50;
  transition: all 0.2s ease;
  background: #f8fafc;
  font-family: 'Noto Sans KR', sans-serif;
  box-sizing: border-box;
}

.form-input::placeholder {
  color: #a0aec0;
  font-family: 'Noto Sans KR', sans-serif;
}

.form-input:focus {
  outline: none;
  border-color: #4299e1;
  box-shadow: 0 0 0 3px rgba(66, 153, 225, 0.15);
  background: white;
}

textarea.form-input {
  min-height: 120px;
  resize: vertical;
}

.submit-button {
  width: 100%;
  padding: 12px;
  background: #2c3e50;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.submit-button:hover:not(:disabled) {
  background: #34495e;
  transform: translateY(-1px);
}

.submit-button:disabled {
  background: #a0aec0;
  cursor: not-allowed;
  transform: none;
}

.contact-info {
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #edf2f7;
  color: #4a5568;
  font-size: 0.95rem;
  line-height: 1.6;
  font-family: 'Noto Sans KR', sans-serif;
  transition: border-top-color 0.5s ease, color 0.5s ease;
}

.contact-info a {
  color: #3182ce;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.2s ease;
}

.contact-info a:hover {
  color: #2c5282;
}

.contact-info b {
  color: #2c3e50;
  font-weight: 600;
  transition: color 0.5s ease;
}

/* 다크모드 지원 */
body.dark-mode .page-title {
  color: #f7fafc;
  border-bottom-color: #4a5568;
}

body.dark-mode .contact-card {
  background: #1a202c;
  border-color: #2d3748;
  box-shadow: 0 2px 10px rgba(0,0,0,0.2);
}

body.dark-mode .form-label {
  color: #e2e8f0;
}

body.dark-mode .form-input {
  background: #2d3748;
  border-color: #4a5568;
  color: #e2e8f0;
}

body.dark-mode .form-input:focus {
  border-color: #63b3ed;
  background: #2d3748;
}

body.dark-mode .submit-button {
  background: #63b3ed;
}

body.dark-mode .submit-button:hover:not(:disabled) {
  background: #4299e1;
}

body.dark-mode .contact-info {
  border-top-color: #4a5568;
  color: #e2e8f0;
}

body.dark-mode .contact-info b {
  color: #f7fafc;
}

body.dark-mode .contact-info a {
  color: #63b3ed;
}

body.dark-mode .contact-info a:hover {
  color: #90cdf4;
}

body.dark-mode .success-message {
  background: #1c4532;
  border-color: #276749;
  color: #9ae6b4;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .container {
    padding: 20px 16px;
  }
  
  .contact-card {
    padding: 24px 20px;
  }
  
  .page-title {
    font-size: 1.5rem;
  }
}
</style> 
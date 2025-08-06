<template>
  <div class="container">
    <div class="contact-main">
      <h1 class="page-title">{{ lang === 'ja' ? 'Contact Me' : 'Contact Me' }}</h1>
      <div class="contact-card">
        <div v-if="successMessage" class="success-message">
          <span>{{ successMessage }}</span>
        </div>
        <form @submit.prevent="submitForm">
          <div class="form-group">
            <label class="form-label" for="name">{{ lang === 'ja' ? 'お名前' : '이름' }}</label>
            <input 
              type="text" 
              id="name" 
              v-model="formData.name" 
              class="form-input" 
              required
              :placeholder="lang === 'ja' ? 'お名前を入力してください' : '이름을 입력해주세요'"
            >
          </div>
          <div class="form-group">
            <label class="form-label" for="email">{{ lang === 'ja' ? 'メールアドレス' : '이메일' }}</label>
            <input 
              type="email" 
              id="email" 
              v-model="formData.email" 
              class="form-input" 
              required
              :placeholder="lang === 'ja' ? 'example@email.com' : 'example@email.com'"
            >
          </div>
          <div class="form-group">
            <label class="form-label" for="subject">{{ lang === 'ja' ? '件名' : '제목' }}</label>
            <input 
              type="text" 
              id="subject" 
              v-model="formData.subject" 
              class="form-input" 
              required
              :placeholder="lang === 'ja' ? '件名を入力してください' : '제목을 입력해주세요'"
            >
          </div>
          <div class="form-group">
            <label class="form-label" for="message">{{ lang === 'ja' ? 'お問い合わせ内容' : '문의 내용' }}</label>
            <textarea 
              id="message" 
              v-model="formData.message" 
              class="form-input" 
              required
              :placeholder="lang === 'ja' ? 'お問い合わせ内容を入力してください' : '문의 내용을 입력해주세요'"
            ></textarea>
          </div>
          <button type="submit" class="submit-button" :disabled="isSubmitting">
            {{ isSubmitting ? (lang === 'ja' ? '送信中...' : '전송 중...') : (lang === 'ja' ? '送信する' : '문의 보내기') }}
          </button>
        </form>
        <div class="contact-info">
          <span>{{ lang === 'ja' ? 'お問い合わせいただいた内容は私に届きます。メールアドレスを添付していただければ、できるだけ早くお返事いたします。' : '문의하신 내용은 저에게 전달됩니다. 메일 주소 첨부시 빠른 시일 내에 답변드리겠습니다.' }}</span><br>
          <b>{{ lang === 'ja' ? 'メールアドレス:' : '이메일:' }}</b> 
          <a href="mailto:ehc28260@gmail.com">ehc28260@gmail.com</a>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ContactView',
  data() {
    return {
      lang: 'ko',
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
  mounted() {
    this.loadSettings();
  },
  methods: {
    async loadSettings() {
      try {
        // 실제 백엔드 설정 API 호출
        // const response = await fetch('/api/settings');
        // const settings = await response.json();
        // this.lang = settings.language || 'ko';
        
        // 현재는 기본값 사용
        this.lang = 'ko';
      } catch (error) {
        console.error('설정 로드 실패:', error);
      }
    },
    async submitForm() {
      this.isSubmitting = true;
      
      try {
        // 실제 백엔드 API 호출
        const response = await fetch('/api/contact', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(this.formData)
        });
        
        if (response.ok) {
          this.successMessage = this.lang === 'ja' ? 
            'お問い合わせを送信しました。ありがとうございます。' : 
            '문의가 성공적으로 전송되었습니다. 감사합니다.';
          
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
          throw new Error('전송 실패');
        }
      } catch (error) {
        console.error('문의 전송 실패:', error);
        this.successMessage = this.lang === 'ja' ? 
          '送信に失敗しました。もう一度お試しください。' : 
          '전송에 실패했습니다. 다시 시도해주세요.';
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
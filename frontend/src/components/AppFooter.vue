<template>
  <footer class="site-footer">
    <div class="footer-content">
      <div class="footer-sns" style="margin-bottom: 12px;">
        <a href="https://github.com/Hyuk0926" target="_blank" class="sns-btn" title="Github">
          <svg width="24" height="24" fill="currentColor" viewBox="0 0 24 24">
            <circle cx="12" cy="12" r="12" fill="#f1f3f6"/>
            <path d="M12 2C6.48 2 2 6.58 2 12.26c0 4.48 2.87 8.28 6.84 9.63.5.09.68-.22.68-.48 0-.24-.01-.87-.01-1.7-2.78.62-3.37-1.36-3.37-1.36-.45-1.18-1.1-1.5-1.1-1.5-.9-.63.07-.62.07-.62 1 .07 1.53 1.05 1.53 1.05.89 1.56 2.34 1.11 2.91.85.09-.66.35-1.11.63-1.37-2.22-.26-4.56-1.14-4.56-5.07 0-1.12.39-2.03 1.03-2.75-.1-.26-.45-1.3.1-2.7 0 0 .84-.28 2.75 1.05A9.38 9.38 0 0 1 12 6.84c.85.004 1.71.12 2.51.35 1.91-1.33 2.75-1.05 2.75-1.05.55 1.4.2 2.44.1 2.7.64.72 1.03 1.63 1.03 2.75 0 3.94-2.34 4.8-4.57 5.06.36.32.68.94.68 1.9 0 1.37-.01 2.47-.01 2.8 0 .26.18.57.69.48A10.01 10.01 0 0 0 22 12.26C22 6.58 17.52 2 12 2Z" fill="#333"/>
          </svg>
        </a>
        <a href="https://x.com/_TaKa_YuuKi" target="_blank" class="sns-btn" title="X(Twitter)">
          <svg width="24" height="24" fill="currentColor" viewBox="0 0 24 24">
            <circle cx="12" cy="12" r="12" fill="#f1f3f6"/>
            <path d="M17.53 7.47h-1.41l-3.12 3.97-3.12-3.97H6.47l3.97 5.03-4.13 5.5h1.41l3.28-4.37 3.28 4.37h1.41l-4.13-5.5 3.97-5.03z" fill="#222"/>
          </svg>
        </a>
      </div>
      <div style="color:#888; font-size:0.98rem; margin-bottom:6px;">
        <span>{{ $i18n.locale === 'ja' ? 'ブログにご訪問いただき、ありがとうございます。' : '블로그에 방문해주셔서 감사합니다.' }}</span>
        <span style="font-size:0.93em;">{{ $i18n.locale === 'ja' ? 'お問い合わせ、ご提案、コラボレーションはいつでもお気軽にご連絡ください。' : '문의, 제안, 협업은 언제든 연락 주세요.' }}</span>
      </div>
      <div style="color:#aaa; font-size:0.95rem; margin-bottom: 35px;">
        <span>{{ currentYear }}</span> <span>© {{ $i18n.locale === 'ja' ? '高原優輝' : 'Takahara Yuuki' }}. All rights reserved.</span>
      </div>
      <div class="admin-link" style="margin-top: 16px;">
        <router-link to="/admin/login" class="admin-button">{{ $i18n.locale === 'ja' ? '管理者ログイン' : '관리자 로그인' }}</router-link>
      </div>
    </div>
  </footer>
</template>

<script>
export default {
  name: 'AppFooter',
  data() {
    return {
      isAdmin: false // 실제로는 사용자 상태에 따라 결정
    }
  },
  computed: {
    currentYear() {
      return new Date().getFullYear();
    }
  }
}
</script>

<style scoped>
.site-footer {
  background: #f8f9fa;
  border-top: 1px solid #e9ecef;
  padding: 2rem 0;
  margin-top: auto;
}

.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
  text-align: center;
}

.footer-sns {
  display: flex;
  justify-content: center;
  gap: 1rem;
}

.sns-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: white;
  border: 1px solid #e9ecef;
  transition: all 0.3s ease;
  text-decoration: none;
}

.sns-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.admin-link {
  margin-top: 1rem;
}

.admin-button {
  display: inline-block;
  padding: 0.5rem 1rem;
  background: #667eea;
  color: white;
  text-decoration: none;
  border-radius: 6px;
  font-size: 0.9rem;
  transition: background-color 0.2s ease;
}

.admin-button:hover {
  background: #5a67d8;
}

/* 다크모드 스타일 */
body.dark-mode .site-footer {
  background: #2d3748;
  border-top-color: #4a5568;
}

body.dark-mode .sns-btn {
  background: #4a5568;
  border-color: #4a5568;
}

body.dark-mode .sns-btn:hover {
  background: #5a67d8;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .footer-content {
    padding: 0 1rem;
  }
  
  .footer-sns {
    gap: 0.75rem;
  }
  
  .sns-btn {
    width: 36px;
    height: 36px;
  }
}
</style>

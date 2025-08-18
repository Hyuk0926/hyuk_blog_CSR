<template>
    <div class="home-container">
      <div class="home-title">{{ $i18n.locale === 'ja' ? '高原のブログ' : 'Takahara\'s Blog' }}</div>
      <div class="home-subtitle">{{ $i18n.locale === 'ja' ? '不可能はない。' : 'Nothing is Impossible' }}</div>
      <div class="home-footer">{{ $i18n.locale === 'ja' ? '© 2025 高原優輝. All rights reserved.' : '© 2025 Takahara Yuuki. All rights reserved.' }}</div>
    </div>
  </template>
  
  <script>
export default {
  name: 'HomeView',
  mounted() {
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
  /* HomeView.vue에만 적용되는 스타일 */
  .home-container {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    text-align: center;
    min-height: calc(100vh - 64px); /* 헤더 높이를 뺀 나머지 전체 높이 */
  }
  .home-title {
    font-family: 'Montserrat', sans-serif;
    font-size: 3rem;
    font-weight: 700;
    color: #222;
    margin-bottom: 18px;
    letter-spacing: 2px;
    transition: color 0.5s ease;
  }
  .home-subtitle {
    font-size: 1.3rem;
    color: #555;
    margin-bottom: 40px;
    transition: color 0.5s ease;
  }
  .home-footer {
    margin-top: 60px;
    color: #222;
    font-size: 0.95rem;
    transition: color 0.5s ease;
  }
  
  @media (max-width: 600px) {
    .home-title { font-size: 2rem; }
  }


    
  </style>
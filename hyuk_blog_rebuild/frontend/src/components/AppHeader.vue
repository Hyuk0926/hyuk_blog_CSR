<template>
    <header class="navbar">
      <div class="container header-flex">
        <router-link :to="homeLink" class="navbar-brand">{{ $i18n.locale === 'ja' ? '高原のブログ' : 'Takahara\'s Blog' }}</router-link>
        <div class="header-right">
          <nav>
            <ul class="navbar-nav">
              <li class="nav-item"><router-link :to="getLink('/knowledge')" class="nav-link">{{ $t('nav.knowledge') }}</router-link></li>
              <li class="nav-item"><router-link :to="getLink('/projects')" class="nav-link">{{ $t('nav.projects') }}</router-link></li>
              <li class="nav-item"><router-link :to="getLink('/resume')" class="nav-link">{{ $t('nav.resume') }}</router-link></li>
              <li class="nav-item"><router-link :to="getLink('/contact')" class="nav-link">{{ $t('nav.contact') }}</router-link></li>
            </ul>
          </nav>
          <div class="header-controls">
            <div v-if="user" class="auth-controls">
              <span class="user-info">{{ user.nickname }}{{ $i18n.locale === 'ja' ? 'さん' : '님' }}</span>
              <a @click.prevent="handleLogout" href="#" class="auth-btn logout-btn">{{ $t('nav.logout') }}</a>
            </div>
            <div v-else class="auth-controls">
              <router-link to="/user/login" class="auth-btn login-btn">{{ $t('nav.login') }}</router-link>
              <router-link to="/user/register" class="auth-btn register-btn">{{ $t('nav.register') }}</router-link>
            </div>
            <button @click="toggleDarkMode" id="darkModeToggle" class="control-btn dark-mode-btn" title="다크모드 전환">
              <span class="dark-icon" :style="{ display: isDarkMode ? 'none' : 'block' }">
                <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 12.79A9 9 0 1 1 11.21 3a7 7 0 0 0 9.79 9.79z"></path></svg>
              </span>
              <span class="light-icon" :style="{ display: isDarkMode ? 'block' : 'none' }">
                <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="5"></circle><line x1="12" y1="1" x2="12" y2="3"></line><line x1="12" y1="21" x2="12" y2="23"></line><line x1="4.22" y1="4.22" x2="5.64" y2="5.64"></line><line x1="18.36" y1="18.36" x2="19.78" y2="19.78"></line><line x1="1" y1="12" x2="3" y2="12"></line><line x1="21" y1="12" x2="23" y2="12"></line><line x1="4.22" y1="19.78" x2="5.64" y2="18.36"></line><line x1="18.36" y1="5.64" x2="19.78" y2="4.22"></line></svg>
              </span>
            </button>
            <div id="langToggle" class="lang-toggle">
              <a @click.prevent="setLang('ko')" href="#" class="lang-btn" :class="{ active: $i18n.locale === 'ko' }">KO</a>
              <span class="lang-separator">|</span>
              <a @click.prevent="setLang('ja')" href="#" class="lang-btn" :class="{ active: $i18n.locale === 'ja' }">JP</a>
            </div>
          </div>
        </div>
      </div>
    </header>
  </template>
  
  <script>
import { inject, ref } from 'vue'

export default {
  name: 'AppHeader',
  setup() {
    const auth = inject('auth')
    const isDarkMode = ref(false)
    
    return {
      user: auth.user,
      isAuthenticated: auth.isAuthenticated,
      isDarkMode,
      logout: auth.logout
    }
  },
    computed: {
      homeLink() {
        return `/?lang=${this.$i18n.locale}`;
      }
    },
    methods: {
      setDarkMode(enabled) {
        this.isDarkMode = enabled;
        if (enabled) {
          document.body.classList.add('dark-mode');
          localStorage.setItem('darkMode', 'true');
        } else {
          document.body.classList.remove('dark-mode');
          localStorage.setItem('darkMode', 'false');
        }
      },
      toggleDarkMode() {
        this.setDarkMode(!this.isDarkMode);
      },
      setLang(language) {
        this.$i18n.locale = language;
        localStorage.setItem('language', language);
        console.log(`Language set to: ${language}`);
        
        // 언어 변경 이벤트 발생
        this.$emit('language-changed', language);
      },
      getLink(path) {
        return `${path}?lang=${this.$i18n.locale}`;
      },
      async handleLogout() {
        try {
          this.logout();
          this.$router.push('/');
        } catch (error) {
          console.error('Logout error:', error);
        }
      },
      // 사용자 정보는 전역 상태에서 관리되므로 별도 메서드 불필요
      // 사용자 정보는 전역 상태에서 관리되므로 별도 메서드 불필요
    },
    mounted() {
      const darkModeSaved = localStorage.getItem('darkMode');
      this.setDarkMode(darkModeSaved === 'true');
    },
    // 인터벌이 없으므로 정리 불필요
    // 전역 상태에서 관리되므로 라우터 변경 시 별도 처리 불필요
  }
  </script>
  
  <style scoped>
  /* 폰트 스타일 */
  .navbar-brand { font-family: 'Montserrat', 'Noto Sans KR', sans-serif; }
  html[lang='ja'] .navbar-brand { font-family: 'Montserrat', 'Noto Sans JP', sans-serif; }
  .navbar { background: #ffffff; box-shadow: 0 1px 3px rgba(0,0,0,0.05); padding: 0; width: 100%; position: static; z-index: 1000; border-bottom: 1px solid #e2e8f0; }
  .navbar .container { display: flex; align-items: center; justify-content: space-between; height: 64px; width: 100%; margin: 0 auto; padding: 0 40px; max-width: 1400px; }
  .header-flex { display: flex; justify-content: space-between; align-items: center; width: 100%; }
  .header-right { display: flex; align-items: center; gap: 32px; }
  .navbar-brand { font-family: 'Playfair Display', serif; font-size: 1.75rem; font-weight: 700; color: #1a202c; text-decoration: none; transition: color 0.3s ease; }
  .navbar-brand:hover { color: #1a202c; }
  .navbar-nav { display: flex; align-items: center; gap: 28px; margin: 0; padding: 0; list-style: none; }
  .nav-link { color: #4a5568; font-size: 1rem; font-weight: 500; text-decoration: none; padding: 6px 0; border-bottom: 2px solid transparent; transition: color 0.2s, border-color 0.2s; }
  .nav-link:hover, .nav-link:focus { color: #1a202c; border-bottom-color: #2c3e50; }
  .header-controls { display: flex; align-items: center; gap: 12px; }
  .auth-controls { display: flex; align-items: center; gap: 8px; }
  .user-info { font-size: 0.9rem; color: #4a5568; font-weight: 500; }
  .auth-btn { padding: 6px 12px; border-radius: 6px; text-decoration: none; font-size: 0.85rem; font-weight: 500; transition: all 0.2s ease; }
  .login-btn { color: #4a5568; background: #f1f5f9; }
  .login-btn:hover { background: #e2e8f0; color: #1a202c; }
  .register-btn { color: #ffffff; background: #667eea; }
  .register-btn:hover { background: #5a67d8; transform: translateY(-1px); }
  .logout-btn { color: #dc3545; background: #fee; }
  .logout-btn:hover { background: #fcc; color: #c82333; }
  .control-btn { background: none; border: none; cursor: pointer; padding: 8px; border-radius: 50%; transition: background-color 0.2s ease; display: flex; align-items: center; justify-content: center; color: #4a5568; }
  .control-btn:hover { background-color: #f1f5f9; }
  .dark-mode-btn { width: 40px; height: 40px; }
  .lang-toggle { display: flex; align-items: center; background: #f1f5f9; border-radius: 20px; padding: 4px; font-size: 0.85rem; font-weight: 600; }
  .lang-btn { padding: 4px 10px; border-radius: 16px; cursor: pointer; transition: all 0.2s ease; color: #4a5568; text-decoration: none; }
  .lang-btn.active { background: #ffffff; color: #1a202c; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
  .lang-separator { color: #cbd5e1; margin: 0 2px; }
  @media (max-width: 768px) {
    .navbar .container { flex-direction: column; height: auto; padding: 12px 20px; }
    .header-right { flex-direction: column; align-items: center; gap: 16px; width: 100%; margin-top: 12px; }
    .navbar-nav { gap: 16px; justify-content: center; width: 100%; }
  }
  </style>
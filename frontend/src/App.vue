<template>
  <div class="app-container">
    <AppHeader v-if="!isAuthPage" />
    <main class="main-content">
      <router-view/>
    </main>
    <AppFooter v-if="!isHomePage && !isAuthPage" />
  </div>
</template>

<script>
import { provide, onMounted } from 'vue'
import AppHeader from './components/AppHeader.vue';
import AppFooter from './components/AppFooter.vue';
import { useAuth } from './composables/useAuth.js';

export default {
  name: 'App',
  components: {
    AppHeader,
    AppFooter
  },
  setup() {
    const { user, isAuthenticated, initializeAuth } = useAuth()
    
    // 전역 상태 제공
    provide('auth', {
      user,
      isAuthenticated,
      ...useAuth()
    })
    
    // 앱 시작 시 인증 상태 초기화
    onMounted(async () => {
      await initializeAuth()
    })
    
    return {
      user,
      isAuthenticated
    }
  },
  computed: {
    isHomePage() {
      return this.$route.path === '/';
    },
    isAuthPage() {
      // 로그인, 회원가입, 관리자 관련 페이지에서는 헤더/푸터 숨김
      const authPaths = ['/user/login', '/user/register', '/admin/login', '/admin/dashboard', '/admin/posts/new', '/admin/posts/edit', '/admin/resume', '/admin/inquiries'];
      return authPaths.some(path => this.$route.path.startsWith(path));
    }
  }
}
</script>

<style>
/* 전역 폰트 및 다크모드 스타일 */
@import url('https://fonts.googleapis.com/css2?family=Montserrat:wght@700&family=Noto+Sans+KR:wght@400;700&display=swap');
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@700&display=swap');

body {
  font-family: 'Montserrat', 'Noto Sans KR', sans-serif;
}

/* 다크모드 - 네비게이션바 스타일 */
body.dark-mode .navbar { background: #1a202c; border-bottom-color: #2d3748; }
body.dark-mode .navbar-brand { color: #e2e8f0; }
body.dark-mode .navbar-brand:hover { color: #f0f0f0ee; }
body.dark-mode .nav-link { color: #a0aec0; }
body.dark-mode .nav-link:hover,
body.dark-mode .nav-link:focus { color: #e2e8f0; border-bottom-color: #63b3ed; }
body.dark-mode .control-btn { color: #a0aec0; }
body.dark-mode .control-btn:hover { background-color: #2d3748; }
body.dark-mode .lang-toggle { background: #2d3748; }
body.dark-mode .lang-btn { color: #a0aec0; }
body.dark-mode .lang-btn.active { background: #1a202c; color: #e2e8f0; }
body.dark-mode .lang-separator { color: #4a5568; }
body.dark-mode .light-icon svg { stroke: #e2e8f0; }
body.dark-mode .user-info { color: #a0aec0; }
body.dark-mode .login-btn { color: #a0aec0; background: #2d3748; }
body.dark-mode .login-btn:hover { background: #4a5568; color: #e2e8f0; }
body.dark-mode .register-btn { color: #ffffff; background: #667eea; }
body.dark-mode .register-btn:hover { background: #5a67d8; }
body.dark-mode .logout-btn { color: #f56565; background: #742a2a; }
body.dark-mode .logout-btn:hover { background: #9b2c2c; color: #fed7d7; }

/* 다크모드 - 홈 화면 스타일 */
body.dark-mode .home-title { color: #fff; }
body.dark-mode .home-subtitle { color: #e0e0e0; }
body.dark-mode .home-footer { color: #e0e0e0; }

/* 앱 컨테이너 스타일 */
.app-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-content {
  flex: 1;
}
</style>
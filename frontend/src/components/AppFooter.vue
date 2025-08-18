<template>
  <footer class="bg-gray-50 border-t border-gray-200 py-8 mt-auto dark:bg-gray-800 dark:border-gray-700">
    <div class="max-w-6xl mx-auto px-8 text-center">
      <div class="flex justify-center gap-4 mb-3">
        <a href="https://github.com/Hyuk0926" target="_blank" class="inline-flex items-center justify-center w-10 h-10 rounded-full bg-white border border-gray-200 transition-all duration-300 hover:-translate-y-0.5 hover:shadow-lg dark:bg-gray-700 dark:border-gray-600 dark:hover:bg-indigo-600" title="Github">
          <svg width="24" height="24" fill="currentColor" viewBox="0 0 24 24">
            <circle cx="12" cy="12" r="12" fill="#f1f3f6"/>
            <path d="M12 2C6.48 2 2 6.58 2 12.26c0 4.48 2.87 8.28 6.84 9.63.5.09.68-.22.68-.48 0-.24-.01-.87-.01-1.7-2.78.62-3.37-1.36-3.37-1.36-.45-1.18-1.1-1.5-1.1-1.5-.9-.63.07-.62.07-.62 1 .07 1.53 1.05 1.53 1.05.89 1.56 2.34 1.11 2.91.85.09-.66.35-1.11.63-1.37-2.22-.26-4.56-1.14-4.56-5.07 0-1.12.39-2.03 1.03-2.75-.1-.26-.45-1.3.1-2.7 0 0 .84-.28 2.75 1.05A9.38 9.38 0 0 1 12 6.84c.85.004 1.71.12 2.51.35 1.91-1.33 2.75-1.05 2.75-1.05.55 1.4.2 2.44.1 2.7.64.72 1.03 1.63 1.03 2.75 0 3.94-2.34 4.8-4.57 5.06.36.32.68.94.68 1.9 0 1.37-.01 2.47-.01 2.8 0 .26.18.57.69.48A10.01 10.01 0 0 0 22 12.26C22 6.58 17.52 2 12 2Z" fill="#333"/>
          </svg>
        </a>
        <a href="https://x.com/_TaKa_YuuKi" target="_blank" class="inline-flex items-center justify-center w-10 h-10 rounded-full bg-white border border-gray-200 transition-all duration-300 hover:-translate-y-0.5 hover:shadow-lg dark:bg-gray-700 dark:border-gray-600 dark:hover:bg-indigo-600" title="X(Twitter)">
          <svg width="24" height="24" fill="currentColor" viewBox="0 0 24 24">
            <circle cx="12" cy="12" r="12" fill="#f1f3f6"/>
            <path d="M17.53 7.47h-1.41l-3.12 3.97-3.12-3.97H6.47l3.97 5.03-4.13 5.5h1.41l3.28-4.37 3.28 4.37h1.41l-4.13-5.5 3.97-5.03z" fill="#222"/>
          </svg>
        </a>
      </div>
      <div class="text-gray-500 text-base mb-1.5">
        <span>{{ $i18n.locale === 'ja' ? 'ブログにご訪問いただき、ありがとうございます。' : '블로그에 방문해주셔서 감사합니다.' }}</span>
        <span class="text-sm">{{ $i18n.locale === 'ja' ? 'お問い合わせ、ご提案、コラボレーションはいつでもお気軽にご連絡ください。' : '문의, 제안, 협업은 언제든 연락 주세요.' }}</span>
      </div>
      <div class="text-gray-400 text-sm mb-9">
        <span>{{ currentYear }}</span> <span>© {{ $i18n.locale === 'ja' ? '高原優輝' : 'Takahara Yuuki' }}. All rights reserved.</span>
      </div>
      <!-- 관리자 로그인 버튼은 관리자가 로그인한 상태에서만 표시 -->
      <div v-if="isAdminLoggedIn" class="mt-4">
        <router-link to="/admin/dashboard" class="inline-block px-4 py-2 bg-indigo-600 text-white text-sm rounded-md transition-colors duration-200 hover:bg-indigo-700">
          {{ $i18n.locale === 'ja' ? '管理者ダッシュボード' : '관리자 대시보드' }}
        </router-link>
      </div>
    </div>
  </footer>
</template>

<script>
import { inject } from 'vue'

export default {
  name: 'AppFooter',
  setup() {
    const auth = inject('auth')
    return {
      auth
    }
  },
  computed: {
    currentYear() {
      return new Date().getFullYear();
    },
    isAdminLoggedIn() {
      // 관리자 권한 확인
      const userRole = localStorage.getItem('userRole')
      const username = localStorage.getItem('username')
      
      // userRole이 ROLE_ADMIN이거나 username이 admin/admin_jp인 경우 관리자로 판단
      // null 체크를 추가하여 로그인되지 않은 상태에서는 false 반환
      return (userRole === 'ROLE_ADMIN' || username === 'admin' || username === 'admin_jp') && 
             userRole !== null && username !== null
    }
  }
}
</script>

<style scoped>
/* Tailwind CSS로 모든 스타일링을 처리하므로 추가 CSS는 불필요 */
/* 반응형 디자인은 Tailwind의 반응형 클래스로 처리됨 */
</style>

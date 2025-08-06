import { createI18n } from 'vue-i18n'
import ko from './locales/ko.js'
import ja from './locales/ja.js'

const messages = {
  ko,
  ja
}

const i18n = createI18n({
  legacy: false, // Vue 3 Composition API 지원
  locale: localStorage.getItem('language') || 'ko', // 기본 언어
  fallbackLocale: 'ko', // 폴백 언어
  messages
})

export default i18n 
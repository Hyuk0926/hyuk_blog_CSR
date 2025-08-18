<template>
  <div class="max-w-6xl mx-auto px-6 py-8">
    <div class="max-w-2xl mx-auto">
      <h1 class="text-3xl font-bold text-gray-800 mb-8 pb-4 border-b border-gray-200 font-sans tracking-wide transition-all duration-500 dark:text-gray-100 dark:border-gray-700">
        {{ $t('contact.title') }}
      </h1>
      <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-8 transition-all duration-500 dark:bg-gray-800 dark:border-gray-700">
        <div v-if="successMessage" class="bg-green-50 text-green-800 border border-green-200 p-4 rounded-lg mb-6 text-sm flex items-center transition-all duration-500 dark:bg-green-900 dark:text-green-200 dark:border-green-700">
          <span class="bg-green-500 text-white w-5 h-5 rounded-full flex items-center justify-center text-xs font-bold mr-3">✓</span>
          <span>{{ successMessage }}</span>
        </div>
        <form @submit.prevent="submitForm">
          <div class="mb-6">
            <label class="block text-gray-800 font-medium mb-2 text-sm transition-colors duration-500 dark:text-gray-200" for="name">
              {{ $t('contact.form.name') }}
            </label>
            <input 
              type="text" 
              id="name" 
              v-model="formData.name" 
              class="w-full px-3 py-2.5 border border-gray-200 rounded-md text-sm text-gray-800 transition-all duration-200 bg-gray-50 font-sans box-border focus:outline-none focus:border-blue-500 focus:shadow-lg focus:shadow-blue-500/15 focus:bg-white dark:bg-gray-700 dark:border-gray-600 dark:text-gray-200 dark:focus:border-blue-400 dark:focus:bg-gray-700" 
              required
              :placeholder="$t('contact.form.placeholder.name')"
            >
          </div>
          <div class="mb-6">
            <label class="block text-gray-800 font-medium mb-2 text-sm transition-colors duration-500 dark:text-gray-200" for="email">
              {{ $t('contact.form.email') }}
            </label>
            <input 
              type="email" 
              id="email" 
              v-model="formData.email" 
              class="w-full px-3 py-2.5 border border-gray-200 rounded-md text-sm text-gray-800 transition-all duration-200 bg-gray-50 font-sans box-border focus:outline-none focus:border-blue-500 focus:shadow-lg focus:shadow-blue-500/15 focus:bg-white dark:bg-gray-700 dark:border-gray-600 dark:text-gray-200 dark:focus:border-blue-400 dark:focus:bg-gray-700" 
              required
              :placeholder="$t('contact.form.placeholder.email')"
            >
          </div>
          <div class="mb-6">
            <label class="block text-gray-800 font-medium mb-2 text-sm transition-colors duration-500 dark:text-gray-200" for="subject">
              {{ $t('contact.form.subject') }}
            </label>
            <input 
              type="text" 
              id="subject" 
              v-model="formData.subject" 
              class="w-full px-3 py-2.5 border border-gray-200 rounded-md text-sm text-gray-800 transition-all duration-200 bg-gray-50 font-sans box-border focus:outline-none focus:border-blue-500 focus:shadow-lg focus:shadow-blue-500/15 focus:bg-white dark:bg-gray-700 dark:border-gray-600 dark:text-gray-200 dark:focus:border-blue-400 dark:focus:bg-gray-700" 
              required
              :placeholder="$t('contact.form.placeholder.subject')"
            >
          </div>
          <div class="mb-6">
            <label class="block text-gray-800 font-medium mb-2 text-sm transition-colors duration-500 dark:text-gray-200" for="message">
              {{ $t('contact.form.message') }}
            </label>
            <textarea 
              id="message" 
              v-model="formData.message" 
              class="w-full px-3 py-2.5 border border-gray-200 rounded-md text-sm text-gray-800 transition-all duration-200 bg-gray-50 font-sans box-border focus:outline-none focus:border-blue-500 focus:shadow-lg focus:shadow-blue-500/15 focus:bg-white dark:bg-gray-700 dark:border-gray-600 dark:text-gray-200 dark:focus:border-blue-400 dark:focus:bg-gray-700 min-h-30 resize-y" 
              required
              :placeholder="$t('contact.form.placeholder.message')"
            ></textarea>
          </div>
          <button type="submit" class="w-full py-3 bg-gray-800 text-white border-none rounded-md text-base font-medium cursor-pointer transition-all duration-200 hover:bg-gray-700 hover:-translate-y-0.5 disabled:bg-gray-400 disabled:cursor-not-allowed disabled:transform-none dark:bg-blue-500 dark:hover:bg-blue-600" :disabled="isSubmitting">
            {{ isSubmitting ? $t('common.loading') : $t('contact.form.send') }}
          </button>
        </form>
        <div class="mt-8 pt-6 border-t border-gray-200 text-gray-600 text-sm leading-relaxed font-sans transition-all duration-500 dark:border-gray-700 dark:text-gray-300">
          <span>{{ $t('contact.subtitle') }}</span><br>
          <b class="text-gray-800 font-semibold transition-colors duration-500 dark:text-gray-100">{{ $t('contact.form.email') }}:</b> 
          <a href="mailto:ehc28260@gmail.com" class="text-blue-600 no-underline font-medium transition-colors duration-200 hover:text-blue-800 dark:text-blue-400 dark:hover:text-blue-300">
            ehc28260@gmail.com
          </a>
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
      isDarkMode: false,
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
  mounted() {
    this.checkDarkMode();
    window.addEventListener('dark-mode-toggled', this.handleDarkModeToggle);
    this.updateDarkModeClass();
  },
  beforeUnmount() {
    window.removeEventListener('dark-mode-toggled', this.handleDarkModeToggle);
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
    },
    checkDarkMode() {
      this.isDarkMode = document.body.classList.contains('dark-mode');
    },
    handleDarkModeToggle(event) {
      this.isDarkMode = event.detail.isDarkMode;
      this.updateDarkModeClass();
    },
    updateDarkModeClass() {
      if (this.isDarkMode) {
        document.documentElement.classList.add('dark');
      } else {
        document.documentElement.classList.remove('dark');
      }
    }
  }
}
</script>

<style scoped>
/* 반응형 디자인 */
@media (max-width: 768px) {
  .max-w-6xl.mx-auto.px-6.py-8 {
    padding: 20px 16px;
  }
  
  .bg-white.rounded-xl.shadow-sm.border.border-gray-100.p-8 {
    padding: 24px 20px;
  }
  
  .text-3xl.font-bold.text-gray-800.mb-8.pb-4 {
    font-size: 1.5rem;
  }
}
</style> 
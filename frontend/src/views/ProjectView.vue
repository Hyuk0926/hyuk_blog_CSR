<template>
  <div class="max-w-6xl mx-auto px-6 py-8">
    <!-- 개인 프로젝트 섹션 -->
    <div class="mb-16">
      <h2 class="text-3xl font-bold text-gray-800 mb-8 pb-4 border-b border-gray-200 dark:text-gray-100 dark:border-gray-700">
        {{ $t('projects.personal.title') }}
      </h2>
      <div class="space-y-10">
        <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-8 dark:bg-gray-800 dark:border-gray-700">
          <div class="flex items-start gap-6 mb-6">
            <img class="w-14 h-14 object-contain" src="/img/blog_logo.png" alt="Hyuk Blog 로고" @error="hideImage">
            <div class="flex-1">
              <div class="text-sm text-gray-500 font-medium mb-2 dark:text-gray-400">
                {{ $t('projects.personal.blog.period') }}
              </div>
              <h3 class="text-2xl font-semibold text-gray-800 mb-4 dark:text-gray-100">
                {{ $t('projects.personal.blog.title') }}
              </h3>
              <div class="flex gap-3 mb-6">
                <img class="w-7 h-7 transition-transform duration-200 hover:-translate-y-0.5" src="/svg/vue.svg" alt="Vue" title="Vue">
                <img class="w-7 h-7 transition-transform duration-200 hover:-translate-y-0.5" src="/svg/java.svg" alt="Java" title="Java">
                <img class="w-7 h-7 transition-transform duration-200 hover:-translate-y-0.5" src="/svg/spring.svg" alt="Spring Boot" title="Spring Boot">
                <img class="w-7 h-7 transition-transform duration-200 hover:-translate-y-0.5" src="/svg/mysql.svg" alt="MySQL" title="MySQL">
                <img class="w-7 h-7 transition-transform duration-200 hover:-translate-y-0.5" src="/svg/AWS.svg" alt="AWS" title="AWS" />
              </div>
            </div>
          </div>
          <div class="text-gray-600 leading-relaxed mb-6 dark:text-gray-300">
            <ul class="pl-5 mb-4">
              <li class="mb-2">{{ $t('projects.personal.blog.desc.1') }}</li>
              <li class="mb-2">{{ $t('projects.personal.blog.desc.2') }}</li>
              <li class="mb-2">{{ $t('projects.personal.blog.desc.3') }}</li>
            </ul>
          </div>
          <div class="text-sm text-gray-600 mb-3 leading-relaxed dark:text-gray-300">
            {{ $t('projects.personal.blog.role') }}
          </div>
          <div class="text-sm text-gray-600 mb-3 leading-relaxed dark:text-gray-300">
            {{ $t('projects.personal.blog.result') }}
          </div>
          <div class="mt-6 flex gap-4">
            <a href="https://github.com/hyuk0926/hyuk_blog" target="_blank" class="text-blue-600 font-medium text-sm transition-colors duration-200 hover:text-blue-800 dark:text-blue-400 dark:hover:text-blue-300">
              {{ $t('projects.link.github') }}
            </a>
          </div>
          <!-- 시연 이미지 슬라이더 -->
          <div class="w-full max-w-4xl h-60 overflow-hidden mx-auto mt-8 rounded-xl shadow-sm">
            <div class="flex animate-slideLeft gap-5 px-2.5 hover:pause">
              <img src="/img/blog_demo1.png" alt="Hyuk Blog 시연1" class="w-80 h-60 object-cover rounded-lg flex-shrink-0 transition-transform duration-300 hover:scale-[1.02]">
              <img src="/img/blog_demo2.png" alt="Hyuk Blog 시연2" class="w-80 h-60 object-cover rounded-lg flex-shrink-0 transition-transform duration-300 hover:scale-[1.02]">
              <img src="/img/blog_demo3.png" alt="Hyuk Blog 시연3" class="w-80 h-60 object-cover rounded-lg flex-shrink-0 transition-transform duration-300 hover:scale-[1.02]">
              <img src="/img/blog_demo4.png" alt="Hyuk Blog 시연4" class="w-80 h-60 object-cover rounded-lg flex-shrink-0 transition-transform duration-300 hover:scale-[1.02]">
              <img src="/img/blog_demo5.png" alt="Hyuk Blog 시연5" class="w-80 h-60 object-cover rounded-lg flex-shrink-0 transition-transform duration-300 hover:scale-[1.02]">
              <img src="/img/blog_demo6.png" alt="Hyuk Blog 시연6" class="w-80 h-60 object-cover rounded-lg flex-shrink-0 transition-transform duration-300 hover:scale-[1.02]">
              <img src="/img/blog_demo1.png" alt="Hyuk Blog 시연1" class="w-80 h-60 object-cover rounded-lg flex-shrink-0 transition-transform duration-300 hover:scale-[1.02]">
              <img src="/img/blog_demo2.png" alt="Hyuk Blog 시연2" class="w-80 h-60 object-cover rounded-lg flex-shrink-0 transition-transform duration-300 hover:scale-[1.02]">
              <img src="/img/blog_demo3.png" alt="Hyuk Blog 시연3" class="w-80 h-60 object-cover rounded-lg flex-shrink-0 transition-transform duration-300 hover:scale-[1.02]">
              <img src="/img/blog_demo4.png" alt="Hyuk Blog 시연4" class="w-80 h-60 object-cover rounded-lg flex-shrink-0 transition-transform duration-300 hover:scale-[1.02]">
              <img src="/img/blog_demo5.png" alt="Hyuk Blog 시연5" class="w-80 h-60 object-cover rounded-lg flex-shrink-0 transition-transform duration-300 hover:scale-[1.02]">
              <img src="/img/blog_demo6.png" alt="Hyuk Blog 시연6" class="w-80 h-60 object-cover rounded-lg flex-shrink-0 transition-transform duration-300 hover:scale-[1.02]">
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 팀 프로젝트 섹션 -->
    <div class="mb-16">
      <h2 class="text-3xl font-bold text-gray-800 mb-8 pb-4 border-b border-gray-200 dark:text-gray-100 dark:border-gray-700">
        {{ $t('projects.team.title') }}
      </h2>
      <div class="space-y-10">
        <div class="bg-white rounded-xl shadow-sm border border-gray-100 p-8 dark:bg-gray-800 dark:border-gray-700">
          <div class="flex items-start gap-6 mb-6">
            <img class="w-14 h-14 object-contain" src="/img/team_logo.png" alt="TeamCollab 로고" @error="hideImage">
            <div class="flex-1">
              <div class="text-sm text-gray-500 font-medium mb-2 dark:text-gray-400">
                {{ $t('projects.team.collab.period') }}
              </div>
              <h3 class="text-2xl font-semibold text-gray-800 mb-4 dark:text-gray-100">
                {{ $t('projects.team.collab.title') }}
              </h3>
              <div class="flex gap-3 mb-6">
                <img class="w-7 h-7 transition-transform duration-200 hover:-translate-y-0.5" src="/svg/react.svg" alt="React" title="React" />
                <img class="w-7 h-7 transition-transform duration-200 hover:-translate-y-0.5" src="/svg/java.svg" alt="Java" title="Java">
                <img class="w-7 h-7 transition-transform duration-200 hover:-translate-y-0.5" src="/svg/spring.svg" alt="Spring Boot" title="Spring Boot">
                <img class="w-7 h-7 transition-transform duration-200 hover:-translate-y-0.5" src="/svg/mysql.svg" alt="MySQL" title="MySQL">
                <img class="w-7 h-7 transition-transform duration-200 hover:-translate-y-0.5" src="/svg/AWS.svg" alt="AWS" title="AWS" />
              </div>
            </div>
          </div>
          <div class="text-gray-600 leading-relaxed mb-6 dark:text-gray-300">
            <ul class="pl-5 mb-4">
              <li class="mb-2">{{ $t('projects.team.collab.desc.1') }}</li>
              <li class="mb-2">{{ $t('projects.team.collab.desc.2') }}</li>
            </ul>
          </div>
          <div class="text-sm text-gray-600 mb-3 leading-relaxed dark:text-gray-300">
            {{ $t('projects.team.collab.role') }}
          </div>
          <div class="text-sm text-gray-600 mb-3 leading-relaxed dark:text-gray-300">
            {{ $t('projects.team.collab.result') }}
          </div>
          <div class="mt-6 flex gap-4">
            <a href="#" target="_blank" class="text-blue-600 font-medium text-sm transition-colors duration-200 hover:text-blue-800 dark:text-blue-400 dark:hover:text-blue-300">
              {{ $t('projects.link.github') }}
            </a>
          </div>
          <div class="text-sm text-gray-500 mt-4 pt-4 border-t border-gray-100 dark:text-gray-400 dark:border-gray-700">
            {{ $t('projects.team.collab.info') }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ProjectView',
  data() {
    return {
      isDarkMode: false
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
    hideImage(event) {
      event.target.style.display = 'none';
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
/* 슬라이더 애니메이션만 유지 */
@keyframes slideLeft {
  0% { transform: translateX(0); }
  100% { transform: translateX(calc(-340px * 6)); }
}

.animate-slideLeft {
  animation: slideLeft 40s linear infinite;
}

.hover\:pause:hover {
  animation-play-state: paused;
}
</style> 
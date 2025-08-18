<template>
  <div>
    <!-- Hero Section -->
    <div class="h-96 relative bg-cover bg-center bg-no-repeat" style="background-image: url('/img/light_mode_bg.jpg');">
      <div class="absolute inset-0 bg-black bg-opacity-20"></div>
      <div class="relative z-10 flex items-center justify-center h-full">
        <h1 class="text-5xl font-bold text-white tracking-wider">
          {{ $i18n.locale === 'ja' ? '高原優輝' : 'Takahara Yuuki' }}
        </h1>
      </div>
    </div>
    
    <!-- Main Container -->
    <div class="max-w-7xl mx-auto px-6 py-8">
      <div class="flex gap-8">
        <!-- Sidebar -->
        <aside class="w-56 flex-shrink-0">
          <div class="sticky top-24">
            <h3 class="text-lg font-bold text-gray-800 mb-4 pb-3 border-b-2 border-gray-800 dark:text-gray-200 dark:border-gray-700">
              {{ $t('knowledge.categories.all') }}
            </h3>
            <ul class="space-y-1">
              <li 
                class="px-3 py-3 cursor-pointer text-gray-600 font-medium rounded-md transition-colors duration-200 hover:bg-gray-100 dark:text-gray-300 dark:hover:bg-gray-700"
                :class="{ 'text-blue-600 bg-blue-50 font-bold dark:text-blue-400 dark:bg-gray-700': selectedCategory === '' }"
                @click="selectCategory('')"
              >
                {{ $t('knowledge.categories.all') }}
              </li>
              <li 
                class="px-3 py-3 cursor-pointer text-gray-600 font-medium rounded-md transition-colors duration-200 hover:bg-gray-100 dark:text-gray-300 dark:hover:bg-gray-700"
                :class="{ 'text-blue-600 bg-blue-50 font-bold dark:text-blue-400 dark:bg-gray-700': selectedCategory === 'JAVA' }"
                @click="selectCategory('JAVA')"
              >
                Java
              </li>
              <li 
                class="px-3 py-3 cursor-pointer text-gray-600 font-medium rounded-md transition-colors duration-200 hover:bg-gray-100 dark:text-gray-300 dark:hover:bg-gray-700"
                :class="{ 'text-blue-600 bg-blue-50 font-bold dark:text-blue-400 dark:bg-gray-700': selectedCategory === 'JAVASCRIPT' }"
                @click="selectCategory('JAVASCRIPT')"
              >
                JavaScript
              </li>
              <li 
                class="px-3 py-3 cursor-pointer text-gray-600 font-medium rounded-md transition-colors duration-200 hover:bg-gray-100 dark:text-gray-300 dark:hover:bg-gray-700"
                :class="{ 'text-blue-600 bg-blue-50 font-bold dark:text-blue-400 dark:bg-gray-700': selectedCategory === 'REACT' }"
                @click="selectCategory('REACT')"
              >
                React
              </li>
              <li 
                class="px-3 py-3 cursor-pointer text-gray-600 font-medium rounded-md transition-colors duration-200 hover:bg-gray-100 dark:text-gray-300 dark:hover:bg-gray-700"
                :class="{ 'text-blue-600 bg-blue-50 font-bold dark:text-blue-400 dark:bg-gray-700': selectedCategory === 'CSS' }"
                @click="selectCategory('CSS')"
              >
                CSS
              </li>
              <li 
                class="px-3 py-3 cursor-pointer text-gray-600 font-medium rounded-md transition-colors duration-200 hover:bg-gray-100 dark:text-gray-300 dark:hover:bg-gray-700"
                :class="{ 'text-blue-600 bg-blue-50 font-bold dark:text-blue-400 dark:bg-gray-700': selectedCategory === 'HTML' }"
                @click="selectCategory('HTML')"
              >
                HTML
              </li>
              <li 
                class="px-3 py-3 cursor-pointer text-gray-600 font-medium rounded-md transition-colors duration-200 hover:bg-gray-100 dark:text-gray-300 dark:hover:bg-gray-700"
                :class="{ 'text-blue-600 bg-blue-50 font-bold dark:text-blue-400 dark:bg-gray-700': selectedCategory === 'SQL' }"
                @click="selectCategory('SQL')"
              >
                SQL
              </li>
              <li 
                class="px-3 py-3 cursor-pointer text-gray-600 font-medium rounded-md transition-colors duration-200 hover:bg-gray-100 dark:text-gray-300 dark:hover:bg-gray-700"
                :class="{ 'text-blue-600 bg-blue-50 font-bold dark:text-blue-400 dark:bg-gray-700': selectedCategory === 'PYTHON' }"
                @click="selectCategory('PYTHON')"
              >
                Python
              </li>
            </ul>
          </div>
        </aside>

        <!-- Main Content -->
        <main class="flex-1 min-w-0">
          <div class="mb-8 pb-4 border-b border-gray-200 dark:border-gray-700">
            <div class="flex justify-between items-center">
              <div class="flex items-center gap-3">
                <h2 class="text-2xl font-semibold text-gray-800 dark:text-gray-100">
                  {{ $t('knowledge.latestPosts') }}
                </h2>
                <span class="text-gray-500 text-sm dark:text-gray-400">
                  {{ posts.length }}{{ $t('knowledge.postCount') }}
                </span>
              </div>
              <div class="relative">
                <input 
                  type="text" 
                  id="search-input" 
                  :placeholder="$t('knowledge.searchPlaceholder')" 
                  class="border-b-2 border-gray-300 px-2 py-2 text-base bg-transparent text-gray-800 w-56 focus:outline-none focus:border-gray-600 transition-all duration-300 dark:text-gray-200 dark:border-gray-600 dark:focus:border-gray-400"
                  v-model="searchQuery"
                  @keyup.enter="searchPosts"
                >
                <button 
                  type="button" 
                  class="absolute right-0 top-1/2 transform -translate-y-1/2 bg-transparent border-none cursor-pointer p-2 text-gray-600 dark:text-gray-400" 
                  @click="searchPosts"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" width="20" height="20">
                    <path d="M15.5 14h-.79l-.28-.27A6.471 6.471 0 0 0 16 9.5 6.5 6.5 0 1 0 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"></path>
                  </svg>
                </button>
              </div>
            </div>
          </div>
          
          <!-- 로딩 상태 -->
          <div v-if="loading" class="flex flex-col items-center justify-center py-16 text-center">
            <div class="w-10 h-10 border-4 border-gray-300 border-t-blue-600 rounded-full animate-spin mb-5"></div>
            <p class="text-gray-600 dark:text-gray-400">{{ $t('knowledge.loading') }}</p>
          </div>

          <!-- 에러 상태 -->
          <div v-else-if="error" class="flex flex-col items-center justify-center py-16 text-center">
            <p class="text-red-600 mb-5 text-lg">{{ error }}</p>
            <button 
              @click="loadPosts" 
              class="px-5 py-2.5 bg-blue-600 text-white border-none rounded-md cursor-pointer text-sm transition-colors duration-200 hover:bg-blue-700 dark:bg-blue-500 dark:hover:bg-blue-600"
            >
              {{ $t('knowledge.retry') }}
            </button>
          </div>

          <!-- 게시글 목록 -->
          <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8" id="post-grid">
            <PostCard
              v-for="post in posts"
              :key="`${post.id}-${isDarkMode}`"
              :post="post"
              :lang="lang"
              :is-dark-mode="isDarkMode"
              @click="(postId) => goToPostDetail(postId)"
            />
          </div>

          <!-- 게시글이 없을 때 -->
          <div v-if="!loading && !error && posts.length === 0" class="flex items-center justify-center py-16 text-center">
            <p class="text-gray-500 text-lg dark:text-gray-400">{{ $t('knowledge.noPosts') }}</p>
          </div>
        </main>
      </div>
    </div>
  </div>
</template>

<script>
import apiService from '@/services/api.js';
import PostCard from '@/components/PostCard.vue';

export default {
  name: 'KnowledgeView',
  components: {
    PostCard
  },
  data() {
    return {
      posts: [],
      loading: false,
      error: null,
      searchQuery: '',
      selectedCategory: '',
      lang: 'ko',
      isDarkMode: false,
      observer: null
    };
  },
  async mounted() {
    this.setLanguage();
    await this.loadPosts();

    this.checkDarkMode();
    window.addEventListener('dark-mode-toggled', this.handleDarkModeToggle);
    this.updateDarkModeClass();

    this.$watch(
      () => this.$route.query.lang,
      async (newLang, oldLang) => {
        if (newLang !== oldLang) {
          this.setLanguage();
          await this.loadPosts();
        }
      }
    );
  },
  beforeUnmount() {
    window.removeEventListener('dark-mode-toggled', this.handleDarkModeToggle);
  },
  methods: {
    setLanguage() {
      // URL 쿼리 파라미터의 언어 설정을 우선 사용, 없으면 현재 locale 사용
      this.lang = this.$route.query.lang || this.$i18n.locale;
    },
    
    setHeroBackground() {
      const heroSection = document.querySelector('.hero-section');
      if (heroSection) {
        const isDarkMode = document.body.classList.contains('dark-mode');
        const lightBg = '/img/light_mode_bg.jpg';
        const darkBg = '/img/dark_mode_bg.jpg';
        
        heroSection.style.backgroundImage = `url(${isDarkMode ? darkBg : lightBg})`;
        heroSection.style.backgroundSize = 'cover';
        heroSection.style.backgroundPosition = 'center';
        heroSection.style.backgroundRepeat = 'no-repeat';
        heroSection.style.transition = 'background-image 0.5s ease';
      }
    },

    async loadPosts() {
      this.loading = true;
      this.error = null;
      
      try {
        this.setLanguage();
        console.log('Loading posts for language:', this.lang);
        const response = await apiService.getPosts(this.lang);
        
        if (response.success) {
          this.posts = [...response.data];
          console.log('Loaded posts:', this.posts.length, 'posts for language:', this.lang);
        } else {
          this.error = response.message || this.$t('knowledge.loadError');
        }
      } catch (error) {
        console.error('게시글 로드 실패:', error);
        this.error = this.$t('knowledge.loadError');
      } finally {
        this.loading = false;
        this.isLanguageChanging = false;
        window.dispatchEvent(new CustomEvent('language-change-status', { detail: { isLanguageChanging: this.isLanguageChanging } }));
      }
    },

    async searchPosts() {
      if (!this.searchQuery.trim()) {
        await this.loadPosts();
        return;
      }

      this.loading = true;
      this.error = null;
      
      try {
        this.setLanguage();
        const response = await apiService.searchPosts(this.searchQuery, this.lang);
        
        if (response.success) {
          this.posts = response.data;
        } else {
          this.error = response.message || this.$t('knowledge.searchError');
        }
      } catch (error) {
        console.error('검색 실패:', error);
        this.error = this.$t('knowledge.searchError');
      } finally {
        this.loading = false;
      }
    },

    async selectCategory(category) {
      this.selectedCategory = category;

      if (!category) {
        await this.loadPosts();
        return;
      }

      this.loading = true;
      this.error = null;
      
      try {
        this.setLanguage();
        // 카테고리 파라미터로 직접 API 호출
        const response = await apiService.publicRequest(`/api/posts?category=${category}&lang=${this.lang}`);
        
        if (response.success) {
          this.posts = response.data;
        } else {
          this.error = response.message || this.$t('knowledge.categoryError');
        }
      } catch (error) {
        console.error('카테고리별 게시글 로드 실패:', error);
        this.error = this.$t('knowledge.categoryError');
      } finally {
        this.loading = false;
      }
    },

    goToPostDetail(postId) {
      console.log('goToPostDetail called with postId:', postId);
      console.log('postId type:', typeof postId);
      console.log('current lang:', this.lang);
      
      // postId가 유효한지 확인
      if (typeof postId === 'number' || (typeof postId === 'string' && !isNaN(postId))) {
        this.setLanguage();
        const route = `/knowledge/${postId}?lang=${this.lang}`;
        console.log('Navigating to:', route);
        this.$router.push(route);
      } else {
        console.error('Invalid postId received:', postId);
      }
    },

    formatDate(dateString) {
      if (!dateString) return '';
      const date = new Date(dateString);
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
    },

    getCategoryDisplayName(category) {
      const categoryMap = {
        'JAVA': 'Java',
        'JAVASCRIPT': 'JavaScript',
        'REACT': 'React',
        'CSS': 'CSS',
        'HTML': 'HTML',
        'SQL': 'SQL',
        'PYTHON': 'Python',
        'BLOG_CODING': 'BlogBuild'
      };
      return categoryMap[category] || category;
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
  .text-5xl {
    font-size: 2rem;
  }
  
  .flex.gap-8 {
    flex-direction: column;
  }
  
  .w-56 {
    width: 100%;
  }
  
  .sticky {
    position: static;
  }
}
</style> 
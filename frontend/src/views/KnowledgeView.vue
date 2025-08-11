<template>
  <div class="hero-section">
    <h1 class="hero-title">{{ $i18n.locale === 'ja' ? '高原優輝' : 'Takahara Yuuki' }}</h1>
  </div>
  
  <div class="page-container">
      <aside class="sidebar">
        <div class="category-list-wrapper">
          <h3 class="category-list-title">카테고리</h3>
          <ul class="category-list">
            <li class="category-item active" data-category="" @click="selectCategory('')">전체보기</li>
            <li class="category-item" data-category="JAVA" @click="selectCategory('JAVA')">Java</li>
            <li class="category-item" data-category="JAVASCRIPT" @click="selectCategory('JAVASCRIPT')">JavaScript</li>
            <li class="category-item" data-category="REACT" @click="selectCategory('REACT')">React</li>
            <li class="category-item" data-category="CSS" @click="selectCategory('CSS')">CSS</li>
            <li class="category-item" data-category="HTML" @click="selectCategory('HTML')">HTML</li>
            <li class="category-item" data-category="SQL" @click="selectCategory('SQL')">SQL</li>
            <li class="category-item" data-category="PYTHON" @click="selectCategory('PYTHON')">Python</li>
          </ul>
        </div>
      </aside>

      <main class="main-content">
        <div class="content-section">
          <div class="section-header">
            <div class="section-header-title">
              <h2 class="section-title">최신 게시글</h2>
              <span class="post-count">{{ posts.length }}개</span>
            </div>
            <div class="search-form">
              <input 
                type="text" 
                id="search-input" 
                placeholder="게시글 검색..." 
                class="search-input"
                v-model="searchQuery"
                @keyup.enter="searchPosts"
              >
              <button type="button" class="search-button" @click="searchPosts">
                <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" width="20" height="20">
                  <path d="M15.5 14h-.79l-.28-.27A6.471 6.471 0 0 0 16 9.5 6.5 6.5 0 1 0 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0C7.01 14 5 11.99 5 9.5S7.01 5 9.5 5 14 7.01 14 9.5 11.99 14 9.5 14z"></path>
                </svg>
              </button>
            </div>
          </div>
          
          <!-- 로딩 상태 -->
          <div v-if="loading" class="loading-container">
            <div class="loading-spinner"></div>
            <p>게시글을 불러오는 중...</p>
          </div>

          <!-- 에러 상태 -->
          <div v-else-if="error" class="error-container">
            <p>{{ error }}</p>
            <button @click="loadPosts" class="retry-button">다시 시도</button>
          </div>

          <!-- 게시글 목록 -->
          <div v-else class="post-grid" id="post-grid">
            <div 
              v-for="post in posts" 
              :key="post.id" 
              class="post-card" 
              @click="goToPostDetail(post.id)"
            >
              <div class="post-image-wrapper">
                <div v-if="post.imageUrl" class="post-image">
                  <img :src="post.imageUrl" :alt="post.title">
                </div>
                <div v-else class="post-image-placeholder">
                  <span>{{ getCategoryDisplayName(post.category) }}</span>
                </div>
              </div>
              <div class="post-content">
                <h3 class="post-title">{{ post.title }}</h3>
                <p class="post-summary">{{ post.summary }}</p>
                <div class="post-meta">
                  <span class="post-date">{{ formatDate(post.createdAt) }}</span>
                  <div class="post-stats">
                    <span class="like-count">{{ post.likeCount || 0 }} ♥</span>
                    <span class="comment-count">{{ post.commentCount || 0 }} 💬</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 게시글이 없을 때 -->
          <div v-if="!loading && !error && posts.length === 0" class="no-posts">
            <p>게시글이 없습니다.</p>
          </div>
        </div>
      </main>
    </div>
</template>

<script>
import apiService from '@/services/api.js';

export default {
  name: 'KnowledgeView',
  data() {
    return {
      posts: [],
      loading: false,
      error: null,
      searchQuery: '',
      selectedCategory: '',
      observer: null
    };
  },
  async mounted() {
    this.setHeroBackground();
    await this.loadPosts();
    
    // 다크모드 변경 감지
    this.observer = new MutationObserver(() => {
      this.setHeroBackground();
    });
    this.observer.observe(document.body, {
      attributes: true,
      attributeFilter: ['class']
    });
    
    // 언어 변경 감지
    this.$watch('$i18n.locale', async (newLang, oldLang) => {
      if (newLang !== oldLang) {
        console.log(`Language changed from ${oldLang} to ${newLang}`);
        // URL 쿼리 파라미터도 함께 업데이트
        this.$router.push({ 
          path: this.$route.path, 
          query: { ...this.$route.query, lang: newLang } 
        });
        await this.loadPosts();
      }
    });
  },
  beforeUnmount() {
    if (this.observer) {
      this.observer.disconnect();
    }
  },
  beforeRouteLeave(to, from, next) {
    if (this.observer) {
      this.observer.disconnect();
    }
    next();
  },
  methods: {
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
        // URL 쿼리 파라미터의 언어 설정을 우선 사용, 없으면 현재 locale 사용
        const lang = this.$route.query.lang || this.$i18n.locale;
        console.log('Loading posts for language:', lang);
        const response = await apiService.getPosts(lang);
        
        if (response.success) {
          this.posts = response.data;
          console.log('Loaded posts:', this.posts.length, 'posts for language:', lang);
        } else {
          this.error = response.message || '게시글을 불러오는데 실패했습니다.';
        }
      } catch (error) {
        console.error('게시글 로드 실패:', error);
        this.error = '게시글을 불러오는데 실패했습니다.';
      } finally {
        this.loading = false;
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
        // URL 쿼리 파라미터의 언어 설정을 우선 사용, 없으면 현재 locale 사용
        const lang = this.$route.query.lang || this.$i18n.locale;
        const response = await apiService.searchPosts(this.searchQuery, lang);
        
        if (response.success) {
          this.posts = response.data;
        } else {
          this.error = response.message || '검색에 실패했습니다.';
        }
      } catch (error) {
        console.error('검색 실패:', error);
        this.error = '검색에 실패했습니다.';
      } finally {
        this.loading = false;
      }
    },

    async selectCategory(category) {
      this.selectedCategory = category;
      
      // 카테고리 선택 UI 업데이트
      document.querySelectorAll('.category-item').forEach(item => {
        item.classList.remove('active');
      });
      event.target.classList.add('active');

      if (!category) {
        await this.loadPosts();
        return;
      }

      this.loading = true;
      this.error = null;
      
      try {
        // URL 쿼리 파라미터의 언어 설정을 우선 사용, 없으면 현재 locale 사용
        const lang = this.$route.query.lang || this.$i18n.locale;
        // 카테고리 파라미터로 직접 API 호출
        const response = await apiService.publicRequest(`/api/posts?category=${category}&lang=${lang}`);
        
        if (response.success) {
          this.posts = response.data;
        } else {
          this.error = response.message || '카테고리별 게시글을 불러오는데 실패했습니다.';
        }
      } catch (error) {
        console.error('카테고리별 게시글 로드 실패:', error);
        this.error = '카테고리별 게시글을 불러오는데 실패했습니다.';
      } finally {
        this.loading = false;
      }
    },

    goToPostDetail(postId) {
      // URL 쿼리 파라미터의 언어 설정을 우선 사용, 없으면 현재 locale 사용
      const lang = this.$route.query.lang || this.$i18n.locale;
      this.$router.push(`/knowledge/${postId}?lang=${lang}`);
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
    }
  }
}
</script>

<style scoped>
.hero-section {
  height: 400px;
  position: relative;
}

.hero-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.2);
  z-index: 1;
}

.hero-title {
  position: relative;
  z-index: 2;
  font-size: 3rem;
  color: white;
  margin: 0;
  letter-spacing: 2px;
}

/* 포스트 카드 클릭 가능 스타일 */
.post-card {
  cursor: pointer;
  transition: transform 0.2s ease;
}

.post-card:hover {
  transform: translateY(-2px);
}

/* 다크모드 지원 */
:global(body.dark-mode) .post-card {
  background: #1a202c !important;
  border-color: #2d3748 !important;
}

:global(body.dark-mode) .post-title {
  color: #f7fafc !important;
}

:global(body.dark-mode) .post-summary {
  color: #e2e8f0 !important;
}

:global(body.dark-mode) .post-date {
  color: #a0aec0 !important;
}

:global(body.dark-mode) .post-stats {
  color: #a0aec0 !important;
}

:global(body.dark-mode) .post-image-placeholder {
  background: #1a202c !important;
  color: #a0aec0 !important;
  border-color: #4a5568 !important;
}

/* 로딩 및 에러 상태 스타일 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #007bff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
}

.error-container p {
  color: #e53e3e;
  margin-bottom: 20px;
  font-size: 1.1rem;
}

.retry-button {
  padding: 10px 20px;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: background-color 0.2s ease;
}

.retry-button:hover {
  background: #0056b3;
}

.no-posts {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
}

.no-posts p {
  color: #718096;
  font-size: 1.1rem;
}

.post-image {
  width: 100%;
  height: 200px;
  overflow: hidden;
  border-radius: 8px 8px 0 0;
}

.post-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .hero-title {
    font-size: 2rem;
  }
}
</style> 
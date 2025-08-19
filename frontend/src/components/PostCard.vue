<template>
  <div class="relative bg-white border border-gray-100 rounded-xl overflow-hidden shadow-sm transition-all duration-300 cursor-pointer flex flex-col h-full hover:-translate-y-0.5 hover:shadow-lg hover:border-gray-200 dark:bg-gray-900 dark:border-gray-800 dark:hover:shadow-gray-900/20 dark:hover:border-gray-700" @click="handleClick($event)">
    <!-- 카테고리 배지 -->
    <div class="absolute top-3 left-3 z-10">
      <span class="inline-block px-3 py-1 bg-white/95 backdrop-blur-sm text-gray-600 text-xs font-semibold rounded-full border border-white/20 shadow-sm dark:bg-gray-800/95 dark:text-gray-300 dark:border-gray-800/30">
        {{ getCategoryDisplayName(post.category) }}
      </span>
    </div>
    
    <!-- 대표 이미지 -->
    <div class="relative w-full pt-[56.25%] overflow-hidden bg-gray-50 dark:bg-gray-800">
      <img 
        v-if="post.imageUrl" 
        :src="post.imageUrl" 
        :alt="post.title"
        class="absolute top-0 left-0 w-full h-full object-cover transition-transform duration-300 hover:scale-[1.01]"
        loading="lazy"
        @click.stop="handleImageClick"
      >
      <div v-else class="absolute top-0 left-0 w-full h-full flex flex-col items-center justify-center bg-gradient-to-br from-gray-100 to-gray-200 text-gray-500 dark:from-gray-700 dark:to-gray-800 dark:text-gray-400">
        <div class="mb-2 opacity-60">
          <svg width="32" height="32" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M21 19V5C21 3.9 20.1 3 19 3H5C3.9 3 3 3.9 3 5V19C3 20.1 3.9 21 5 21H19C20.1 21 21 20.1 21 19ZM8.5 13.5L11 16.51L14.5 12L19 18H5L8.5 13.5Z" fill="currentColor"/>
          </svg>
        </div>
        <span class="text-sm font-medium">{{ getCategoryDisplayName(post.category) }}</span>
      </div>
    </div>
    
    <!-- 카드 내용 -->
    <div class="p-8 flex flex-col flex-grow gap-3 min-w-0">
      <!-- 제목 -->
      <h3 class="text-lg font-bold text-gray-800 leading-tight m-0 line-clamp-2 break-keep dark:text-white">
        {{ displayTitle }}
      </h3>
      
      <!-- 요약 -->
      <p v-if="displaySummary" class="text-gray-600 text-sm leading-relaxed m-0 line-clamp-2 flex-grow dark:text-gray-300">
        {{ displaySummary }}
      </p>
      
      <!-- 메타 정보 -->
      <div class="flex justify-between items-center mt-auto pt-3 border-t border-gray-100 dark:border-gray-800">
        <div class="flex items-center gap-2">
          <span class="text-xs text-gray-400 font-medium dark:text-gray-500">
            {{ formatDate(post.createdAt) }}
          </span>
        </div>
        <div class="flex items-center gap-3">
          <span v-if="post.likeCount > 0" class="flex items-center gap-1 text-xs text-gray-400 font-medium dark:text-gray-500">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor" class="opacity-70">
              <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/>
            </svg>
            {{ post.likeCount }}
          </span>
          <span v-if="post.commentCount > 0" class="flex items-center gap-1 text-xs text-gray-400 font-medium dark:text-gray-500">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor" class="opacity-70">
              <path d="M21.99 4c0-1.1-.89-2-2-2H4c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h14l4 4-.01-18zM18 14H6v-2h12v2zm0-3H6V9h12v2zm0-3H6V6h12v2z"/>
            </svg>
            {{ post.commentCount }}
          </span>
        </div>
      </div>
      
      <!-- 태그 (선택사항) -->
      <div v-if="post.tags && post.tags.trim()" class="flex flex-wrap gap-1.5 mt-2">
        <span 
          v-for="tag in getTags(post.tags)" 
          :key="tag" 
          class="text-xs text-blue-600 bg-blue-50 px-2 py-0.5 rounded-full font-medium transition-all duration-200 hover:bg-blue-100 hover:text-blue-700 dark:text-blue-400 dark:bg-blue-900/30 dark:hover:bg-blue-900/50 dark:hover:text-blue-300"
        >
          #{{ tag.trim() }}
        </span>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'PostCard',
  props: {
    post: {
      type: Object,
      required: true
    },
    lang: {
      type: String,
      default: 'ko'
    },
    isDarkMode: {
      type: Boolean,
      default: false
    }
  },
  mounted() {
    // 컴포넌트 마운트 완료
  },
  computed: {
    displayTitle() {
      return this.lang === 'ja' && this.post.titleJa ? this.post.titleJa : this.post.titleKo || this.post.title || '제목 없음';
    },
    displaySummary() {
      return this.lang === 'ja' && this.post.summaryJa ? this.post.summaryJa : this.post.summaryKo || this.post.summary || '';
    }
  },
  methods: {
    handleClick(event) {
      // 이벤트 전파 방지
      event.preventDefault();
      event.stopPropagation();
      
      // post.id가 숫자인지 확인하고 emit
      const postId = this.post.id;
      if (typeof postId === 'number' || (typeof postId === 'string' && !isNaN(postId))) {
        this.$emit('click', postId);
      } else {
        console.error('Invalid post ID:', postId);
      }
    },
    
    handleImageClick(event) {
      // 이미지 클릭 시에도 동일한 로직 적용
      event.preventDefault();
      event.stopPropagation();
      
      const postId = this.post.id;
      if (typeof postId === 'number' || (typeof postId === 'string' && !isNaN(postId))) {
        this.$emit('click', postId);
      } else {
        console.error('Invalid post ID from image click:', postId);
      }
    },
    formatDate(dateString) {
      if (!dateString) {
        return '';
      }
      
      const date = new Date(dateString);
      
      // 유효한 날짜인지 확인
      if (isNaN(date.getTime())) {
        return '';
      }
      
      // PostDetailView.vue와 동일한 형식으로 날짜 표시
      return `${date.getFullYear()}년 ${String(date.getMonth() + 1).padStart(2, '0')}월 ${String(date.getDate()).padStart(2, '0')}일`;
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
      // 매핑된 카테고리가 있으면 사용, 없으면 원본 카테고리명 반환
      return categoryMap[category] || category;
    },
    getTags(tagsString) {
      if (!tagsString) return [];
      return tagsString.split(',').filter(tag => tag.trim());
    }
  }
}
</script>

<style scoped>
/* line-clamp 유틸리티 클래스 */
.line-clamp-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .p-8 {
    padding: 1.75rem 2.25rem;
  }
  
  .text-lg {
    font-size: 1rem;
  }
  
  .text-sm {
    font-size: 0.8125rem;
  }
  
  .top-3 {
    top: 0.5rem;
  }
  
  .left-3 {
    left: 0.5rem;
  }
  
  .px-3 {
    padding-left: 0.625rem;
    padding-right: 0.625rem;
  }
  
  .py-1 {
    padding-top: 0.25rem;
    padding-bottom: 0.25rem;
  }
  
  .text-xs {
    font-size: 0.6875rem;
  }
}

@media (max-width: 480px) {
  .p-8 {
    padding: 1.5rem 2rem;
  }
  
  .text-lg {
    font-size: 0.9375rem;
  }
  
  .text-sm {
    font-size: 0.75rem;
  }
  
  .pt-3 {
    padding-top: 0.5rem;
  }
  
  .text-xs {
    font-size: 0.6875rem;
  }
}
</style>

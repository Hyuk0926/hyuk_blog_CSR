<template>
  <div class="post-card" :class="{ 'dark-mode': isDarkMode }" @click="handleClick($event)">
    <!-- 카테고리 배지 -->
    <div class="post-category">
      <span class="category-badge">{{ getCategoryDisplayName(post.category) }}</span>
    </div>
    
    <!-- 대표 이미지 -->
    <div class="post-image-wrapper">
      <img 
        v-if="post.imageUrl" 
        :src="post.imageUrl" 
        :alt="post.title"
        class="post-image"
        loading="lazy"
        @click.stop="handleImageClick"
      >
      <div v-else class="post-image-placeholder">
        <div class="placeholder-icon">
          <svg width="32" height="32" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M21 19V5C21 3.9 20.1 3 19 3H5C3.9 3 3 3.9 3 5V19C3 20.1 3.9 21 5 21H19C20.1 21 21 20.1 21 19ZM8.5 13.5L11 16.51L14.5 12L19 18H5L8.5 13.5Z" fill="currentColor"/>
          </svg>
        </div>
        <span class="placeholder-text">{{ getCategoryDisplayName(post.category) }}</span>
      </div>
    </div>
    
    <!-- 카드 내용 -->
    <div class="post-content">
      <!-- 제목 -->
      <h3 class="post-title">{{ displayTitle }}</h3>
      
      <!-- 요약 -->
      <p v-if="displaySummary" class="post-summary">{{ displaySummary }}</p>
      
      <!-- 메타 정보 -->
      <div class="post-meta">
        <div class="meta-left">
          <span class="post-date">{{ formatDate(post.createdAt) }}</span>
        </div>
        <div class="meta-right">
          <span v-if="post.likeCount > 0" class="post-stat">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor">
              <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/>
            </svg>
            {{ post.likeCount }}
          </span>
          <span v-if="post.commentCount > 0" class="post-stat">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="currentColor">
              <path d="M21.99 4c0-1.1-.89-2-2-2H4c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h14l4 4-.01-18zM18 14H6v-2h12v2zm0-3H6V9h12v2zm0-3H6V6h12v2z"/>
            </svg>
            {{ post.commentCount }}
          </span>
        </div>
      </div>
      
      <!-- 태그 (선택사항) -->
      <div v-if="post.tags && post.tags.trim()" class="post-tags">
        <span 
          v-for="tag in getTags(post.tags)" 
          :key="tag" 
          class="tag"
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
      if (!dateString) return '';
      const date = new Date(dateString);
      const now = new Date();
      const diffTime = Math.abs(now - date);
      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
      
      if (diffDays === 1) {
        return this.lang === 'ja' ? '今日' : '오늘';
      } else if (diffDays <= 7) {
        const days = diffDays - 1;
        return this.lang === 'ja' ? `${days}日前` : `${days}일 전`;
      } else {
        const locale = this.lang === 'ja' ? 'ja-JP' : 'ko-KR';
        return date.toLocaleDateString(locale, {
          year: 'numeric',
          month: '2-digit',
          day: '2-digit'
        });
      }
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
.post-card {
  position: relative;
  background: #ffffff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.02);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  display: flex;
  flex-direction: column;
  height: 100%;
  border: 1px solid #f1f5f9;
}

.post-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  border-color: #e2e8f0;
}

.post-card:hover .post-image {
  transform: scale(1.01);
}

/* 카테고리 배지 */
.post-category {
  position: absolute;
  top: 12px;
  left: 12px;
  z-index: 2;
}

.category-badge {
  display: inline-block;
  padding: 4px 12px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(8px);
  color: #475569;
  font-size: 0.75rem;
  font-weight: 600;
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.02);
}

/* 이미지 영역 */
.post-image-wrapper {
  position: relative;
  width: 100%;
  padding-top: 56.25%; /* 16:9 비율 */
  overflow: hidden;
  background: #f8fafc;
}

.post-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.post-image-placeholder {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f1f5f9 0%, #e2e8f0 100%);
  color: #64748b;
}

.placeholder-icon {
  margin-bottom: 8px;
  opacity: 0.6;
}

.placeholder-text {
  font-size: 0.875rem;
  font-weight: 500;
}

/* 카드 내용 */
.post-content {
  padding: 32px 45px;
  display: flex;
  flex-direction: column;
  flex-grow: 1;
  gap: 12px;
  min-width: 0; /* 텍스트 오버플로우 방지 */
}

.post-title {
  font-size: 1.125rem;
  font-weight: 700;
  color: #1e293b;
  line-height: 1.4;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  word-break: keep-all;
}

.post-summary {
  color: #64748b;
  font-size: 0.875rem;
  line-height: 1.6;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  flex-grow: 1;
}

/* 메타 정보 */
.post-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
  padding-top: 12px;
  border-top: 1px solid #f1f5f9;
}

.meta-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.post-date {
  font-size: 0.75rem;
  color: #94a3b8;
  font-weight: 500;
}

.meta-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.post-stat {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 0.75rem;
  color: #94a3b8;
  font-weight: 500;
}

.post-stat svg {
  opacity: 0.7;
}

/* 태그 */
.post-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 8px;
}

.tag {
  font-size: 0.75rem;
  color: #3b82f6;
  background: #eff6ff;
  padding: 2px 8px;
  border-radius: 12px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.tag:hover {
  background: #dbeafe;
  color: #2563eb;
}

/* 다크모드 지원 */
.post-card.dark-mode {
  background: #0f172a !important;
  border-color: #1e293b !important;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1) !important;
}

.post-card.dark-mode:hover {
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15) !important;
  border-color: #334155 !important;
  background: #1e293b !important;
}

.post-card.dark-mode .category-badge {
  background: rgba(30, 41, 59, 0.95);
  color: #cbd5e1;
  border-color: rgba(30, 41, 59, 0.3);
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);
}

.post-card.dark-mode .post-image-placeholder {
  background: linear-gradient(135deg, #334155 0%, #475569 100%);
  color: #94a3b8;
}

.post-card.dark-mode .post-title {
  color: #ffffff !important;
}

.post-card.dark-mode .post-summary {
  color: #cbd5e1 !important;
}

.post-card.dark-mode .post-meta {
  border-top-color: #334155 !important;
}

.post-card.dark-mode .post-date,
.post-card.dark-mode .post-stat {
  color: #64748b !important;
}

.post-card.dark-mode .tag {
  color: #60a5fa;
  background: #1e3a8a;
}

.post-card.dark-mode .tag:hover {
  background: #1e40af;
  color: #93c5fd;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .post-content {
    padding: 28px 36px;
    gap: 10px;
  }
  
  .post-title {
    font-size: 1rem;
  }
  
  .post-summary {
    font-size: 0.8125rem;
  }
  
  .post-category {
    top: 8px;
    left: 8px;
  }
  
  .category-badge {
    padding: 3px 10px;
    font-size: 0.6875rem;
  }
}

@media (max-width: 480px) {
  .post-content {
    padding: 24px 32px;
  }
  
  .post-title {
    font-size: 0.9375rem;
  }
  
  .post-summary {
    font-size: 0.75rem;
  }
  
  .post-meta {
    padding-top: 8px;
  }
  
  .post-date,
  .post-stat {
    font-size: 0.6875rem;
  }
}
</style>

<template>
  <div class="post-detail-container">
    <!-- 알림 메시지 컨테이너 -->
    <div id="notification-container" class="notification-container" style="display: none;">
      <div class="notification-message">
        <span id="notification-text"></span>
        <button class="notification-close" @click="closeNotification">×</button>
      </div>
    </div>
    
    <!-- 로그인 확인 모달 -->
    <div id="login-confirm-modal" class="modal-overlay" style="display: none;">
      <div class="modal-content">
        <div class="modal-header">
          <h3 class="modal-title" id="modal-title">로그인 필요</h3>
        </div>
        <div class="modal-body">
          <p id="modal-message">좋아요 기능을 사용하려면 로그인이 필요합니다. 로그인 페이지로 이동하시겠습니까?</p>
        </div>
        <div class="modal-footer">
          <button class="modal-btn cancel" @click="closeLoginModal">취소</button>
          <button class="modal-btn confirm" @click="confirmLogin">확인</button>
        </div>
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
      <button @click="loadPost" class="retry-button">다시 시도</button>
    </div>

    <!-- 게시글 내용 -->
    <div v-else>
      <div class="post-header">
        <h1>{{ post.title }}</h1>
        <div class="post-meta" style="text-align: right;">
          <span>{{ formatDate(post.createdAt) }}</span>
        </div>
      </div>
    
    <div class="post-summary" v-if="post.summary">
      <p>{{ post.summary }}</p>
    </div>
    
    <div class="post-image-container" v-if="post.imageUrl">
      <img :src="post.imageUrl" :alt="post.title" class="post-detail-image">
    </div>
    
    <div class="post-content" id="post-content">
      <div v-html="post.content"></div>
    </div>
    
    <!-- 미리보기 모드 표시 -->
    <div class="preview-notice" v-if="isPreview" style="background: #fff3cd; border: 1px solid #ffeaa7; color: #856404; padding: 16px; border-radius: 8px; margin: 20px auto; max-width: 700px; text-align: center; font-weight: 500;">
      <span>미리보기 모드</span>
    </div>
    
    <!-- 좋아요 버튼 (미리보기 모드에서는 비활성화) -->
    <div class="like-section" v-if="!isPreview">
      <button class="like-button" @click="handleLikeToggle" :data-post-id="post.id">
        <span class="heart-icon" :class="{ liked: isLiked }">{{ isLiked ? '♥' : '♡' }}</span>
        <span class="like-count">{{ likeCount }}</span>
      </button>
    </div>
    
    <div class="post-navigation">
      <a v-if="isPreview" href="/admin" class="back-button">관리자 페이지로 돌아가기</a>
      <a v-else href="/knowledge" class="back-button">목록으로 돌아가기</a>
    </div>
    
    <!-- 댓글 시스템 (미리보기 모드에서는 비활성화) -->
    <div class="comment-section" v-if="!isPreview">
      <h3 class="comment-title">댓글</h3>
      
      <!-- 댓글 목록 -->
      <div id="comment-list" class="comment-list">
        <div v-for="comment in comments" :key="comment.id" class="comment-item">
          <div class="comment-header">
            <div class="comment-info">
              <div class="comment-nickname">{{ comment.nickname }}</div>
              <div class="comment-date">
                {{ formatDate(comment.createdAt) }}
                <span v-if="comment.isEdited" class="edited-badge">(수정됨)</span>
              </div>
            </div>
            <div class="comment-actions" v-if="isAuthenticated && user && comment.userId === user.id">
              <button v-if="editingCommentId !== comment.id" class="comment-action-btn" @click="startEditComment(comment)">수정</button>
              <button class="comment-action-btn delete" @click="deleteComment(comment.id)">삭제</button>
            </div>
          </div>
          
          <!-- 댓글 내용 (수정 모드가 아닐 때) -->
          <div v-if="editingCommentId !== comment.id" class="comment-content">{{ comment.content }}</div>
          
          <!-- 댓글 수정 폼 (수정 모드일 때) -->
          <div v-if="editingCommentId === comment.id" class="comment-edit-form">
            <textarea 
              v-model="editingContent"
              class="comment-edit-textarea"
              placeholder="댓글을 수정하세요..."
            ></textarea>
            <div class="comment-edit-actions">
              <button class="comment-action-btn" @click="updateComment(comment.id)">저장</button>
              <button class="comment-action-btn cancel" @click="cancelEditComment">취소</button>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 댓글 작성 폼 -->
      <div class="comment-form" v-if="isAuthenticated">
        <div class="comment-content-group">
          <textarea 
            id="comment-content" 
            placeholder="댓글을 입력하세요..." 
            class="comment-textarea"
            v-model="newComment"
          ></textarea>
          <button id="comment-submit" class="comment-submit-btn" @click="submitComment">댓글 작성</button>
        </div>
      </div>
      
      <!-- 로그인 필요 메시지 -->
      <div class="comment-login-message" v-if="!isAuthenticated">
        <p>댓글을 작성하려면 <a href="/user/login" class="login-link">로그인</a>이 필요합니다.</p>
      </div>
    </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
import apiService from '@/services/api.js';
import { useAuth } from '@/composables/useAuth.js';

// Composables
const route = useRoute();
const router = useRouter();
const { locale } = useI18n();
const { user, isAuthenticated, fetchUserInfo } = useAuth();

// Reactive data
const post = ref({
  id: null,
  title: '',
  summary: '',
  content: '',
  imageUrl: '',
  createdAt: null,
  category: null,
  postType: null
});

const isLiked = ref(false);
const likeCount = ref(0);
const isPreview = ref(false);
const comments = ref([]);
const newComment = ref('');
const loading = ref(false);
const error = ref(null);

// Methods
const loadPost = async () => {
  const postId = route.params.id;
  loading.value = true;
  error.value = null;
  
  try {
    // URL 쿼리 파라미터의 언어 설정을 우선 사용, 없으면 현재 locale 사용
    const lang = route.query.lang || locale.value;
    const response = await apiService.getPost(postId, lang);
    
    if (response.success) {
      post.value = response.data;
    } else {
      error.value = response.message || '게시글을 찾을 수 없습니다.';
      post.value = {
        id: postId,
        title: '게시글을 찾을 수 없습니다',
        summary: '',
        content: '<p>요청하신 게시글을 찾을 수 없습니다.</p>',
        imageUrl: '',
        createdAt: new Date(),
        likeCount: 0
      };
    }
  } catch (error) {
    console.error('포스트 로드 실패:', error);
    error.value = '게시글을 불러오는데 실패했습니다.';
    post.value = {
      id: postId,
      title: '게시글을 찾을 수 없습니다',
      summary: '',
      content: '<p>요청하신 게시글을 찾을 수 없습니다.</p>',
      imageUrl: '',
      createdAt: new Date(),
      likeCount: 0
    };
  } finally {
    loading.value = false;
  }
};

const loadLikeStatus = async () => {
  if (!post.value.id) return;
  
  try {
    // URL 쿼리 파라미터의 언어 설정을 우선 사용, 없으면 현재 locale 사용
    const lang = route.query.lang || locale.value;
    const postType = post.value.postType || apiService.getPostTypeFromLang(lang);
    const response = await apiService.getLikeStatus(post.value.id, postType);
    
    if (response) {
      likeCount.value = response.likeCount || 0;
      isLiked.value = response.isLiked || false;
    }
  } catch (error) {
    console.error('좋아요 상태 로드 실패:', error);
    // 에러가 발생해도 기본값으로 설정
    likeCount.value = 0;
    isLiked.value = false;
  }
};

const loadComments = async () => {
  if (!post.value.id) return;
  
  try {
    // URL 쿼리 파라미터의 언어 설정을 우선 사용, 없으면 현재 locale 사용
    const lang = route.query.lang || locale.value;
    const postType = post.value.postType || apiService.getPostTypeFromLang(lang);
    const response = await apiService.getComments(post.value.id, postType);
    
    if (response && Array.isArray(response)) {
      comments.value = response;
    } else {
      console.error('댓글 로드 실패:', response);
      comments.value = [];
    }
  } catch (error) {
    console.error('댓글 로드 실패:', error);
    comments.value = [];
  }
};

const formatDate = (date) => {
  if (!date) return '';
  const d = new Date(date);
  return `${d.getFullYear()}년 ${String(d.getMonth() + 1).padStart(2, '0')}월 ${String(d.getDate()).padStart(2, '0')}일`;
};

const handleLikeToggle = async () => {
  if (!isAuthenticated.value) {
    showLoginModal();
    return;
  }
  
  try {
    // URL 쿼리 파라미터의 언어 설정을 우선 사용, 없으면 현재 locale 사용
    const lang = route.query.lang || locale.value;
    const postType = post.value.postType || apiService.getPostTypeFromLang(lang);
    const response = await apiService.toggleLike(post.value.id, postType);
    
    if (response.success) {
      // 즉각적인 사용자 경험을 위해 로컬 상태를 직접 업데이트
      isLiked.value = !isLiked.value;
      if (isLiked.value) {
        likeCount.value++;
      } else {
        likeCount.value--;
      }
      showNotification(isLiked.value ? '좋아요를 눌렀습니다!' : '좋아요를 취소했습니다.');
    } else {
      showNotification(response.message || '좋아요 처리 중 오류가 발생했습니다.');
    }
  } catch (error) {
    console.error('좋아요 토글 실패:', error);
    showNotification('좋아요 처리 중 오류가 발생했습니다.');
  }
};

const submitComment = async () => {
  if (!newComment.value.trim()) {
    showNotification('댓글 내용을 입력해주세요.');
    return;
  }
  
  // 로그인 상태 확인
  if (!isAuthenticated.value) {
    showNotification('댓글을 작성하려면 로그인이 필요합니다.');
    return;
  }
  
  // JWT 토큰 확인
  const token = localStorage.getItem('jwtToken');
  if (!token) {
    showNotification('인증 토큰이 없습니다. 다시 로그인해주세요.');
    return;
  }
  
  console.log('댓글 작성 시도:', {
    postId: post.value.id,
    postType: post.value.postType || apiService.getPostTypeFromLang(locale.value),
    isAuthenticated: isAuthenticated.value,
    hasToken: !!token,
    user: user.value
  });
  
  try {
    // URL 쿼리 파라미터의 언어 설정을 우선 사용, 없으면 현재 locale 사용
    const lang = route.query.lang || locale.value;
    const postType = post.value.postType || apiService.getPostTypeFromLang(lang);
    const commentData = {
      content: newComment.value
    };
    
    const response = await apiService.addComment(post.value.id, postType, commentData);
    
    if (response) {
      // 새 댓글을 목록에 추가
      comments.value.unshift(response);
      newComment.value = '';
      showNotification('댓글이 작성되었습니다.');
    } else {
      showNotification('댓글 작성 중 오류가 발생했습니다.');
    }
  } catch (error) {
    console.error('댓글 작성 실패:', error);
    if (error.message && error.message.includes('401')) {
      showNotification('인증이 만료되었습니다. 다시 로그인해주세요.');
    } else {
      showNotification('댓글 작성 중 오류가 발생했습니다.');
    }
  }
};

// 댓글 수정 관련 상태
const editingCommentId = ref(null);
const editingContent = ref('');

const startEditComment = (comment) => {
  editingCommentId.value = comment.id;
  editingContent.value = comment.content;
};

const cancelEditComment = () => {
  editingCommentId.value = null;
  editingContent.value = '';
};

const updateComment = async (commentId) => {
  if (!editingContent.value.trim()) {
    showNotification('댓글 내용을 입력해주세요.');
    return;
  }
  
  try {
    const commentData = {
      content: editingContent.value
    };
    
    const response = await apiService.updateComment(commentId, commentData);
    
    if (response) {
      // 로컬 상태에서 댓글 업데이트
      const commentIndex = comments.value.findIndex(c => c.id === commentId);
      if (commentIndex !== -1) {
        comments.value[commentIndex] = {
          ...comments.value[commentIndex],
          content: editingContent.value,
          isEdited: true,
          updatedAt: new Date().toISOString()
        };
      }
      
      editingCommentId.value = null;
      editingContent.value = '';
      showNotification('댓글이 수정되었습니다.');
    } else {
      showNotification('댓글 수정 중 오류가 발생했습니다.');
    }
  } catch (error) {
    console.error('댓글 수정 실패:', error);
    showNotification('댓글 수정 중 오류가 발생했습니다.');
  }
};

const deleteComment = async (commentId) => {
  if (confirm('댓글을 삭제하시겠습니까?')) {
    try {
      await apiService.deleteComment(commentId);
      // 로컬 상태에서 댓글 제거
      comments.value = comments.value.filter(c => c.id !== commentId);
      showNotification('댓글이 삭제되었습니다.');
    } catch (error) {
      console.error('댓글 삭제 실패:', error);
      showNotification('댓글 삭제 중 오류가 발생했습니다.');
    }
  }
};

const showLoginModal = () => {
  document.getElementById('login-confirm-modal').style.display = 'flex';
};

const closeLoginModal = () => {
  document.getElementById('login-confirm-modal').style.display = 'none';
};

const confirmLogin = () => {
  closeLoginModal();
  router.push('/user/login');
};

const showNotification = (message) => {
  const container = document.getElementById('notification-container');
  const text = document.getElementById('notification-text');
  
  text.textContent = message;
  container.style.display = 'block';
  
  setTimeout(() => {
    closeNotification();
  }, 3000);
};

const closeNotification = () => {
  document.getElementById('notification-container').style.display = 'none';
};

const setupCodeCopy = () => {
  nextTick(() => {
    const codeBlocks = document.querySelectorAll('#post-content pre');
    codeBlocks.forEach(block => {
      // 이미 wrapper가 있는지 확인
      if (block.parentNode.classList.contains('code-block-wrapper')) {
        return;
      }
      
      const wrapper = document.createElement('div');
      wrapper.className = 'code-block-wrapper';
      block.parentNode.insertBefore(wrapper, block);
      wrapper.appendChild(block);
      
      const copyBtn = document.createElement('button');
      copyBtn.className = 'copy-btn';
      copyBtn.textContent = '복사';
      copyBtn.onclick = () => copyCode(block, copyBtn);
      wrapper.appendChild(copyBtn);
    });
  });
};

const copyCode = (block, btn) => {
  const code = block.textContent;
  navigator.clipboard.writeText(code).then(() => {
    btn.textContent = '복사됨!';
    btn.disabled = true;
    setTimeout(() => {
      btn.textContent = '복사';
      btn.disabled = false;
    }, 2000);
  });
};

const setupPrism = () => {
  nextTick(() => {
    // Prism.js가 로드될 때까지 대기
    const checkPrism = () => {
      if (window.Prism) {
        window.Prism.highlightAll();
      } else {
        setTimeout(checkPrism, 100);
      }
    };
    checkPrism();
  });
};

// Watch for content changes
watch(() => post.value.content, () => {
  nextTick(() => {
    setupCodeCopy();
    setupPrism();
  });
});

// Lifecycle
onMounted(async () => {
  await fetchUserInfo();
  await loadPost();
  await loadLikeStatus();
  await loadComments();
  setupCodeCopy();
  setupPrism();
});
</script>

<style scoped>
/* 제공된 CSS 스타일을 여기에 포함 */
.post-detail-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 40px 20px;
}

.post-header {
  border-bottom: 1px solid #e2e8f0;
  padding-bottom: 20px;
  margin-bottom: 32px;
}

.post-header h1 {
  font-size: 2.5rem;
  font-weight: 700;
  color: #1a202c;
  margin: 0 0 16px 0;
  line-height: 1.2;
}

.post-meta {
  margin-top: 12px;
  font-size: 0.9rem;
  color: #718096;
  font-weight: 400;
}

.post-summary {
  margin: 32px auto 36px auto;
  max-width: 700px;
  font-size: 1.1rem;
  color: #4a5568;
  background: #f8fafc;
  border-radius: 8px;
  padding: 24px 28px;
  font-weight: 400;
  line-height: 1.6;
  border-left: 4px solid #e2e8f0;
}

.post-summary p {
  margin: 0;
  font-size: 1.1rem;
  color: inherit;
  font-weight: 400;
}

.post-image-container {
  margin: 32px auto;
  max-width: 700px;
  text-align: center;
}

.post-detail-image {
  max-width: 700px;
  width: 100%;
  height: auto;
  display: block;
  margin: 0 auto;
  border-radius: 14px;
  object-fit: contain;
  box-shadow: 0 4px 24px rgba(0,0,0,0.07);
}

.post-content {
  margin-bottom: 64px;
  line-height: 1.8;
  font-size: 1.1rem;
  color: #2d3748;
}

.post-content h1,
.post-content h2,
.post-content h3,
.post-content h4,
.post-content h5,
.post-content h6 {
  color: #1a202c;
  font-weight: 600;
  margin-top: 2rem;
  margin-bottom: 1rem;
}

.post-content h2 {
  font-size: 1.8rem;
  border-bottom: 2px solid #e2e8f0;
  padding-bottom: 0.5rem;
}

.post-content h3 {
  font-size: 1.5rem;
}

.post-content p {
  margin-bottom: 1.5rem;
}

.post-content ul,
.post-content ol {
  margin-bottom: 1.5rem;
  padding-left: 2rem;
}

.post-content li {
  margin-bottom: 0.5rem;
}

.post-content pre {
  background: #23272a;
  color: #ffe082;
  padding: 16px;
  border-radius: 8px;
  font-size: 1rem;
  font-family: 'Fira Mono', 'Consolas', 'Menlo', 'Monaco', monospace;
  overflow-x: auto;
  margin: 16px 0;
  box-shadow: 0 2px 12px rgba(0,0,0,0.10);
  border: 1px solid #444;
  position: relative;
  transition: all 0.3s ease;
}

.post-content pre:hover {
  box-shadow: 0 4px 20px rgba(0,0,0,0.15);
  transform: translateY(-1px);
}

.post-content pre code {
  background: none;
  color: inherit;
  padding: 0;
  border-radius: 0;
  font-size: inherit;
  font-family: inherit;
}

.post-content code {
  background: #23272a;
  color: #ffe082;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 0.98em;
  font-family: 'Fira Mono', 'Consolas', 'Menlo', 'Monaco', monospace;
}

.code-block-wrapper {
  position: relative;
}

.copy-btn {
  position: absolute;
  top: 8px;
  right: 8px;
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(12px);
  color: #a0aec0;
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: 8px;
  padding: 8px 12px;
  font-size: 0.8rem;
  cursor: pointer;
  opacity: 0.3;
  z-index: 2;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  font-family: inherit;
  font-weight: 500;
  min-width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.code-block-wrapper:hover .copy-btn {
  opacity: 1;
  transform: translateY(0);
}

.copy-btn:hover {
  background: rgba(255, 255, 255, 0.15);
  color: #ffffff;
  border-color: rgba(255, 255, 255, 0.25);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.copy-btn:active {
  transform: translateY(0);
  transition: all 0.1s ease;
}

.copy-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
}

.like-section {
  text-align: center;
  margin: 40px 0;
}

.like-button {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
  font-family: inherit;
  font-weight: 500;
  font-size: 0.9rem;
  color: #4a5568;
}

.like-button:hover {
  background: #f7fafc;
  border-color: #cbd5e0;
}

.heart-icon {
  font-size: 1.2rem;
  transition: all 0.2s ease;
  color: #a0aec0;
}

.heart-icon.liked {
  color: #e53e3e;
}

.like-count {
  font-weight: 500;
  color: #4a5568;
}

.back-button {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 10px 16px;
  background: #ffffff;
  color: #4a5568;
  text-decoration: none;
  border-radius: 6px;
  font-weight: 500;
  font-size: 0.9rem;
  font-family: inherit;
  transition: all 0.2s ease;
  border: 1px solid #e2e8f0;
}

.back-button::before {
  content: '←';
  font-size: 1rem;
  font-weight: 400;
}

.back-button:hover {
  background: #f7fafc;
  border-color: #cbd5e0;
}

.post-navigation {
  margin-top: 40px;
  margin-bottom: 20px;
}

.preview-notice {
  background: #fff3cd;
  border: 1px solid #ffeaa7;
  color: #856404;
  padding: 16px;
  border-radius: 8px;
  margin: 20px auto;
  max-width: 700px;
  text-align: center;
  font-weight: 500;
}

/* 댓글 시스템 스타일 */
.comment-section {
  margin-top: 60px;
  max-width: 700px;
  margin-left: auto;
  margin-right: auto;
}

.comment-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: #1a202c;
  margin-bottom: 24px;
  font-family: inherit;
}

.comment-form {
  background: #ffffff;
  border-radius: 8px;
  padding: 24px;
  border: 1px solid #e2e8f0;
  margin-bottom: 32px;
  width: 100%;
  box-sizing: border-box;
}

.comment-login-message {
  background: #fff3cd;
  border: 1px solid #ffeaa7;
  color: #856404;
  padding: 16px;
  border-radius: 6px;
  text-align: center;
  margin-bottom: 20px;
  font-weight: 400;
}

.comment-login-message .login-link {
  color: #667eea;
  text-decoration: none;
  font-weight: bold;
}

.comment-login-message .login-link:hover {
  text-decoration: underline;
}

.comment-content-group {
  display: flex;
  gap: 16px;
  align-items: flex-start;
}

.comment-textarea {
  flex: 1;
  padding: 12px 16px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  font-size: 0.9rem;
  min-height: 80px;
  resize: vertical;
  font-family: inherit;
  transition: all 0.2s ease;
  background: #ffffff;
  line-height: 1.5;
}

.comment-textarea:focus {
  outline: none;
  border-color: #007bff;
}

.comment-submit-btn {
  padding: 12px 20px;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 6px;
  font-weight: 500;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.2s ease;
  white-space: nowrap;
}

.comment-submit-btn:hover {
  background: #0056b3;
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-bottom: 32px;
}

.comment-item {
  background: #ffffff;
  border-radius: 8px;
  padding: 20px;
  border: 1px solid #e2e8f0;
  width: 100%;
  box-sizing: border-box;
  transition: all 0.2s ease;
}

.comment-item:hover {
  background: #f8fafc;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}

.comment-info {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.comment-nickname {
  font-weight: 600;
  color: #1a202c;
  font-size: 0.95rem;
  font-family: inherit;
}

.comment-date {
  font-size: 0.8rem;
  color: #718096;
  font-weight: 400;
}

.comment-actions {
  display: flex;
  gap: 10px;
}

.comment-action-btn {
  padding: 4px 8px;
  border: 1px solid #e2e8f0;
  background: transparent;
  color: #718096;
  border-radius: 4px;
  font-size: 0.75rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  font-family: inherit;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  gap: 4px;
}

.comment-action-btn:hover {
  background: #f7fafc;
  border-color: #007bff;
  color: #007bff;
  transform: translateY(-1px);
}

.comment-action-btn.delete {
  color: #e53e3e;
  border-color: #fed7d7;
}

.comment-action-btn.delete:hover {
  background: #fed7d7;
  border-color: #e53e3e;
  color: #c53030;
}

.comment-action-btn.cancel {
  color: #718096;
  border-color: #e2e8f0;
}

.comment-action-btn.cancel:hover {
  background: #f7fafc;
  border-color: #cbd5e0;
  color: #4a5568;
}

.comment-content {
  color: #2d3748;
  line-height: 1.6;
  font-size: 0.9rem;
  font-weight: 400;
  background: #f8fafc;
  padding: 12px 16px;
  border-radius: 6px;
  border-left: 3px solid #007bff;
  margin-top: 12px;
  white-space: pre-wrap;
  word-wrap: break-word;
}

.edited-badge {
  color: #718096;
  font-size: 0.75rem;
  font-style: italic;
  margin-left: 8px;
}

.comment-edit-form {
  margin-top: 12px;
}

.comment-edit-textarea {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  font-size: 0.9rem;
  min-height: 80px;
  resize: vertical;
  font-family: inherit;
  transition: all 0.2s ease;
  background: #ffffff;
  line-height: 1.5;
  margin-bottom: 12px;
}

.comment-edit-textarea:focus {
  outline: none;
  border-color: #007bff;
}

.comment-edit-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

/* 알림 메시지 스타일 */
.notification-container {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 10000;
  max-width: 400px;
  animation: slideInRight 0.3s ease-out;
}

.notification-message {
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 16px 20px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  font-size: 0.9rem;
  color: #2d3748;
  backdrop-filter: blur(12px);
}

.notification-close {
  background: none;
  border: none;
  color: #718096;
  font-size: 1.2rem;
  cursor: pointer;
  padding: 0;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.2s ease;
}

.notification-close:hover {
  background: #f7fafc;
  color: #4a5568;
}

@keyframes slideInRight {
  from {
    transform: translateX(100%);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

/* 로그인 확인 모달 스타일 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10001;
  backdrop-filter: blur(4px);
}

.modal-content {
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  max-width: 400px;
  width: 90%;
  animation: modalSlideIn 0.3s ease-out;
  border: 1px solid #e2e8f0;
}

@keyframes modalSlideIn {
  from {
    opacity: 0;
    transform: translateY(-20px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.modal-header {
  padding: 20px 24px 0 24px;
  border-bottom: 1px solid #e2e8f0;
}

.modal-title {
  margin: 0;
  font-size: 1.25rem;
  font-weight: 600;
  color: #1a202c;
  font-family: inherit;
}

.modal-body {
  padding: 20px 24px;
}

.modal-body p {
  margin: 0;
  font-size: 1rem;
  color: #4a5568;
  line-height: 1.6;
  font-family: inherit;
}

.modal-footer {
  padding: 0 24px 20px 24px;
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.modal-btn {
  padding: 10px 20px;
  border-radius: 6px;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  font-family: inherit;
  border: 1px solid transparent;
}

.modal-btn.cancel {
  background: #f7fafc;
  color: #4a5568;
  border-color: #e2e8f0;
}

.modal-btn.cancel:hover {
  background: #edf2f7;
  border-color: #cbd5e0;
}

.modal-btn.confirm {
  background: #007bff;
  color: white;
  border-color: #007bff;
}

.modal-btn.confirm:hover {
  background: #0056b3;
  border-color: #0056b3;
}

/* 다크모드 스타일 */
:global(body.dark-mode) .post-detail-container {
  background: #1a202c;
}

:global(body.dark-mode) .post-header h1 {
  color: #f7fafc;
}

:global(body.dark-mode) .post-content,
:global(body.dark-mode) .post-content p,
:global(body.dark-mode) .post-content li,
:global(body.dark-mode) .post-content ul,
:global(body.dark-mode) .post-content ol,
:global(body.dark-mode) .post-content h1,
:global(body.dark-mode) .post-content h2,
:global(body.dark-mode) .post-content h3,
:global(body.dark-mode) .post-content h4,
:global(body.dark-mode) .post-content h5,
:global(body.dark-mode) .post-content h6,
:global(body.dark-mode) .post-content th,
:global(body.dark-mode) .post-content td,
:global(body.dark-mode) .post-content span,
:global(body.dark-mode) .post-content strong,
:global(body.dark-mode) .post-content em,
:global(body.dark-mode) .post-content table {
  color: #f7fafc !important;
  background: transparent !important;
}

:global(body.dark-mode) .post-content table {
  background: #23272a !important;
  border-color: #444 !important;
}

:global(body.dark-mode) .post-content th, 
:global(body.dark-mode) .post-content td {
  border-color: #444 !important;
}

:global(body.dark-mode) .post-content code, 
:global(body.dark-mode) .post-content pre {
  color: #ffe082 !important;
  background: #23272a !important;
}

:global(body.dark-mode) .post-content pre code {
  background: none !important;
  color: inherit !important;
}

:global(body.dark-mode) .post-header {
  border-bottom-color: #4a5568;
}

:global(body.dark-mode) .post-meta {
  color: #a0aec0;
}

:global(body.dark-mode) .post-summary {
  background: #2d3748;
  color: #e2e8f0;
  border-left-color: #4a5568;
}

:global(body.dark-mode) .like-button {
  background: #2d3748;
  border-color: #4a5568;
  color: #e2e8f0;
}

:global(body.dark-mode) .like-button:hover {
  background: #4a5568;
}

:global(body.dark-mode) .heart-icon {
  color: #a0aec0;
}

:global(body.dark-mode) .heart-icon.liked {
  color: #fc8181;
}

:global(body.dark-mode) .like-count {
  color: #e2e8f0;
}

:global(body.dark-mode) .back-button {
  background: #2d3748;
  color: #e2e8f0;
  border-color: #4a5568;
}

:global(body.dark-mode) .back-button:hover {
  background: #4a5568;
}

:global(body.dark-mode) .preview-notice {
  background: #742a2a;
  border: 1px solid #f56565;
  color: #fed7d7;
}

:global(body.dark-mode) .comment-title {
  color: #f7fafc;
}

:global(body.dark-mode) .comment-form {
  background: #2d3748;
  border-color: #4a5568;
}

:global(body.dark-mode) .comment-login-message {
  background: #742a2a;
  border: 1px solid #f56565;
  color: #fed7d7;
}

:global(body.dark-mode) .comment-login-message .login-link {
  color: #90cdf4;
}

:global(body.dark-mode) .comment-login-message .login-link:hover {
  color: #63b3ed;
}

:global(body.dark-mode) .comment-item {
  background: #2d3748;
  border-color: #4a5568;
}

:global(body.dark-mode) .comment-item:hover {
  background: #4a5568;
}

:global(body.dark-mode) .comment-textarea {
  background: #1a202c;
  border-color: #4a5568;
  color: #e2e8f0;
}

:global(body.dark-mode) .comment-textarea:focus {
  border-color: #ffe082;
  box-shadow: 0 0 0 3px rgba(255, 224, 130, 0.1);
}

:global(body.dark-mode) .comment-nickname {
  color: #f7fafc;
}

:global(body.dark-mode) .comment-content {
  color: #e2e8f0;
  background: #1a202c;
  border-left-color: #ffe082;
}

:global(body.dark-mode) .comment-action-btn {
  background: transparent;
  border-color: #4a5568;
  color: #a0aec0;
}

:global(body.dark-mode) .comment-action-btn:hover {
  background: #4a5568;
  border-color: #ffe082;
  color: #ffe082;
}

:global(body.dark-mode) .comment-action-btn.delete {
  color: #fc8181;
  border-color: #742a2a;
}

:global(body.dark-mode) .comment-action-btn.delete:hover {
  background: #742a2a;
  border-color: #fc8181;
  color: #fed7d7;
}

:global(body.dark-mode) .comment-action-btn.cancel {
  color: #a0aec0;
  border-color: #4a5568;
}

:global(body.dark-mode) .comment-action-btn.cancel:hover {
  background: #4a5568;
  border-color: #a0aec0;
  color: #e2e8f0;
}

:global(body.dark-mode) .edited-badge {
  color: #a0aec0;
}

:global(body.dark-mode) .comment-edit-textarea {
  background: #1a202c;
  border-color: #4a5568;
  color: #e2e8f0;
}

:global(body.dark-mode) .comment-edit-textarea:focus {
  border-color: #ffe082;
  box-shadow: 0 0 0 3px rgba(255, 224, 130, 0.1);
}

:global(body.dark-mode) .notification-message {
  background: #2d3748;
  border-color: #4a5568;
  color: #e2e8f0;
}

:global(body.dark-mode) .notification-close {
  color: #a0aec0;
}

:global(body.dark-mode) .notification-close:hover {
  background: #4a5568;
  color: #e2e8f0;
}

:global(body.dark-mode) .modal-content {
  background: #2d3748;
  border-color: #4a5568;
}

:global(body.dark-mode) .modal-header {
  border-bottom-color: #4a5568;
}

:global(body.dark-mode) .modal-title {
  color: #f7fafc;
}

:global(body.dark-mode) .modal-body p {
  color: #e2e8f0;
}

:global(body.dark-mode) .modal-btn.cancel {
  background: #4a5568;
  color: #e2e8f0;
  border-color: #4a5568;
}

:global(body.dark-mode) .modal-btn.cancel:hover {
  background: #718096;
  border-color: #718096;
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

/* 반응형 디자인 */
@media (max-width: 768px) {
  .post-detail-container {
    padding: 20px 15px;
  }
  
  .post-header h1 {
    font-size: 2rem;
  }
  
  .comment-content-group {
    flex-direction: column;
  }
  
  .comment-submit-btn {
    align-self: flex-end;
  }
}
</style> 
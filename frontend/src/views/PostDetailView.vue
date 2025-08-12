<template>
  <div class="post-detail-container" :class="{ 'dark-mode': isDarkMode }">
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
        <h1>{{ currentTitle }}</h1>
        <div class="post-meta" style="text-align: right;">
          <span>{{ formatDate(post.createdAt) }}</span>
        </div>
      </div>
    
    <div class="post-summary" v-if="currentSummary">
      <p>{{ currentSummary }}</p>
    </div>
    
    <div class="post-image-container" v-if="post.imageUrl">
      <img :src="post.imageUrl" :alt="post.title" class="post-detail-image">
    </div>
    
    <div class="post-content" id="post-content">
      <div v-html="currentContent"></div>
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
            <div class="comment-actions" v-if="isAuthenticated && user && canEditComment(comment)">
              <button v-if="editingCommentId !== comment.id" class="comment-action-btn edit-btn" @click="startEditComment(comment)" title="댓글 수정">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                  <path d="m18.5 2.5 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                </svg>
              </button>
              <button class="comment-action-btn delete-btn" @click="confirmDeleteComment(comment.id)" title="댓글 삭제">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M3 6h18"></path>
                  <path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6"></path>
                  <path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2"></path>
                </svg>
              </button>
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
              @keydown="handleEditCommentKeydown"
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
            @keydown="handleCommentKeydown"
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
import { ref, onMounted, nextTick, watch, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
import apiService from '@/services/api.js';
import { useAuth } from '@/composables/useAuth.js';
import '/public/css/post.css';

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
  postType: null,
  titleKo: '',
  titleJa: '',
  summaryKo: '',
  summaryJa: '',
  contentKo: '',
  contentJa: '',
  lang: 'ko'
});

const isLiked = ref(false);
const likeCount = ref(0);
const isPreview = ref(false);
const comments = ref([]);
const newComment = ref('');
const loading = ref(false);
const error = ref(null);

const isDarkMode = computed(() => {
  return document.body.classList.contains('dark-mode');
});

// Computed properties for safe data access
const currentTitle = computed(() => {
  const lang = route.query.lang || locale.value;
  if (lang === 'ja' && post.value.titleJa) {
    return post.value.titleJa;
  }
  return post.value.titleKo || post.value.title || '제목 없음';
});

const currentSummary = computed(() => {
  const lang = route.query.lang || locale.value;
  if (lang === 'ja' && post.value.summaryJa) {
    return post.value.summaryJa;
  }
  return post.value.summaryKo || post.value.summary || '';
});

const currentContent = computed(() => {
  const lang = route.query.lang || locale.value;
  if (lang === 'ja' && post.value.contentJa) {
    return post.value.contentJa;
  }
  return post.value.contentKo || post.value.content || '<p>내용이 없습니다.</p>';
});

// Methods
const loadPost = async () => {
  const postId = route.params.id;
  loading.value = true;
  error.value = null;
  
  console.log("=== POST DETAIL VIEW DEBUG ===");
  console.log("Loading post with ID:", postId);
  console.log("Current route params:", route.params);
  console.log("Current route query:", route.query);
  
  try {
    // URL 쿼리 파라미터의 언어 설정을 우선 사용, 없으면 현재 locale 사용
    const lang = route.query.lang || locale.value;
    console.log("Using language:", lang);
    
    const response = await apiService.getPost(postId, lang);
    console.log("API response:", response);

    if (response.success) {
      post.value = response.data;
      console.log("Post data loaded:", post.value);
      
      // 언어별 데이터 확인
      console.log("Post title (ko):", post.value.titleKo);
      console.log("Post title (ja):", post.value.titleJa);
      console.log("Post content (ko):", post.value.contentKo);
      console.log("Post content (ja):", post.value.contentJa);
    } else {
      console.error("API response not successful:", response);
      error.value = response.message || '게시글을 찾을 수 없습니다.';
      post.value = {
        id: postId,
        title: '게시글을 찾을 수 없습니다',
        summary: '',
        content: '<p>요청하신 게시글을 찾을 수 없습니다.</p>',
        imageUrl: '',
        createdAt: new Date(),
        // likeCount: 0
      };
    }
  } catch (error) {
    console.error('포스트 로드 실패:', error);
    console.error('Error details:', {
      message: error.message,
      stack: error.stack,
      name: error.name
    });
    error.value = '게시글을 불러오는데 실패했습니다: ' + error.message;
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

// 좋아요 상태 로드
const loadLikeStatus = async () => {
  try {
    console.log("=== LOAD LIKE STATUS DEBUG ===");
    console.log("Loading like status for post ID:", post.value.id);
    
    // URL 쿼리 파라미터의 언어 설정을 우선 사용, 없으면 현재 locale 사용
    const lang = route.query.lang || locale.value;
    const postType = post.value.postType || apiService.getPostTypeFromLang(lang);
    
    console.log("Using postType:", postType);
    console.log("User authenticated:", isAuthenticated.value);
    
    const response = await apiService.getLikeStatus(post.value.id, postType);
    console.log("Like status API response:", response);
    
    if (response && typeof response.likeCount !== 'undefined') {
      likeCount.value = response.likeCount;
      isLiked.value = response.liked || false;
      console.log("Like status loaded - count:", likeCount.value, "isLiked:", isLiked.value);
    } else {
      console.warn("Unexpected like status response format:", response);
      likeCount.value = 0;
      isLiked.value = false;
    }
  } catch (error) {
    console.error('좋아요 상태 로드 실패:', error);
    console.error('Error details:', {
      message: error.message,
      stack: error.stack,
      name: error.name
    });
    likeCount.value = 0;
    isLiked.value = false;
  }
};

// 댓글 로드
const loadComments = async () => {
  try {
    console.log("=== LOAD COMMENTS DEBUG ===");
    console.log("Loading comments for post ID:", post.value.id);
    
    // URL 쿼리 파라미터의 언어 설정을 우선 사용, 없으면 현재 locale 사용
    const lang = route.query.lang || locale.value;
    const postType = post.value.postType || apiService.getPostTypeFromLang(lang);
    
    console.log("Using postType:", postType);
    console.log("User authenticated:", isAuthenticated.value);
    
    const response = await apiService.getComments(post.value.id, postType);
    console.log("Comments API response:", response);
    
    if (response && response.success && Array.isArray(response.data)) {
      comments.value = response.data;
      console.log("Comments loaded:", comments.value.length, "comments");
      console.log("Comments data:", comments.value);
      console.log("Current user:", user.value);
      console.log("User authenticated:", isAuthenticated.value);
    } else if (Array.isArray(response)) {
      // 직접 배열로 반환되는 경우 (하위 호환성)
      comments.value = response;
      console.log("Comments loaded (legacy format):", comments.value.length, "comments");
    } else if (response && response.data && Array.isArray(response.data)) {
      // success 필드가 없지만 data 필드가 있는 경우
      comments.value = response.data;
      console.log("Comments loaded (data format):", comments.value.length, "comments");
    } else {
      console.warn("Unexpected comments response format:", response);
      comments.value = [];
    }
  } catch (error) {
    console.error('댓글 로드 실패:', error);
    console.error('Error details:', {
      message: error.message,
      stack: error.stack,
      name: error.name
    });
    comments.value = []; // 에러 발생 시 빈 배열로 초기화
  }
};

const formatDate = (date) => {
  if (!date) return '';
  const d = new Date(date);
  return `${d.getFullYear()}년 ${String(d.getMonth() + 1).padStart(2, '0')}월 ${String(d.getDate()).padStart(2, '0')}일`;
};

const handleLikeToggle = async () => {
  console.log("=== LIKE TOGGLE DEBUG ===");
  console.log("Like toggle clicked for post ID:", post.value.id);
  console.log("User authenticated:", isAuthenticated.value);
  
  if (!isAuthenticated.value) {
    console.log("User not authenticated, showing login modal");
    showLoginModal();
    return;
  }
  
  try {
    // URL 쿼리 파라미터의 언어 설정을 우선 사용, 없으면 현재 locale 사용
    const lang = route.query.lang || locale.value;
    const postType = post.value.postType || apiService.getPostTypeFromLang(lang);
    
    console.log("Using postType:", postType);
    console.log("Current like state - isLiked:", isLiked.value, "count:", likeCount.value);
    
    const response = await apiService.toggleLike(post.value.id, postType);
    console.log("Toggle like API response:", response);
    
    if (response && response.success) {
      // 즉각적인 사용자 경험을 위해 로컬 상태를 직접 업데이트
      isLiked.value = response.isLiked;
      if (response.isLiked) {
        likeCount.value++;
      } else {
        likeCount.value--;
      }
      console.log("Like state updated - isLiked:", isLiked.value, "count:", likeCount.value);
      showNotification(response.isLiked ? '좋아요를 눌렀습니다!' : '좋아요를 취소했습니다.');
    } else {
      console.warn("Toggle like response not successful:", response);
      showNotification(response?.message || '좋아요 처리 중 오류가 발생했습니다.');
    }
  } catch (error) {
    console.error('좋아요 토글 실패:', error);
    console.error('Error details:', {
      message: error.message,
      stack: error.stack,
      name: error.name
    });
    showNotification('좋아요 처리 중 오류가 발생했습니다.');
  }
};

const submitComment = async () => {
  console.log("=== SUBMIT COMMENT DEBUG ===");
  console.log("Submitting comment for post ID:", post.value.id);
  console.log("Comment content:", newComment.value);
  console.log("User authenticated:", isAuthenticated.value);
  
  if (!newComment.value.trim()) {
    console.log("Comment content is empty");
    showNotification('댓글 내용을 입력해주세요.');
    return;
  }

  if (!isAuthenticated.value) {
    console.log("User not authenticated, showing login modal");
    showLoginModal();
    return;
  }

  const token = localStorage.getItem('jwtToken');
  console.log("JWT token exists:", !!token);
  console.log("User info:", {
    hasToken: !!token,
    user: user.value
  });
  
  try {
    // URL 쿼리 파라미터의 언어 설정을 우선 사용, 없으면 현재 locale 사용
    const lang = route.query.lang || locale.value;
    const postType = post.value.postType || apiService.getPostTypeFromLang(lang);
    
    console.log("Using postType:", postType);
    
    const commentData = {
      content: newComment.value
    };
    console.log("Comment data to send:", commentData);
    
    const response = await apiService.addComment(post.value.id, postType, commentData);
    console.log("Add comment API response:", response);
    
    if (response && (response.success || response.id)) {
      // 새 댓글을 목록에 추가 (최신 댓글이 아래에 오도록)
      const newCommentData = response.data || response;
      comments.value.push(newCommentData);
      newComment.value = '';
      console.log("Comment added successfully, total comments:", comments.value.length);
      showNotification('댓글이 작성되었습니다.');
    } else {
      console.warn("Add comment response not successful:", response);
      showNotification(response?.message || '댓글 작성 중 오류가 발생했습니다.');
    }
  } catch (error) {
    console.error('댓글 작성 실패:', error);
    console.error('Error details:', {
      message: error.message,
      stack: error.stack,
      name: error.name
    });
    
    if (error.message && error.message.includes('401')) {
      showNotification('인증이 만료되었습니다. 다시 로그인해주세요.');
      router.push('/user/login');
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
    
    if (response && (response.success || response.id)) {
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
      showNotification(response?.message || '댓글 수정 중 오류가 발생했습니다.');
    }
  } catch (error) {
    console.error('댓글 수정 실패:', error);
    
    if (error.message && error.message.includes('401')) {
      showNotification('인증이 만료되었습니다. 다시 로그인해주세요.');
      router.push('/user/login');
    } else if (error.message && error.message.includes('403')) {
      showNotification('댓글을 수정할 권한이 없습니다.');
    } else {
      showNotification('댓글 수정 중 오류가 발생했습니다.');
    }
  }
};

// 댓글 작성 키보드 이벤트 처리
const handleCommentKeydown = (event) => {
  if (event.key === 'Enter' && !event.shiftKey) {
    event.preventDefault();
    submitComment();
  }
};

// 댓글 수정 키보드 이벤트 처리
const handleEditCommentKeydown = (event) => {
  if (event.key === 'Enter' && !event.shiftKey) {
    event.preventDefault();
    updateComment(editingCommentId.value);
  }
};

// 댓글 편집 권한 확인 함수
const canEditComment = (comment) => {
  if (!user.value) return false;
  
  // 다양한 사용자 ID 필드명에 대응
  const commentUserId = comment.userId || comment.user_id || comment.user?.id;
  const commentUsername = comment.username || comment.user?.username;
  const commentNickname = comment.nickname || comment.user?.nickname;
  
  // 현재 사용자 정보와 비교
  return (
    commentUserId === user.value.id ||
    commentUsername === user.value.username ||
    commentNickname === user.value.nickname
  );
};

const deleteCommentDirectly = async (commentId) => {
  try {
    console.log('=== DELETE COMMENT DEBUG ===');
    console.log('Deleting comment ID:', commentId);
    console.log('User authenticated:', isAuthenticated.value);
    console.log('User info:', user.value);
    console.log('JWT token exists:', !!localStorage.getItem('jwtToken'));
    
    const response = await apiService.deleteComment(commentId);
    console.log('Delete API response:', response);
    
    if (response && (response.success || response === true || response.message)) {
      // 로컬 상태에서 댓글 제거
      comments.value = comments.value.filter(c => c.id !== commentId);
      showNotification('댓글이 삭제되었습니다.');
    } else {
      showNotification(response?.message || '댓글 삭제 중 오류가 발생했습니다.');
    }
  } catch (error) {
    console.error('댓글 삭제 실패:', error);
    console.error('Error details:', {
      message: error.message,
      stack: error.stack,
      name: error.name
    });
    
    if (error.message && error.message.includes('401')) {
      console.log('401 Unauthorized error - redirecting to login');
      showNotification('인증이 만료되었습니다. 다시 로그인해주세요.');
      // 로그인 페이지로 리다이렉트
      router.push('/user/login');
    } else if (error.message && error.message.includes('403')) {
      showNotification('댓글을 삭제할 권한이 없습니다.');
    } else {
      showNotification('댓글 삭제 중 오류가 발생했습니다.');
    }
  }
};

const confirmDeleteComment = (commentId) => {
  // 커스텀 확인 모달 표시
  const comment = comments.value.find(c => c.id === commentId);
  const commentText = comment ? comment.content.substring(0, 50) + (comment.content.length > 50 ? '...' : '') : '';
  
  nextTick(() => {
    // 모달 요소들 확인
    const modalTitle = document.getElementById('modal-title');
    const modalMessage = document.getElementById('modal-message');
    const modal = document.getElementById('login-confirm-modal');
    const confirmBtn = document.querySelector('.modal-btn.confirm');
    
    if (!modalTitle || !modalMessage || !modal || !confirmBtn) {
      console.error('Modal elements not found');
      // 대안으로 confirm 사용
      if (confirm(`정말로 이 댓글을 삭제하시겠습니까?\n\n"${commentText}"\n\n삭제된 댓글은 복구할 수 없습니다.`)) {
        deleteCommentDirectly(commentId);
      }
      return;
    }
    
    // 모달 제목과 메시지 설정
    modalTitle.textContent = '댓글 삭제';
    modalMessage.innerHTML = `정말로 이 댓글을 삭제하시겠습니까?<br><br><em>"${commentText}"</em><br><br>삭제된 댓글은 복구할 수 없습니다.`;
    
    // 모달 표시
    modal.style.display = 'flex';
    
    // 확인 버튼에 삭제 이벤트 연결
    const originalOnClick = confirmBtn.onclick;
    
    confirmBtn.onclick = async () => {
      await deleteCommentDirectly(commentId);
      closeLoginModal();
      // 원래 이벤트 복원
      confirmBtn.onclick = originalOnClick;
    };
  });
};



const showLoginModal = () => {
  nextTick(() => {
    const modal = document.getElementById('login-confirm-modal');
    if (modal) {
      modal.style.display = 'flex';
    }
  });
};

const closeLoginModal = () => {
  nextTick(() => {
    const modal = document.getElementById('login-confirm-modal');
    if (modal) {
      modal.style.display = 'none';
    }
  });
};

const confirmLogin = () => {
  closeLoginModal();
  router.push('/user/login');
};

const showNotification = (message) => {
  nextTick(() => {
    const container = document.getElementById('notification-container');
    const text = document.getElementById('notification-text');
    
    if (!container || !text) {
      console.error('Notification elements not found');
      // 대안으로 alert 사용
      alert(message);
      return;
    }
    
    text.textContent = message;
    container.style.display = 'block';
    
    // 3초 후 자동으로 닫기
    setTimeout(() => {
      closeNotification();
    }, 3000);
  });
};

const closeNotification = () => {
  nextTick(() => {
    const container = document.getElementById('notification-container');
    if (container) {
      container.style.display = 'none';
    }
  });
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
    if (window.Prism) {
      const codeBlocks = document.querySelectorAll('#post-content pre code');
      codeBlocks.forEach((block) => {
        window.Prism.highlightElement(block);
      });
    }
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

<style>
/* 제공된 CSS 스타일을 여기에 포함 */
.post-detail-container {
  max-width: 1000px;
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

/* post.css에서 기본 스타일을 제공하므로 여기서는 추가 스타일만 정의 */
.post-content {
  margin-bottom: 64px;
}

/* post.css에서 기본 스타일을 제공하므로 여기서는 추가 스타일만 정의 */
.post-content h2 {
  border-bottom: 2px solid #e2e8f0;
  padding-bottom: 0.5rem;
}

.post-content h3 {
  font-size: 1.5rem;
}

.post-content ul,
.post-content ol {
  padding-left: 2rem;
}

/* post.css에서 기본 코드 스타일을 제공하므로 여기서는 추가 스타일만 정의 */
.post-content pre {
  padding: 16px;
  border-radius: 8px;
  font-size: 1rem;
  font-family: 'Fira Mono', 'Consolas', 'Menlo', 'Monaco', monospace;
  overflow-x: auto;
  margin: 16px 0;
  box-shadow: 0 2px 12px rgba(0,0,0,0.10);
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
  gap: 6px;
  opacity: 0;
  transition: opacity 0.2s ease;
}

.comment-item:hover .comment-actions {
  opacity: 1;
}

.comment-action-btn {
  width: 28px;
  height: 28px;
  border: none;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(8px);
  color: #718096;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
  font-family: inherit;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  border: 1px solid rgba(226, 232, 240, 0.3);
}

.comment-action-btn:hover {
  background: rgba(0, 123, 255, 0.1);
  border-color: rgba(0, 123, 255, 0.3);
  color: #007bff;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 123, 255, 0.15);
}

.comment-action-btn.edit-btn:hover {
  background: rgba(34, 197, 94, 0.1);
  border-color: rgba(34, 197, 94, 0.3);
  color: #22c55e;
  box-shadow: 0 2px 8px rgba(34, 197, 94, 0.15);
}

.comment-action-btn.delete-btn:hover {
  background: rgba(239, 68, 68, 0.1);
  border-color: rgba(239, 68, 68, 0.3);
  color: #ef4444;
  box-shadow: 0 2px 8px rgba(239, 68, 68, 0.15);
}

.comment-action-btn:active {
  transform: translateY(0);
  transition: all 0.1s ease;
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

/* post.css에서 다크모드 스타일을 제공하므로 여기서는 추가 스타일만 정의 */
.post-detail-container.dark-mode .post-detail-container {
  background: #1a202c;
}

.post-detail-container.dark-mode .post-header h1 {
  color: #f7fafc;
}

.post-detail-container.dark-mode .post-content {
  color: #e2e8f0;
}

/* post.css에서 다크모드 스타일을 제공하므로 여기서는 추가 스타일만 정의 */

.post-detail-container.dark-mode .post-header {
  border-bottom-color: #4a5568;
}

/* 좋아요 버튼 다크모드 */
.post-detail-container.dark-mode .like-button {
  background: #2d3748;
  border-color: #4a5568;
  color: #e2e8f0;
}

.post-detail-container.dark-mode .like-button:hover {
  background: #4a5568;
}

.post-detail-container.dark-mode .heart-icon {
  color: #a0aec0;
}

.post-detail-container.dark-mode .heart-icon.liked {
  color: #fc8181;
}

.post-detail-container.dark-mode .like-count {
  color: #e2e8f0;
}

/* 댓글 시스템 다크모드 */
.post-detail-container.dark-mode .comment-title {
  color: #f7fafc;
}

.post-detail-container.dark-mode .comment-form {
  background: #2d3748;
  border-color: #4a5568;
}

.post-detail-container.dark-mode .comment-login-message {
  background: #742a2a;
  border: 1px solid #f56565;
  color: #fed7d7;
}

.post-detail-container.dark-mode .comment-login-message .login-link {
  color: #90cdf4;
}

.post-detail-container.dark-mode .comment-login-message .login-link:hover {
  color: #63b3ed;
}

.post-detail-container.dark-mode .comment-item {
  background: #2d3748;
  border-color: #4a5568;
}

.post-detail-container.dark-mode .comment-item:hover {
  background: #4a5568;
}

.post-detail-container.dark-mode .comment-textarea {
  background: #1a202c;
  border-color: #4a5568;
  color: #e2e8f0;
}

.post-detail-container.dark-mode .comment-textarea:focus {
  border-color: #ffe082;
  box-shadow: 0 0 0 3px rgba(255, 224, 130, 0.1);
}

.post-detail-container.dark-mode .comment-nickname {
  color: #f7fafc;
}

.post-detail-container.dark-mode .comment-content {
  color: #e2e8f0;
  background: #1a202c;
  border-left-color: #ffe082;
}

.post-detail-container.dark-mode .comment-action-btn {
  background: rgba(45, 55, 72, 0.3);
  border-color: rgba(74, 85, 104, 0.4);
  color: #a0aec0;
}

.post-detail-container.dark-mode .comment-action-btn:hover {
  background: rgba(74, 85, 104, 0.6);
  border-color: rgba(255, 224, 130, 0.4);
  color: #ffe082;
  box-shadow: 0 2px 8px rgba(255, 224, 130, 0.2);
}

.post-detail-container.dark-mode .comment-action-btn.edit-btn:hover {
  background: rgba(34, 197, 94, 0.2);
  border-color: rgba(34, 197, 94, 0.4);
  color: #4ade80;
  box-shadow: 0 2px 8px rgba(34, 197, 94, 0.2);
}

.post-detail-container.dark-mode .comment-action-btn.delete-btn:hover {
  background: rgba(239, 68, 68, 0.2);
  border-color: rgba(239, 68, 68, 0.4);
  color: #f87171;
  box-shadow: 0 2px 8px rgba(239, 68, 68, 0.2);
}

.post-detail-container.dark-mode .comment-action-btn.cancel {
  color: #a0aec0;
  border-color: #4a5568;
}

.post-detail-container.dark-mode .comment-action-btn.cancel:hover {
  background: #4a5568;
  border-color: #a0aec0;
  color: #e2e8f0;
}

.post-detail-container.dark-mode .edited-badge {
  color: #a0aec0;
}

.post-detail-container.dark-mode .comment-edit-textarea {
  background: #1a202c;
  border-color: #4a5568;
  color: #e2e8f0;
}

.post-detail-container.dark-mode .comment-edit-textarea:focus {
  border-color: #ffe082;
  box-shadow: 0 0 0 3px rgba(255, 224, 130, 0.1);
}

/* 뒤로가기 버튼 다크모드 */
.post-detail-container.dark-mode .back-button {
  background: #2d3748;
  color: #e2e8f0;
  border-color: #4a5568;
}

.post-detail-container.dark-mode .back-button:hover {
  background: #4a5568;
}

/* 미리보기 알림 다크모드 */
.post-detail-container.dark-mode .preview-notice {
  background: #742a2a;
  border: 1px solid #f56565;
  color: #fed7d7;
}

.post-detail-container.dark-mode .post-meta {
  color: #a0aec0;
}

.post-detail-container.dark-mode .post-summary {
  background: #2d3748;
  color: #e2e8f0;
  border-left-color: #4a5568;
}

.post-detail-container.dark-mode .notification-message {
  background: #2d3748;
  border-color: #4a5568;
  color: #e2e8f0;
}

.post-detail-container.dark-mode .notification-close {
  color: #a0aec0;
}

.post-detail-container.dark-mode .notification-close:hover {
  background: #4a5568;
  color: #e2e8f0;
}

.post-detail-container.dark-mode .modal-content {
  background: #2d3748;
  border-color: #4a5568;
}

.post-detail-container.dark-mode .modal-header {
  border-bottom-color: #4a5568;
}

.post-detail-container.dark-mode .modal-title {
  color: #f7fafc;
}

.post-detail-container.dark-mode .modal-body p {
  color: #e2e8f0;
}

.post-detail-container.dark-mode .modal-btn.cancel {
  background: #4a5568;
  color: #e2e8f0;
  border-color: #4a5568;
}

.post-detail-container.dark-mode .modal-btn.cancel:hover {
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

/* post.css에서 반응형 스타일을 제공하므로 여기서는 추가 스타일만 정의 */

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
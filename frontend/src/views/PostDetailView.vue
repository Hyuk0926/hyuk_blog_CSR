<template>
  <div class="max-w-4xl mx-auto px-5 py-10">
    <!-- 알림 메시지 컨테이너 -->
    <div id="notification-container" class="fixed top-5 right-5 z-50 max-w-sm hidden">
      <div class="bg-white border border-gray-200 rounded-lg p-4 shadow-lg flex items-center justify-between gap-3 text-sm text-gray-800 backdrop-blur-sm dark:bg-gray-800 dark:border-gray-700 dark:text-gray-200">
        <span id="notification-text"></span>
        <button class="bg-transparent border-none text-gray-500 text-xl cursor-pointer p-0 w-5 h-5 flex items-center justify-center rounded-full transition-colors duration-200 hover:bg-gray-100 hover:text-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-gray-200" @click="closeNotification">×</button>
      </div>
    </div>
    
    <!-- 로그인 확인 모달 -->
    <div id="login-confirm-modal" class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 hidden backdrop-blur-sm">
      <div class="bg-white rounded-xl shadow-2xl max-w-md w-11/12 animate-[modalSlideIn_0.3s_ease-out] border border-gray-200 dark:bg-gray-800 dark:border-gray-700">
        <div class="p-6 pb-0 border-b border-gray-200 dark:border-gray-700">
          <h3 class="text-xl font-semibold text-gray-900 m-0 dark:text-gray-100" id="modal-title">로그인 필요</h3>
        </div>
        <div class="p-6">
          <p class="m-0 text-base text-gray-700 leading-relaxed dark:text-gray-300" id="modal-message">좋아요 기능을 사용하려면 로그인이 필요합니다. 로그인 페이지로 이동하시겠습니까?</p>
        </div>
        <div class="p-6 pt-0 flex gap-3 justify-end">
          <button class="px-5 py-2.5 rounded-md text-sm font-medium cursor-pointer transition-all duration-200 font-sans border border-transparent bg-gray-100 text-gray-700 border-gray-200 hover:bg-gray-200 hover:border-gray-300 dark:bg-gray-700 dark:text-gray-300 dark:border-gray-700 dark:hover:bg-gray-600" @click="closeLoginModal">취소</button>
          <button class="px-5 py-2.5 rounded-md text-sm font-medium cursor-pointer transition-all duration-200 font-sans border border-transparent bg-blue-600 text-white border-blue-600 hover:bg-blue-700 hover:border-blue-700" @click="confirmLogin">확인</button>
        </div>
      </div>
    </div>
    
    <!-- 로딩 상태 -->
    <div v-if="loading" class="flex flex-col items-center justify-center py-16 text-center">
      <div class="w-10 h-10 border-4 border-gray-300 border-t-blue-600 rounded-full animate-spin mb-5"></div>
      <p class="text-gray-600 dark:text-gray-400">게시글을 불러오는 중...</p>
    </div>

    <!-- 에러 상태 -->
    <div v-else-if="error" class="flex flex-col items-center justify-center py-16 text-center">
      <p class="text-red-600 mb-5 text-lg">{{ error }}</p>
      <button @click="loadPost" class="px-5 py-2.5 bg-blue-600 text-white border-none rounded-md cursor-pointer text-sm transition-colors duration-200 hover:bg-blue-700">다시 시도</button>
    </div>

    <!-- 게시글 내용 -->
    <div v-else>
      <div class="border-b border-gray-200 pb-5 mb-8 dark:border-gray-700">
        <h1 class="text-4xl font-bold text-gray-900 mb-4 leading-tight dark:text-white">{{ currentTitle }}</h1>
        <div class="text-right">
          <span class="text-sm text-gray-500 font-normal dark:text-gray-400">{{ formatDate(post.createdAt) }}</span>
        </div>
      </div>
    
    <div class="my-8 mx-auto max-w-4xl text-lg text-gray-700 bg-gray-50 rounded-lg p-6 font-normal leading-relaxed border-l-4 border-gray-200 dark:bg-gray-800 dark:text-gray-300 dark:border-gray-700" v-if="currentSummary">
      <p class="m-0 text-lg text-inherit font-normal">{{ currentSummary }}</p>
    </div>
    
    <div class="my-8 mx-auto max-w-4xl text-center" v-if="post.imageUrl">
      <img :src="post.imageUrl" :alt="post.title" class="max-w-4xl w-full h-auto block mx-auto rounded-xl object-contain shadow-lg">
    </div>
    
         <div class="max-w-6xl mx-auto my-10 bg-white rounded-xl shadow-sm p-8 transition-all duration-500 dark:bg-gray-800 dark:shadow-none dark:border dark:border-gray-700" id="post-content">
       <div v-html="currentContent" class="prose prose-base max-w-none dark:prose-invert prose-headings:text-gray-900 prose-headings:dark:text-white prose-p:text-gray-700 prose-p:dark:text-gray-300 prose-strong:text-gray-900 prose-strong:dark:text-white prose-code:text-gray-900 prose-code:dark:text-gray-300 prose-pre:bg-gray-900 prose-pre:dark:bg-gray-900 prose-pre:text-gray-100 prose-pre:dark:text-gray-100"></div>
     </div>
    
    <!-- 미리보기 모드 표시 -->
    <div class="bg-yellow-50 border border-yellow-200 text-yellow-800 p-4 rounded-lg my-5 mx-auto max-w-2xl text-center font-medium dark:bg-yellow-900/20 dark:border-yellow-800 dark:text-yellow-200" v-if="isPreview">
      <span>미리보기 모드</span>
    </div>
    
    <!-- 좋아요 버튼 (미리보기 모드에서는 비활성화) -->
    <div class="text-center my-10" v-if="!isPreview">
      <button class="inline-flex items-center gap-2 px-5 py-2.5 bg-white border border-gray-200 rounded-md cursor-pointer transition-all duration-200 font-medium text-sm text-gray-700 hover:bg-gray-50 hover:border-gray-300 dark:bg-gray-800 dark:border-gray-700 dark:text-gray-300 dark:hover:bg-gray-700" @click="handleLikeToggle" :data-post-id="post.id">
        <span class="text-xl transition-all duration-200" :class="{ 'text-red-500': isLiked, 'text-gray-400': !isLiked }">{{ isLiked ? '♥' : '♡' }}</span>
        <span class="font-medium text-gray-700 dark:text-gray-300">{{ likeCount }}</span>
      </button>
    </div>
    
    <div class="mt-10 mb-5">
      <a v-if="isPreview" href="/admin" class="inline-flex items-center gap-1.5 px-4 py-2.5 bg-white text-gray-700 no-underline rounded-md font-medium text-sm font-sans transition-all duration-200 border border-gray-200 hover:bg-gray-50 hover:border-gray-300 dark:bg-gray-800 dark:text-gray-300 dark:border-gray-700 dark:hover:bg-gray-700">관리자 페이지로 돌아가기</a>
      <a v-else href="/knowledge" class="inline-flex items-center gap-1.5 px-4 py-2.5 bg-white text-gray-700 no-underline rounded-md font-medium text-sm font-sans transition-all duration-200 border border-gray-200 hover:bg-gray-50 hover:border-gray-300 dark:bg-gray-800 dark:text-gray-300 dark:border-gray-700 dark:hover:bg-gray-700">목록으로 돌아가기</a>
    </div>
    
    <!-- 댓글 시스템 (미리보기 모드에서는 비활성화) -->
    <div class="mt-15 max-w-2xl mx-auto" v-if="!isPreview">
      <h3 class="text-2xl font-semibold text-gray-900 mb-6 dark:text-white">댓글</h3>
      
      <!-- 댓글 목록 -->
      <div id="comment-list" class="flex flex-col gap-5 mb-8">
        <div v-for="comment in comments" :key="comment.id" class="group bg-white rounded-lg p-5 border border-gray-200 w-full box-border transition-all duration-200 hover:bg-gray-50 dark:bg-gray-800 dark:border-gray-700 dark:hover:bg-gray-700">
          <div class="flex justify-between items-start mb-4">
            <div class="flex flex-col gap-1.5">
              <div class="font-semibold text-gray-900 text-sm dark:text-white">{{ comment.nickname }}</div>
              <div class="text-xs text-gray-500 font-normal dark:text-gray-400">
                {{ formatDate(comment.createdAt) }}
                <span v-if="comment.isEdited" class="text-gray-500 text-xs italic ml-2 dark:text-gray-400">(수정됨)</span>
              </div>
            </div>
            <div class="flex gap-1.5 opacity-0 transition-opacity duration-200 group-hover:opacity-100" v-if="isAuthenticated && user && canEditComment(comment)">
              <button v-if="editingCommentId !== comment.id" class="w-7 h-7 border-none bg-white/10 backdrop-blur-sm text-gray-500 rounded-md cursor-pointer transition-all duration-200 font-sans flex items-center justify-center relative border border-gray-200/30 hover:bg-blue-100 hover:border-blue-300 hover:text-blue-600 hover:-translate-y-0.5 hover:shadow-md dark:bg-gray-700/30 dark:border-gray-600/30 dark:text-gray-400 dark:hover:bg-blue-900/20 dark:hover:border-blue-400/40 dark:hover:text-blue-400" @click="startEditComment(comment)" title="댓글 수정">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                  <path d="m18.5 2.5 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                </svg>
              </button>
              <button class="w-7 h-7 border-none bg-white/10 backdrop-blur-sm text-gray-500 rounded-md cursor-pointer transition-all duration-200 font-sans flex items-center justify-center relative border border-gray-200/30 hover:bg-red-100 hover:border-red-300 hover:text-red-600 hover:-translate-y-0.5 hover:shadow-md dark:bg-gray-700/30 dark:border-gray-600/30 dark:text-gray-400 dark:hover:bg-red-900/20 dark:hover:border-red-400/40 dark:hover:text-red-400" @click="confirmDeleteComment(comment.id)" title="댓글 삭제">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M3 6h18"></path>
                  <path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6"></path>
                  <path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2"></path>
                </svg>
              </button>
            </div>
          </div>
          
          <!-- 댓글 내용 (수정 모드가 아닐 때) -->
          <div v-if="editingCommentId !== comment.id" class="text-gray-700 leading-relaxed text-sm font-normal bg-gray-50 p-3 rounded-md border-l-4 border-blue-600 mt-3 whitespace-pre-wrap break-words dark:text-gray-300 dark:bg-gray-900 dark:border-blue-400">{{ comment.content }}</div>
          
          <!-- 댓글 수정 폼 (수정 모드일 때) -->
          <div v-if="editingCommentId === comment.id" class="mt-3">
            <textarea 
              v-model="editingContent"
              class="w-full p-3 border border-gray-200 rounded-md text-sm min-h-20 resize-y font-sans transition-all duration-200 bg-white leading-relaxed mb-3 focus:outline-none focus:border-blue-600 dark:bg-gray-900 dark:border-gray-700 dark:text-gray-300 dark:focus:border-blue-400"
              placeholder="댓글을 수정하세요..."
              @keydown="handleEditCommentKeydown"
            ></textarea>
            <div class="flex gap-2.5 justify-end">
              <button class="px-3 py-1.5 border-none bg-blue-600 text-white rounded-md font-medium text-sm cursor-pointer transition-all duration-200 font-sans border border-transparent hover:bg-blue-700" @click="updateComment(comment.id)">저장</button>
              <button class="px-3 py-1.5 border-none bg-gray-100 text-gray-700 border-gray-200 rounded-md font-medium text-sm cursor-pointer transition-all duration-200 font-sans border hover:bg-gray-200 hover:border-gray-300 dark:bg-gray-700 dark:text-gray-300 dark:border-gray-700 dark:hover:bg-gray-600" @click="cancelEditComment">취소</button>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 댓글 작성 폼 -->
      <div class="bg-white rounded-lg p-6 border border-gray-200 mb-8 w-full box-border dark:bg-gray-800 dark:border-gray-700" v-if="isAuthenticated">
        <div class="flex gap-4 items-start">
          <textarea 
            id="comment-content" 
            placeholder="댓글을 입력하세요..." 
            class="flex-1 p-3 border border-gray-200 rounded-md text-sm min-h-20 resize-y font-sans transition-all duration-200 bg-white leading-relaxed focus:outline-none focus:border-blue-600 dark:bg-gray-900 dark:border-gray-700 dark:text-gray-300 dark:focus:border-blue-400"
            v-model="newComment"
            @keydown="handleCommentKeydown"
          ></textarea>
          <button id="comment-submit" class="px-5 py-3 bg-blue-600 text-white border-none rounded-md font-medium text-sm cursor-pointer transition-all duration-200 whitespace-nowrap hover:bg-blue-700" @click="submitComment">댓글 작성</button>
        </div>
      </div>
      
      <!-- 로그인 필요 메시지 -->
      <div class="bg-yellow-50 border border-yellow-200 text-yellow-800 p-4 rounded-md text-center mb-5 font-normal dark:bg-yellow-900/20 dark:border-yellow-800 dark:text-yellow-200" v-if="!isAuthenticated">
        <p class="m-0">댓글을 작성하려면 <a href="/user/login" class="text-blue-600 no-underline font-bold hover:underline dark:text-blue-400">로그인</a>이 필요합니다.</p>
      </div>
    </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick, watch, computed } from 'vue';
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

const isDarkMode = ref(false);

const checkDarkMode = () => {
  isDarkMode.value = document.body.classList.contains('dark-mode');
};

const handleDarkModeToggle = (event) => {
  isDarkMode.value = event.detail.isDarkMode;
  updateDarkModeClass();
};

const updateDarkModeClass = () => {
  if (isDarkMode.value) {
    document.documentElement.classList.add('dark');
  } else {
    document.documentElement.classList.remove('dark');
  }
};

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
  
  // admin 사용자는 모든 댓글을 편집/삭제할 수 있음
  const isAdmin = user.value.username === 'admin' || user.value.username === 'admin_jp';
  if (isAdmin) return true;
  
  // 일반 사용자는 자신의 댓글만 편집/삭제할 수 있음
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
    console.log('Response type:', typeof response);
    console.log('Response success:', response?.success);
    console.log('Response message:', response?.message);
    
    if (response && (response.success === true || response === true || response.message)) {
      console.log('Comment deletion successful, updating local state');
      // 로컬 상태에서 댓글 제거
      comments.value = comments.value.filter(c => c.id !== commentId);
      showNotification('댓글이 삭제되었습니다.');
    } else {
      console.log('Comment deletion failed or unexpected response format');
      showNotification(response?.message || '댓글 삭제 중 오류가 발생했습니다.');
    }
  } catch (error) {
    console.error('댓글 삭제 실패:', error);
    console.error('Error details:', {
      message: error.message,
      stack: error.stack,
      name: error.name
    });
    
    // 401 에러가 발생해도 로그인 페이지로 이동하지 않고 에러 메시지만 표시
    if (error.message && error.message.includes('401')) {
      console.log('401 Unauthorized error - showing error message only');
      showNotification('댓글 삭제 중 인증 오류가 발생했습니다.');
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
    const confirmBtn = modal ? modal.querySelector('button:last-child') : null; // 두 번째 버튼 (확인 버튼)
    
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
    // Vue의 이벤트 리스너도 제거
    confirmBtn.removeAttribute('onclick');
    const newConfirmBtn = confirmBtn.cloneNode(true);
    confirmBtn.parentNode.replaceChild(newConfirmBtn, confirmBtn);
    
    newConfirmBtn.onclick = async () => {
      try {
        await deleteCommentDirectly(commentId);
        closeLoginModal();
      } catch (error) {
        console.error('Error in confirmBtn.onclick:', error);
        closeLoginModal();
      }
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
      wrapper.className = 'code-block-wrapper relative';
      block.parentNode.insertBefore(wrapper, block);
      wrapper.appendChild(block);
      
      // 코드 블록의 언어/기능에 따른 색상 적용
      applyCodeBlockStyling(block, wrapper);
      
      const copyBtn = document.createElement('button');
      copyBtn.className = 'copy-btn absolute top-2 right-2 bg-white/10 backdrop-blur-sm text-gray-400 border border-white/20 rounded-lg p-2 text-xs cursor-pointer opacity-30 z-10 transition-all duration-300 font-medium min-w-8 h-8 flex items-center justify-center hover:bg-white/20 hover:text-white hover:border-white/30 hover:-translate-y-0.5 hover:shadow-lg active:translate-y-0 active:transition-all active:duration-100 disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none';
      copyBtn.innerHTML = `
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <rect x="9" y="9" width="13" height="13" rx="2" ry="2"></rect>
          <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"></path>
        </svg>
      `;
      copyBtn.onclick = () => copyCode(block, copyBtn);
      wrapper.appendChild(copyBtn);
      
      // 호버 시 버튼 표시
      wrapper.addEventListener('mouseenter', () => {
        copyBtn.style.opacity = '1';
        copyBtn.style.transform = 'translateY(0)';
      });
      
      wrapper.addEventListener('mouseleave', () => {
        copyBtn.style.opacity = '0.3';
        copyBtn.style.transform = 'translateY(0)';
      });
    });
  });
};

// 코드 블록의 기능별 색상 적용
const applyCodeBlockStyling = (block, wrapper) => {
  const codeElement = block.querySelector('code');
  if (!codeElement) return;
  
  // 클래스명에서 언어/기능 추출
  const classNames = codeElement.className || '';
  const languageMatch = classNames.match(/language-(\w+)/);
  const language = languageMatch ? languageMatch[1] : '';
  
  // 기능별 색상 매핑
  const colorMap = {
    // 프론트엔드
    'javascript': { border: 'border-blue-500', bg: 'bg-blue-50', text: 'text-blue-700' },
    'js': { border: 'border-blue-500', bg: 'bg-blue-50', text: 'text-blue-700' },
    'typescript': { border: 'border-blue-600', bg: 'bg-blue-50', text: 'text-blue-700' },
    'ts': { border: 'border-blue-600', bg: 'bg-blue-50', text: 'text-blue-700' },
    'vue': { border: 'border-green-500', bg: 'bg-green-50', text: 'text-green-700' },
    'html': { border: 'border-orange-500', bg: 'bg-orange-50', text: 'text-orange-700' },
    'css': { border: 'border-purple-500', bg: 'bg-purple-50', text: 'text-purple-700' },
    'scss': { border: 'border-pink-500', bg: 'bg-pink-50', text: 'text-pink-700' },
    'sass': { border: 'border-pink-500', bg: 'bg-pink-50', text: 'text-pink-700' },
    
    // 백엔드
    'java': { border: 'border-red-500', bg: 'bg-red-50', text: 'text-red-700' },
    'spring': { border: 'border-green-600', bg: 'bg-green-50', text: 'text-green-700' },
    'python': { border: 'border-yellow-500', bg: 'bg-yellow-50', text: 'text-yellow-700' },
    'sql': { border: 'border-indigo-500', bg: 'bg-indigo-50', text: 'text-indigo-700' },
    'mysql': { border: 'border-indigo-500', bg: 'bg-indigo-50', text: 'text-indigo-700' },
    
    // 기타
    'bash': { border: 'border-gray-500', bg: 'bg-gray-50', text: 'text-gray-700' },
    'shell': { border: 'border-gray-500', bg: 'bg-gray-50', text: 'text-gray-700' },
    'json': { border: 'border-yellow-600', bg: 'bg-yellow-50', text: 'text-yellow-700' },
    'xml': { border: 'border-orange-600', bg: 'bg-orange-50', text: 'text-orange-700' },
    'yaml': { border: 'border-blue-500', bg: 'bg-blue-50', text: 'text-blue-700' },
    'yml': { border: 'border-blue-500', bg: 'bg-blue-50', text: 'text-blue-700' }
  };
  
  // 다크모드 색상 매핑
  const darkColorMap = {
    'javascript': { border: 'dark:border-blue-400', bg: 'dark:bg-blue-900/20', text: 'dark:text-blue-300' },
    'js': { border: 'dark:border-blue-400', bg: 'dark:bg-blue-900/20', text: 'dark:text-blue-300' },
    'typescript': { border: 'dark:border-blue-400', bg: 'dark:bg-blue-900/20', text: 'dark:text-blue-300' },
    'ts': { border: 'dark:border-blue-400', bg: 'dark:bg-blue-900/20', text: 'dark:text-blue-300' },
    'vue': { border: 'dark:border-green-400', bg: 'dark:bg-green-900/20', text: 'dark:text-green-300' },
    'html': { border: 'dark:border-orange-400', bg: 'dark:bg-orange-900/20', text: 'dark:text-orange-300' },
    'css': { border: 'dark:border-purple-400', bg: 'dark:bg-purple-900/20', text: 'dark:text-purple-300' },
    'scss': { border: 'dark:border-pink-400', bg: 'dark:bg-pink-900/20', text: 'dark:text-pink-300' },
    'sass': { border: 'dark:border-pink-400', bg: 'dark:bg-pink-900/20', text: 'dark:text-pink-300' },
    'java': { border: 'dark:border-red-400', bg: 'dark:bg-red-900/20', text: 'dark:text-red-300' },
    'spring': { border: 'dark:border-green-400', bg: 'dark:bg-green-900/20', text: 'dark:text-green-300' },
    'python': { border: 'dark:border-yellow-400', bg: 'dark:bg-yellow-900/20', text: 'dark:text-yellow-300' },
    'sql': { border: 'dark:border-indigo-400', bg: 'dark:bg-indigo-900/20', text: 'dark:text-indigo-300' },
    'mysql': { border: 'dark:border-indigo-400', bg: 'dark:bg-indigo-900/20', text: 'dark:text-indigo-300' },
    'bash': { border: 'dark:border-gray-400', bg: 'dark:bg-gray-900/20', text: 'dark:text-gray-300' },
    'shell': { border: 'dark:border-gray-400', bg: 'dark:bg-gray-900/20', text: 'dark:text-gray-300' },
    'json': { border: 'dark:border-yellow-400', bg: 'dark:bg-yellow-900/20', text: 'dark:text-yellow-300' },
    'xml': { border: 'dark:border-orange-400', bg: 'dark:bg-orange-900/20', text: 'dark:text-orange-300' },
    'yaml': { border: 'dark:border-blue-400', bg: 'dark:bg-blue-900/20', text: 'dark:text-blue-300' },
    'yml': { border: 'dark:border-blue-400', bg: 'dark:bg-blue-900/20', text: 'dark:text-blue-300' }
  };
  
  if (colorMap[language]) {
    const colors = colorMap[language];
    const darkColors = darkColorMap[language];
    
    // 라이트모드 스타일 적용
    wrapper.classList.add(
      'border-l-4', 
      colors.border, 
      colors.bg, 
      colors.text
    );
    
    // 다크모드 스타일 적용
    if (darkColors) {
      wrapper.classList.add(
        darkColors.border,
        darkColors.bg,
        darkColors.text
      );
    }
    
    // 언어 라벨 추가
    const languageLabel = document.createElement('div');
    languageLabel.className = `absolute top-2 left-2 px-2 py-1 text-xs font-medium rounded ${colors.bg} ${colors.text} ${darkColors ? darkColors.bg + ' ' + darkColors.text : ''}`;
    languageLabel.textContent = language.toUpperCase();
    wrapper.appendChild(languageLabel);
  }
};

const copyCode = (block, btn) => {
  const code = block.textContent;
  navigator.clipboard.writeText(code).then(() => {
    // 복사 성공 시 체크 아이콘으로 변경
    btn.innerHTML = `
      <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <polyline points="20,6 9,17 4,12"></polyline>
      </svg>
    `;
    btn.disabled = true;
    btn.classList.add('text-green-400');
    
    setTimeout(() => {
      // 2초 후 원래 아이콘으로 복원
      btn.innerHTML = `
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <rect x="9" y="9" width="13" height="13" rx="2" ry="2"></rect>
          <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"></path>
        </svg>
      `;
      btn.disabled = false;
      btn.classList.remove('text-green-400');
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
  
  checkDarkMode();
  window.addEventListener('dark-mode-toggled', handleDarkModeToggle);
  updateDarkModeClass();
});

onUnmounted(() => {
  window.removeEventListener('dark-mode-toggled', handleDarkModeToggle);
});
</script>

<style scoped>
/* 코드 복사 버튼 스타일 - Tailwind CSS로 대체되었으므로 최소한의 스타일만 유지 */
.code-block-wrapper {
  position: relative;
  margin: 1.5rem 0;
  border-radius: 0.5rem;
  overflow: hidden;
  transition: all 0.3s ease;
}

.code-block-wrapper:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
}

.code-block-wrapper pre {
  margin: 0;
  padding: 1rem;
  padding-top: 3rem;
  background: #1e293b;
  color: #e2e8f0;
  font-family: 'Fira Code', 'Monaco', 'Consolas', monospace;
  font-size: 0.875rem;
  line-height: 1.6;
  overflow-x: auto;
}

.code-block-wrapper pre code {
  background: transparent;
  padding: 0;
  border-radius: 0;
  font-size: inherit;
  color: inherit;
}

/* 언어 라벨 스타일 */
.code-block-wrapper .language-label {
  position: absolute;
  top: 0.5rem;
  left: 0.5rem;
  padding: 0.25rem 0.5rem;
  font-size: 0.75rem;
  font-weight: 600;
  border-radius: 0.25rem;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  z-index: 10;
}

/* 복사 버튼 스타일 */
.copy-btn {
  position: absolute;
  top: 0.5rem;
  right: 0.5rem;
  padding: 0.5rem;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(8px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 0.375rem;
  color: #94a3b8;
  cursor: pointer;
  opacity: 0.3;
  transition: all 0.3s ease;
  z-index: 10;
  min-width: 2rem;
  height: 2rem;
  display: flex;
  align-items: center;
  justify-content: center;
}

.copy-btn:hover {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border-color: rgba(255, 255, 255, 0.3);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  opacity: 1;
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

/* 다크모드 스타일 */
.dark .code-block-wrapper pre {
  background: #0f172a;
  color: #cbd5e1;
}

.dark .copy-btn {
  background: rgba(0, 0, 0, 0.3);
  border-color: rgba(255, 255, 255, 0.1);
  color: #64748b;
}

.dark .copy-btn:hover {
  background: rgba(0, 0, 0, 0.5);
  color: #e2e8f0;
  border-color: rgba(255, 255, 255, 0.2);
}

/* 모달 애니메이션 */
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

/* 반응형 디자인 */
@media (max-width: 768px) {
  .max-w-4xl {
    padding: 1.25rem 0.9375rem;
  }
  
  .text-4xl {
    font-size: 2rem;
  }
  
  .flex.gap-4 {
    flex-direction: column;
  }
  
  .self-end {
    align-self: flex-end;
  }
  
  .code-block-wrapper pre {
    font-size: 0.75rem;
    padding: 0.75rem;
    padding-top: 2.5rem;
  }
  
  .copy-btn {
    min-width: 1.75rem;
    height: 1.75rem;
    padding: 0.375rem;
  }
  
  .language-label {
    font-size: 0.625rem;
    padding: 0.125rem 0.375rem;
  }
}

@media (max-width: 900px) {
  .p-10 {
    padding: 1.125rem 1.5vw 1.5rem 1.5vw;
  }
}

@media (max-width: 600px) {
  .p-10 {
    padding: 0.5rem 1vw 0.75rem 1vw;
  }
}
</style> 
<template>
  <div class="min-h-screen bg-gradient-to-br from-gray-50 to-gray-100 dark:from-gray-900 dark:to-gray-800 py-8">
    <div class="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8">
      <!-- 헤더 -->
      <div class="mb-8">
        <h1 class="text-3xl font-bold text-gray-900 dark:text-white mb-2">
          {{ $t('mypage.title') }}
        </h1>
        <p class="text-gray-600 dark:text-gray-300">
          {{ $t('mypage.subtitle') }}
        </p>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
        <!-- 왼쪽: 프로필 정보 -->
        <div class="lg:col-span-1">
          <!-- 프로필 카드 -->
          <div class="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6 mb-6">
            <div class="text-center">
              <!-- 프로필 이미지 -->
              <div class="relative inline-block mb-4">
                <div class="w-24 h-24 bg-gradient-to-br from-blue-500 to-purple-600 rounded-full flex items-center justify-center mx-auto overflow-hidden">
                  <img 
                    v-if="user?.profileImage" 
                    :src="user.profileImage" 
                    :alt="getUserDisplayName()"
                    class="w-full h-full object-cover"
                  />
                  <span v-else class="text-white text-2xl font-bold">
                    {{ getUserInitial() }}
                  </span>
                </div>
                <button 
                  @click="showImageUpload = true"
                  class="absolute bottom-0 right-0 bg-blue-600 text-white p-2 rounded-full hover:bg-blue-700 transition-colors"
                  title="프로필 이미지 변경"
                >
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z"></path>
                  </svg>
                </button>
              </div>
              
              <h2 class="text-xl font-semibold text-gray-900 dark:text-white mb-2">
                {{ getUserDisplayName() }}
              </h2>
              <p class="text-gray-600 dark:text-gray-300 mb-4">{{ user?.email }}</p>
              
              <!-- 활동 정보 -->
              <div class="grid grid-cols-2 gap-4 text-sm">
                <div class="text-center">
                  <div class="text-gray-500 dark:text-gray-400">{{ $t('mypage.memberSince') }}</div>
                  <div class="font-medium text-gray-900 dark:text-white">{{ formatDate(user?.createdAt) }}</div>
                </div>
                <div class="text-center">
                  <div class="text-gray-500 dark:text-gray-400">{{ $t('mypage.commentCount') }}</div>
                  <div class="font-medium text-gray-900 dark:text-white">{{ commentCount }}{{ $t('common.count') }}</div>
                </div>
              </div>
            </div>
          </div>

          <!-- 빠른 메뉴 -->
          <div class="bg-white dark:bg-gray-800 rounded-lg shadow-md p-6">
            <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-4">{{ $t('mypage.quickMenu') }}</h3>
            <div class="space-y-3">
              <button 
                @click="showProfileEdit = true"
                class="w-full text-left p-3 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors flex items-center text-gray-900 dark:text-white"
              >
                <svg class="w-5 h-5 text-blue-600 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path>
                </svg>
                {{ $t('mypage.editProfile') }}
              </button>
              <button 
                @click="showPasswordChange = true"
                class="w-full text-left p-3 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors flex items-center text-gray-900 dark:text-white"
              >
                <svg class="w-5 h-5 text-green-600 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"></path>
                </svg>
                {{ $t('mypage.changePassword') }}
              </button>
              <button 
                @click="showDeleteAccount = true"
                class="w-full text-left p-3 rounded-lg hover:bg-red-50 dark:hover:bg-red-900/20 transition-colors flex items-center text-red-600"
              >
                <svg class="w-5 h-5 text-red-600 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path>
                </svg>
                {{ $t('mypage.deleteAccount') }}
              </button>
            </div>
          </div>
        </div>

        <!-- 오른쪽: 댓글 관리 -->
        <div class="lg:col-span-2">
          <div class="bg-white dark:bg-gray-800 rounded-lg shadow-md">
            <div class="p-6 border-b border-gray-200 dark:border-gray-700">
              <h3 class="text-lg font-semibold text-gray-900 dark:text-white">{{ $t('mypage.myComments') }}</h3>
              <p class="text-gray-600 dark:text-gray-300 text-sm mt-1">{{ $t('mypage.commentDescription') }}</p>
            </div>
            
            <!-- 댓글 목록 -->
            <div v-if="loading" class="p-6 text-center">
              <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600 mx-auto"></div>
              <p class="text-gray-600 dark:text-gray-300 mt-2">{{ $t('common.loading') }}</p>
            </div>
            
            <div v-else-if="comments.length === 0" class="p-6 text-center">
              <svg class="w-12 h-12 text-gray-400 dark:text-gray-500 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"></path>
              </svg>
              <p class="text-gray-600 dark:text-gray-300">{{ $t('mypage.noComments') }}</p>
            </div>
            
            <div v-else class="divide-y divide-gray-200 dark:divide-gray-700">
              <div 
                v-for="comment in comments" 
                :key="comment.id" 
                class="p-6 hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors"
              >
                <div class="flex justify-between items-start mb-3">
                  <div class="flex-1">
                    <div class="flex items-center mb-2">
                      <span class="text-sm text-gray-500 dark:text-gray-400">{{ formatDate(comment.createdAt) }}</span>
                      <span class="mx-2 text-gray-300 dark:text-gray-600">•</span>
                                                          <router-link
                                      :to="`/knowledge/${comment.postId}?lang=${comment.postType === 'JP' ? 'ja' : 'ko'}`"
                                      class="text-blue-600 hover:text-blue-800 dark:text-blue-400 dark:hover:text-blue-300 text-sm font-medium"
                                    >
                                      {{ comment.postTitle }}
                                    </router-link>
                    </div>
                    <p class="text-gray-900 dark:text-white">{{ comment.content }}</p>
                  </div>
                  <div class="flex space-x-2 ml-4">
                    <button 
                      @click="editComment(comment)"
                      class="text-blue-600 hover:text-blue-800 dark:text-blue-400 dark:hover:text-blue-300 p-1"
                      title="댓글 수정"
                    >
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"></path>
                      </svg>
                    </button>
                    <button 
                      @click="deleteComment(comment.id)"
                      class="text-red-600 hover:text-red-800 dark:text-red-400 dark:hover:text-red-300 p-1"
                      title="댓글 삭제"
                    >
                      <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path>
                      </svg>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 프로필 이미지 업로드 모달 -->
    <div v-if="showImageUpload" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white dark:bg-gray-800 rounded-lg p-6 w-full max-w-md mx-4">
        <h3 class="text-lg font-semibold mb-4 text-gray-900 dark:text-white">{{ $t('mypage.changeProfileImage') }}</h3>
        <div class="mb-4">
          <input 
            type="file" 
            ref="imageInput"
            @change="handleImageUpload"
            accept="image/*"
            class="w-full p-2 border border-gray-300 dark:border-gray-600 rounded bg-white dark:bg-gray-700 text-gray-900 dark:text-white"
          />
        </div>
        <div class="flex justify-end space-x-3">
          <button 
            @click="showImageUpload = false"
            class="px-4 py-2 text-gray-600 dark:text-gray-300 hover:text-gray-800 dark:hover:text-white"
          >
            {{ $t('common.cancel') }}
          </button>
          <button 
            @click="uploadImage"
            :disabled="!selectedImage"
            class="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 disabled:opacity-50"
          >
            {{ $t('common.save') }}
          </button>
        </div>
      </div>
    </div>

    <!-- 프로필 수정 모달 -->
    <div v-if="showProfileEdit" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white dark:bg-gray-800 rounded-lg p-6 w-full max-w-md mx-4">
        <h3 class="text-lg font-semibold mb-4 text-gray-900 dark:text-white">{{ $t('mypage.editProfile') }}</h3>
        <form @submit.prevent="updateProfile">
          <div class="mb-4">
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">{{ $t('mypage.nickname') }}</label>
            <input 
              v-model="profileForm.nickname"
              type="text"
              class="w-full p-2 border border-gray-300 dark:border-gray-600 rounded bg-white dark:bg-gray-700 text-gray-900 dark:text-white"
              required
            />
          </div>
          <div class="mb-4">
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">{{ $t('mypage.email') }}</label>
            <input 
              v-model="profileForm.email"
              type="email"
              class="w-full p-2 border border-gray-300 dark:border-gray-600 rounded bg-white dark:bg-gray-700 text-gray-900 dark:text-white"
              required
            />
          </div>
          <div class="mb-4">
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">{{ $t('mypage.bio') }}</label>
            <textarea 
              v-model="profileForm.bio"
              rows="3"
              class="w-full p-2 border border-gray-300 dark:border-gray-600 rounded bg-white dark:bg-gray-700 text-gray-900 dark:text-white"
              placeholder="자기소개를 입력하세요"
            ></textarea>
          </div>
          <div class="flex justify-end space-x-3">
            <button 
              type="button"
              @click="showProfileEdit = false"
              class="px-4 py-2 text-gray-600 dark:text-gray-300 hover:text-gray-800 dark:hover:text-white"
            >
              {{ $t('common.cancel') }}
            </button>
            <button 
              type="submit"
              class="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
            >
              {{ $t('common.save') }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- 비밀번호 변경 모달 -->
    <div v-if="showPasswordChange" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white dark:bg-gray-800 rounded-lg p-6 w-full max-w-md mx-4">
        <h3 class="text-lg font-semibold mb-4 text-gray-900 dark:text-white">{{ $t('mypage.changePassword') }}</h3>
        <form @submit.prevent="changePassword">
          <div class="mb-4">
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">{{ $t('mypage.currentPassword') }}</label>
            <input 
              v-model="passwordForm.currentPassword"
              type="password"
              class="w-full p-2 border border-gray-300 dark:border-gray-600 rounded bg-white dark:bg-gray-700 text-gray-900 dark:text-white"
              required
            />
          </div>
          <div class="mb-4">
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">{{ $t('mypage.newPassword') }}</label>
            <input 
              v-model="passwordForm.newPassword"
              type="password"
              class="w-full p-2 border border-gray-300 dark:border-gray-600 rounded bg-white dark:bg-gray-700 text-gray-900 dark:text-white"
              required
            />
          </div>
          <div class="mb-4">
            <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">{{ $t('mypage.confirmNewPassword') }}</label>
            <input 
              v-model="passwordForm.confirmPassword"
              type="password"
              class="w-full p-2 border border-gray-300 dark:border-gray-600 rounded bg-white dark:bg-gray-700 text-gray-900 dark:text-white"
              required
            />
          </div>
          <div class="flex justify-end space-x-3">
            <button 
              type="button"
              @click="showPasswordChange = false"
              class="px-4 py-2 text-gray-600 dark:text-gray-300 hover:text-gray-800 dark:hover:text-white"
            >
              {{ $t('common.cancel') }}
            </button>
            <button 
              type="submit"
              class="px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700"
            >
              {{ $t('mypage.changePassword') }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- 계정 삭제 확인 모달 -->
    <div v-if="showDeleteAccount" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
      <div class="bg-white dark:bg-gray-800 rounded-lg p-6 w-full max-w-md mx-4">
        <div class="text-center">
          <svg class="w-16 h-16 text-red-600 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-2.5L13.732 4c-.77-.833-1.964-.833-2.732 0L3.34 16.5c-.77.833.192 2.5 1.732 2.5z"></path>
          </svg>
          <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-2">{{ $t('mypage.deleteAccount') }}</h3>
          <p class="text-gray-600 dark:text-gray-300 mb-6">{{ $t('mypage.deleteWarning') }}</p>
          <div class="mb-4">
            <input 
              v-model="deleteConfirm"
              type="text"
              :placeholder="$t('mypage.deleteConfirmPlaceholder')"
              class="w-full p-2 border border-gray-300 dark:border-gray-600 rounded bg-white dark:bg-gray-700 text-gray-900 dark:text-white"
            />
          </div>
          <div class="flex justify-center space-x-3">
            <button 
              @click="showDeleteAccount = false"
              class="px-4 py-2 text-gray-600 dark:text-gray-300 hover:text-gray-800 dark:hover:text-white"
            >
              {{ $t('common.cancel') }}
            </button>
            <button 
              @click="confirmDeleteAccount"
              :disabled="deleteConfirm !== 'DELETE'"
              class="px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700 disabled:opacity-50"
            >
              {{ $t('mypage.deleteAccount') }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { inject, ref } from 'vue'
import api from '@/services/api'

export default {
  name: 'UserMyPageView',
  setup() {
    const auth = inject('auth')
    const router = inject('router')
    
    // 상태 관리
    const loading = ref(false)
    const comments = ref([])
    const commentCount = ref(0)
    
    // 모달 상태
    const showImageUpload = ref(false)
    const showProfileEdit = ref(false)
    const showPasswordChange = ref(false)
    const showDeleteAccount = ref(false)
    
    // 폼 데이터
    const selectedImage = ref(null)
    const profileForm = ref({
      nickname: '',
      email: '',
      bio: ''
    })
    const passwordForm = ref({
      currentPassword: '',
      newPassword: '',
      confirmPassword: ''
    })
    const deleteConfirm = ref('')
    
    return {
      user: auth.user,
      isAuthenticated: auth.isAuthenticated,
      loading,
      comments,
      commentCount,
      showImageUpload,
      showProfileEdit,
      showPasswordChange,
      showDeleteAccount,
      selectedImage,
      profileForm,
      passwordForm,
      deleteConfirm,
      router
    }
  },
  methods: {
    getUserDisplayName() {
      if (!this.user) return ''
      
      if (this.user.name) {
        return this.user.name
      }
      
      if (this.user.nickname) {
        return this.user.nickname
      }
      
      return this.user.username || ''
    },
    getUserInitial() {
      const name = this.getUserDisplayName()
      return name.charAt(0).toUpperCase()
    },
    formatDate(dateString) {
      if (!dateString) return ''
      
      const date = new Date(dateString)
      return date.toLocaleDateString(this.$i18n.locale === 'ja' ? 'ja-JP' : 'ko-KR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      })
    },
    async loadComments() {
      try {
        this.loading = true;
        const response = await api.getUserComments();
        
        if (response && response.success) {
          this.comments = response.data || [];
          this.commentCount = response.count || this.comments.length;
          console.log('댓글 로드 성공:', this.comments.length, '개');
        } else {
          console.error('댓글 로드 실패:', response);
          this.comments = [];
          this.commentCount = 0;
        }
      } catch (error) {
        console.error('댓글 로드 중 오류:', error);
        this.comments = [];
        this.commentCount = 0;
      } finally {
        this.loading = false;
      }
    },
    handleImageUpload(event) {
      const file = event.target.files[0]
      if (file) {
        this.selectedImage = file
      }
    },
    async uploadImage() {
      if (!this.selectedImage) return
      
      try {
        const formData = new FormData()
        formData.append('image', this.selectedImage)
        
        const response = await api.post('/api/user/profile-image', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        })
        
        // 사용자 정보 업데이트
        this.user.profileImage = response.data.profileImage
        this.showImageUpload = false
        this.selectedImage = null
        
        // 성공 메시지
        alert(this.$t('mypage.imageUploadSuccess'))
      } catch (error) {
        console.error('이미지 업로드 실패:', error)
        alert(this.$t('mypage.imageUploadError'))
      }
    },
    async updateProfile() {
      try {
        const response = await api.put('/api/user/profile', this.profileForm)
        
        // 사용자 정보 업데이트
        Object.assign(this.user, response.data)
        this.showProfileEdit = false
        
        // 성공 메시지
        alert(this.$t('mypage.profileUpdateSuccess'))
      } catch (error) {
        console.error('프로필 업데이트 실패:', error)
        alert(this.$t('mypage.profileUpdateError'))
      }
    },
    async changePassword() {
      if (this.passwordForm.newPassword !== this.passwordForm.confirmPassword) {
        alert(this.$t('mypage.passwordMismatch'))
        return
      }
      
      try {
        await api.put('/api/user/password', {
          currentPassword: this.passwordForm.currentPassword,
          newPassword: this.passwordForm.newPassword
        })
        
        this.showPasswordChange = false
        this.passwordForm = {
          currentPassword: '',
          newPassword: '',
          confirmPassword: ''
        }
        
        // 성공 메시지
        alert(this.$t('mypage.passwordChangeSuccess'))
      } catch (error) {
        console.error('비밀번호 변경 실패:', error)
        alert(this.$t('mypage.passwordChangeError'))
      }
    },
    async deleteComment(commentId) {
      if (!confirm(this.$t('mypage.commentDeleteConfirm'))) {
        return;
      }

      try {
        const response = await api.deleteComment(commentId);
        
        if (response && response.success) {
          // 댓글 목록에서 삭제된 댓글 제거
          this.comments = this.comments.filter(comment => comment.id !== commentId);
          this.commentCount = this.comments.length;
          alert(this.$t('mypage.commentDeleteSuccess'));
        } else {
          alert(response?.error || this.$t('mypage.commentDeleteError'));
        }
      } catch (error) {
        console.error('댓글 삭제 중 오류:', error);
        alert(this.$t('mypage.commentDeleteError'));
      }
    },
         editComment() {
       // 댓글 수정 기능 (향후 구현)
       alert('댓글 수정 기능은 향후 구현 예정입니다.')
     },
    async confirmDeleteAccount() {
      if (this.deleteConfirm !== 'DELETE') {
        alert(this.$t('mypage.deleteConfirmError'))
        return
      }
      
      try {
        await api.delete('/api/user/account')
        
        // 로그아웃 처리
        this.auth.logout()
        this.router.push('/')
        
        alert(this.$t('mypage.accountDeleteSuccess'))
      } catch (error) {
        console.error('계정 삭제 실패:', error)
        alert(this.$t('mypage.accountDeleteError'))
      }
    }
  },
  async mounted() {
    // 인증 확인
    if (!this.isAuthenticated) {
      this.$router.push('/user/login')
      return
    }
    
    // 프로필 폼 초기화
    this.profileForm = {
      nickname: this.user?.nickname || '',
      email: this.user?.email || '',
      bio: this.user?.bio || ''
    }
    
    // 댓글 로딩
    await this.loadComments()
  }
}
</script>

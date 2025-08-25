<template>
  <div class="min-h-screen py-8" :style="backgroundStyle">
    <div class="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8">
             <!-- 헤더 -->
       <div class="mb-8 p-6 rounded-lg bg-white/80 dark:bg-gray-800/80 backdrop-blur-sm shadow-lg">
         <h1 class="text-3xl font-bold text-gray-900 dark:text-white mb-2 drop-shadow-sm">
           {{ $t('mypage.title') }}
         </h1>
         <p class="text-gray-600 dark:text-gray-300 drop-shadow-sm">
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
                    :src="getImageUrl(user.profileImage)" 
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
               <p class="text-gray-600 dark:text-gray-300 mb-2">{{ user?.email }}</p>
               
               <!-- 자기소개 -->
               <div v-if="user?.bio" class="mb-4">
                 <p class="text-sm text-gray-700 dark:text-gray-300 leading-relaxed bg-gray-50 dark:bg-gray-700 p-3 rounded-lg border-l-4 border-blue-500">
                   {{ user.bio }}
                 </p>
               </div>
              
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
                 @click="showBackgroundCustomization = true"
                 class="w-full text-left p-3 rounded-lg hover:bg-purple-50 dark:hover:bg-purple-900/20 transition-colors flex items-center text-purple-600"
               >
                 <svg class="w-5 h-5 text-purple-600 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                   <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 21a4 4 0 01-4-4V5a2 2 0 012-2h4a2 2 0 012 2v12a4 4 0 01-4 4zM21 5a2 2 0 00-2-2h-4a2 2 0 00-2 2v12a4 4 0 004 4h4a2 2 0 002-2V5z"></path>
                 </svg>
                 배경화면 커스터마이징
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
      <div class="bg-white dark:bg-gray-800 rounded-lg p-6 w-full max-w-2xl mx-4 max-h-[90vh] overflow-y-auto">
        <div class="flex justify-between items-center mb-4">
          <h3 class="text-lg font-semibold text-gray-900 dark:text-white">{{ $t('mypage.changeProfileImage') }}</h3>
          <button 
            @click="closeImageUpload"
            class="text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-200"
          >
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
            </svg>
          </button>
        </div>

                 <!-- 이미지 업로드 규칙 안내 -->
         <div class="mb-6 p-3 bg-gray-50 dark:bg-gray-700 rounded-lg">
           <p class="text-sm text-gray-600 dark:text-gray-300">
             지원 형식: JPG, PNG, GIF | 최대 파일 크기: 5MB
           </p>
           <p class="text-sm text-gray-600 dark:text-gray-300 mt-1">
             권장 크기: 400x400 픽셀 이상 | 정사각형 이미지 권장
           </p>
         </div>

        <!-- 이미지 편집 단계 -->
        <div v-if="!selectedImage && !croppedImage" class="mb-6">
          <!-- 드래그 앤 드롭 영역 -->
          <div 
            @drop="handleDrop"
            @dragover.prevent
            @dragenter.prevent
            @dragleave.prevent
            class="border-2 border-dashed border-gray-300 dark:border-gray-600 rounded-lg p-8 text-center hover:border-blue-400 dark:hover:border-blue-500 transition-colors cursor-pointer"
            @click="$refs.imageInput.click()"
          >
            <div class="space-y-4">
              <svg class="w-16 h-16 mx-auto text-gray-400 dark:text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12"></path>
              </svg>
              <div>
                <p class="text-lg font-medium text-gray-700 dark:text-gray-300">이미지를 여기에 드래그하거나 클릭하세요</p>
                <p class="text-sm text-gray-500 dark:text-gray-400 mt-1">또는 파일을 선택하세요</p>
              </div>
            </div>
          </div>
          
          <!-- 파일 선택 버튼 -->
          <input 
            ref="imageInput"
            type="file" 
            @change="handleImageUpload"
            accept="image/*"
            class="hidden"
          />
        </div>

        <!-- 이미지 미리보기 및 편집 -->
        <div v-if="selectedImage && !croppedImage" class="mb-6">
          <div class="text-center mb-4">
            <h4 class="font-semibold text-gray-900 dark:text-white mb-2">이미지 편집</h4>
            <p class="text-sm text-gray-600 dark:text-gray-400">이미지를 드래그하여 원하는 부분을 선택하세요</p>
          </div>
          
          <!-- 이미지 편집 영역 -->
          <div class="relative w-full h-96 bg-gray-100 dark:bg-gray-700 rounded-lg overflow-hidden">
            <div 
              ref="imageContainer"
              class="relative w-full h-full overflow-hidden"
              @mousedown="startDrag"
              @mousemove="onDrag"
              @mouseup="stopDrag"
              @mouseleave="stopDrag"
              @touchstart="startDrag"
              @touchmove="onDrag"
              @touchend="stopDrag"
            >
                             <img 
                 ref="editableImage"
                 :src="imagePreview" 
                 alt="미리보기"
                 class="absolute w-full h-full object-contain cursor-move"
                 :style="imageStyle"
                 draggable="false"
               />
              <!-- 크롭 가이드 -->
              <div class="absolute inset-0 flex items-center justify-center pointer-events-none">
                <div class="border-2 border-white border-dashed w-48 h-48 rounded-full opacity-70 shadow-lg"></div>
              </div>
              
              <!-- 줌 컨트롤 -->
              <div class="absolute bottom-4 right-4 bg-black bg-opacity-50 rounded-lg p-2 pointer-events-auto">
                <div class="flex flex-col space-y-2">
                  <button 
                  @click="zoomIn"
                  class="w-8 h-8 bg-white rounded flex items-center justify-center hover:bg-gray-100"
                  title="확대"
                  >
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0zM10 7v3m0 0v3m0-3h3m-3 0H7"></path>
                  </svg>
                  </button>
                  <button 
                  @click="zoomOut"
                  class="w-8 h-8 bg-white rounded flex items-center justify-center hover:bg-gray-100"
                  title="축소"
                  >
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0zM7 10h6"></path>
                  </svg>
                  </button>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 편집 컨트롤 -->
          <div class="flex justify-center space-x-4 mt-4">
            <button 
              @click="cropImage"
              class="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
            >
              편집 완료
            </button>
            <button 
              @click="resetImage"
              class="px-4 py-2 bg-gray-500 text-white rounded hover:bg-gray-600"
            >
              다시 선택
            </button>
          </div>
        </div>

        <!-- 편집된 이미지 미리보기 -->
        <div v-if="croppedImage" class="mb-6">
          <div class="text-center mb-4">
            <h4 class="font-semibold text-gray-900 dark:text-white mb-2">편집된 이미지</h4>
          </div>
          
          <div class="flex justify-center">
            <div class="w-32 h-32 rounded-full overflow-hidden border-4 border-gray-200 dark:border-gray-600">
              <img 
                :src="croppedImage" 
                alt="편집된 이미지"
                class="w-full h-full object-cover"
              />
            </div>
          </div>
          
          <div class="flex justify-center space-x-4 mt-4">
            <button 
              @click="resetImage"
              class="px-4 py-2 bg-gray-500 text-white rounded hover:bg-gray-600"
            >
              다시 편집
            </button>
          </div>
        </div>

        <!-- 하단 버튼 -->
        <div class="flex justify-end space-x-3">
          <button 
            @click="closeImageUpload"
            class="px-4 py-2 text-gray-600 dark:text-gray-300 hover:text-gray-800 dark:hover:text-white"
          >
            {{ $t('common.cancel') }}
          </button>
          <button 
            @click="uploadImage"
            :disabled="!croppedImage"
            class="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed"
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

         <!-- 배경화면 커스터마이징 모달 -->
     <div v-if="showBackgroundCustomization" class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
       <div class="bg-white dark:bg-gray-800 rounded-lg p-6 w-full max-w-2xl mx-4 max-h-[90vh] overflow-y-auto">
         <div class="flex justify-between items-center mb-4">
           <h3 class="text-lg font-semibold text-gray-900 dark:text-white">배경화면 커스터마이징</h3>
           <button 
             @click="showBackgroundCustomization = false"
             class="text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-200"
           >
             <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
               <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path>
             </svg>
           </button>
         </div>
         
                   <!-- 배경 스타일 선택 -->
          <div class="mb-6">
            <h4 class="text-md font-semibold text-gray-900 dark:text-white mb-3">배경 스타일</h4>
            <div class="grid grid-cols-2 gap-3">
              <button 
                @click="backgroundForm.backgroundStyle = 'solid'"
                :class="[
                  'p-3 rounded-lg border-2 transition-all',
                  backgroundForm.backgroundStyle === 'solid' 
                    ? 'border-blue-500 bg-blue-50 dark:bg-blue-900/20' 
                    : 'border-gray-200 dark:border-gray-600 hover:border-gray-300'
                ]"
              >
                <div class="w-full h-16 bg-gray-200 dark:bg-gray-600 rounded mb-2"></div>
                <span class="text-sm font-medium text-gray-900 dark:text-white">단색</span>
              </button>
              <button 
                @click="backgroundForm.backgroundStyle = 'image'"
                :class="[
                  'p-3 rounded-lg border-2 transition-all',
                  backgroundForm.backgroundStyle === 'image' 
                    ? 'border-blue-500 bg-blue-50 dark:bg-blue-900/20' 
                    : 'border-gray-200 dark:border-gray-600 hover:border-gray-300'
                ]"
              >
                <div class="w-full h-16 bg-gray-200 dark:bg-gray-600 rounded mb-2 flex items-center justify-center">
                  <svg class="w-6 h-6 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"></path>
                  </svg>
                </div>
                <span class="text-sm font-medium text-gray-900 dark:text-white">이미지</span>
              </button>
            </div>
          </div>
         
         <!-- 배경색 선택 (단색 스타일일 때) -->
         <div v-if="backgroundForm.backgroundStyle === 'solid'" class="mb-6">
           <h4 class="text-md font-semibold text-gray-900 dark:text-white mb-3">배경색</h4>
           <div class="grid grid-cols-6 gap-2">
             <button 
               v-for="color in backgroundColors" 
               :key="color.value"
               @click="backgroundForm.backgroundColor = color.value"
               :class="[
                 'w-12 h-12 rounded-lg border-2 transition-all',
                 backgroundForm.backgroundColor === color.value ? 'border-blue-500 scale-110' : 'border-gray-200 dark:border-gray-600'
               ]"
               :style="{ backgroundColor: color.value }"
               :title="color.name"
             ></button>
           </div>
           <div class="mt-3">
             <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">커스텀 색상</label>
             <input 
               v-model="backgroundForm.backgroundColor"
               type="color"
               class="w-full h-10 rounded border border-gray-300 dark:border-gray-600"
             />
           </div>
         </div>
         
         <!-- 배경 이미지 업로드 (이미지 스타일일 때) -->
         <div v-if="backgroundForm.backgroundStyle === 'image'" class="mb-6">
           <h4 class="text-md font-semibold text-gray-900 dark:text-white mb-3">배경 이미지</h4>
           <div 
             @drop="handleBackgroundImageDrop"
             @dragover.prevent
             @dragenter.prevent
             @dragleave.prevent
             class="border-2 border-dashed border-gray-300 dark:border-gray-600 rounded-lg p-6 text-center hover:border-blue-400 dark:hover:border-blue-500 transition-colors cursor-pointer"
             @click="$refs.backgroundImageInput.click()"
           >
             <div class="space-y-2">
               <svg class="w-8 h-8 mx-auto text-gray-400 dark:text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                 <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"></path>
               </svg>
               <p class="text-sm text-gray-600 dark:text-gray-400">이미지를 드래그하거나 클릭하세요</p>
             </div>
           </div>
           <input 
             ref="backgroundImageInput"
             type="file" 
             @change="handleBackgroundImageUpload"
             accept="image/*"
             class="hidden"
           />
         </div>
         
         <!-- 미리보기 -->
         <div class="mb-6">
           <h4 class="text-md font-semibold text-gray-900 dark:text-white mb-3">미리보기</h4>
           <div 
             class="w-full h-32 rounded-lg border border-gray-200 dark:border-gray-600 overflow-hidden"
             :style="previewBackgroundStyle"
           ></div>
         </div>
         
         <!-- 하단 버튼 -->
         <div class="flex justify-end space-x-3">
           <button 
             @click="closeBackgroundCustomization"
             class="px-4 py-2 text-gray-600 dark:text-gray-300 hover:text-gray-800 dark:hover:text-white"
           >
             {{ $t('common.cancel') }}
           </button>
           <button 
             @click="saveBackgroundCustomization"
             class="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
           >
             {{ $t('common.save') }}
           </button>
         </div>
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
import { useRouter } from 'vue-router'
import api from '@/services/api'

export default {
  name: 'UserMyPageView',
  setup() {
    const auth = inject('auth')
    const router = useRouter()
    
    // 상태 관리
    const loading = ref(false)
    const comments = ref([])
    const commentCount = ref(0)
    
         // 모달 상태
     const showImageUpload = ref(false)
     const showProfileEdit = ref(false)
     const showPasswordChange = ref(false)
     const showDeleteAccount = ref(false)
     const showBackgroundCustomization = ref(false)
    
    // 폼 데이터
    const selectedImage = ref(null)
    const croppedImage = ref(null)
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
     
           // 배경화면 커스터마이징 관련
      const backgroundForm = ref({
        backgroundStyle: 'solid',
        backgroundColor: '#f0f0f0',
        backgroundImage: ''
      })
     
     // 배경색 옵션들
     const backgroundColors = ref([
       { name: '흰색', value: '#ffffff' },
       { name: '회색', value: '#f0f0f0' },
       { name: '연한 파랑', value: '#e3f2fd' },
       { name: '연한 초록', value: '#e8f5e8' },
       { name: '연한 노랑', value: '#fff8e1' },
       { name: '연한 빨강', value: '#ffebee' },
       { name: '연한 보라', value: '#f3e5f5' },
       { name: '연한 주황', value: '#fff3e0' },
       { name: '진한 파랑', value: '#1976d2' },
       { name: '진한 초록', value: '#388e3c' },
       { name: '진한 빨강', value: '#d32f2f' },
       { name: '진한 보라', value: '#7b1fa2' }
     ])
    
    // 이미지 편집 관련 상태
    const isDragging = ref(false)
    const dragStart = ref({ x: 0, y: 0 })
    const imagePosition = ref({ x: 0, y: 0 })
    const imageScale = ref(1)
    const imageContainer = ref(null)
    const editableImage = ref(null)
    
    return {
      user: auth.user,
      isAuthenticated: auth.isAuthenticated,
      auth, // auth 객체 추가
      loading,
      comments,
      commentCount,
             showImageUpload,
       showProfileEdit,
       showPasswordChange,
       showDeleteAccount,
       showBackgroundCustomization,
      selectedImage,
      croppedImage,
      profileForm,
      passwordForm,
      deleteConfirm,
      router,
      // 이미지 편집 관련 상태
      isDragging,
      dragStart,
      imagePosition,
      imageScale,
             imageContainer,
       editableImage,
       backgroundForm,
       backgroundColors
    }
  },
  computed: {
    imagePreview() {
      if (!this.selectedImage) return '';
      return URL.createObjectURL(this.selectedImage);
    },
         imageStyle() {
       return {
         transform: `translate(${this.imagePosition.x}px, ${this.imagePosition.y}px) scale(${this.imageScale})`,
         transformOrigin: 'center'
       }
     },
           backgroundStyle() {
        if (!this.user) {
          return {
            background: 'linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%)'
          }
        }
        
        const style = this.user.backgroundStyle || 'solid'
        const color = this.user.backgroundColor || '#f0f0f0'
        const image = this.user.backgroundImage
        
        switch (style) {
          case 'solid':
            return {
              backgroundColor: color
            }
          case 'image':
            if (image) {
              // 이미지 URL이 이미 완전한 URL인지 확인
              let imageUrl = image
              if (!image.startsWith('http') && !image.startsWith('data:') && !image.startsWith('blob:')) {
                // 상대 경로인 경우에만 getImageUrl 사용
                imageUrl = this.getImageUrl(image)
              }
              return {
                backgroundImage: `url(${imageUrl})`,
                backgroundSize: 'cover',
                backgroundPosition: 'center',
                backgroundRepeat: 'no-repeat'
              }
            } else {
              return {
                backgroundColor: color
              }
            }
          default:
            return {
              backgroundColor: color
            }
        }
      },
           previewBackgroundStyle() {
        const style = this.backgroundForm.backgroundStyle
        const color = this.backgroundForm.backgroundColor
        const image = this.backgroundForm.backgroundImage
        
        switch (style) {
          case 'solid':
            return {
              backgroundColor: color
            }
          case 'image':
            if (image) {
              // 이미지 URL이 이미 완전한 URL인지 확인
              let imageUrl = image
              if (!image.startsWith('http') && !image.startsWith('data:') && !image.startsWith('blob:')) {
                // 상대 경로인 경우에만 getImageUrl 사용
                imageUrl = this.getImageUrl(image)
              }
              return {
                backgroundImage: `url(${imageUrl})`,
                backgroundSize: 'cover',
                backgroundPosition: 'center',
                backgroundRepeat: 'no-repeat'
              }
            } else {
              return {
                backgroundColor: color
              }
            }
          default:
            return {
              backgroundColor: color
            }
        }
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
    getImageUrl(imagePath) {
      if (!imagePath) return ''
      
      // 프록시 설정을 사용하므로 상대 경로 그대로 반환
      return imagePath
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
        // 이미지 편집 상태 초기화
        this.resetImageEdit()
      }
    },
    async uploadImage() {
      if (!this.croppedImage) {
        alert(this.$t('mypage.imageCropError') || '편집된 이미지를 선택해야 합니다.');
        return;
      }
      
      try {
        // Data URL을 File 객체로 변환
        const response = await fetch(this.croppedImage);
        const blob = await response.blob();
        const file = new File([blob], 'profile-image.jpg', { type: 'image/jpeg' });
        
        const formData = new FormData()
        formData.append('image', file)
        
        const apiResponse = await api.post('/api/user/profile-image', formData)
        
        // 응답 검증
        if (apiResponse && apiResponse.success && apiResponse.profileImage) {
          // 사용자 정보 업데이트
          this.user.profileImage = apiResponse.profileImage
          // 로컬 스토리지에 profileImage 저장
          localStorage.setItem('profileImage', apiResponse.profileImage)
          this.showImageUpload = false
          this.selectedImage = null
          this.croppedImage = null // 편집된 이미지 초기화
          
          // 성공 메시지
          alert(this.$t('mypage.imageUploadSuccess') || '프로필 이미지가 성공적으로 업로드되었습니다.')
        } else {
          throw new Error(apiResponse?.error || '이미지 업로드에 실패했습니다.')
        }
      } catch (error) {
        console.error('이미지 업로드 실패:', error)
        alert(this.$t('mypage.imageUploadError') || '이미지 업로드 중 오류가 발생했습니다.')
      }
    },
    async updateProfile() {
      try {
        const response = await api.put('/api/user/profile', this.profileForm)
        
        // 사용자 정보 업데이트
        Object.assign(this.user, response.data)
        
        // 로컬 스토리지에 정보 저장
        if (response.data.bio) localStorage.setItem('userBio', response.data.bio)
        if (response.data.backgroundStyle) localStorage.setItem('userBackgroundStyle', response.data.backgroundStyle)
        if (response.data.backgroundColor) localStorage.setItem('userBackgroundColor', response.data.backgroundColor)
        if (response.data.backgroundImage) localStorage.setItem('userBackgroundImage', response.data.backgroundImage)
        
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
    // 드래그 시작
    startDrag(event) {
      event.preventDefault();
      this.isDragging = true;
      
      const clientX = event.touches ? event.touches[0].clientX : event.clientX;
      const clientY = event.touches ? event.touches[0].clientY : event.clientY;
      
      this.dragStart = {
        x: clientX - this.imagePosition.x,
        y: clientY - this.imagePosition.y
      };
    },
    
    // 드래그 중
    onDrag(event) {
      if (!this.isDragging) return;
      event.preventDefault();
      
      const clientX = event.touches ? event.touches[0].clientX : event.clientX;
      const clientY = event.touches ? event.touches[0].clientY : event.clientY;
      
      this.imagePosition = {
        x: clientX - this.dragStart.x,
        y: clientY - this.dragStart.y
      };
    },
    
    // 드래그 종료
    stopDrag() {
      this.isDragging = false;
    },
    
    // 줌 인
    zoomIn() {
      this.imageScale = Math.min(this.imageScale * 1.2, 3);
    },
    
    // 줌 아웃
    zoomOut() {
      this.imageScale = Math.max(this.imageScale / 1.2, 0.5);
    },
    
    // 이미지 편집 상태 초기화
    resetImageEdit() {
      this.imagePosition = { x: 0, y: 0 };
      this.imageScale = 1;
      this.isDragging = false;
    },
    
    cropImage() {
      // 정확한 이미지 크롭 기능
      const canvas = document.createElement('canvas');
      const ctx = canvas.getContext('2d');
      const img = new Image();
      
      img.onload = () => {
        // 컨테이너와 이미지 크기 계산
        const containerRect = this.$refs.imageContainer.getBoundingClientRect();
        const imageRect = this.$refs.editableImage.getBoundingClientRect();
        
        // 원본 이미지와 화면에 표시된 이미지의 비율 계산
        const displayWidth = imageRect.width;
        const displayHeight = imageRect.height;
        const originalWidth = img.naturalWidth;
        const originalHeight = img.naturalHeight;
        
        // 이미지가 화면에 어떻게 표시되는지 계산 (object-contain 고려)
        let displayRatio, imageDisplayWidth, imageDisplayHeight, offsetX, offsetY;
        
        if (displayWidth / displayHeight > originalWidth / originalHeight) {
          // 컨테이너가 더 넓음 - 높이에 맞춤
          displayRatio = displayHeight / originalHeight;
          imageDisplayWidth = originalWidth * displayRatio;
          imageDisplayHeight = displayHeight;
          offsetX = (displayWidth - imageDisplayWidth) / 2;
          offsetY = 0;
        } else {
          // 컨테이너가 더 높음 - 너비에 맞춤
          displayRatio = displayWidth / originalWidth;
          imageDisplayWidth = displayWidth;
          imageDisplayHeight = originalHeight * displayRatio;
          offsetX = 0;
          offsetY = (displayHeight - imageDisplayHeight) / 2;
        }
        
        // 크롭 영역 계산 (원형 가이드 기준)
        const cropSize = Math.min(containerRect.width, containerRect.height) * 0.5; // 192px (48/96 = 0.5)
        const cropX = (containerRect.width - cropSize) / 2;
        const cropY = (containerRect.height - cropSize) / 2;
        
        // 드래그 위치와 줌을 고려한 실제 크롭 좌표 계산
        const actualCropX = cropX - this.imagePosition.x;
        const actualCropY = cropY - this.imagePosition.y;
        
        // 화면 좌표를 원본 이미지 좌표로 변환 (줌 고려)
        const sourceX = (actualCropX - offsetX) / (displayRatio * this.imageScale);
        const sourceY = (actualCropY - offsetY) / (displayRatio * this.imageScale);
        const sourceSize = cropSize / (displayRatio * this.imageScale);
        
        // 캔버스 크기 설정 (400x400 권장)
        const outputSize = 400;
        canvas.width = outputSize;
        canvas.height = outputSize;
        
        // 원형 크롭을 위한 마스크 생성
        ctx.save();
        ctx.beginPath();
        ctx.arc(outputSize / 2, outputSize / 2, outputSize / 2, 0, 2 * Math.PI);
        ctx.clip();
        
        // 이미지 그리기
        ctx.drawImage(
          img, 
          sourceX, sourceY, sourceSize, sourceSize,  // 소스 영역
          0, 0, outputSize, outputSize  // 대상 영역
        );
        
        ctx.restore();
        
        // JPEG로 변환 (품질 0.8)
        this.croppedImage = canvas.toDataURL('image/jpeg', 0.8);
        
        // 미리보기 URL 해제
        if (this.imagePreview) {
          URL.revokeObjectURL(this.imagePreview);
        }
      };
      
      img.src = this.imagePreview;
    },
    resetImage() {
      this.selectedImage = null;
      this.croppedImage = null;
      this.resetImageEdit();
    },
    closeImageUpload() {
      this.showImageUpload = false;
      this.selectedImage = null;
      this.croppedImage = null;
    },
    handleDrop(event) {
      event.preventDefault();
      const file = event.dataTransfer.files[0];
      if (file && file.type.startsWith('image/')) {
        this.selectedImage = file;
      } else {
        alert('이미지 파일만 업로드 가능합니다.');
      }
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
     },
     
     // 배경화면 커스터마이징 관련 메서드들
     handleBackgroundImageUpload(event) {
       const file = event.target.files[0]
       if (file) {
         this.backgroundForm.backgroundImage = URL.createObjectURL(file)
       }
     },
     
     handleBackgroundImageDrop(event) {
       event.preventDefault()
       const file = event.dataTransfer.files[0]
       if (file && file.type.startsWith('image/')) {
         this.backgroundForm.backgroundImage = URL.createObjectURL(file)
       } else {
         alert('이미지 파일만 업로드 가능합니다.')
       }
     },
     
     async saveBackgroundCustomization() {
       try {
         let backgroundImageUrl = this.backgroundForm.backgroundImage
         
         // 배경 이미지가 Data URL 또는 blob URL인 경우 업로드
         if (this.backgroundForm.backgroundStyle === 'image' && 
             this.backgroundForm.backgroundImage && 
             (this.backgroundForm.backgroundImage.startsWith('data:') || 
              this.backgroundForm.backgroundImage.startsWith('blob:'))) {
           
           // Data URL 또는 blob URL을 File 객체로 변환
           const response = await fetch(this.backgroundForm.backgroundImage)
           const blob = await response.blob()
           const file = new File([blob], 'background-image.jpg', { type: 'image/jpeg' })
           
           const formData = new FormData()
           formData.append('image', file)
           
           const uploadResponse = await api.post('/api/user/background-image', formData)
           if (uploadResponse && uploadResponse.success) {
             backgroundImageUrl = uploadResponse.backgroundImage
           }
         }
         
         const response = await api.put('/api/user/background', {
           backgroundStyle: this.backgroundForm.backgroundStyle,
           backgroundColor: this.backgroundForm.backgroundColor,
           backgroundImage: backgroundImageUrl
         })
         
                   // 사용자 정보 업데이트
          if (response && response.data) {
            Object.assign(this.user, response.data)
            
            // 로컬 스토리지에 배경화면 정보 저장
            if (response.data.bio) localStorage.setItem('userBio', response.data.bio)
            if (response.data.backgroundStyle) localStorage.setItem('userBackgroundStyle', response.data.backgroundStyle)
            if (response.data.backgroundColor) localStorage.setItem('userBackgroundColor', response.data.backgroundColor)
            if (response.data.backgroundImage) localStorage.setItem('userBackgroundImage', response.data.backgroundImage)
          }
          this.showBackgroundCustomization = false
          
          // blob URL 정리
          if (this.backgroundForm.backgroundImage && 
              this.backgroundForm.backgroundImage.startsWith('blob:')) {
            URL.revokeObjectURL(this.backgroundForm.backgroundImage)
            this.backgroundForm.backgroundImage = ''
          }
          
          alert('배경화면이 성공적으로 저장되었습니다.')
       } catch (error) {
         console.error('배경화면 저장 실패:', error)
         alert('배경화면 저장 중 오류가 발생했습니다.')
       }
     },
     
     closeBackgroundCustomization() {
       // blob URL 정리
       if (this.backgroundForm.backgroundImage && 
           this.backgroundForm.backgroundImage.startsWith('blob:')) {
         URL.revokeObjectURL(this.backgroundForm.backgroundImage)
         this.backgroundForm.backgroundImage = ''
       }
       
       // 폼을 원래 상태로 복원
       this.backgroundForm = {
         backgroundStyle: this.user?.backgroundStyle || 'solid',
         backgroundColor: this.user?.backgroundColor || '#f0f0f0',
         backgroundImage: this.user?.backgroundImage || ''
       }
       
       this.showBackgroundCustomization = false
     }
  },
  async mounted() {
    // 인증 상태 다시 확인
    if (!this.isAuthenticated) {
      // 로컬 스토리지에 토큰이 있는지 확인
      const token = localStorage.getItem('jwtToken')
      if (!token) {
        this.$router.push('/user/login')
        return
      }
      
      // 토큰이 있지만 인증 상태가 초기화되지 않은 경우, 다시 시도
      try {
        await this.auth.fetchUserInfo()
        if (!this.auth.isAuthenticated.value) {
          this.$router.push('/user/login')
          return
        }
      } catch (error) {
        console.error('인증 상태 확인 실패:', error)
        this.$router.push('/user/login')
        return
      }
    }
    
         // 프로필 폼 초기화
     this.profileForm = {
       nickname: this.user?.nickname || '',
       email: this.user?.email || '',
       bio: this.user?.bio || ''
     }
     
           // 배경화면 폼 초기화
      this.backgroundForm = {
        backgroundStyle: this.user?.backgroundStyle || 'solid',
        backgroundColor: this.user?.backgroundColor || '#f0f0f0',
        backgroundImage: this.user?.backgroundImage || ''
      }
      
      // 배경 이미지가 blob URL인 경우 초기화 (편집 모드에서는 새로 선택하도록)
      if (this.backgroundForm.backgroundImage && 
          (this.backgroundForm.backgroundImage.startsWith('blob:') || 
           this.backgroundForm.backgroundImage.startsWith('data:'))) {
        this.backgroundForm.backgroundImage = ''
      }
    
    // 댓글 로딩
    await this.loadComments()
  }
}
</script>

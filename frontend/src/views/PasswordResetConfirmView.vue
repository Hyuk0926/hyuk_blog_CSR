<template>
  <div class="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
    <div class="max-w-md w-full space-y-8">
      <div class="bg-white rounded-lg shadow-xl p-8">
        <!-- 헤더 -->
        <div class="text-center mb-8">
          <div class="mx-auto h-12 w-12 bg-green-100 rounded-full flex items-center justify-center mb-4">
            <svg class="h-6 w-6 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 7a2 2 0 012 2m4 0a6 6 0 01-7.743 5.743L11 17H9v2H7v2H4a1 1 0 01-1-1v-2.586a1 1 0 01.293-.707l5.964-5.964A6 6 0 1121 9z"></path>
            </svg>
          </div>
          <h2 class="text-3xl font-bold text-gray-900 mb-2">새 비밀번호 설정</h2>
          <p class="text-gray-600">새로운 비밀번호를 입력해주세요</p>
        </div>

        <!-- 로딩 상태 -->
        <div v-if="loading" class="text-center py-8">
          <svg class="animate-spin h-8 w-8 text-blue-600 mx-auto mb-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
          <p class="text-gray-600">토큰을 확인하는 중...</p>
        </div>

        <!-- 토큰 무효 메시지 -->
        <div v-else-if="!tokenValid" class="text-center py-8">
          <div class="mx-auto h-12 w-12 bg-red-100 rounded-full flex items-center justify-center mb-4">
            <svg class="h-6 w-6 text-red-600" fill="currentColor" viewBox="0 0 20 20">
              <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd"></path>
            </svg>
          </div>
          <h3 class="text-lg font-medium text-gray-900 mb-2">유효하지 않은 링크</h3>
          <p class="text-gray-600 mb-6">이 링크는 만료되었거나 유효하지 않습니다.</p>
          <router-link
            to="/password-reset"
            class="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-lg text-white bg-red-600 hover:bg-red-700"
          >
            새로운 재설정 요청하기
          </router-link>
        </div>

        <!-- 비밀번호 재설정 폼 -->
        <form v-else @submit.prevent="resetPassword" class="space-y-6">
          <div>
            <label for="newPassword" class="block text-sm font-medium text-gray-700 mb-2">
              새 비밀번호
            </label>
            <input
              id="newPassword"
              v-model="newPassword"
              type="password"
              required
              minlength="6"
              class="appearance-none relative block w-full px-3 py-3 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500 focus:border-green-500 focus:z-10 sm:text-sm"
              placeholder="새 비밀번호 (6자 이상)"
              :disabled="submitting"
            />
          </div>

          <div>
            <label for="confirmPassword" class="block text-sm font-medium text-gray-700 mb-2">
              새 비밀번호 확인
            </label>
            <input
              id="confirmPassword"
              v-model="confirmPassword"
              type="password"
              required
              class="appearance-none relative block w-full px-3 py-3 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-lg focus:outline-none focus:ring-2 focus:ring-green-500 focus:border-green-500 focus:z-10 sm:text-sm"
              placeholder="새 비밀번호 다시 입력"
              :disabled="submitting"
            />
            <p v-if="passwordMismatch" class="mt-1 text-sm text-red-600">
              비밀번호가 일치하지 않습니다.
            </p>
          </div>

          <!-- 에러 메시지 -->
          <div v-if="error" class="bg-red-50 border border-red-200 rounded-lg p-4">
            <div class="flex">
              <div class="flex-shrink-0">
                <svg class="h-5 w-5 text-red-400" fill="currentColor" viewBox="0 0 20 20">
                  <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z" clip-rule="evenodd"></path>
                </svg>
              </div>
              <div class="ml-3">
                <p class="text-sm text-red-700">{{ error }}</p>
              </div>
            </div>
          </div>

          <!-- 성공 메시지 -->
          <div v-if="success" class="bg-green-50 border border-green-200 rounded-lg p-4">
            <div class="flex">
              <div class="flex-shrink-0">
                <svg class="h-5 w-5 text-green-400" fill="currentColor" viewBox="0 0 20 20">
                  <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"></path>
                </svg>
              </div>
              <div class="ml-3">
                <p class="text-sm text-green-700 font-medium">{{ success }}</p>
                <p class="text-xs text-green-600 mt-1">잠시 후 로그인 페이지로 이동합니다...</p>
              </div>
            </div>
          </div>

          <!-- 제출 버튼 -->
          <button
            type="submit"
            :disabled="submitting || passwordMismatch"
            class="group relative w-full flex justify-center py-3 px-4 border border-transparent text-sm font-medium rounded-lg text-white bg-green-600 hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-green-500 disabled:opacity-50 disabled:cursor-not-allowed transition-colors duration-200"
          >
            <span v-if="submitting" class="absolute left-0 inset-y-0 flex items-center pl-3">
              <svg class="animate-spin h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
            </span>
            {{ submitting ? '처리 중...' : '비밀번호 변경하기' }}
          </button>
        </form>

        <!-- 로그인 링크 -->
        <div class="text-center mt-6">
          <router-link
            to="/user/login"
            class="text-sm text-green-600 hover:text-green-500 font-medium"
          >
            ← 로그인으로 돌아가기
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '@/services/api'

export default {
  name: 'PasswordResetConfirmView',
  setup() {
    const route = useRoute()
    const router = useRouter()
    const token = route.query.token
    const newPassword = ref('')
    const confirmPassword = ref('')
    const loading = ref(true)
    const submitting = ref(false)
    const tokenValid = ref(false)
    const error = ref('')
    const success = ref('')

    const passwordMismatch = computed(() => {
      return newPassword.value && confirmPassword.value && newPassword.value !== confirmPassword.value
    })

    // 브라우저 알림 권한 요청 함수
    const requestNotificationPermission = async () => {
      if ('Notification' in window && Notification.permission === 'default') {
        try {
          await Notification.requestPermission()
        } catch (error) {
          console.log('알림 권한 요청 실패:', error)
        }
      }
    }

    // 성공 알림 표시 함수
    const showSuccessNotification = (title, message) => {
      // 브라우저 알림 지원 확인
      if ('Notification' in window && Notification.permission === 'granted') {
        new Notification(title, {
          body: message,
          icon: '/favicon.ico',
          badge: '/favicon.ico'
        })
      }
      
      // 페이지 내 알림도 표시 (이미 success.value로 처리됨)
    }

    const validateToken = async () => {
      if (!token) {
        tokenValid.value = false
        loading.value = false
        return
      }

      try {
        const response = await api.get(`/api/password-reset/validate?token=${token}`)
        console.log('토큰 검증 응답:', response)
        tokenValid.value = response.valid
      } catch (err) {
        console.error('토큰 검증 실패:', err)
        tokenValid.value = false
      } finally {
        loading.value = false
      }
    }

    const resetPassword = async () => {
      if (passwordMismatch.value) {
        return
      }

      submitting.value = true
      error.value = ''
      success.value = ''

      try {
        const response = await api.post('/api/password-reset/confirm', {
          token: token,
          newPassword: newPassword.value,
          confirmPassword: confirmPassword.value
        })

        success.value = response.message
        newPassword.value = ''
        confirmPassword.value = ''

        // 성공 알림 표시
        showSuccessNotification('비밀번호가 성공적으로 재설정되었습니다!', '로그인 페이지로 이동합니다...')

        // 2초 후 로그인 페이지로 이동 (성공 메시지와 함께)
        setTimeout(() => {
          router.push({
            path: '/user/login',
            query: { 
              message: '비밀번호가 성공적으로 재설정되었습니다! 새로운 비밀번호로 로그인해주세요.' 
            }
          })
        }, 2000)
      } catch (err) {
        console.error('비밀번호 재설정 실패:', err)
        error.value = err.message || '비밀번호 재설정에 실패했습니다. 다시 시도해주세요.'
      } finally {
        submitting.value = false
      }
    }

    onMounted(() => {
      validateToken()
      // 브라우저 알림 권한 요청
      requestNotificationPermission()
    })

    return {
      newPassword,
      confirmPassword,
      loading,
      submitting,
      tokenValid,
      error,
      success,
      passwordMismatch,
      resetPassword
    }
  }
}
</script>

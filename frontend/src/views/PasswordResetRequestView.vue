<template>
  <div class="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center py-12 px-4 sm:px-6 lg:px-8">
    <div class="max-w-md w-full space-y-8">
      <div class="bg-white rounded-lg shadow-xl p-8">
        <!-- 헤더 -->
        <div class="text-center mb-8">
          <div class="mx-auto h-12 w-12 bg-red-100 rounded-full flex items-center justify-center mb-4">
            <svg class="h-6 w-6 text-red-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"></path>
            </svg>
          </div>
          <h2 class="text-3xl font-bold text-gray-900 mb-2">비밀번호 재설정</h2>
          <p class="text-gray-600">가입한 이메일 주소를 입력하세요</p>
        </div>

        <!-- 폼 -->
        <form @submit.prevent="requestReset" class="space-y-6">
          <div>
            <label for="email" class="block text-sm font-medium text-gray-700 mb-2">
              이메일 주소
            </label>
            <input
              id="email"
              v-model="email"
              type="email"
              required
              class="appearance-none relative block w-full px-3 py-3 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-lg focus:outline-none focus:ring-2 focus:ring-red-500 focus:border-red-500 focus:z-10 sm:text-sm"
              placeholder="example@email.com"
              :disabled="loading"
            />
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
                <p class="text-sm text-green-700">{{ success }}</p>
              </div>
            </div>
          </div>

          <!-- 제출 버튼 -->
          <button
            type="submit"
            :disabled="loading"
            class="group relative w-full flex justify-center py-3 px-4 border border-transparent text-sm font-medium rounded-lg text-white bg-red-600 hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-red-500 disabled:opacity-50 disabled:cursor-not-allowed transition-colors duration-200"
          >
            <span v-if="loading" class="absolute left-0 inset-y-0 flex items-center pl-3">
              <svg class="animate-spin h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
            </span>
            {{ loading ? '처리 중...' : '비밀번호 재설정 이메일 보내기' }}
          </button>
        </form>

        <!-- 로그인 링크 -->
        <div class="text-center mt-6">
          <router-link
            to="/user/login"
            class="text-sm text-red-600 hover:text-red-500 font-medium"
          >
            ← 로그인으로 돌아가기
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import api from '@/services/api'

export default {
  name: 'PasswordResetRequestView',
  setup() {
    const email = ref('')
    const loading = ref(false)
    const error = ref('')
    const success = ref('')

    const requestReset = async () => {
      if (!email.value) {
        error.value = '이메일 주소를 입력해주세요.'
        return
      }

      loading.value = true
      error.value = ''
      success.value = ''

      try {
        const response = await api.post('/api/password-reset/request', {
          email: email.value
        })

        success.value = response?.data?.message || '비밀번호 재설정 이메일이 발송되었습니다.'
        email.value = ''
      } catch (err) {
        console.error('비밀번호 재설정 요청 실패:', err)
        error.value = err.response?.data?.error || '비밀번호 재설정 이메일 발송에 실패했습니다. 다시 시도해주세요.'
      } finally {
        loading.value = false
      }
    }

    return {
      email,
      loading,
      error,
      success,
      requestReset
    }
  }
}
</script>

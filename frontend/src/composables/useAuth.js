import { ref } from 'vue'
import apiService from '@/services/api.js'

// 전역 인증 상태
const user = ref(null)
const isAuthenticated = ref(false)

export function useAuth() {
  // 사용자 정보 가져오기
  const fetchUserInfo = async () => {
    try {
      if (apiService.isLoggedIn()) {
        const userData = await apiService.getCurrentUser()
        if (userData) {
          user.value = userData
          isAuthenticated.value = true
          return userData
        } else {
          user.value = null
          isAuthenticated.value = false
          return null
        }
      } else {
        user.value = null
        isAuthenticated.value = false
        return null
      }
    } catch (error) {
      console.warn('사용자 정보를 가져올 수 없습니다:', error.message)
      // 403, 401 오류는 로그인하지 않은 사용자이므로 조용히 처리
      if (error.message && (error.message.includes('403') || error.message.includes('401'))) {
        user.value = null
        isAuthenticated.value = false
        return null
      }
      // 백엔드 서버가 없어도 로컬 스토리지의 정보로 로그인 상태 유지
      if (localStorage.getItem('jwtToken')) {
        const username = localStorage.getItem('username')
        const role = localStorage.getItem('userRole')
        if (username) {
          user.value = {
            username: username,
            nickname: username, // 기본값으로 username 사용
            role: role || 'USER'
          }
          isAuthenticated.value = true
          return user.value
        }
      }
      user.value = null
      isAuthenticated.value = false
      return null
    }
  }

  // 로그인 처리
  const login = async (credentials) => {
    try {
      const response = await apiService.login(credentials)
      
      // response가 undefined인 경우 처리
      if (!response) {
        throw new Error('로그인 응답이 없습니다.')
      }
      
      // JWT 토큰을 로컬 스토리지에 저장
      localStorage.setItem('jwtToken', response.token)
      localStorage.setItem('userRole', response.role === 'USER' ? 'ROLE_USER' : response.role)
      localStorage.setItem('username', response.username)
      
      // 사용자 정보 업데이트
      await fetchUserInfo()
      
      return response
    } catch (error) {
      console.error('Login error:', error)
      throw error
    }
  }

  // 로그아웃 처리
  const logout = () => {
    apiService.logout()
    user.value = null
    isAuthenticated.value = false
  }

  // 초기 상태 설정
  const initializeAuth = async () => {
    try {
      if (apiService.isLoggedIn()) {
        await fetchUserInfo()
      }
    } catch (error) {
      console.warn('백엔드 서버에 연결할 수 없습니다. 오프라인 모드로 실행됩니다:', error.message)
      // 백엔드 서버가 없어도 프론트엔드는 정상 작동하도록 함
      // 로컬 스토리지에 토큰이 있으면 로그인 상태로 유지
      if (localStorage.getItem('jwtToken')) {
        const username = localStorage.getItem('username')
        const role = localStorage.getItem('userRole')
        if (username) {
          user.value = {
            username: username,
            nickname: username, // 기본값으로 username 사용
            role: role || 'USER'
          }
          isAuthenticated.value = true
        }
      }
    }
  }

  return {
    user: user,
    isAuthenticated: isAuthenticated,
    fetchUserInfo,
    login,
    logout,
    initializeAuth
  }
}

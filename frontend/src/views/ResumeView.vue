<template>
  <div class="max-w-7xl mx-auto px-6 py-8">
    <div class="flex-1 min-w-0">
      <div id="resume-container" class="font-sans bg-transparent shadow-none transition-all duration-500" :class="{ 'opacity-60 pointer-events-none': loading }">
        <!-- 로딩 상태 표시 -->
        <div v-if="loading" class="text-center py-10 text-lg text-gray-600">
          <p>{{ $t('common.loading') }}</p>
        </div>
        
        <!-- 에러 메시지 표시 -->
        <div v-if="error" class="text-red-600 text-center py-5 px-5 bg-red-50 border border-red-200 rounded-lg my-5">
          <p>{{ error }}</p>
        </div>

        <header class="bg-gray-800 text-white p-10 rounded-xl mb-8 shadow-lg dark:bg-gray-900 dark:border dark:border-gray-700">
          <div class="text-center">
            <div class="flex items-center justify-center gap-6 mb-6 flex-wrap">
              <img v-if="resume.photoUrl" :src="resume.photoUrl" alt="프로필 사진" class="w-30 h-30 object-cover rounded-full border-4 border-white shadow-lg">
              <div class="text-center">
                <h1 class="font-bold text-5xl mb-2 text-white">
                  {{ currentLang === 'ja' ? resume.nameJa : resume.nameKo }}
                </h1>
                <p v-if="currentLang === 'ja' ? resume.jobTitleJa : resume.jobTitleKo" class="text-xl text-gray-300 font-medium">
                  {{ currentLang === 'ja' ? resume.jobTitleJa : resume.jobTitleKo }}
                </p>
              </div>
            </div>
            <div class="flex justify-center flex-wrap gap-5 bg-white bg-opacity-10 p-4 rounded-lg text-sm dark:bg-white dark:bg-opacity-5">
              <span v-if="resume.email" class="flex items-center text-gray-100">
                <i class="icon email w-4 h-4 mr-2 bg-gray-100 mask-repeat-none mask-size-contain"></i>
                {{ resume.email }}
              </span>
              <span v-if="showPhone === 'true' && resume.phone" class="flex items-center text-gray-100">
                <i class="icon phone w-4 h-4 mr-2 bg-gray-100 mask-repeat-none mask-size-contain"></i>
                {{ resume.phone }}
              </span>
              <span v-if="showPhone === 'masked' && resume.phone" class="flex items-center text-gray-100">
                <i class="icon phone w-4 h-4 mr-2 bg-gray-100 mask-repeat-none mask-size-contain"></i>
                {{ maskPhone(resume.phone) }}
              </span>
              <span v-if="showPhone === 'contact-info' && resume.phone" class="flex items-center text-gray-100">
                <i class="icon phone w-4 h-4 mr-2 bg-gray-100 mask-repeat-none mask-size-contain"></i>
                문의 시 이메일로 연락
              </span>
              <span v-if="showAddress === 'true' && (currentLang === 'ja' ? resume.addressJa : resume.addressKo)" class="flex items-center text-gray-100">
                <i class="icon address w-4 h-4 mr-2 bg-gray-100 mask-repeat-none mask-size-contain"></i>
                {{ currentLang === 'ja' ? resume.addressJa : resume.addressKo }}
              </span>
              <span v-if="showAddress === 'masked' && (currentLang === 'ja' ? resume.addressJa : resume.addressKo)" class="flex items-center text-gray-100">
                <i class="icon address w-4 h-4 mr-2 bg-gray-100 mask-repeat-none mask-size-contain"></i>
                {{ maskAddress(currentLang === 'ja' ? resume.addressJa : resume.addressKo) }}
              </span>
            </div>
          </div>
        </header>

        <main class="grid grid-cols-1 lg:grid-cols-3 gap-8">
          <!-- 왼쪽 컬럼 (2/3) -->
          <div class="lg:col-span-2">
            <section v-if="currentLang === 'ja' ? resume.introductionJa : resume.introductionKo" class="mb-8">
              <h2 class="text-xl font-bold text-gray-800 mb-5 pb-3 border-b-2 border-gray-800 dark:text-gray-100 dark:border-gray-700">
                {{ $t('resume.introduction') }}
              </h2>
              <p class="leading-relaxed text-gray-600 whitespace-pre-line text-lg mb-4 dark:text-gray-300">
                {{ currentLang === 'ja' ? resume.introductionJa : resume.introductionKo }}
              </p>
            </section>

            <section v-if="resume.skills" class="mb-8">
              <h2 class="text-xl font-bold text-gray-800 mb-5 pb-3 border-b-2 border-gray-800 dark:text-gray-100 dark:border-gray-700">
                {{ $t('resume.skills') }}
              </h2>
              <div class="flex flex-wrap gap-2.5">
                <span class="bg-gray-200 text-gray-800 px-4 py-2 rounded-full text-sm font-medium border border-gray-300 dark:bg-gray-700 dark:text-gray-200 dark:border-gray-600" v-for="skill in skillsList" :key="skill">
                  {{ skill }}
                </span>
              </div>
            </section>

            <section v-if="currentLang === 'ja' ? resume.studentLifeJa : resume.studentLifeKo" class="mb-8">
              <h2 class="text-xl font-bold text-gray-800 mb-5 pb-3 border-b-2 border-gray-800 dark:text-gray-100 dark:border-gray-700">
                {{ $t('resume.studentLife') }}
              </h2>
              <p class="leading-relaxed text-gray-600 whitespace-pre-line text-lg mb-4 dark:text-gray-300">
                {{ currentLang === 'ja' ? resume.studentLifeJa : resume.studentLifeKo }}
              </p>
            </section>

            <section v-if="currentLang === 'ja' ? resume.strengthsWeaknessesJa : resume.strengthsWeaknessesKo" class="mb-8">
              <h2 class="text-xl font-bold text-gray-800 mb-5 pb-3 border-b-2 border-gray-800 dark:text-gray-100 dark:border-gray-700">
                {{ $t('resume.strengthsWeaknesses') }}
              </h2>
              <p class="leading-relaxed text-gray-600 whitespace-pre-line text-lg mb-4 dark:text-gray-300">
                {{ currentLang === 'ja' ? resume.strengthsWeaknessesJa : resume.strengthsWeaknessesKo }}
              </p>
            </section>

            <section v-if="currentLang === 'ja' ? resume.effortExperienceJa : resume.effortExperienceKo" class="mb-8">
              <h2 class="text-xl font-bold text-gray-800 mb-5 pb-3 border-b-2 border-gray-800 dark:text-gray-100 dark:border-gray-700">
                {{ $t('resume.effortExperience') }}
              </h2>
              <p class="leading-relaxed text-gray-600 whitespace-pre-line text-lg mb-4 dark:text-gray-300">
                {{ currentLang === 'ja' ? resume.effortExperienceJa : resume.effortExperienceKo }}
              </p>
            </section>

            <section v-if="currentLang === 'ja' ? resume.japanItMotivationJa : resume.japanItMotivationKo" class="mb-8">
              <h2 class="text-xl font-bold text-gray-800 mb-5 pb-3 border-b-2 border-gray-800 dark:text-gray-100 dark:border-gray-700">
                {{ $t('resume.japanItMotivation') }}
              </h2>
              <p class="leading-relaxed text-gray-600 whitespace-pre-line text-lg mb-4 dark:text-gray-300">
                {{ currentLang === 'ja' ? resume.japanItMotivationJa : resume.japanItMotivationKo }}
              </p>
            </section>

            <section v-if="currentLang === 'ja' ? resume.futurePlanJa : resume.futurePlanKo" class="mb-8">
              <h2 class="text-xl font-bold text-gray-800 mb-5 pb-3 border-b-2 border-gray-800 dark:text-gray-100 dark:border-gray-700">
                {{ $t('resume.futurePlan') }}
              </h2>
              <p class="leading-relaxed text-gray-600 whitespace-pre-line text-lg mb-4 dark:text-gray-300">
                {{ currentLang === 'ja' ? resume.futurePlanJa : resume.futurePlanKo }}
              </p>
            </section>
          </div>
          
          <!-- 오른쪽 컬럼 (1/3) -->
          <div class="lg:col-span-1">
            <section v-if="resume.educations && resume.educations.length > 0" class="mb-8">
              <h2 class="text-xl font-bold text-gray-800 mb-5 pb-3 border-b-2 border-gray-800 dark:text-gray-100 dark:border-gray-700">
                {{ $t('resume.education') }}
              </h2>
              <div v-for="edu in resume.educations" :key="edu.period" class="mb-6 border-l-3 border-gray-200 pl-4 dark:border-gray-700">
                <h3 class="text-base font-semibold mb-1 text-gray-800 dark:text-gray-100 break-words">
                  {{ currentLang === 'ja' ? edu.schoolJa : edu.schoolKo }}
                </h3>
                <p class="text-gray-500 text-sm mb-2 dark:text-gray-400">
                  {{ edu.period }}
                </p>
                <p class="text-sm text-gray-600 dark:text-gray-300 break-words">
                  {{ currentLang === 'ja' ? edu.degreeJa : edu.degreeKo }}
                </p>
              </div>
            </section>
          </div>
        </main>
      </div>
    </div>
  </div>
</template>

<script>
import apiService from '@/services/api.js'

export default {
  name: 'ResumeView',
  data() {
    return {
      showPhone: 'false',
      showAddress: 'false',
      isDarkMode: false,
      resume: {
        nameKo: '',
        nameJa: '',
        jobTitleKo: '',
        jobTitleJa: '',
        email: '',
        phone: '',
        photoUrl: null,
        addressKo: '',
        addressJa: '',
        skills: '',
        introductionKo: '',
        introductionJa: '',
        studentLifeKo: '',
        studentLifeJa: '',
        strengthsWeaknessesKo: '',
        strengthsWeaknessesJa: '',
        effortExperienceKo: '',
        effortExperienceJa: '',
        japanItMotivationKo: '',
        japanItMotivationJa: '',
        futurePlanKo: '',
        futurePlanJa: '',
        educations: []
      },
      loading: false,
      error: null
    }
  },
  computed: {
    currentLang() {
      return this.$i18n.locale
    },
    skillsList() {
      if (!this.resume.skills) return []
      return this.resume.skills.split(',').map(skill => skill.trim()).filter(skill => skill)
    }
  },
  watch: {
    currentLang: {
      handler(newLang) {
        this.loadResumeData(newLang)
      },
      immediate: true
    }
  },
  mounted() {
    this.loadResumeData(this.currentLang)
    this.loadSettings()
    this.checkDarkMode();
    window.addEventListener('dark-mode-toggled', this.handleDarkModeToggle);
    // 다크모드 상태에 따라 body에 dark 클래스 추가/제거
    this.updateDarkModeClass();
  },
  beforeUnmount() {
    window.removeEventListener('dark-mode-toggled', this.handleDarkModeToggle);
  },
  methods: {
    async loadResumeData(lang = 'ko') {
      this.loading = true
      this.error = null
      
      try {
        const response = await apiService.getResume(lang)
        
        if (response.success) {
          this.resume = response.data
          console.log('이력서 데이터 로드 성공:', this.resume)
        } else {
          this.error = response.message || '이력서 데이터를 불러오는데 실패했습니다.'
        }
      } catch (error) {
        console.error('이력서 데이터 로드 실패:', error)
        console.error('에러 상세 정보:', {
          message: error.message,
          status: error.status,
          response: error.response
        })
        this.error = `이력서 데이터를 불러오는데 실패했습니다. (${error.message})`
      } finally {
        this.loading = false
      }
    },
    
    async loadSettings() {
      try {
        // 실제 백엔드 설정 API 호출
        // const response = await apiService.getSettings();
        // this.showPhone = response.data.showPhone;
        // this.showAddress = response.data.showAddress;
        
        // 현재는 기본값 사용
        this.showPhone = 'false'
        this.showAddress = 'false'
      } catch (error) {
        console.error('설정 로드 실패:', error)
        // 기본값 사용
        this.showPhone = 'false'
        this.showAddress = 'false'
      }
    },
    
    maskPhone(phone) {
      if (!phone) return ''
      return phone.substring(0, 3) + '-****-' + phone.substring(phone.length - 4)
    },
    
    maskAddress(address) {
      if (!address) return ''
      return address.substring(0, 3) + '***'
    },
    checkDarkMode() {
      this.isDarkMode = document.body.classList.contains('dark-mode');
    },
    handleDarkModeToggle(event) {
      this.isDarkMode = event.detail.isDarkMode;
      this.updateDarkModeClass();
    },
    updateDarkModeClass() {
      if (this.isDarkMode) {
        document.documentElement.classList.add('dark');
      } else {
        document.documentElement.classList.remove('dark');
      }
    }
  }
}
</script>

<style scoped>
/* 아이콘 스타일만 유지 */
.icon.email { 
  mask-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="black"><path d="M20 4H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 4l-8 5-8-5V6l8 5 8-5v2z"/></svg>'); 
}

.icon.phone { 
  mask-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="black"><path d="M6.62 10.79c1.44 2.83 3.76 5.14 6.59 6.59l2.2-2.2c.27-.27.67-.36 1.02-.24 1.12.37 2.33.57 3.57.57.55 0 1 .45 1 1V20c0 .55-.45 1-1 1-9.39 0-17-7.61-17-17 0-.55.45-1 1-1h3.5c.55 0 1 .45 1 1 0 1.25.2 2.45.57 3.57.11.35.03.74-.25 1.02l-2.2 2.2z"/></svg>'); 
}

.icon.address { 
  mask-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="black"><path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5c-1.38 0-2.5-1.12-2.5-2.5s1.12-2.5 2.5-2.5 2.5 1.12 2.5 2.5-1.12 2.5-2.5-2.5z"/></svg>'); 
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .flex.items-center.justify-center.gap-6.mb-6.flex-wrap {
    flex-direction: column;
    text-align: center;
  }
  
  .font-bold.text-5xl.mb-2.text-white {
    font-size: 2rem;
  }
  
  .text-xl.text-gray-300.font-medium {
    font-size: 1rem;
  }
  
  .flex.justify-center.flex-wrap.gap-5 {
    gap: 15px;
    font-size: 12px;
  }
  
  .bg-gray-800.text-white.p-10.rounded-xl.mb-8.shadow-lg {
    padding: 30px 20px;
  }
  
  /* 모바일에서 그리드 레이아웃 조정 */
  .grid.grid-cols-1.lg\\:grid-cols-3 {
    grid-template-columns: 1fr;
  }
  
  .lg\\:col-span-2,
  .lg\\:col-span-1 {
    grid-column: span 1;
  }
}
</style> 
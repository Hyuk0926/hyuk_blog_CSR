<template>
  <div class="resume-content container" :class="{ loading: loading }">
    <!-- 로딩 상태 표시 -->
    <div v-if="loading" class="loading-message">
      <p>{{ $t('common.loading') }}</p>
    </div>
    
    <!-- 에러 메시지 표시 -->
    <div v-if="error" class="error-message">
      <p>{{ error }}</p>
    </div>

    <header class="resume-header">
      <img v-if="resume.photoUrl" :src="resume.photoUrl" alt="프로필 사진" class="profile-photo">
      <div>
        <h1>{{ currentLang === 'ja' ? resume.nameJa : resume.nameKo }}</h1>
        <p class="intro">{{ currentLang === 'ja' ? resume.introductionJa : resume.introductionKo }}</p>
        <p class="contact">
          <span>{{ resume.email }}</span>
          <span v-if="resume.email && resume.phone && showPhone === 'true'"> | </span>
          <span v-if="showPhone === 'true'">{{ resume.phone }}</span>
          <!-- 전화번호 마스킹 처리 -->
          <span v-if="showPhone === 'masked' && resume.phone"> | </span>
          <span v-if="showPhone === 'masked'">{{ maskPhone(resume.phone) }}</span>
          <!-- 연락처 안내 메시지 -->
          <span v-if="showPhone === 'contact-info' && resume.phone"> | </span>
          <span v-if="showPhone === 'contact-info'">문의 시 이메일로 연락</span>
          <!-- 주소 표시 -->
          <span v-if="showAddress === 'true' && (currentLang === 'ja' ? resume.addressJa : resume.addressKo)"> | </span>
          <span v-if="showAddress === 'true'">{{ currentLang === 'ja' ? resume.addressJa : resume.addressKo }}</span>
          <!-- 주소 마스킹 처리 -->
          <span v-if="showAddress === 'masked' && (currentLang === 'ja' ? resume.addressJa : resume.addressKo)"> | </span>
          <span v-if="showAddress === 'masked'">{{ maskAddress(currentLang === 'ja' ? resume.addressJa : resume.addressKo) }}</span>
        </p>
      </div>
    </header>

    <main>
      <section v-if="resume.educations && resume.educations.length > 0">
        <h2>{{ $t('resume.education') }}</h2>
        <table class="custom-table">
          <thead>
            <tr>
              <th>{{ $t('resume.school') }}</th>
              <th>{{ $t('resume.degree') }}</th>
              <th>{{ $t('resume.period') }}</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="edu in resume.educations" :key="edu.period">
              <td>{{ currentLang === 'ja' ? edu.schoolJa : edu.schoolKo }}</td>
              <td>{{ currentLang === 'ja' ? edu.degreeJa : edu.degreeKo }}</td>
              <td>{{ edu.period }}</td>
            </tr>
          </tbody>
        </table>
      </section>

      <section v-if="resume.skills">
        <h2>{{ $t('resume.skills') }}</h2>
        <div class="skills">
          <span class="skill-tag" v-for="skill in skillsList" :key="skill">{{ skill }}</span>
        </div>
      </section>

      <section v-if="currentLang === 'ja' ? resume.studentLifeJa : resume.studentLifeKo">
        <h2>{{ $t('resume.studentLife') }}</h2>
        <p class="self-intro">{{ currentLang === 'ja' ? resume.studentLifeJa : resume.studentLifeKo }}</p>
      </section>

      <section v-if="currentLang === 'ja' ? resume.strengthsWeaknessesJa : resume.strengthsWeaknessesKo">
        <h2>{{ $t('resume.strengthsWeaknesses') }}</h2>
        <p class="self-intro">{{ currentLang === 'ja' ? resume.strengthsWeaknessesJa : resume.strengthsWeaknessesKo }}</p>
      </section>

      <section v-if="currentLang === 'ja' ? resume.effortExperienceJa : resume.effortExperienceKo">
        <h2>{{ $t('resume.effortExperience') }}</h2>
        <p class="self-intro">{{ currentLang === 'ja' ? resume.effortExperienceJa : resume.effortExperienceKo }}</p>
      </section>

      <section v-if="currentLang === 'ja' ? resume.japanItMotivationJa : resume.japanItMotivationKo">
        <h2>{{ $t('resume.japanItMotivation') }}</h2>
        <p class="self-intro">{{ currentLang === 'ja' ? resume.japanItMotivationJa : resume.japanItMotivationKo }}</p>
      </section>

      <section v-if="currentLang === 'ja' ? resume.futurePlanJa : resume.futurePlanKo">
        <h2>{{ $t('resume.futurePlan') }}</h2>
        <p class="self-intro">{{ currentLang === 'ja' ? resume.futurePlanJa : resume.futurePlanKo }}</p>
      </section>
    </main>
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
      resume: {
        name: '',
        email: '',
        phone: '',
        photoUrl: null,
        address: '',
        skills: '',
        introduction: '',
        studentLife: '',
        strengthsWeaknesses: '',
        effortExperience: '',
        japanItMotivation: '',
        futurePlan: '',
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
  },
  methods: {
    async loadResumeData(lang = 'ko') {
      this.loading = true
      this.error = null
      
      try {
        const response = await apiService.getResume(lang)
        
        if (response.success) {
          this.resume = response.data
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
    }
  }
}
</script>

<style scoped>
/* resume.css가 이미 로드되어 있으므로 추가 스타일만 정의 */
.resume-content {
  margin-top: 0;
}

/* 다크 모드 전환 애니메이션 */
.resume-content {
  transition: background-color 0.5s ease, color 0.5s ease, box-shadow 0.5s ease;
}

/* 로딩 상태 스타일 */
.resume-content.loading {
  opacity: 0.6;
  pointer-events: none;
}

/* 에러 메시지 스타일 */
.error-message {
  color: #dc3545;
  text-align: center;
  padding: 20px;
  background-color: #f8d7da;
  border: 1px solid #f5c6cb;
  border-radius: 4px;
  margin: 20px 0;
}

/* 로딩 메시지 스타일 */
.loading-message {
  text-align: center;
  padding: 40px;
  font-size: 18px;
  color: #666;
}

/* 반응형 조정 */
@media (max-width: 768px) {
  .resume-content {
    margin: 20px auto;
    padding: 20px 16px;
  }
}
</style> 
<template>
  <div class="page-container">
    <div class="main-content">
      <div id="resume-container" class="resume-container" :class="{ loading: loading }">
        <!-- 로딩 상태 표시 -->
        <div v-if="loading" class="loading-message">
          <p>{{ $t('common.loading') }}</p>
        </div>
        
        <!-- 에러 메시지 표시 -->
        <div v-if="error" class="error-message">
          <p>{{ error }}</p>
        </div>

        <header class="resume-header">
          <div class="header-content">
            <div class="profile-section">
              <img v-if="resume.photoUrl" :src="resume.photoUrl" alt="프로필 사진" class="profile-photo">
              <div class="profile-info">
                <h1>{{ currentLang === 'ja' ? resume.nameJa : resume.nameKo }}</h1>
                <p v-if="currentLang === 'ja' ? resume.jobTitleJa : resume.jobTitleKo" class="job-title">{{ currentLang === 'ja' ? resume.jobTitleJa : resume.jobTitleKo }}</p>
              </div>
            </div>
            <div class="contact-bar">
              <span v-if="resume.email"><i class="icon email"></i>{{ resume.email }}</span>
              <span v-if="showPhone === 'true' && resume.phone"><i class="icon phone"></i>{{ resume.phone }}</span>
              <span v-if="showPhone === 'masked' && resume.phone"><i class="icon phone"></i>{{ maskPhone(resume.phone) }}</span>
              <span v-if="showPhone === 'contact-info' && resume.phone"><i class="icon phone"></i>문의 시 이메일로 연락</span>
              <span v-if="showAddress === 'true' && (currentLang === 'ja' ? resume.addressJa : resume.addressKo)">
                <i class="icon address"></i>{{ currentLang === 'ja' ? resume.addressJa : resume.addressKo }}
              </span>
              <span v-if="showAddress === 'masked' && (currentLang === 'ja' ? resume.addressJa : resume.addressKo)">
                <i class="icon address"></i>{{ maskAddress(currentLang === 'ja' ? resume.addressJa : resume.addressKo) }}
              </span>
            </div>
          </div>
        </header>

        <main class="resume-body">
          <div class="left-column">
            <section v-if="currentLang === 'ja' ? resume.introductionJa : resume.introductionKo">
              <h2 class="section-title">{{ $t('resume.introduction') }}</h2>
              <p class="self-intro">{{ currentLang === 'ja' ? resume.introductionJa : resume.introductionKo }}</p>
            </section>

            <section v-if="resume.skills">
              <h2 class="section-title">{{ $t('resume.skills') }}</h2>
              <div class="skills-grid">
                <span class="skill-tag" v-for="skill in skillsList" :key="skill">{{ skill }}</span>
              </div>
            </section>

            <section v-if="currentLang === 'ja' ? resume.studentLifeJa : resume.studentLifeKo">
              <h2 class="section-title">{{ $t('resume.studentLife') }}</h2>
              <p class="self-intro">{{ currentLang === 'ja' ? resume.studentLifeJa : resume.studentLifeKo }}</p>
            </section>

            <section v-if="currentLang === 'ja' ? resume.strengthsWeaknessesJa : resume.strengthsWeaknessesKo">
              <h2 class="section-title">{{ $t('resume.strengthsWeaknesses') }}</h2>
              <p class="self-intro">{{ currentLang === 'ja' ? resume.strengthsWeaknessesJa : resume.strengthsWeaknessesKo }}</p>
            </section>

            <section v-if="currentLang === 'ja' ? resume.effortExperienceJa : resume.effortExperienceKo">
              <h2 class="section-title">{{ $t('resume.effortExperience') }}</h2>
              <p class="self-intro">{{ currentLang === 'ja' ? resume.effortExperienceJa : resume.effortExperienceKo }}</p>
            </section>

            <section v-if="currentLang === 'ja' ? resume.japanItMotivationJa : resume.japanItMotivationKo">
              <h2 class="section-title">{{ $t('resume.japanItMotivation') }}</h2>
              <p class="self-intro">{{ currentLang === 'ja' ? resume.japanItMotivationJa : resume.japanItMotivationKo }}</p>
            </section>

            <section v-if="currentLang === 'ja' ? resume.futurePlanJa : resume.futurePlanKo">
              <h2 class="section-title">{{ $t('resume.futurePlan') }}</h2>
              <p class="self-intro">{{ currentLang === 'ja' ? resume.futurePlanJa : resume.futurePlanKo }}</p>
            </section>
          </div>
          
          <div class="right-column">
            <section v-if="resume.educations && resume.educations.length > 0">
              <h2 class="section-title">{{ $t('resume.education') }}</h2>
              <div v-for="edu in resume.educations" :key="edu.period" class="item">
                <h3>{{ currentLang === 'ja' ? edu.schoolJa : edu.schoolKo }}</h3>
                <p class="period">{{ edu.period }}</p>
                <p>{{ currentLang === 'ja' ? edu.degreeJa : edu.degreeKo }}</p>
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
    }
  }
}
</script>

<style scoped>
.resume-container {
  font-family: 'Montserrat', 'Noto Sans KR', sans-serif;
  background: transparent;
  box-shadow: none;
  transition: background-color 0.5s ease, color 0.5s ease;
}

/* 로딩 상태 스타일 */
.resume-container.loading {
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
  border-radius: 8px;
  margin: 20px 0;
}

/* 로딩 메시지 스타일 */
.loading-message {
  text-align: center;
  padding: 40px;
  font-size: 18px;
  color: #666;
}

.resume-header {
  background: #2c3e50;
  color: #fff;
  padding: 40px;
  border-radius: 12px;
  margin-bottom: 32px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.header-content {
  text-align: center;
}

.profile-section {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 24px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.profile-photo {
  width: 120px;
  height: 120px;
  object-fit: cover;
  border-radius: 50%;
  border: 4px solid #fff;
  box-shadow: 0 4px 12px rgba(0,0,0,0.2);
}

.profile-info h1 {
  font-family: 'Montserrat', sans-serif;
  font-size: 2.5rem;
  font-weight: 700;
  margin: 0 0 8px 0;
  color: #fff;
}

.profile-info .job-title {
  font-size: 1.2rem;
  margin: 0;
  color: #bdc3c7;
  font-weight: 500;
}

.contact-bar {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 20px;
  background: rgba(255,255,255,0.1);
  padding: 16px;
  border-radius: 8px;
  font-size: 14px;
}

.contact-bar span {
  display: flex;
  align-items: center;
  color: #ecf0f1;
}

.contact-bar .icon {
  display: inline-block;
  width: 16px;
  height: 16px;
  margin-right: 8px;
  background-color: #ecf0f1;
  mask-repeat: no-repeat;
  mask-size: contain;
}

.icon.email { 
  mask-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="black"><path d="M20 4H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 4l-8 5-8-5V6l8 5 8-5v2z"/></svg>'); 
}

.icon.phone { 
  mask-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="black"><path d="M6.62 10.79c1.44 2.83 3.76 5.14 6.59 6.59l2.2-2.2c.27-.27.67-.36 1.02-.24 1.12.37 2.33.57 3.57.57.55 0 1 .45 1 1V20c0 .55-.45 1-1 1-9.39 0-17-7.61-17-17 0-.55.45-1 1-1h3.5c.55 0 1 .45 1 1 0 1.25.2 2.45.57 3.57.11.35.03.74-.25 1.02l-2.2 2.2z"/></svg>'); 
}

.icon.address { 
  mask-image: url('data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="black"><path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5c-1.38 0-2.5-1.12-2.5-2.5s1.12-2.5 2.5-2.5 2.5 1.12 2.5 2.5-1.12 2.5-2.5-2.5z"/></svg>'); 
}

.resume-body {
  display: flex;
  gap: 40px;
}

.left-column { 
  flex: 2;
}

.right-column { 
  flex: 1;
}

.section-title {
  font-family: 'Montserrat', sans-serif;
  font-size: 1.25rem;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 2px solid #2c3e50;
}

.self-intro, .description {
  line-height: 1.8;
  color: #4a5568;
  white-space: pre-line;
  font-size: 1.05rem;
  margin-bottom: 1em;
}

.skills-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.skill-tag {
  background: #e2e8f0;
  color: #2c3e50;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  border: 1px solid #cbd5e0;
}

.item {
  margin-bottom: 25px;
  border-left: 3px solid #e2e8f0;
  padding-left: 15px;
}

.item h3 {
  font-size: 16px;
  margin: 0 0 5px;
  color: #2c3e50;
  font-weight: 600;
}

.item .period {
  color: #718096;
  font-size: 13px;
  margin-bottom: 8px;
}

.item p {
  margin: 0;
  font-size: 14px;
  color: #4a5568;
}

/* 반응형 조정 */
@media (max-width: 768px) {
  .resume-body {
    flex-direction: column;
  }
  
  .profile-section {
    flex-direction: column;
    text-align: center;
  }
  
  .profile-info h1 {
    font-size: 2rem;
  }
  
  .profile-info .job-title {
    font-size: 1rem;
  }
  
  .contact-bar {
    gap: 15px;
    font-size: 12px;
  }
  
  .resume-header {
    padding: 30px 20px;
  }
}

/* 다크 모드 지원 */
body.dark-mode .resume-container {
  background: transparent;
  color: #e2e8f0;
}

body.dark-mode .resume-header {
  background: #1a202c;
  border: 1px solid #2d3748;
}

body.dark-mode .profile-info .job-title {
  color: #a0aec0;
}

body.dark-mode .contact-bar {
  background: rgba(255,255,255,0.05);
}

body.dark-mode .contact-bar span {
  color: #a0aec0;
}

body.dark-mode .section-title {
  color: #f7fafc;
  border-bottom-color: #4a5568;
}

body.dark-mode .self-intro, 
body.dark-mode .description {
  color: #a0aec0;
}

body.dark-mode .skill-tag {
  background: #2d3748;
  color: #e2e8f0;
  border-color: #4a5568;
}

body.dark-mode .item {
  border-left-color: #2d3748;
}

body.dark-mode .item h3 {
  color: #f7fafc;
}

body.dark-mode .item .period {
  color: #718096;
}

body.dark-mode .item p {
  color: #a0aec0;
}
</style> 
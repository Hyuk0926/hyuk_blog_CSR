<template>
  <div class="resume-container">
    <div class="resume-form-container">
      <h1 class="resume-form-title">
        <i class="fas fa-user-tie"></i>
        이력서 관리
        <span v-if="loading" class="loading-indicator">
          <i class="fas fa-spinner fa-spin"></i>
          로딩 중...
        </span>
      </h1>
      
      <form @submit.prevent="saveResume" class="resume-form">
        <!-- 프로필 사진 -->
        <div class="form-section">
          <h3 class="section-title">
            <i class="fas fa-camera"></i>
            프로필 사진
          </h3>
          <div class="form-group">
            <label class="form-label">사진 URL</label>
            <div class="input-with-preview">
              <input 
                v-model="resume.photoUrl" 
                type="text" 
                class="form-input" 
                placeholder="이미지 URL을 입력하세요"
              >
              <div v-if="resume.photoUrl" class="photo-preview">
                <img :src="resume.photoUrl" alt="프로필 사진" @error="handleImageError">
              </div>
            </div>
          </div>
        </div>

        <!-- 기본 정보 -->
        <div class="form-section">
          <h3 class="section-title">
            <i class="fas fa-info-circle"></i>
            기본 정보
          </h3>
          
          <div class="form-row">
            <div class="form-group">
              <label class="form-label">
                <span class="flag-ko">🇰🇷</span>
                이름 (한국어)
              </label>
              <input 
                v-model="resume.nameKo" 
                type="text" 
                class="form-input" 
                placeholder="예: 최은혁" 
                required
              >
            </div>
            <div class="form-group">
              <label class="form-label">
                <span class="flag-ja">🇯🇵</span>
                이름 (일본어)
              </label>
              <input 
                v-model="resume.nameJa" 
                type="text" 
                class="form-input" 
                placeholder="例: 崔恩爀(通称：高原優輝）" 
                required
              >
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label class="form-label">
                <i class="fas fa-phone"></i>
                연락처
              </label>
              <input 
                v-model="resume.phone" 
                type="text" 
                class="form-input" 
                placeholder="010-1234-5678"
              >
            </div>
            <div class="form-group">
              <label class="form-label">
                <i class="fas fa-envelope"></i>
                이메일
              </label>
              <input 
                v-model="resume.email" 
                type="email" 
                class="form-input" 
                placeholder="example@email.com"
              >
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label class="form-label">
                <i class="fas fa-birthday-cake"></i>
                생년월일
              </label>
              <input 
                v-model="resume.birth" 
                type="date" 
                class="form-input"
              >
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label class="form-label">
                <span class="flag-ko">🇰🇷</span>
                주소 (한국어)
              </label>
              <input 
                v-model="resume.addressKo" 
                type="text" 
                class="form-input" 
                placeholder="예: 대한민국 경기도 용인시 ..."
              >
            </div>
            <div class="form-group">
              <label class="form-label">
                <span class="flag-ja">🇯🇵</span>
                주소 (일본어)
              </label>
              <input 
                v-model="resume.addressJa" 
                type="text" 
                class="form-input" 
                placeholder="例: 韓民国 京畿道 ..."
              >
            </div>
          </div>
        </div>

        <!-- 학력 -->
        <div class="form-section">
          <h3 class="section-title">
            <i class="fas fa-graduation-cap"></i>
            학력
          </h3>
          
          <div class="education-table-container">
            <table class="education-table">
              <thead>
                <tr>
                  <th class="flag-ko">학교명 (🇰🇷)</th>
                  <th class="flag-ja">학교명 (🇯🇵)</th>
                  <th class="flag-ko">전공 (🇰🇷)</th>
                  <th class="flag-ja">전공 (🇯🇵)</th>
                  <th>기간</th>
                  <th>삭제</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(education, index) in resume.educations" :key="index" :class="{ 'even-row': index % 2 === 0 }">
                  <td>
                    <input 
                      v-model="education.schoolKo" 
                      type="text" 
                      class="form-input compact-input" 
                      placeholder="예: 상현고등학교"
                    >
                  </td>
                  <td>
                    <input 
                      v-model="education.schoolJa" 
                      type="text" 
                      class="form-input compact-input" 
                      placeholder="例: 上峴高校"
                    >
                  </td>
                  <td>
                    <input 
                      v-model="education.degreeKo" 
                      type="text" 
                      class="form-input compact-input" 
                      placeholder="예: 인문계"
                    >
                  </td>
                  <td>
                    <input 
                      v-model="education.degreeJa" 
                      type="text" 
                      class="form-input compact-input" 
                      placeholder="例: 人文系"
                    >
                  </td>
                  <td>
                    <input 
                      v-model="education.period" 
                      type="text" 
                      class="form-input compact-input" 
                      placeholder="2015.03-2018.02"
                    >
                  </td>
                  <td class="action-cell">
                    <button 
                      type="button" 
                      class="remove-btn" 
                      @click="removeEducation(index)"
                      title="삭제"
                    >
                      <i class="fas fa-trash"></i>
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
            <button 
              type="button" 
              class="add-btn" 
              @click="addEducation"
            >
              <i class="fas fa-plus"></i>
              학력 추가
            </button>
          </div>
        </div>

        <!-- 기술 스택 -->
        <div class="form-section">
          <h3 class="section-title">
            <i class="fas fa-code"></i>
            기술 스택
          </h3>
          <div class="form-group">
            <label class="form-label">보유 기술</label>
            <input 
              v-model="resume.skills" 
              type="text" 
              class="form-input" 
              placeholder="Java, Spring, React, Vue.js, ..."
            >
          </div>
        </div>

        <!-- 자기소개 -->
        <div class="form-section">
          <h3 class="section-title">
            <i class="fas fa-user-edit"></i>
            자기소개
          </h3>
          
          <div class="form-group">
            <label class="form-label">
              <span class="flag-ko">🇰🇷</span>
              자기소개 (한국어)
            </label>
            <textarea 
              v-model="resume.introductionKo" 
              class="form-textarea" 
              placeholder="한국어 자기소개를 입력하세요"
              rows="4"
            ></textarea>
          </div>
          
          <div class="form-group">
            <label class="form-label">
              <span class="flag-ja">🇯🇵</span>
              自己紹介 (일본어)
            </label>
            <textarea 
              v-model="resume.introductionJa" 
              class="form-textarea" 
              placeholder="日本語 自己紹介を入力してください"
              rows="4"
            ></textarea>
          </div>
          
          <div class="form-group">
            <label class="form-label">
              <span class="flag-ko">🇰🇷</span>
              학생생활 (한국어)
            </label>
            <textarea 
              v-model="resume.studentLifeKo" 
              class="form-textarea" 
              placeholder="한국어 성장과정을 입력하세요"
              rows="4"
            ></textarea>
          </div>
          
          <div class="form-group">
            <label class="form-label">
              <span class="flag-ja">🇯🇵</span>
              学生生活 (일본어)
            </label>
            <textarea 
              v-model="resume.studentLifeJa" 
              class="form-textarea" 
              placeholder="日本語 学生生活を入力してください"
              rows="4"
            ></textarea>
          </div>
        </div>

        <!-- 장단점 -->
        <div class="form-section">
          <h3 class="section-title">
            <i class="fas fa-balance-scale"></i>
            장점과 단점
          </h3>
          
          <div class="form-group">
            <label class="form-label">
              <span class="flag-ko">🇰🇷</span>
              장점과 단점 (한국어)
            </label>
            <textarea 
              v-model="resume.strengthsWeaknessesKo" 
              class="form-textarea" 
              placeholder="한국어 장단점을 입력하세요"
              rows="4"
            ></textarea>
          </div>
          
          <div class="form-group">
            <label class="form-label">
              <span class="flag-ja">🇯🇵</span>
              長所と短所 (일본어)
            </label>
            <textarea 
              v-model="resume.strengthsWeaknessesJa" 
              class="form-textarea" 
              placeholder="日本語 長所と短所を入力してください"
              rows="4"
            ></textarea>
          </div>
        </div>

        <!-- 노력 경험 -->
        <div class="form-section">
          <h3 class="section-title">
            <i class="fas fa-trophy"></i>
            노력 경험
          </h3>
          
          <div class="form-group">
            <label class="form-label">
              <span class="flag-ko">🇰🇷</span>
              노력 경험 (한국어)
            </label>
            <textarea 
              v-model="resume.effortExperienceKo" 
              class="form-textarea" 
              placeholder="한국어 노력 경험을 입력하세요"
              rows="4"
            ></textarea>
          </div>
          
          <div class="form-group">
            <label class="form-label">
              <span class="flag-ja">🇯🇵</span>
              努力経験 (일본어)
            </label>
            <textarea 
              v-model="resume.effortExperienceJa" 
              class="form-textarea" 
              placeholder="日本語 努力経験を入力してください"
              rows="4"
            ></textarea>
          </div>
        </div>

        <!-- 일본 IT 취업 동기 -->
        <div class="form-section">
          <h3 class="section-title">
            <i class="fas fa-rocket"></i>
            일본 IT 취업 동기
          </h3>
          
          <div class="form-group">
            <label class="form-label">
              <span class="flag-ko">🇰🇷</span>
              일본 IT 취업 동기 (한국어)
            </label>
            <textarea 
              v-model="resume.japanItMotivationKo" 
              class="form-textarea" 
              placeholder="한국어 일본 IT 취업 동기를 입력하세요"
              rows="4"
            ></textarea>
          </div>
          
          <div class="form-group">
            <label class="form-label">
              <span class="flag-ja">🇯🇵</span>
              日本IT就職志望動機 (일본어)
            </label>
            <textarea 
              v-model="resume.japanItMotivationJa" 
              class="form-textarea" 
              placeholder="日本語 IT志望動機を入力してください"
              rows="4"
            ></textarea>
          </div>
        </div>

        <!-- 장래 계획 -->
        <div class="form-section">
          <h3 class="section-title">
            <i class="fas fa-chart-line"></i>
            장래 계획
          </h3>
          
          <div class="form-group">
            <label class="form-label">
              <span class="flag-ko">🇰🇷</span>
              장래 계획 (한국어)
            </label>
            <textarea 
              v-model="resume.futurePlanKo" 
              class="form-textarea" 
              placeholder="한국어 장래 계획을 입력하세요"
              rows="4"
            ></textarea>
          </div>
          
          <div class="form-group">
            <label class="form-label">
              <span class="flag-ja">🇯🇵</span>
              将来の計画 (일본어)
            </label>
            <textarea 
              v-model="resume.futurePlanJa" 
              class="form-textarea" 
              placeholder="日本語 将来の計画を入力してください"
              rows="4"
            ></textarea>
          </div>
        </div>

        <!-- 버튼 -->
        <div class="form-buttons">
          <button type="submit" class="btn-submit" :disabled="saving">
            <i class="fas fa-save"></i>
            {{ saving ? '저장 중...' : '저장' }}
          </button>
          <router-link to="/admin" class="btn-cancel" :class="{ 'disabled': saving }">
            <i class="fas fa-times"></i>
            취소
          </router-link>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import apiService from '@/services/api.js'

export default {
  name: 'ResumeView',
  data() {
    return {
      resume: {
        photoUrl: '',
        nameKo: '',
        nameJa: '',
        phone: '',
        email: '',
        birth: '',
        addressKo: '',
        addressJa: '',
        educations: [],
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
        futurePlanJa: ''
      },
      loading: false,
      saving: false
    }
  },
  mounted() {
    // 페이지 진입 시 배경 스타일 설정
    document.body.style.background = 'linear-gradient(135deg, #1a1a1a 0%, #2d2d2d 25%, #404040 50%, #2d2d2d 75%, #1a1a1a 100%)';
    document.body.style.minHeight = '100vh';
    document.body.style.margin = '0';
    document.body.style.padding = '0';
    document.body.style.fontFamily = "'Noto Sans KR', sans-serif";
    
    this.loadResume()
  },
  beforeUnmount() {
    // 페이지 나갈 때 배경 스타일 초기화
    document.body.style.background = '';
    document.body.style.minHeight = '';
    document.body.style.margin = '';
    document.body.style.padding = '';
    document.body.style.fontFamily = '';
  },
  methods: {
    async loadResume() {
      try {
        this.loading = true;
        const response = await apiService.getResume('ko');
        
        if (response.success && response.data) {
          // API 응답 데이터를 폼 데이터로 변환
          this.resume = {
            photoUrl: response.data.photoUrl || '',
            nameKo: response.data.nameKo || '',
            nameJa: response.data.nameJa || '',
            phone: response.data.phone || '',
            email: response.data.email || '',
            birth: response.data.birth || '',
            addressKo: response.data.addressKo || '',
            addressJa: response.data.addressJa || '',
            educations: response.data.educations || [],
            skills: response.data.skills || '',
            introductionKo: response.data.introductionKo || '',
            introductionJa: response.data.introductionJa || '',
            studentLifeKo: response.data.studentLifeKo || '',
            studentLifeJa: response.data.studentLifeJa || '',
            strengthsWeaknessesKo: response.data.strengthsWeaknessesKo || '',
            strengthsWeaknessesJa: response.data.strengthsWeaknessesJa || '',
            effortExperienceKo: response.data.effortExperienceKo || '',
            effortExperienceJa: response.data.effortExperienceJa || '',
            japanItMotivationKo: response.data.japanItMotivationKo || '',
            japanItMotivationJa: response.data.japanItMotivationJa || '',
            futurePlanKo: response.data.futurePlanKo || '',
            futurePlanJa: response.data.futurePlanJa || ''
          };
        }
        
        // 학력 데이터가 없으면 기본 추가
        if (this.resume.educations.length === 0) {
          this.addEducation()
        }
      } catch (error) {
        console.error('이력서 로드 실패:', error)
        // 에러가 발생해도 기본 학력 데이터는 추가
        if (this.resume.educations.length === 0) {
          this.addEducation()
        }
      } finally {
        this.loading = false;
      }
    },
    
    async saveResume() {
      try {
        this.saving = true;
        
        // 폼 데이터를 API 요청 형식으로 변환
        const resumeData = {
          nameKo: this.resume.nameKo,
          nameJa: this.resume.nameJa,
          phone: this.resume.phone,
          email: this.resume.email,
          photoUrl: this.resume.photoUrl,
          birth: this.resume.birth,
          addressKo: this.resume.addressKo,
          addressJa: this.resume.addressJa,
          educations: this.resume.educations,
          skills: this.resume.skills,
          introductionKo: this.resume.introductionKo,
          introductionJa: this.resume.introductionJa,
          studentLifeKo: this.resume.studentLifeKo,
          studentLifeJa: this.resume.studentLifeJa,
          strengthsWeaknessesKo: this.resume.strengthsWeaknessesKo,
          strengthsWeaknessesJa: this.resume.strengthsWeaknessesJa,
          effortExperienceKo: this.resume.effortExperienceKo,
          effortExperienceJa: this.resume.effortExperienceJa,
          japanItMotivationKo: this.resume.japanItMotivationKo,
          japanItMotivationJa: this.resume.japanItMotivationJa,
          futurePlanKo: this.resume.futurePlanKo,
          futurePlanJa: this.resume.futurePlanJa
        };
        
        const response = await apiService.updateResume(resumeData);
        
        if (response.success) {
          alert('이력서가 성공적으로 저장되었습니다.');
          this.$router.push('/admin');
        } else {
          alert('저장에 실패했습니다: ' + (response.message || '알 수 없는 오류'));
        }
      } catch (error) {
        console.error('이력서 저장 실패:', error)
        alert('저장에 실패했습니다: ' + (error.message || '알 수 없는 오류'));
      } finally {
        this.saving = false;
      }
    },
    
    addEducation() {
      this.resume.educations.push({
        schoolKo: '',
        schoolJa: '',
        degreeKo: '',
        degreeJa: '',
        period: ''
      })
    },
    
    removeEducation(index) {
      this.resume.educations.splice(index, 1)
    },
    
    handleImageError(event) {
      event.target.style.display = 'none'
      alert('이미지를 불러올 수 없습니다. URL을 확인해주세요.')
    }
  }
}
</script>

<style scoped>
.resume-container {
  min-height: 100vh;
  padding: 20px;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  overflow-x: hidden;
}

.resume-form-container {
  max-width: 1200px;
  width: 100%;
  margin: 0 auto;
  background: rgba(45, 45, 45, 0.95);
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.5);
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.resume-form-title {
  background: linear-gradient(135deg, #333333 0%, #555555 100%);
  color: white;
  margin: 0;
  padding: 30px;
  font-size: 2rem;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.loading-indicator {
  font-size: 1rem;
  font-weight: normal;
  opacity: 0.8;
}

.loading-indicator i {
  margin-right: 8px;
}

.resume-form {
  padding: 40px;
}

.form-section {
  margin-bottom: 40px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  padding: 25px;
  background: rgba(60, 60, 60, 0.3);
}

.section-title {
  color: #ffffff;
  font-size: 1.4rem;
  font-weight: 600;
  margin-bottom: 25px;
  display: flex;
  align-items: center;
  gap: 10px;
  border-bottom: 2px solid #888888;
  padding-bottom: 10px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 20px;
  width: 100%;
}

.form-group {
  margin-bottom: 20px;
}

.form-label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: #ffffff;
  font-size: 0.95rem;
}

.form-input, .form-textarea {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #555555;
  border-radius: 8px;
  font-size: 1rem;
  font-family: inherit;
  transition: all 0.3s ease;
  background: rgba(60, 60, 60, 0.8);
  color: #ffffff;
  box-sizing: border-box;
}

.form-input:focus, .form-textarea:focus {
  outline: none;
  border-color: #888888;
  box-shadow: 0 0 0 3px rgba(136, 136, 136, 0.2);
  background: rgba(70, 70, 70, 0.9);
}

.form-input::placeholder, .form-textarea::placeholder {
  color: #aaaaaa;
  opacity: 1;
}

.form-textarea {
  min-height: 100px;
  resize: vertical;
}

.input-with-preview {
  display: flex;
  gap: 20px;
  align-items: flex-start;
  flex-wrap: wrap;
}

.photo-preview {
  flex-shrink: 0;
}

.photo-preview img {
  width: 120px;
  height: 120px;
  object-fit: cover;
  border-radius: 12px;
  border: 3px solid #888888;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

.flag-ko {
  color: #1565c0;
  font-weight: bold;
}

.flag-ja {
  color: #f9a825;
  font-weight: bold;
}

.education-table-container {
  background: rgba(45, 45, 45, 0.8);
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.education-table {
  width: 100%;
  border-collapse: collapse;
  margin-bottom: 15px;
  table-layout: fixed;
}

.education-table th {
  background: linear-gradient(135deg, #333333 0%, #555555 100%);
  color: white;
  padding: 15px 10px;
  font-weight: 600;
  text-align: left;
  font-size: 0.9rem;
  word-wrap: break-word;
}

.education-table td {
  padding: 12px 10px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  word-wrap: break-word;
  overflow: hidden;
}

.education-table tr:nth-child(even) {
  background: rgba(70, 70, 70, 0.3);
}

.education-table tr:hover {
  background: rgba(100, 100, 100, 0.3);
}

.compact-input {
  padding: 8px 12px;
  font-size: 0.9rem;
}

.action-cell {
  text-align: center;
  width: 60px;
}

.remove-btn {
  background: #e74c3c;
  color: white;
  border: none;
  border-radius: 6px;
  padding: 8px 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.9rem;
}

.remove-btn:hover {
  background: #c0392b;
  transform: scale(1.05);
}

.add-btn {
  background: linear-gradient(145deg, #666666 0%, #444444 100%);
  color: white;
  border: none;
  border-radius: 8px;
  padding: 12px 20px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 600;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 auto;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

.add-btn:hover {
  background: linear-gradient(145deg, #777777 0%, #555555 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.4);
}

.form-buttons {
  display: flex;
  gap: 15px;
  justify-content: center;
  margin-top: 40px;
  padding-top: 30px;
  border-top: 2px solid rgba(255, 255, 255, 0.1);
}

.btn-submit, .btn-cancel {
  padding: 15px 30px;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  text-decoration: none;
  text-align: center;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
  min-width: 120px;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

.btn-submit {
  background: linear-gradient(145deg, #666666 0%, #444444 100%);
  color: white;
}

.btn-submit:hover {
  background: linear-gradient(145deg, #777777 0%, #555555 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.4);
}

.btn-cancel {
  background: linear-gradient(145deg, #555555 0%, #333333 100%);
  color: white;
}

.btn-cancel:hover {
  background: linear-gradient(145deg, #666666 0%, #444444 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.4);
}

.btn-submit:disabled,
.btn-cancel.disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none !important;
  box-shadow: none !important;
}

.btn-submit:disabled:hover,
.btn-cancel.disabled:hover {
  background: linear-gradient(145deg, #555555 0%, #333333 100%);
  transform: none;
  box-shadow: none;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .resume-container {
    padding: 10px;
  }
  
  .resume-form {
    padding: 20px;
  }
  
  .form-row {
    grid-template-columns: 1fr;
    gap: 15px;
  }
  
  .education-table {
    font-size: 0.8rem;
  }
  
  .education-table th,
  .education-table td {
    padding: 8px 6px;
  }
  
  .input-with-preview {
    flex-direction: column;
  }
  
  .photo-preview img {
    width: 100px;
    height: 100px;
  }
  
  .form-section {
    padding: 20px;
    margin-bottom: 30px;
  }
}

@media (max-width: 480px) {
  .resume-container {
    padding: 5px;
  }
  
  .resume-form-container {
    border-radius: 12px;
  }
  
  .resume-form-title {
    font-size: 1.5rem;
    padding: 20px;
  }
  
  .section-title {
    font-size: 1.2rem;
  }
  
  .form-buttons {
    flex-direction: column;
  }
  
  .btn-submit, .btn-cancel {
    width: 100%;
  }
  
  .form-section {
    padding: 15px;
    margin-bottom: 25px;
  }
  
  .education-table th,
  .education-table td {
    padding: 6px 4px;
    font-size: 0.75rem;
  }
  
  .compact-input {
    padding: 6px 8px;
    font-size: 0.8rem;
  }
}
</style> 
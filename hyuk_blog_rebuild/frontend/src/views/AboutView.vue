<template>
  <div class="resume-content container">
    <header class="resume-header">
      <img v-if="resume.photoUrl" :src="resume.photoUrl" alt="프로필 사진" class="profile-photo">
      <div>
        <h1>{{ lang === 'ja' ? resume.nameJa : resume.nameKo }}</h1>
        <p class="intro">{{ resume.introductionKo }}</p>
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
          <span v-if="showAddress === 'true' && getAddress()"> | </span>
          <span v-if="showAddress === 'true'">{{ getAddress() }}</span>
          <!-- 주소 마스킹 처리 -->
          <span v-if="showAddress === 'masked' && getAddress()"> | </span>
          <span v-if="showAddress === 'masked'">{{ maskAddress(getAddress()) }}</span>
        </p>
      </div>
    </header>

    <main>
      <section v-if="resume.educations && resume.educations.length > 0">
        <h2>{{ lang === 'ja' ? '학력' : '학력' }}</h2>
        <table class="custom-table">
          <thead>
            <tr>
              <th>{{ lang === 'ja' ? '학교명' : '학교명' }}</th>
              <th>{{ lang === 'ja' ? '전공/학위' : '전공/학위' }}</th>
              <th>{{ lang === 'ja' ? '기간' : '기간' }}</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="edu in resume.educations" :key="edu.id">
              <td>{{ lang === 'ja' ? edu.schoolJa : edu.schoolKo }}</td>
              <td>{{ lang === 'ja' ? edu.degreeJa : edu.degreeKo }}</td>
              <td>{{ edu.period }}</td>
            </tr>
          </tbody>
        </table>
      </section>

      <section v-if="resume.skills && resume.skills.length > 0">
        <h2>{{ lang === 'ja' ? '기술스택' : '기술스택' }}</h2>
        <div class="skills">
          <span class="skill-tag" v-for="skill in resume.skills" :key="skill">{{ skill }}</span>
        </div>
      </section>

      <section v-if="getStudentLife()">
        <h2>{{ lang === 'ja' ? '학생생활(성장과정)' : '학생생활(성장과정)' }}</h2>
        <p class="self-intro">{{ getStudentLife() }}</p>
      </section>

      <section v-if="getStrengthsWeaknesses()">
        <h2>{{ lang === 'ja' ? '장점과 단점' : '장점과 단점' }}</h2>
        <p class="self-intro">{{ getStrengthsWeaknesses() }}</p>
      </section>

      <section v-if="getEffortExperience()">
        <h2>{{ lang === 'ja' ? '인생에서 노력했던 경험' : '인생에서 노력했던 경험' }}</h2>
        <p class="self-intro">{{ getEffortExperience() }}</p>
      </section>

      <section v-if="getJapanItMotivation()">
        <h2>{{ lang === 'ja' ? '일본 IT 취업 지망동기' : '일본 IT 취업 지망동기' }}</h2>
        <p class="self-intro">{{ getJapanItMotivation() }}</p>
      </section>

      <section v-if="getFuturePlan()">
        <h2>{{ lang === 'ja' ? '장래의 계획 및 포부' : '장래의 계획 및 포부' }}</h2>
        <p class="self-intro">{{ getFuturePlan() }}</p>
      </section>
    </main>
  </div>
</template>

<script>
export default {
  name: 'AboutView',
  data() {
    return {
      lang: 'ko',
      showPhone: 'false',
      showAddress: 'false',
      resume: {
        nameKo: 'Takahara Yuuki',
        nameJa: 'タカハラ ユウキ',
        introductionKo: '웹 개발자로서 사용자 중심의 솔루션을 만드는 것을 목표로 합니다.',
        email: 'takahara@example.com',
        phone: '010-1234-5678',
        addressKo: '서울특별시 강남구',
        addressJa: '東京都渋谷区',
        photoUrl: null,
        educations: [
          {
            id: 1,
            schoolKo: '한국대학교',
            schoolJa: '韓国大学',
            degreeKo: '컴퓨터공학과 학사',
            degreeJa: 'コンピュータ工学学士',
            period: '2018-2022'
          }
        ],
        skills: ['JavaScript', 'Vue.js', 'React', 'Node.js', 'Python', 'Java', 'SQL'],
        studentLifeKo: '대학 시절 다양한 프로젝트를 통해 팀워크와 문제 해결 능력을 기를 수 있었습니다. 특히 웹 개발 동아리에서 프론트엔드와 백엔드 개발을 모두 경험하며 전체적인 시스템 이해도를 높였습니다.',
        studentLifeJa: '大学時代、様々なプロジェクトを通じてチームワークと問題解決能力を身につけることができました。特にウェブ開発サークルでフロントエンドとバックエンド開発の両方を経験し、システム全体の理解度を高めました。',
        strengthsWeaknessesKo: '장점은 새로운 기술을 빠르게 학습하고 적용하는 능력입니다. 단점은 때로 완벽주의적 성향으로 인해 일정을 초과하는 경우가 있다는 점입니다.',
        strengthsWeaknessesJa: '長所は新しい技術を素早く学習し適用する能力です。短所は時々完璧主義的な傾向によりスケジュールを超過することがある点です。',
        effortExperienceKo: '대학 3학년 때부터 매일 2시간씩 코딩 연습을 하며 실력을 향상시켰습니다. 온라인 강의와 개인 프로젝트를 병행하여 실무에 필요한 기술들을 습득했습니다.',
        effortExperienceJa: '大学3年生の時から毎日2時間ずつコーディング練習をして実力を向上させました。オンライン講座と個人プロジェクトを並行して実務に必要な技術を習得しました。',
        japanItMotivationKo: '일본의 IT 산업 발전과 기술 혁신에 매료되어 일본에서 일하고 싶습니다. 또한 일본의 워크 문화와 기술적 전문성을 배우고 싶습니다.',
        japanItMotivationJa: '日本のIT産業発展と技術革新に魅了され、日本で働きたいと思います。また、日本のワーク文化と技術的専門性を学びたいと思います。',
        futurePlanKo: '앞으로 5년 내에 풀스택 개발자로서 성장하고, 10년 내에는 기술 리더가 되어 팀을 이끌고 싶습니다. 또한 새로운 기술 트렌드를 지속적으로 학습하여 혁신적인 솔루션을 제공하고 싶습니다.',
        futurePlanJa: '今後5年以内にフルスタック開発者として成長し、10年以内には技術リーダーとしてチームを率いたいと思います。また、新しい技術トレンドを継続的に学習して革新的なソリューションを提供したいと思います。'
      }
    }
  },
  mounted() {
    this.loadResumeData();
    this.loadSettings();
  },
  methods: {
    async loadResumeData() {
      try {
        // 실제 백엔드 API 호출
        // const response = await fetch('/api/resume');
        // this.resume = await response.json();
        
        // 현재는 샘플 데이터 사용
        console.log('이력서 데이터 로드됨');
      } catch (error) {
        console.error('이력서 데이터 로드 실패:', error);
      }
    },
    async loadSettings() {
      try {
        // 실제 백엔드 설정 API 호출
        // const response = await fetch('/api/settings');
        // const settings = await response.json();
        // this.showPhone = settings.showPhone;
        // this.showAddress = settings.showAddress;
        
        // 현재는 기본값 사용
        this.showPhone = 'false';
        this.showAddress = 'false';
      } catch (error) {
        console.error('설정 로드 실패:', error);
      }
    },
    getAddress() {
      return this.lang === 'ja' ? this.resume.addressJa : this.resume.addressKo;
    },
    getStudentLife() {
      return this.lang === 'ja' ? this.resume.studentLifeJa : this.resume.studentLifeKo;
    },
    getStrengthsWeaknesses() {
      return this.lang === 'ja' ? this.resume.strengthsWeaknessesJa : this.resume.strengthsWeaknessesKo;
    },
    getEffortExperience() {
      return this.lang === 'ja' ? this.resume.effortExperienceJa : this.resume.effortExperienceKo;
    },
    getJapanItMotivation() {
      return this.lang === 'ja' ? this.resume.japanItMotivationJa : this.resume.japanItMotivationKo;
    },
    getFuturePlan() {
      return this.lang === 'ja' ? this.resume.futurePlanJa : this.resume.futurePlanKo;
    },
    maskPhone(phone) {
      if (!phone) return '';
      return phone.substring(0, 3) + '-****-' + phone.substring(phone.length - 4);
    },
    maskAddress(address) {
      if (!address) return '';
      return address.substring(0, 3) + '***';
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

/* 반응형 조정 */
@media (max-width: 768px) {
  .resume-content {
    margin: 20px auto;
    padding: 20px 16px;
  }
}
</style> 
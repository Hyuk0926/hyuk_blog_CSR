<template>
  <div class="container">
    <!-- 개인 프로젝트 섹션 -->
    <div class="project-section">
      <h2>{{ $t('projects.personal.title') }}</h2>
      <div class="project-list">
        <div class="project-card">
          <div class="project-header">
            <img class="project-logo" src="/img/blog_logo.png" alt="Hyuk Blog 로고" @error="hideImage">
            <div class="project-info">
              <div class="project-meta">{{ $t('projects.personal.blog.period') }}</div>
              <h3 class="project-title">{{ $t('projects.personal.blog.title') }}</h3>
              <div class="tech-stack">
                <img class="tech-icon" src="/svg/java.svg" alt="Java" title="Java">
                <img class="tech-icon" src="/svg/spring.svg" alt="Spring Boot" title="Spring Boot">
                <img class="tech-icon" src="/svg/mysql.svg" alt="MySQL" title="MySQL">
                <img class="tech-icon" src="/svg/AWS.svg" alt="AWS" title="AWS" />
              </div>
            </div>
          </div>
          <div class="project-description">
            <ul>
              <li>{{ $t('projects.personal.blog.desc.1') }}</li>
              <li>{{ $t('projects.personal.blog.desc.2') }}</li>
              <li>{{ $t('projects.personal.blog.desc.3') }}</li>
            </ul>
          </div>
          <div class="project-role">{{ $t('projects.personal.blog.role') }}</div>
          <div class="project-result">{{ $t('projects.personal.blog.result') }}</div>
          <div class="project-links">
            <a href="https://github.com/hyuk0926/hyuk_blog" target="_blank">{{ $t('projects.link.github') }}</a>
          </div>
          <!-- 시연 이미지 슬라이더 -->
          <div class="project-demo-slider">
            <div class="slider-track">
              <img src="/img/blog_demo1.png" alt="Hyuk Blog 시연1">
              <img src="/img/blog_demo2.png" alt="Hyuk Blog 시연2">
              <img src="/img/blog_demo3.png" alt="Hyuk Blog 시연3">
              <img src="/img/blog_demo4.png" alt="Hyuk Blog 시연4">
              <img src="/img/blog_demo5.png" alt="Hyuk Blog 시연5">
              <img src="/img/blog_demo6.png" alt="Hyuk Blog 시연6">
              <img src="/img/blog_demo1.png" alt="Hyuk Blog 시연1">
              <img src="/img/blog_demo2.png" alt="Hyuk Blog 시연2">
              <img src="/img/blog_demo3.png" alt="Hyuk Blog 시연3">
              <img src="/img/blog_demo4.png" alt="Hyuk Blog 시연4">
              <img src="/img/blog_demo5.png" alt="Hyuk Blog 시연5">
              <img src="/img/blog_demo6.png" alt="Hyuk Blog 시연6">
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 팀 프로젝트 섹션 -->
    <div class="project-section">
      <h2>{{ $t('projects.team.title') }}</h2>
      <div class="project-list">
        <div class="project-card">
          <div class="project-header">
            <img class="project-logo" src="/img/team_logo.png" alt="TeamCollab 로고" @error="hideImage">
            <div class="project-info">
              <div class="project-meta">{{ $t('projects.team.collab.period') }}</div>
              <h3 class="project-title">{{ $t('projects.team.collab.title') }}</h3>
              <div class="tech-stack">
                <img class="tech-icon" src="/svg/react.svg" alt="React" title="React" />
                <img class="tech-icon" src="/svg/java.svg" alt="Java" title="Java">
                <img class="tech-icon" src="/svg/spring.svg" alt="Spring Boot" title="Spring Boot">
                <img class="tech-icon" src="/svg/mysql.svg" alt="MySQL" title="MySQL">
                <img class="tech-icon" src="/svg/AWS.svg" alt="AWS" title="AWS" />
              </div>
            </div>
          </div>
          <div class="project-description">
            <ul>
              <li>{{ $t('projects.team.collab.desc.1') }}</li>
              <li>{{ $t('projects.team.collab.desc.2') }}</li>
            </ul>
          </div>
          <div class="project-role">{{ $t('projects.team.collab.role') }}</div>
          <div class="project-result">{{ $t('projects.team.collab.result') }}</div>
          <div class="project-links">
            <a href="#" target="_blank">{{ $t('projects.link.github') }}</a>
          </div>
          <div class="team-info">
            {{ $t('projects.team.collab.info') }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ProjectView',
  methods: {
    hideImage(event) {
      event.target.style.display = 'none';
    }
  }
}
</script>

<style scoped>
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 32px 24px;
}

.project-section {
  margin-bottom: 64px;
}

.project-section h2 {
  font-size: 1.75rem;
  color: #2c3e50;
  font-weight: 700;
  margin-bottom: 32px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e0e0e0;
}

.project-list {
  display: flex;
  flex-direction: column;
  gap: 40px;
}

.project-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
  padding: 32px;
  border: 1px solid #edf2f7;
}

.project-header {
  display: flex;
  align-items: flex-start;
  gap: 24px;
  margin-bottom: 24px;
}

.project-logo {
  width: 56px;
  height: 56px;
  object-fit: contain;
}

.project-info {
  flex: 1;
}

.project-meta {
  font-size: 0.9rem;
  color: #718096;
  margin-bottom: 8px;
  font-weight: 500;
}

.project-title {
  font-size: 1.5rem;
  color: #2c3e50;
  font-weight: 600;
  margin-bottom: 16px;
}

.tech-stack {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
}

.tech-icon {
  width: 28px;
  height: 28px;
  transition: transform 0.2s ease;
}

.tech-icon:hover {
  transform: translateY(-2px);
}

.project-description {
  color: #4a5568;
  line-height: 1.6;
  margin-bottom: 24px;
}

.project-description ul {
  padding-left: 20px;
  margin-bottom: 16px;
}

.project-description li {
  margin-bottom: 8px;
}

.project-role, 
.project-result {
  font-size: 0.95rem;
  color: #4a5568;
  margin-bottom: 12px;
  line-height: 1.5;
}

.project-role b, 
.project-result b {
  color: #2c3e50;
  font-weight: 600;
}

.project-links {
  margin-top: 24px;
  display: flex;
  gap: 16px;
}

.project-links a {
  color: #3182ce;
  text-decoration: none;
  font-weight: 500;
  font-size: 0.95rem;
  transition: color 0.2s ease;
}

.project-links a:hover {
  color: #2c5282;
}

.project-demo-slider {
  width: 100%;
  max-width: 800px;
  height: 240px;
  overflow: hidden;
  margin: 32px auto 0;
  border-radius: 12px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}

.slider-track {
  display: flex;
  animation: slideLeft 40s linear infinite;
  gap: 20px;
  padding: 0 10px;
}

.slider-track:hover {
  animation-play-state: paused;
}

.slider-track img {
  width: 320px;
  height: 240px;
  object-fit: cover;
  border-radius: 8px;
  flex-shrink: 0;
  transition: transform 0.3s ease;
}

.slider-track img:hover {
  transform: scale(1.02);
}

@keyframes slideLeft {
  0% { transform: translateX(0); }
  100% { transform: translateX(calc(-340px * 6)); }
}

.team-info {
  font-size: 0.95rem;
  color: #718096;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #edf2f7;
}

.team-info b {
  color: #2c3e50;
  font-weight: 600;
}

/* 다크모드 지원 */
</style>

<style>
body.dark-mode .project-section h2 {
  color: #f7fafc !important;
  border-bottom-color: #4a5568 !important;
}

body.dark-mode .project-card {
  background: #2d3748 !important;
  border-color: #4a5568 !important;
}

body.dark-mode .project-title {
  color: #f7fafc !important;
}

body.dark-mode .project-meta {
  color: #a0aec0 !important;
}

body.dark-mode .project-description,
body.dark-mode .project-role,
body.dark-mode .project-result {
  color: #e2e8f0 !important;
}

body.dark-mode .project-role b,
body.dark-mode .project-result b,
body.dark-mode .team-info b {
  color: #f7fafc !important;
}

body.dark-mode .team-info {
  color: #a0aec0 !important;
  border-top-color: #4a5568 !important;
}

body.dark-mode .project-links a {
  color: #63b3ed !important;
}

body.dark-mode .project-links a:hover {
  color: #90cdf4 !important;
}
</style> 
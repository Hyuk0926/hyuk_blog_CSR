<template>
  <div class="projects-container">
    <h1>프로젝트</h1>
    <p>프로젝트 페이지입니다.</p>
  </div>
</template>

<script>
export default {
  name: 'ProjectsView',
  mounted() {
    // 배경 이미지 제거
    document.body.style.backgroundImage = 'none';
    document.body.style.backgroundSize = '';
    document.body.style.backgroundPosition = '';
    document.body.style.backgroundRepeat = '';
    document.body.style.backgroundAttachment = '';
  }
}
</script>

<style scoped>
.projects-container {
  padding: 2rem;
  text-align: center;
}
</style> 
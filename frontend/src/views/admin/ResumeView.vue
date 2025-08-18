<template>
  <div class="min-h-screen p-5 flex items-start justify-center overflow-x-hidden">
    <div class="max-w-6xl w-full mx-auto bg-[rgba(45,45,45,0.95)] rounded-2xl shadow-[0_10px_30px_rgba(0,0,0,0.5)] overflow-hidden border border-[rgba(255,255,255,0.1)]">
      <h1 class="bg-gradient-to-r from-[#333333] to-[#555555] text-white m-0 p-8 text-4xl font-semibold flex items-center justify-between">
        <div class="flex items-center gap-3">
          <i class="fas fa-user-tie"></i>
          이력서 관리
        </div>
        <span v-if="loading" class="text-base font-normal opacity-80">
          <i class="fas fa-spinner fa-spin mr-2"></i>
          로딩 중...
        </span>
      </h1>
      
      <form @submit.prevent="saveResume" class="p-10">
        <!-- 프로필 사진 -->
        <div class="mb-10 border border-[rgba(255,255,255,0.1)] rounded-xl p-6 bg-[rgba(60,60,60,0.3)]">
          <h3 class="text-white text-2xl font-semibold mb-6 flex items-center gap-3 border-b-2 border-[#888888] pb-3">
            <i class="fas fa-camera"></i>
            프로필 사진
          </h3>
          <div class="mb-5">
            <label class="block mb-2 font-semibold text-white text-sm">사진 URL</label>
            <div class="flex gap-5 items-start flex-wrap">
              <input 
                v-model="resume.photoUrl" 
                type="text" 
                class="flex-1 p-3 px-4 border-2 border-[#555555] rounded-lg text-base font-inherit transition-all duration-300 ease-in-out bg-[rgba(60,60,60,0.8)] text-white focus:outline-none focus:border-[#888888] focus:shadow-[0_0_0_3px_rgba(136,136,136,0.2)] focus:bg-[rgba(70,70,70,0.9)] placeholder-[#aaaaaa]"
                placeholder="이미지 URL을 입력하세요"
              >
              <div v-if="resume.photoUrl" class="flex-shrink-0">
                <img :src="resume.photoUrl" alt="프로필 사진" @error="handleImageError" class="w-30 h-30 object-cover rounded-xl border-3 border-[#888888] shadow-[0_4px_12px_rgba(0,0,0,0.3)]">
              </div>
            </div>
          </div>
        </div>

        <!-- 기본 정보 -->
        <div class="mb-10 border border-[rgba(255,255,255,0.1)] rounded-xl p-6 bg-[rgba(60,60,60,0.3)]">
          <h3 class="text-white text-2xl font-semibold mb-6 flex items-center gap-3 border-b-2 border-[#888888] pb-3">
            <i class="fas fa-info-circle"></i>
            기본 정보
          </h3>
          
          <div class="grid grid-cols-2 gap-5 mb-5 w-full">
            <div class="mb-5">
              <label class="block mb-2 font-semibold text-white text-sm">
                <span class="text-[#1565c0] font-bold">🇰🇷</span>
                이름 (한국어)
              </label>
              <input 
                v-model="resume.nameKo" 
                type="text" 
                class="w-full p-3 px-4 border-2 border-[#555555] rounded-lg text-base font-inherit transition-all duration-300 ease-in-out bg-[rgba(60,60,60,0.8)] text-white focus:outline-none focus:border-[#888888] focus:shadow-[0_0_0_3px_rgba(136,136,136,0.2)] focus:bg-[rgba(70,70,70,0.9)] placeholder-[#aaaaaa]"
                placeholder="예: 최은혁" 
                required
              >
            </div>
            <div class="mb-5">
              <label class="block mb-2 font-semibold text-white text-sm">
                <span class="text-[#f9a825] font-bold">🇯🇵</span>
                이름 (일본어)
              </label>
              <input 
                v-model="resume.nameJa" 
                type="text" 
                class="w-full p-3 px-4 border-2 border-[#555555] rounded-lg text-base font-inherit transition-all duration-300 ease-in-out bg-[rgba(60,60,60,0.8)] text-white focus:outline-none focus:border-[#888888] focus:shadow-[0_0_0_3px_rgba(136,136,136,0.2)] focus:bg-[rgba(70,70,70,0.9)] placeholder-[#aaaaaa]"
                placeholder="例: 崔恩爀(通称：高原優輝）" 
                required
              >
            </div>
          </div>

          <div class="grid grid-cols-2 gap-5 mb-5 w-full">
            <div class="mb-5">
              <label class="block mb-2 font-semibold text-white text-sm">
                <i class="fas fa-phone"></i>
                연락처
              </label>
              <input 
                v-model="resume.phone" 
                type="text" 
                class="w-full p-3 px-4 border-2 border-[#555555] rounded-lg text-base font-inherit transition-all duration-300 ease-in-out bg-[rgba(60,60,60,0.8)] text-white focus:outline-none focus:border-[#888888] focus:shadow-[0_0_0_3px_rgba(136,136,136,0.2)] focus:bg-[rgba(70,70,70,0.9)] placeholder-[#aaaaaa]"
                placeholder="010-1234-5678"
              >
            </div>
            <div class="mb-5">
              <label class="block mb-2 font-semibold text-white text-sm">
                <i class="fas fa-envelope"></i>
                이메일
              </label>
              <input 
                v-model="resume.email" 
                type="email" 
                class="w-full p-3 px-4 border-2 border-[#555555] rounded-lg text-base font-inherit transition-all duration-300 ease-in-out bg-[rgba(60,60,60,0.8)] text-white focus:outline-none focus:border-[#888888] focus:shadow-[0_0_0_3px_rgba(136,136,136,0.2)] focus:bg-[rgba(70,70,70,0.9)] placeholder-[#aaaaaa]"
                placeholder="example@email.com"
              >
            </div>
          </div>

          <div class="grid grid-cols-2 gap-5 mb-5 w-full">
            <div class="mb-5">
              <label class="block mb-2 font-semibold text-white text-sm">
                <i class="fas fa-birthday-cake"></i>
                생년월일
              </label>
              <input 
                v-model="resume.birth" 
                type="date" 
                class="w-full p-3 px-4 border-2 border-[#555555] rounded-lg text-base font-inherit transition-all duration-300 ease-in-out bg-[rgba(60,60,60,0.8)] text-white focus:outline-none focus:border-[#888888] focus:shadow-[0_0_0_3px_rgba(136,136,136,0.2)] focus:bg-[rgba(70,70,70,0.9)] placeholder-[#aaaaaa]"
              >
            </div>
          </div>

          <div class="grid grid-cols-2 gap-5 mb-5 w-full">
            <div class="mb-5">
              <label class="block mb-2 font-semibold text-white text-sm">
                <span class="text-[#1565c0] font-bold">🇰🇷</span>
                주소 (한국어)
              </label>
              <input 
                v-model="resume.addressKo" 
                type="text" 
                class="w-full p-3 px-4 border-2 border-[#555555] rounded-lg text-base font-inherit transition-all duration-300 ease-in-out bg-[rgba(60,60,60,0.8)] text-white focus:outline-none focus:border-[#888888] focus:shadow-[0_0_0_3px_rgba(136,136,136,0.2)] focus:bg-[rgba(70,70,70,0.9)] placeholder-[#aaaaaa]"
                placeholder="예: 대한민국 경기도 용인시 ..."
              >
            </div>
            <div class="mb-5">
              <label class="block mb-2 font-semibold text-white text-sm">
                <span class="text-[#f9a825] font-bold">🇯🇵</span>
                주소 (일본어)
              </label>
              <input 
                v-model="resume.addressJa" 
                type="text" 
                class="w-full p-3 px-4 border-2 border-[#555555] rounded-lg text-base font-inherit transition-all duration-300 ease-in-out bg-[rgba(60,60,60,0.8)] text-white focus:outline-none focus:border-[#888888] focus:shadow-[0_0_0_3px_rgba(136,136,136,0.2)] focus:bg-[rgba(70,70,70,0.9)] placeholder-[#aaaaaa]"
                placeholder="例: 韓民国 京畿道 ..."
              >
            </div>
          </div>
        </div>

        <!-- 학력 -->
        <div class="mb-10 border border-[rgba(255,255,255,0.1)] rounded-xl p-6 bg-[rgba(60,60,60,0.3)]">
          <h3 class="text-white text-2xl font-semibold mb-6 flex items-center gap-3 border-b-2 border-[#888888] pb-3">
            <i class="fas fa-graduation-cap"></i>
            학력
          </h3>
          
          <div class="bg-[rgba(45,45,45,0.8)] rounded-lg overflow-hidden shadow-[0_2px_8px_rgba(0,0,0,0.3)] border border-[rgba(255,255,255,0.1)]">
            <table class="w-full border-collapse mb-4 table-fixed">
              <thead>
                <tr>
                  <th class="bg-gradient-to-r from-[#333333] to-[#555555] text-white p-4 font-semibold text-left text-sm break-words text-[#1565c0] font-bold">학교명 (🇰🇷)</th>
                  <th class="bg-gradient-to-r from-[#333333] to-[#555555] text-white p-4 font-semibold text-left text-sm break-words text-[#f9a825] font-bold">학교명 (🇯🇵)</th>
                  <th class="bg-gradient-to-r from-[#333333] to-[#555555] text-white p-4 font-semibold text-left text-sm break-words text-[#1565c0] font-bold">전공 (🇰🇷)</th>
                  <th class="bg-gradient-to-r from-[#333333] to-[#555555] text-white p-4 font-semibold text-left text-sm break-words text-[#f9a825] font-bold">전공 (🇯🇵)</th>
                  <th class="bg-gradient-to-r from-[#333333] to-[#555555] text-white p-4 font-semibold text-left text-sm break-words">기간</th>
                  <th class="bg-gradient-to-r from-[#333333] to-[#555555] text-white p-4 font-semibold text-left text-sm break-words">삭제</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(education, index) in resume.educations" :key="index" :class="index % 2 === 0 ? 'bg-[rgba(70,70,70,0.3)]' : ''" class="hover:bg-[rgba(100,100,100,0.3)]">
                  <td class="p-3 border-b border-[rgba(255,255,255,0.1)] break-words overflow-hidden">
                    <input 
                      v-model="education.schoolKo" 
                      type="text" 
                      class="w-full p-2 px-3 border-2 border-[#555555] rounded-lg text-sm font-inherit transition-all duration-300 ease-in-out bg-[rgba(60,60,60,0.8)] text-white focus:outline-none focus:border-[#888888] focus:shadow-[0_0_0_3px_rgba(136,136,136,0.2)] focus:bg-[rgba(70,70,70,0.9)] placeholder-[#aaaaaa]"
                      placeholder="예: 상현고등학교"
                    >
                  </td>
                  <td class="p-3 border-b border-[rgba(255,255,255,0.1)] break-words overflow-hidden">
                    <input 
                      v-model="education.schoolJa" 
                      type="text" 
                      class="w-full p-2 px-3 border-2 border-[#555555] rounded-lg text-sm font-inherit transition-all duration-300 ease-in-out bg-[rgba(60,60,60,0.8)] text-white focus:outline-none focus:border-[#888888] focus:shadow-[0_0_0_3px_rgba(136,136,136,0.2)] focus:bg-[rgba(70,70,70,0.9)] placeholder-[#aaaaaa]"
                      placeholder="例: 上峴高校"
                    >
                  </td>
                  <td class="p-3 border-b border-[rgba(255,255,255,0.1)] break-words overflow-hidden">
                    <input 
                      v-model="education.degreeKo" 
                      type="text" 
                      class="w-full p-2 px-3 border-2 border-[#555555] rounded-lg text-sm font-inherit transition-all duration-300 ease-in-out bg-[rgba(60,60,60,0.8)] text-white focus:outline-none focus:border-[#888888] focus:shadow-[0_0_0_3px_rgba(136,136,136,0.2)] focus:bg-[rgba(70,70,70,0.9)] placeholder-[#aaaaaa]"
                      placeholder="예: 인문계"
                    >
                  </td>
                  <td class="p-3 border-b border-[rgba(255,255,255,0.1)] break-words overflow-hidden">
                    <input 
                      v-model="education.degreeJa" 
                      type="text" 
                      class="w-full p-2 px-3 border-2 border-[#555555] rounded-lg text-sm font-inherit transition-all duration-300 ease-in-out bg-[rgba(60,60,60,0.8)] text-white focus:outline-none focus:border-[#888888] focus:shadow-[0_0_0_3px_rgba(136,136,136,0.2)] focus:bg-[rgba(70,70,70,0.9)] placeholder-[#aaaaaa]"
                      placeholder="例: 人文系"
                    >
                  </td>
                  <td class="p-3 border-b border-[rgba(255,255,255,0.1)] break-words overflow-hidden">
                    <input 
                      v-model="education.period" 
                      type="text" 
                      class="w-full p-2 px-3 border-2 border-[#555555] rounded-lg text-sm font-inherit transition-all duration-300 ease-in-out bg-[rgba(60,60,60,0.8)] text-white focus:outline-none focus:border-[#888888] focus:shadow-[0_0_0_3px_rgba(136,136,136,0.2)] focus:bg-[rgba(70,70,70,0.9)] placeholder-[#aaaaaa]"
                      placeholder="2015.03-2018.02"
                    >
                  </td>
                  <td class="p-3 border-b border-[rgba(255,255,255,0.1)] break-words overflow-hidden text-center w-15">
                    <button 
                      type="button" 
                      class="bg-[#e74c3c] text-white border-none rounded-md p-2 px-3 cursor-pointer transition-all duration-300 ease-in-out text-sm hover:bg-[#c0392b] hover:scale-105"
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
              class="bg-gradient-to-r from-[#666666] to-[#444444] text-white border-none rounded-lg p-3 px-5 cursor-pointer text-base font-semibold transition-all duration-300 ease-in-out flex items-center gap-2 mx-auto shadow-[0_4px_12px_rgba(0,0,0,0.3)] hover:from-[#777777] hover:to-[#555555] hover:-translate-y-0.5 hover:shadow-[0_6px_20px_rgba(0,0,0,0.4)]"
              @click="addEducation"
            >
              <i class="fas fa-plus"></i>
              학력 추가
            </button>
          </div>
        </div>

        <!-- 기술 스택 -->
        <div class="mb-10 border border-[rgba(255,255,255,0.1)] rounded-xl p-6 bg-[rgba(60,60,60,0.3)]">
          <h3 class="text-white text-2xl font-semibold mb-6 flex items-center gap-3 border-b-2 border-[#888888] pb-3">
            <i class="fas fa-code"></i>
            기술 스택
          </h3>
          <div class="mb-5">
            <label class="block mb-2 font-semibold text-white text-sm">보유 기술</label>
            <input 
              v-model="resume.skills" 
              type="text" 
              class="w-full p-3 px-4 border-2 border-[#555555] rounded-lg text-base font-inherit transition-all duration-300 ease-in-out bg-[rgba(60,60,60,0.8)] text-white focus:outline-none focus:border-[#888888] focus:shadow-[0_0_0_3px_rgba(136,136,136,0.2)] focus:bg-[rgba(70,70,70,0.9)] placeholder-[#aaaaaa]"
              placeholder="Java, Spring, React, Vue.js, ..."
            >
          </div>
        </div>

        <!-- 자기소개 -->
        <div class="mb-10 border border-[rgba(255,255,255,0.1)] rounded-xl p-6 bg-[rgba(60,60,60,0.3)]">
          <h3 class="text-white text-2xl font-semibold mb-6 flex items-center gap-3 border-b-2 border-[#888888] pb-3">
            <i class="fas fa-user-edit"></i>
            자기소개
          </h3>
          
          <div class="mb-5">
            <label class="block mb-2 font-semibold text-white text-sm">
              <span class="text-[#1565c0] font-bold">🇰🇷</span>
              자기소개 (한국어)
            </label>
            <textarea 
              v-model="resume.introductionKo" 
              class="w-full p-3 px-4 border-2 border-[#555555] rounded-lg text-base font-inherit transition-all duration-300 ease-in-out bg-[rgba(60,60,60,0.8)] text-white focus:outline-none focus:border-[#888888] focus:shadow-[0_0_0_3px_rgba(136,136,136,0.2)] focus:bg-[rgba(70,70,70,0.9)] placeholder-[#aaaaaa] min-h-[100px] resize-y"
              placeholder="한국어 자기소개를 입력하세요"
              rows="4"
            ></textarea>
          </div>
          
          <div class="mb-5">
            <label class="block mb-2 font-semibold text-white text-sm">
              <span class="text-[#f9a825] font-bold">🇯🇵</span>
              自己紹介 (일본어)
            </label>
            <textarea 
              v-model="resume.introductionJa" 
              class="w-full p-3 px-4 border-2 border-[#555555] rounded-lg text-base font-inherit transition-all duration-300 ease-in-out bg-[rgba(60,60,60,0.8)] text-white focus:outline-none focus:border-[#888888] focus:shadow-[0_0_0_3px_rgba(136,136,136,0.2)] focus:bg-[rgba(70,70,70,0.9)] placeholder-[#aaaaaa] min-h-[100px] resize-y"
              placeholder="日本語 自己紹介を入力してください"
              rows="4"
            ></textarea>
          </div>
          
          <div class="mb-5">
            <label class="block mb-2 font-semibold text-white text-sm">
              <span class="text-[#1565c0] font-bold">🇰🇷</span>
              학생생활 (한국어)
            </label>
            <textarea 
              v-model="resume.studentLifeKo" 
              class="w-full p-3 px-4 border-2 border-[#555555] rounded-lg text-base font-inherit transition-all duration-300 ease-in-out bg-[rgba(60,60,60,0.8)] text-white focus:outline-none focus:border-[#888888] focus:shadow-[0_0_0_3px_rgba(136,136,136,0.2)] focus:bg-[rgba(70,70,70,0.9)] placeholder-[#aaaaaa] min-h-[100px] resize-y"
              placeholder="한국어 성장과정을 입력하세요"
              rows="4"
            ></textarea>
          </div>
          
          <div class="mb-5">
            <label class="block mb-2 font-semibold text-white text-sm">
              <span class="text-[#f9a825] font-bold">🇯🇵</span>
              学生生活 (일본어)
            </label>
            <textarea 
              v-model="resume.studentLifeJa" 
              class="w-full p-3 px-4 border-2 border-[#555555] rounded-lg text-base font-inherit transition-all duration-300 ease-in-out bg-[rgba(60,60,60,0.8)] text-white focus:outline-none focus:border-[#888888] focus:shadow-[0_0_0_3px_rgba(136,136,136,0.2)] focus:bg-[rgba(70,70,70,0.9)] placeholder-[#aaaaaa] min-h-[100px] resize-y"
              placeholder="日本語 学生生活を入力してください"
              rows="4"
            ></textarea>
          </div>
        </div>

        <!-- 장단점 -->
        <div class="mb-10 border border-[rgba(255,255,255,0.1)] rounded-xl p-6 bg-[rgba(60,60,60,0.3)]">
          <h3 class="text-white text-2xl font-semibold mb-6 flex items-center gap-3 border-b-2 border-[#888888] pb-3">
            <i class="fas fa-balance-scale"></i>
            장점과 단점
          </h3>
          
          <div class="mb-5">
            <label class="block mb-2 font-semibold text-white text-sm">
              <span class="text-[#1565c0] font-bold">🇰🇷</span>
              장점과 단점 (한국어)
            </label>
            <textarea 
              v-model="resume.strengthsWeaknessesKo" 
              class="w-full p-3 px-4 border-2 border-[#555555] rounded-lg text-base font-inherit transition-all duration-300 ease-in-out bg-[rgba(60,60,60,0.8)] text-white focus:outline-none focus:border-[#888888] focus:shadow-[0_0_0_3px_rgba(136,136,136,0.2)] focus:bg-[rgba(70,70,70,0.9)] placeholder-[#aaaaaa] min-h-[100px] resize-y"
              placeholder="한국어 장단점을 입력하세요"
              rows="4"
            ></textarea>
          </div>
          
          <div class="mb-5">
            <label class="block mb-2 font-semibold text-white text-sm">
              <span class="text-[#f9a825] font-bold">🇯🇵</span>
              長所と短所 (일본어)
            </label>
            <textarea 
              v-model="resume.strengthsWeaknessesJa" 
              class="w-full p-3 px-4 border-2 border-[#555555] rounded-lg text-base font-inherit transition-all duration-300 ease-in-out bg-[rgba(60,60,60,0.8)] text-white focus:outline-none focus:border-[#888888] focus:shadow-[0_0_0_3px_rgba(136,136,136,0.2)] focus:bg-[rgba(70,70,70,0.9)] placeholder-[#aaaaaa] min-h-[100px] resize-y"
              placeholder="日本語 長所と短所を入力してください"
              rows="4"
            ></textarea>
          </div>
        </div>

        <!-- 노력 경험 -->
        <div class="mb-10 border border-[rgba(255,255,255,0.1)] rounded-xl p-6 bg-[rgba(60,60,60,0.3)]">
          <h3 class="text-white text-2xl font-semibold mb-6 flex items-center gap-3 border-b-2 border-[#888888] pb-3">
            <i class="fas fa-trophy"></i>
            노력 경험
          </h3>
          
          <div class="mb-5">
            <label class="block mb-2 font-semibold text-white text-sm">
              <span class="text-[#1565c0] font-bold">🇰🇷</span>
              노력 경험 (한국어)
            </label>
            <textarea 
              v-model="resume.effortExperienceKo" 
              class="w-full p-3 px-4 border-2 border-[#555555] rounded-lg text-base font-inherit transition-all duration-300 ease-in-out bg-[rgba(60,60,60,0.8)] text-white focus:outline-none focus:border-[#888888] focus:shadow-[0_0_0_3px_rgba(136,136,136,0.2)] focus:bg-[rgba(70,70,70,0.9)] placeholder-[#aaaaaa] min-h-[100px] resize-y"
              placeholder="한국어 노력 경험을 입력하세요"
              rows="4"
            ></textarea>
          </div>
          
          <div class="mb-5">
            <label class="block mb-2 font-semibold text-white text-sm">
              <span class="text-[#f9a825] font-bold">🇯🇵</span>
              努力経験 (일본어)
            </label>
            <textarea 
              v-model="resume.effortExperienceJa" 
              class="w-full p-3 px-4 border-2 border-[#555555] rounded-lg text-base font-inherit transition-all duration-300 ease-in-out bg-[rgba(60,60,60,0.8)] text-white focus:outline-none focus:border-[#888888] focus:shadow-[0_0_0_3px_rgba(136,136,136,0.2)] focus:bg-[rgba(70,70,70,0.9)] placeholder-[#aaaaaa] min-h-[100px] resize-y"
              placeholder="日本語 努力経験を入力してください"
              rows="4"
            ></textarea>
          </div>
        </div>

        <!-- 일본 IT 취업 동기 -->
        <div class="mb-10 border border-[rgba(255,255,255,0.1)] rounded-xl p-6 bg-[rgba(60,60,60,0.3)]">
          <h3 class="text-white text-2xl font-semibold mb-6 flex items-center gap-3 border-b-2 border-[#888888] pb-3">
            <i class="fas fa-rocket"></i>
            일본 IT 취업 동기
          </h3>
          
          <div class="mb-5">
            <label class="block mb-2 font-semibold text-white text-sm">
              <span class="text-[#1565c0] font-bold">🇰🇷</span>
              일본 IT 취업 동기 (한국어)
            </label>
            <textarea 
              v-model="resume.japanItMotivationKo" 
              class="w-full p-3 px-4 border-2 border-[#555555] rounded-lg text-base font-inherit transition-all duration-300 ease-in-out bg-[rgba(60,60,60,0.8)] text-white focus:outline-none focus:border-[#888888] focus:shadow-[0_0_0_3px_rgba(136,136,136,0.2)] focus:bg-[rgba(70,70,70,0.9)] placeholder-[#aaaaaa] min-h-[100px] resize-y"
              placeholder="한국어 일본 IT 취업 동기를 입력하세요"
              rows="4"
            ></textarea>
          </div>
          
          <div class="mb-5">
            <label class="block mb-2 font-semibold text-white text-sm">
              <span class="text-[#f9a825] font-bold">🇯🇵</span>
              日本IT就職志望動機 (일본어)
            </label>
            <textarea 
              v-model="resume.japanItMotivationJa" 
              class="w-full p-3 px-4 border-2 border-[#555555] rounded-lg text-base font-inherit transition-all duration-300 ease-in-out bg-[rgba(60,60,60,0.8)] text-white focus:outline-none focus:border-[#888888] focus:shadow-[0_0_0_3px_rgba(136,136,136,0.2)] focus:bg-[rgba(70,70,70,0.9)] placeholder-[#aaaaaa] min-h-[100px] resize-y"
              placeholder="日本語 IT志望動機を入力してください"
              rows="4"
            ></textarea>
          </div>
        </div>

        <!-- 장래 계획 -->
        <div class="mb-10 border border-[rgba(255,255,255,0.1)] rounded-xl p-6 bg-[rgba(60,60,60,0.3)]">
          <h3 class="text-white text-2xl font-semibold mb-6 flex items-center gap-3 border-b-2 border-[#888888] pb-3">
            <i class="fas fa-chart-line"></i>
            장래 계획
          </h3>
          
          <div class="mb-5">
            <label class="block mb-2 font-semibold text-white text-sm">
              <span class="text-[#1565c0] font-bold">🇰🇷</span>
              장래 계획 (한국어)
            </label>
            <textarea 
              v-model="resume.futurePlanKo" 
              class="w-full p-3 px-4 border-2 border-[#555555] rounded-lg text-base font-inherit transition-all duration-300 ease-in-out bg-[rgba(60,60,60,0.8)] text-white focus:outline-none focus:border-[#888888] focus:shadow-[0_0_0_3px_rgba(136,136,136,0.2)] focus:bg-[rgba(70,70,70,0.9)] placeholder-[#aaaaaa] min-h-[100px] resize-y"
              placeholder="한국어 장래 계획을 입력하세요"
              rows="4"
            ></textarea>
          </div>
          
          <div class="mb-5">
            <label class="block mb-2 font-semibold text-white text-sm">
              <span class="text-[#f9a825] font-bold">🇯🇵</span>
              将来の計画 (일본어)
            </label>
            <textarea 
              v-model="resume.futurePlanJa" 
              class="w-full p-3 px-4 border-2 border-[#555555] rounded-lg text-base font-inherit transition-all duration-300 ease-in-out bg-[rgba(60,60,60,0.8)] text-white focus:outline-none focus:border-[#888888] focus:shadow-[0_0_0_3px_rgba(136,136,136,0.2)] focus:bg-[rgba(70,70,70,0.9)] placeholder-[#aaaaaa] min-h-[100px] resize-y"
              placeholder="日本語 将来の計画を入力してください"
              rows="4"
            ></textarea>
          </div>
        </div>

        <!-- 버튼 -->
        <div class="flex gap-4 justify-center mt-10 pt-8 border-t-2 border-[rgba(255,255,255,0.1)]">
          <button type="submit" class="p-4 px-8 border-none rounded-lg text-base font-semibold cursor-pointer text-decoration-none text-center flex items-center gap-2 transition-all duration-300 ease-in-out min-w-[120px] justify-center shadow-[0_4px_12px_rgba(0,0,0,0.3)] bg-gradient-to-r from-[#666666] to-[#444444] text-white hover:from-[#777777] hover:to-[#555555] hover:-translate-y-0.5 hover:shadow-[0_6px_20px_rgba(0,0,0,0.4)] disabled:opacity-60 disabled:cursor-not-allowed disabled:transform-none disabled:shadow-none" :disabled="saving">
            <i class="fas fa-save"></i>
            {{ saving ? '저장 중...' : '이력서 확인' }}
          </button>
          <router-link to="/admin/dashboard" class="p-4 px-8 border-none rounded-lg text-base font-semibold cursor-pointer text-decoration-none text-center flex items-center gap-2 transition-all duration-300 ease-in-out min-w-[120px] justify-center shadow-[0_4px_12px_rgba(0,0,0,0.3)] bg-gradient-to-r from-[#555555] to-[#333333] text-white hover:from-[#666666] hover:to-[#444444] hover:-translate-y-0.5 hover:shadow-[0_6px_20px_rgba(0,0,0,0.4)] disabled:opacity-60 disabled:cursor-not-allowed disabled:transform-none disabled:shadow-none" :class="{ 'opacity-60 cursor-not-allowed transform-none shadow-none': saving }">
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
          this.$router.push('/admin/dashboard');
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
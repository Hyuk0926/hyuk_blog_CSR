<template>
  <div class="p-5 flex items-start justify-center overflow-x-hidden bg-[#f8f9fa] min-h-screen">
    <div class="max-w-7xl w-full mx-auto bg-white rounded-xl shadow-[0_2px_10px_rgba(0,0,0,0.1)] overflow-hidden border border-[#e9ecef]">
      <h1 class="bg-[#495057] text-white m-0 p-6 px-8 text-2xl font-semibold flex items-center gap-4 border-b border-[#dee2e6]">
        <i class="fas fa-envelope-open-text"></i>
        문의 관리
      </h1>
      
      <div class="p-10">
        <!-- 통계 카드 -->
        <div class="grid grid-cols-1 md:grid-cols-3 gap-5 mb-8">
          <div class="bg-white border border-[#e9ecef] rounded-lg p-5 flex items-center gap-4 transition-all duration-300 ease-in-out shadow-[0_1px_3px_rgba(0,0,0,0.1)] hover:bg-[#f8f9fa] hover:-translate-y-0.5 hover:shadow-[0_4px_12px_rgba(0,0,0,0.15)]">
            <div class="w-12 h-12 bg-[#495057] rounded-lg flex items-center justify-center text-2xl text-white">
              <i class="fas fa-inbox"></i>
            </div>
            <div class="flex-1">
              <div class="text-4xl font-bold text-[#495057] mb-1">{{ stats.totalInquiries }}</div>
              <div class="text-[#6c757d] text-sm">전체 문의</div>
            </div>
          </div>
          <div class="bg-white border border-[#e9ecef] rounded-lg p-5 flex items-center gap-4 transition-all duration-300 ease-in-out shadow-[0_1px_3px_rgba(0,0,0,0.1)] hover:bg-[#f8f9fa] hover:-translate-y-0.5 hover:shadow-[0_4px_12px_rgba(0,0,0,0.15)]">
            <div class="w-12 h-12 bg-[#495057] rounded-lg flex items-center justify-center text-2xl text-white">
              <i class="fas fa-clock"></i>
            </div>
            <div class="flex-1">
              <div class="text-4xl font-bold text-[#495057] mb-1">{{ stats.newInquiries }}</div>
              <div class="text-[#6c757d] text-sm">새 문의</div>
            </div>
          </div>
          <div class="bg-white border border-[#e9ecef] rounded-lg p-5 flex items-center gap-4 transition-all duration-300 ease-in-out shadow-[0_1px_3px_rgba(0,0,0,0.1)] hover:bg-[#f8f9fa] hover:-translate-y-0.5 hover:shadow-[0_4px_12px_rgba(0,0,0,0.15)]">
            <div class="w-12 h-12 bg-[#495057] rounded-lg flex items-center justify-center text-2xl text-white">
              <i class="fas fa-check-circle"></i>
            </div>
            <div class="flex-1">
              <div class="text-4xl font-bold text-[#495057] mb-1">{{ stats.repliedInquiries }}</div>
              <div class="text-[#6c757d] text-sm">답변 완료</div>
            </div>
          </div>
        </div>

        <!-- 필터 및 검색 -->
        <div class="flex justify-between items-center mb-8 gap-5 flex-wrap">
          <div class="relative flex-1 max-w-md">
            <i class="fas fa-search absolute left-4 top-1/2 transform -translate-y-1/2 text-[#aaaaaa]"></i>
            <input 
              v-model="searchQuery" 
              type="text" 
              placeholder="이름, 이메일, 제목으로 검색..."
              class="w-full p-3 px-4 pl-12 border border-[#dee2e6] rounded-md bg-white text-[#495057] text-base transition-all duration-300 ease-in-out focus:outline-none focus:border-[#495057] focus:bg-white focus:shadow-[0_0_0_3px_rgba(73,80,87,0.1)] placeholder-[#6c757d]"
            >
          </div>
          <div class="flex gap-2">
            <button 
              @click="setFilter('all')" 
              class="px-5 py-2.5 border border-[#dee2e6] rounded-md bg-white text-[#495057] cursor-pointer transition-all duration-300 ease-in-out text-sm hover:bg-[#f8f9fa] hover:border-[#495057]"
              :class="{ 'bg-[#495057] border-[#495057] text-gray': currentFilter === 'all' }"
            >
              전체
            </button>
            <button
              @click="setFilter('new')" 
              class="px-5 py-2.5 border border-[#dee2e6] rounded-md bg-white text-[#495057] cursor-pointer transition-all duration-300 ease-in-out text-sm hover:bg-[#f8f9fa] hover:border-[#495057]"
              :class="{ 'bg-[#495057] border-[#495057] text-gray': currentFilter === 'new' }"
            >
              새 문의
            </button>
            <button 
              @click="setFilter('replied')" 
              class="px-5 py-2.5 border border-[#dee2e6] rounded-md bg-white text-[#495057] cursor-pointer transition-all duration-300 ease-in-out text-sm hover:bg-[#f8f9fa] hover:border-[#495057]"
              :class="{ 'bg-[#495057] border-[#495057] text-gray': currentFilter === 'replied' }"
            >
              답변 완료
            </button>
          </div>
        </div>

        <!-- 문의 목록 -->
        <div class="bg-white rounded-lg overflow-hidden border border-[#e9ecef] shadow-[0_1px_3px_rgba(0,0,0,0.1)]">
          <div v-if="filteredInquiries.length === 0" class="text-center py-16 px-5 text-[#6c757d]">
            <i class="fas fa-inbox text-6xl mb-5 opacity-50 text-[#dee2e6]"></i>
            <h3 class="mb-2.5 text-[#495057]">문의가 없습니다</h3>
            <p>새로운 문의가 들어오면 여기에 표시됩니다.</p>
          </div>
          
          <table v-else class="w-full border-collapse text-sm">
            <thead>
              <tr>
                <th class="bg-[#495057] text-white p-4 font-semibold text-left border-b border-[#dee2e6]">ID</th>
                <th class="bg-[#495057] text-white p-4 font-semibold text-left border-b border-[#dee2e6]">이름</th>
                <th class="bg-[#495057] text-white p-4 font-semibold text-left border-b border-[#dee2e6]">이메일</th>
                <th class="bg-[#495057] text-white p-4 font-semibold text-left border-b border-[#dee2e6]">제목</th>
                <th class="bg-[#495057] text-white p-4 font-semibold text-left border-b border-[#dee2e6]">내용</th>
                <th class="bg-[#495057] text-white p-4 font-semibold text-left border-b border-[#dee2e6]">날짜</th>
                <th class="bg-[#495057] text-white p-4 font-semibold text-left border-b border-[#dee2e6]">상태</th>
                <th class="bg-[#495057] text-white p-4 font-semibold text-left border-b border-[#dee2e6]">작업</th>
              </tr>
            </thead>
            <tbody>
              <tr 
                v-for="inquiry in filteredInquiries" 
                :key="inquiry.id"
                :class="!(inquiry.replied || false) ? 'bg-[rgba(255,193,7,0.1)] hover:bg-[rgba(255,193,7,0.2)]' : 'hover:bg-[#f8f9fa]'"
                class="transition-all duration-300 ease-in-out cursor-pointer"
                @click="viewInquiry(inquiry)"
              >
                <td class="p-3 border-b border-[#e9ecef] text-[#495057]">{{ inquiry.id }}</td>
                <td class="p-3 border-b border-[#e9ecef] text-[#495057]">{{ inquiry.name }}</td>
                <td class="p-3 border-b border-[#e9ecef] text-[#495057]">{{ inquiry.email }}</td>
                <td class="p-3 border-b border-[#e9ecef] text-[#495057] max-w-[200px] overflow-hidden text-ellipsis whitespace-nowrap">{{ inquiry.subject }}</td>
                <td class="p-3 border-b border-[#e9ecef] text-[#495057] max-w-[250px] overflow-hidden text-ellipsis whitespace-nowrap">{{ truncateText(inquiry.message, 50) }}</td>
                <td class="p-3 border-b border-[#e9ecef] text-[#495057]">{{ formatDate(inquiry.createdAt) }}</td>
                <td class="p-3 border-b border-[#e9ecef] text-[#495057]">
                  <span :class="(inquiry.replied || false) ? 'bg-[#d4edda] text-[#155724] border border-[#c3e6cb]' : 'bg-[#fff3cd] text-[#856404] border border-[#ffeaa7]'" class="px-2 py-1 rounded-md text-xs font-semibold">
                    {{ (inquiry.replied || false) ? '답변완료' : '새문의' }}
                  </span>
                </td>
                <td class="p-3 border-b border-[#e9ecef] text-[#495057]">
                  <div class="flex gap-2 justify-center">
                    <button 
                      @click.stop="viewInquiry(inquiry)" 
                      class="w-8 h-8 border-none rounded-md cursor-pointer transition-all duration-300 ease-in-out flex items-center justify-center text-sm bg-[#e3f2fd] text-[#1976d2] hover:bg-[#bbdefb] hover:scale-105"
                      title="상세보기"
                    >
                      <i class="fas fa-eye"></i>
                    </button>
                    <button 
                      @click.stop="showReplyModal(inquiry)" 
                      class="w-8 h-8 border-none rounded-md cursor-pointer transition-all duration-300 ease-in-out flex items-center justify-center text-sm bg-[#e8f5e8] text-[#2e7d32] hover:bg-[#c8e6c9] hover:scale-105"
                      title="답변하기"
                    >
                      <i class="fas fa-reply"></i>
                    </button>
                    <button 
                      @click.stop="deleteInquiry(inquiry.id)" 
                      class="w-8 h-8 border-none rounded-md cursor-pointer transition-all duration-300 ease-in-out flex items-center justify-center text-sm bg-[#ffebee] text-[#c62828] hover:bg-[#ffcdd2] hover:scale-105"
                      title="삭제"
                    >
                      <i class="fas fa-trash"></i>
                    </button>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- 문의 상세 모달 -->
    <div v-if="selectedInquiry" class="fixed inset-0 bg-[rgba(0,0,0,0.5)] flex items-center justify-center z-[1000] p-5" @click="closeModal">
      <div class="bg-white rounded-xl max-w-2xl w-full max-h-[80vh] overflow-hidden border border-[#e9ecef] shadow-[0_10px_30px_rgba(0,0,0,0.3)]" @click.stop>
        <div class="bg-[#495057] text-white p-5 flex justify-between items-center border-b border-[#dee2e6]">
          <h3 class="m-0">문의 상세보기</h3>
          <button @click="closeModal" class="bg-none border-none text-white text-xl cursor-pointer p-1 rounded transition-all duration-300 ease-in-out hover:bg-[rgba(255,255,255,0.1)]">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="p-5 max-h-[400px] overflow-y-auto">
          <div class="text-[#495057]">
            <div class="mb-4 flex gap-2.5">
              <label class="font-semibold min-w-[80px] text-[#6c757d]">ID:</label>
              <span>{{ selectedInquiry.id }}</span>
            </div>
            <div class="mb-4 flex gap-2.5">
              <label class="font-semibold min-w-[80px] text-[#6c757d]">이름:</label>
              <span>{{ selectedInquiry.name }}</span>
            </div>
            <div class="mb-4 flex gap-2.5">
              <label class="font-semibold min-w-[80px] text-[#6c757d]">이메일:</label>
              <span>{{ selectedInquiry.email }}</span>
            </div>
            <div class="mb-4 flex gap-2.5">
              <label class="font-semibold min-w-[80px] text-[#6c757d]">제목:</label>
              <span>{{ selectedInquiry.subject }}</span>
            </div>
            <div class="mb-4 flex gap-2.5">
              <label class="font-semibold min-w-[80px] text-[#6c757d]">내용:</label>
              <div class="bg-[#f8f9fa] p-4 rounded-lg mt-2.5 whitespace-pre-wrap leading-relaxed border border-[#e9ecef] flex-1">{{ selectedInquiry.message }}</div>
            </div>
            <div class="mb-4 flex gap-2.5">
              <label class="font-semibold min-w-[80px] text-[#6c757d]">날짜:</label>
              <span>{{ formatDate(selectedInquiry.createdAt) }}</span>
            </div>
            
            <!-- 답변 정보 표시 -->
            <div v-if="selectedInquiry.replied" class="mt-5 pt-5 border-t border-[#e9ecef]">
              <div class="mb-4 flex gap-2.5">
                <label class="font-semibold min-w-[80px] text-[#6c757d]">답변:</label>
                <div class="bg-[#e8f5e8] p-4 rounded-lg mt-2.5 whitespace-pre-wrap leading-relaxed border border-[#c3e6cb] text-[#155724] flex-1">{{ selectedInquiry.replyMessage }}</div>
              </div>
              <div class="mb-4 flex gap-2.5">
                <label class="font-semibold min-w-[80px] text-[#6c757d]">답변자:</label>
                <span>{{ selectedInquiry.repliedBy }}</span>
              </div>
              <div class="mb-4 flex gap-2.5">
                <label class="font-semibold min-w-[80px] text-[#6c757d]">답변일:</label>
                <span>{{ formatDate(selectedInquiry.repliedAt) }}</span>
              </div>
            </div>
          </div>
        </div>
        <div class="p-5 flex gap-2.5 justify-end border-t border-[#e9ecef]">
          <button v-if="!selectedInquiry.replied" @click="showReplyModal(selectedInquiry)" class="px-5 py-2.5 border-none rounded-lg cursor-pointer text-sm flex items-center gap-2 transition-all duration-300 ease-in-out bg-[#28a745] text-white hover:bg-[#218838] hover:-translate-y-0.5 hover:shadow-[0_4px_12px_rgba(40,167,69,0.3)]">
            <i class="fas fa-reply"></i>
            답변하기
          </button>
          <button @click="closeModal" class="px-5 py-2.5 border-none rounded-lg cursor-pointer text-sm flex items-center gap-2 transition-all duration-300 ease-in-out bg-[#6c757d] text-white hover:bg-[#5a6268]">
            <i class="fas fa-times"></i>
            닫기
          </button>
        </div>
      </div>
    </div>

    <!-- 답변 작성 모달 -->
    <div v-if="showReplyForm" class="fixed inset-0 bg-[rgba(0,0,0,0.5)] flex items-center justify-center z-[1000] p-5" @click="closeReplyModal">
      <div class="bg-white rounded-xl max-w-3xl w-full max-h-[80vh] overflow-hidden border border-[#e9ecef] shadow-[0_10px_30px_rgba(0,0,0,0.3)]" @click.stop>
        <div class="bg-[#495057] text-white p-5 flex justify-between items-center border-b border-[#dee2e6]">
          <h3 class="m-0">답변 작성</h3>
          <button @click="closeReplyModal" class="bg-none border-none text-white text-xl cursor-pointer p-1 rounded transition-all duration-300 ease-in-out hover:bg-[rgba(255,255,255,0.1)]">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="p-5">
          <div class="text-[#495057]">
            <div class="mb-5">
              <label class="block font-semibold mb-2 text-[#495057]">문의 내용:</label>
              <div class="bg-[#f8f9fa] p-4 rounded-lg border border-[#e9ecef] leading-relaxed text-[#495057]">
                <strong>{{ replyTarget.name }}</strong>님의 문의<br>
                <strong>제목:</strong> {{ replyTarget.subject }}<br>
                <strong>내용:</strong> {{ replyTarget.message }}
              </div>
            </div>
            <div class="mb-5">
              <label for="replyMessage" class="block font-semibold mb-2 text-[#495057]">답변 내용:</label>
              <textarea 
                id="replyMessage"
                v-model="replyForm.replyMessage" 
                placeholder="답변 내용을 입력하세요..."
                rows="8"
                class="w-full p-3 border border-[#dee2e6] rounded-md bg-white text-[#495057] text-base font-inherit resize-y transition-all duration-300 ease-in-out focus:outline-none focus:border-[#495057] focus:shadow-[0_0_0_3px_rgba(73,80,87,0.1)] placeholder-[#6c757d]"
              ></textarea>
            </div>
          </div>
        </div>
        <div class="p-5 flex gap-2.5 justify-end border-t border-[#e9ecef]">
          <button @click="submitReply" class="px-5 py-2.5 border-none rounded-lg cursor-pointer text-sm flex items-center gap-2 transition-all duration-300 ease-in-out bg-[#28a745] text-white hover:bg-[#218838] hover:-translate-y-0.5 hover:shadow-[0_4px_12px_rgba(40,167,69,0.3)] disabled:opacity-50 disabled:cursor-not-allowed" :disabled="!replyForm.replyMessage.trim()">
            <i class="fas fa-paper-plane"></i>
            답변 전송
          </button>
          <button @click="closeReplyModal" class="px-5 py-2.5 border-none rounded-lg cursor-pointer text-sm flex items-center gap-2 transition-all duration-300 ease-in-out bg-[#6c757d] text-white hover:bg-[#5a6268]">
            <i class="fas fa-times"></i>
            취소
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import apiService from '@/services/api.js'

export default {
  name: 'InquiryView',
  data() {
    return {
      searchQuery: '',
      currentFilter: 'all',
      selectedInquiry: null,
      stats: {
        totalInquiries: 0,
        newInquiries: 0,
        repliedInquiries: 0
      },
      inquiries: [],
      showReplyForm: false,
      replyTarget: {},
      replyForm: {
        replyMessage: ''
      }
    }
  },
  computed: {
    filteredInquiries() {
      let filtered = this.inquiries

      // 필터 적용
      if (this.currentFilter === 'new') {
        filtered = filtered.filter(inquiry => !(inquiry.replied || false))
      } else if (this.currentFilter === 'replied') {
        filtered = filtered.filter(inquiry => inquiry.replied || false)
      }

      // 검색 적용
      if (this.searchQuery) {
        const query = this.searchQuery.toLowerCase()
        filtered = filtered.filter(inquiry => 
          inquiry.name.toLowerCase().includes(query) ||
          inquiry.email.toLowerCase().includes(query) ||
          inquiry.subject.toLowerCase().includes(query) ||
          inquiry.message.toLowerCase().includes(query)
        )
      }

      return filtered
    }
  },
  mounted() {
    this.loadInquiries()
  },
  methods: {
    async loadInquiries() {
      try {
        console.log('문의 데이터 로드 시작...')
        const response = await apiService.getAdminInquiries()
        
        if (response && response.success) {
          this.inquiries = response.data || []
          console.log('문의 데이터 로드 성공:', this.inquiries.length, '개')
        } else {
          console.warn('문의 데이터 응답이 올바르지 않습니다:', response)
          this.inquiries = []
        }
        
        this.updateStats()
      } catch (error) {
        console.error('문의 로드 실패:', error)
        // 오프라인 모드나 API 오류 시 빈 배열로 설정
        this.inquiries = []
        this.updateStats()
      }
    },
    
    updateStats() {
      this.stats.totalInquiries = this.inquiries.length
      this.stats.newInquiries = this.inquiries.filter(inquiry => !(inquiry.replied || false)).length
      this.stats.repliedInquiries = this.inquiries.filter(inquiry => inquiry.replied || false).length
    },
    
    setFilter(filter) {
      this.currentFilter = filter
    },
    
    viewInquiry(inquiry) {
      this.selectedInquiry = inquiry
    },
    
    closeModal() {
      this.selectedInquiry = null
    },
    
    showReplyModal(inquiry) {
      this.replyTarget = inquiry
      this.replyForm.replyMessage = ''
      this.showReplyForm = true
    },
    
    closeReplyModal() {
      this.showReplyForm = false
      this.replyTarget = {}
      this.replyForm.replyMessage = ''
    },
    
    async submitReply() {
      if (!this.replyForm.replyMessage.trim()) {
        alert('답변 내용을 입력해주세요.')
        return
      }
      
      try {
        const response = await apiService.replyToInquiry(this.replyTarget.id, {
          replyMessage: this.replyForm.replyMessage,
          repliedBy: '관리자' // TODO: 실제 로그인한 관리자 정보 사용
        })
        
        if (response && response.success) {
          // 문의 목록 새로고침
          await this.loadInquiries()
          
          // 모달 닫기
          this.closeReplyModal()
          this.closeModal()
          
          alert('답변이 성공적으로 등록되었습니다.')
        } else {
          alert('답변 등록에 실패했습니다.')
        }
      } catch (error) {
        console.error('답변 등록 실패:', error)
        alert('답변 등록에 실패했습니다.')
      }
    },
    
    async deleteInquiry(id) {
      if (confirm('정말로 이 문의를 삭제하시겠습니까?')) {
        try {
          const response = await apiService.deleteInquiry(id)
          
          if (response && response.success) {
            this.inquiries = this.inquiries.filter(inquiry => inquiry.id !== id)
            this.updateStats()
            alert('문의가 삭제되었습니다.')
          } else {
            alert('삭제에 실패했습니다.')
          }
        } catch (error) {
          console.error('문의 삭제 실패:', error)
          alert('삭제에 실패했습니다.')
        }
      }
    },
    
    truncateText(text, maxLength) {
      if (text.length <= maxLength) return text
      return text.substring(0, maxLength) + '...'
    },
    
    formatDate(dateString) {
      const date = new Date(dateString)
      return date.toLocaleDateString('ko-KR', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    }
  }
}
</script> 
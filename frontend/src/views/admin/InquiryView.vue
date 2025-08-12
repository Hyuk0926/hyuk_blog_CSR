<template>
  <div class="inquiry-container">
    <div class="inquiry-form-container">
      <h1 class="inquiry-form-title">
        <i class="fas fa-envelope-open-text"></i>
        문의 관리
      </h1>
      
      <div class="inquiry-content">
        <!-- 통계 카드 -->
        <div class="stats-section">
          <div class="stat-card">
            <div class="stat-icon">
              <i class="fas fa-inbox"></i>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.totalInquiries }}</div>
              <div class="stat-label">전체 문의</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon">
              <i class="fas fa-clock"></i>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.newInquiries }}</div>
              <div class="stat-label">새 문의</div>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon">
              <i class="fas fa-check-circle"></i>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ stats.repliedInquiries }}</div>
              <div class="stat-label">답변 완료</div>
            </div>
          </div>
        </div>

        <!-- 필터 및 검색 -->
        <div class="filter-section">
          <div class="search-box">
            <i class="fas fa-search"></i>
            <input 
              v-model="searchQuery" 
              type="text" 
              placeholder="이름, 이메일, 제목으로 검색..."
              class="search-input"
            >
          </div>
          <div class="filter-buttons">
            <button 
              @click="setFilter('all')" 
              :class="['filter-btn', { active: currentFilter === 'all' }]"
            >
              전체
            </button>
            <button 
              @click="setFilter('new')" 
              :class="['filter-btn', { active: currentFilter === 'new' }]"
            >
              새 문의
            </button>
            <button 
              @click="setFilter('replied')" 
              :class="['filter-btn', { active: currentFilter === 'replied' }]"
            >
              답변 완료
            </button>
          </div>
        </div>

        <!-- 문의 목록 -->
        <div class="inquiry-table-container">
          <div v-if="filteredInquiries.length === 0" class="no-inquiry">
            <i class="fas fa-inbox"></i>
            <h3>문의가 없습니다</h3>
            <p>새로운 문의가 들어오면 여기에 표시됩니다.</p>
          </div>
          
          <table v-else class="inquiry-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>이름</th>
                <th>이메일</th>
                <th>제목</th>
                <th>내용</th>
                <th>날짜</th>
                <th>상태</th>
                <th>작업</th>
              </tr>
            </thead>
            <tbody>
              <tr 
                v-for="inquiry in filteredInquiries" 
                :key="inquiry.id"
                :class="{ 'new-inquiry': !(inquiry.replied || false) }"
                @click="viewInquiry(inquiry)"
              >
                <td>{{ inquiry.id }}</td>
                <td>{{ inquiry.name }}</td>
                <td>{{ inquiry.email }}</td>
                <td class="subject-cell">{{ inquiry.subject }}</td>
                <td class="message-cell">{{ truncateText(inquiry.message, 50) }}</td>
                <td>{{ formatDate(inquiry.createdAt) }}</td>
                <td>
                  <span :class="['status-badge', (inquiry.replied || false) ? 'status-replied' : 'status-new']">
                    {{ (inquiry.replied || false) ? '답변완료' : '새문의' }}
                  </span>
                </td>
                <td class="action-cell">
                  <button 
                    @click.stop="viewInquiry(inquiry)" 
                    class="action-btn view-btn"
                    title="상세보기"
                  >
                    <i class="fas fa-eye"></i>
                  </button>
                  <button 
                    @click.stop="showReplyModal(inquiry)" 
                    class="action-btn reply-btn"
                    title="답변하기"
                  >
                    <i class="fas fa-reply"></i>
                  </button>
                  <button 
                    @click.stop="deleteInquiry(inquiry.id)" 
                    class="action-btn delete-btn"
                    title="삭제"
                  >
                    <i class="fas fa-trash"></i>
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- 문의 상세 모달 -->
    <div v-if="selectedInquiry" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>문의 상세보기</h3>
          <button @click="closeModal" class="close-btn">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <div class="inquiry-detail">
            <div class="detail-row">
              <label>ID:</label>
              <span>{{ selectedInquiry.id }}</span>
            </div>
            <div class="detail-row">
              <label>이름:</label>
              <span>{{ selectedInquiry.name }}</span>
            </div>
            <div class="detail-row">
              <label>이메일:</label>
              <span>{{ selectedInquiry.email }}</span>
            </div>
            <div class="detail-row">
              <label>제목:</label>
              <span>{{ selectedInquiry.subject }}</span>
            </div>
            <div class="detail-row">
              <label>내용:</label>
              <div class="message-content">{{ selectedInquiry.message }}</div>
            </div>
            <div class="detail-row">
              <label>날짜:</label>
              <span>{{ formatDate(selectedInquiry.createdAt) }}</span>
            </div>
            
            <!-- 답변 정보 표시 -->
            <div v-if="selectedInquiry.replied" class="reply-section">
              <div class="detail-row">
                <label>답변:</label>
                <div class="reply-content">{{ selectedInquiry.replyMessage }}</div>
              </div>
              <div class="detail-row">
                <label>답변자:</label>
                <span>{{ selectedInquiry.repliedBy }}</span>
              </div>
              <div class="detail-row">
                <label>답변일:</label>
                <span>{{ formatDate(selectedInquiry.repliedAt) }}</span>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button v-if="!selectedInquiry.replied" @click="showReplyModal(selectedInquiry)" class="btn-reply">
            <i class="fas fa-reply"></i>
            답변하기
          </button>
          <button @click="closeModal" class="btn-cancel">
            <i class="fas fa-times"></i>
            닫기
          </button>
        </div>
      </div>
    </div>

    <!-- 답변 작성 모달 -->
    <div v-if="showReplyForm" class="modal-overlay" @click="closeReplyModal">
      <div class="modal-content reply-modal" @click.stop>
        <div class="modal-header">
          <h3>답변 작성</h3>
          <button @click="closeReplyModal" class="close-btn">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <div class="reply-form">
            <div class="form-group">
              <label>문의 내용:</label>
              <div class="inquiry-summary">
                <strong>{{ replyTarget.name }}</strong>님의 문의<br>
                <strong>제목:</strong> {{ replyTarget.subject }}<br>
                <strong>내용:</strong> {{ replyTarget.message }}
              </div>
            </div>
            <div class="form-group">
              <label for="replyMessage">답변 내용:</label>
              <textarea 
                id="replyMessage"
                v-model="replyForm.replyMessage" 
                placeholder="답변 내용을 입력하세요..."
                rows="8"
                class="reply-textarea"
              ></textarea>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="submitReply" class="btn-reply" :disabled="!replyForm.replyMessage.trim()">
            <i class="fas fa-paper-plane"></i>
            답변 전송
          </button>
          <button @click="closeReplyModal" class="btn-cancel">
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

<style scoped>
.inquiry-container {
  padding: 20px;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  overflow-x: hidden;
  background: #f8f9fa;
  min-height: 100vh;
}

.inquiry-form-container {
  max-width: 1400px;
  width: 100%;
  margin: 0 auto;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  border: 1px solid #e9ecef;
}

.inquiry-form-title {
  background: #495057;
  color: white;
  margin: 0;
  padding: 24px 30px;
  font-size: 1.75rem;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 15px;
  border-bottom: 1px solid #dee2e6;
}

.inquiry-content {
  padding: 40px;
}

/* 통계 카드 */
.stats-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: white;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 15px;
  transition: all 0.3s ease;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.stat-card:hover {
  background: #f8f9fa;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.stat-icon {
  width: 50px;
  height: 50px;
  background: #495057;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  color: white;
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 2rem;
  font-weight: bold;
  color: #495057;
  margin-bottom: 5px;
}

.stat-label {
  color: #6c757d;
  font-size: 0.9rem;
}

/* 필터 및 검색 */
.filter-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  gap: 20px;
  flex-wrap: wrap;
}

.search-box {
  position: relative;
  flex: 1;
  max-width: 400px;
}

.search-box i {
  position: absolute;
  left: 15px;
  top: 50%;
  transform: translateY(-50%);
  color: #aaaaaa;
}

.search-input {
  width: 100%;
  padding: 12px 16px 12px 45px;
  border: 1px solid #dee2e6;
  border-radius: 6px;
  background: white;
  color: #495057;
  font-size: 1rem;
  transition: all 0.3s ease;
}

.search-input:focus {
  outline: none;
  border-color: #495057;
  background: white;
  box-shadow: 0 0 0 3px rgba(73, 80, 87, 0.1);
}

.search-input::placeholder {
  color: #6c757d;
}

.filter-buttons {
  display: flex;
  gap: 10px;
}

.filter-btn {
  padding: 10px 20px;
  border: 1px solid #dee2e6;
  border-radius: 6px;
  background: white;
  color: #495057;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.9rem;
}

.filter-btn:hover {
  background: #f8f9fa;
  border-color: #495057;
}

.filter-btn.active {
  background: #495057;
  border-color: #495057;
  color: white;
}

/* 테이블 */
.inquiry-table-container {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #e9ecef;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.inquiry-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.9rem;
}

.inquiry-table th {
  background: #495057;
  color: white;
  padding: 15px 12px;
  font-weight: 600;
  text-align: left;
  border-bottom: 1px solid #dee2e6;
}

.inquiry-table td {
  padding: 12px;
  border-bottom: 1px solid #e9ecef;
  color: #495057;
}

.inquiry-table tr {
  transition: all 0.3s ease;
  cursor: pointer;
}

.inquiry-table tr:hover {
  background: #f8f9fa;
}

.inquiry-table tr.new-inquiry {
  background: rgba(255, 193, 7, 0.1);
}

.inquiry-table tr.new-inquiry:hover {
  background: rgba(255, 193, 7, 0.2);
}

.subject-cell {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.message-cell {
  max-width: 250px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.status-badge {
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 0.8rem;
  font-weight: 600;
}

.status-new {
  background: #fff3cd;
  color: #856404;
  border: 1px solid #ffeaa7;
}

.status-replied {
  background: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
}

.action-cell {
  display: flex;
  gap: 8px;
  justify-content: center;
}

.action-btn {
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.8rem;
}

.view-btn {
  background: #e3f2fd;
  color: #1976d2;
}

.view-btn:hover {
  background: #bbdefb;
  transform: scale(1.05);
}

.reply-btn {
  background: #e8f5e8;
  color: #2e7d32;
}

.reply-btn:hover {
  background: #c8e6c9;
  transform: scale(1.05);
}

.delete-btn {
  background: #ffebee;
  color: #c62828;
}

.delete-btn:hover {
  background: #ffcdd2;
  transform: scale(1.05);
}

/* 빈 상태 */
.no-inquiry {
  text-align: center;
  padding: 60px 20px;
  color: #6c757d;
}

.no-inquiry i {
  font-size: 3rem;
  margin-bottom: 20px;
  opacity: 0.5;
  color: #dee2e6;
}

.no-inquiry h3 {
  margin-bottom: 10px;
  color: #495057;
}

/* 모달 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
}

.modal-content {
  background: white;
  border-radius: 12px;
  max-width: 600px;
  width: 100%;
  max-height: 80vh;
  overflow: hidden;
  border: 1px solid #e9ecef;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
}

.modal-header {
  background: #495057;
  color: white;
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #dee2e6;
}

.modal-header h3 {
  margin: 0;
}

.close-btn {
  background: none;
  border: none;
  color: white;
  font-size: 1.2rem;
  cursor: pointer;
  padding: 5px;
  border-radius: 4px;
  transition: background 0.3s ease;
}

.close-btn:hover {
  background: rgba(255, 255, 255, 0.1);
}

.modal-body {
  padding: 20px;
  max-height: 400px;
  overflow-y: auto;
}

.inquiry-detail {
  color: #495057;
}

.detail-row {
  margin-bottom: 15px;
  display: flex;
  gap: 10px;
}

.detail-row label {
  font-weight: 600;
  min-width: 80px;
  color: #6c757d;
}

.message-content {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
  margin-top: 10px;
  white-space: pre-wrap;
  line-height: 1.5;
  border: 1px solid #e9ecef;
}

.modal-footer {
  padding: 20px;
  display: flex;
  gap: 10px;
  justify-content: flex-end;
  border-top: 1px solid #e9ecef;
}

.btn-reply, .btn-cancel {
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 0.9rem;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
}

.btn-reply {
  background: #28a745;
  color: white;
}

.btn-reply:hover {
  background: #218838;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(40, 167, 69, 0.3);
}

.btn-cancel {
  background: #6c757d;
  color: white;
}

.btn-cancel:hover {
  background: #5a6268;
}

/* 답변 섹션 */
.reply-section {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #e9ecef;
}

.reply-content {
  background: #e8f5e8;
  padding: 15px;
  border-radius: 8px;
  margin-top: 10px;
  white-space: pre-wrap;
  line-height: 1.5;
  border: 1px solid #c3e6cb;
  color: #155724;
}

/* 답변 모달 */
.reply-modal {
  max-width: 700px;
}

.reply-form {
  color: #495057;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  font-weight: 600;
  margin-bottom: 8px;
  color: #495057;
}

.inquiry-summary {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
  border: 1px solid #e9ecef;
  line-height: 1.6;
  color: #495057;
}

.reply-textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #dee2e6;
  border-radius: 6px;
  background: white;
  color: #495057;
  font-size: 1rem;
  font-family: inherit;
  resize: vertical;
  transition: all 0.3s ease;
}

.reply-textarea:focus {
  outline: none;
  border-color: #495057;
  box-shadow: 0 0 0 3px rgba(73, 80, 87, 0.1);
}

.reply-textarea::placeholder {
  color: #6c757d;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .inquiry-container {
    padding: 10px;
  }
  
  .inquiry-content {
    padding: 20px;
  }
  
  .stats-section {
    grid-template-columns: 1fr;
  }
  
  .filter-section {
    flex-direction: column;
    align-items: stretch;
  }
  
  .search-box {
    max-width: none;
  }
  
  .filter-buttons {
    justify-content: center;
  }
  
  .inquiry-table {
    font-size: 0.8rem;
  }
  
  .inquiry-table th,
  .inquiry-table td {
    padding: 8px 6px;
  }
  
  .subject-cell,
  .message-cell {
    max-width: 120px;
  }
  
  .action-cell {
    flex-direction: column;
    gap: 4px;
  }
  
  .action-btn {
    width: 28px;
    height: 28px;
    font-size: 0.7rem;
  }
}

@media (max-width: 480px) {
  .inquiry-form-title {
    font-size: 1.5rem;
    padding: 20px;
  }
  
  .inquiry-content {
    padding: 15px;
  }
  
  .modal-content {
    margin: 10px;
    max-height: 90vh;
  }
  
  .modal-body {
    padding: 15px;
  }
  
  .detail-row {
    flex-direction: column;
    gap: 5px;
  }
  
  .detail-row label {
    min-width: auto;
  }
}
</style> 
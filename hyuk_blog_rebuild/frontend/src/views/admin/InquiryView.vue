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
                :class="{ 'new-inquiry': !inquiry.replied }"
                @click="viewInquiry(inquiry)"
              >
                <td>{{ inquiry.id }}</td>
                <td>{{ inquiry.name }}</td>
                <td>{{ inquiry.email }}</td>
                <td class="subject-cell">{{ inquiry.subject }}</td>
                <td class="message-cell">{{ truncateText(inquiry.message, 50) }}</td>
                <td>{{ formatDate(inquiry.createdAt) }}</td>
                <td>
                  <span :class="['status-badge', inquiry.replied ? 'status-replied' : 'status-new']">
                    {{ inquiry.replied ? '답변완료' : '새문의' }}
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
                    @click.stop="replyInquiry(inquiry)" 
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
          </div>
        </div>
        <div class="modal-footer">
          <button @click="replyInquiry(selectedInquiry)" class="btn-reply">
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
  </div>
</template>

<script>
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
      inquiries: []
    }
  },
  computed: {
    filteredInquiries() {
      let filtered = this.inquiries

      // 필터 적용
      if (this.currentFilter === 'new') {
        filtered = filtered.filter(inquiry => !inquiry.replied)
      } else if (this.currentFilter === 'replied') {
        filtered = filtered.filter(inquiry => inquiry.replied)
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
    // 페이지 진입 시 배경 스타일 설정
    document.body.style.background = 'linear-gradient(135deg, #1a1a1a 0%, #2d2d2d 25%, #404040 50%, #2d2d2d 75%, #1a1a1a 100%)';
    document.body.style.minHeight = '100vh';
    document.body.style.margin = '0';
    document.body.style.padding = '0';
    document.body.style.fontFamily = "'Noto Sans KR', sans-serif";
    
    this.loadInquiries()
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
    async loadInquiries() {
      try {
        // TODO: API 호출로 문의 데이터 로드
        // const response = await this.$http.get('/api/admin/inquiries')
        // this.inquiries = response.data.inquiries
        // this.stats = response.data.stats
        
        // 샘플 데이터
        this.inquiries = [
          {
            id: 1,
            name: '김철수',
            email: 'kim@example.com',
            subject: '웹사이트 문의',
            message: '안녕하세요. 웹사이트에 대해 문의드립니다. 어떤 기술 스택을 사용하셨나요?',
            createdAt: '2024-01-15T10:30:00',
            replied: false
          },
          {
            id: 2,
            name: '이영희',
            email: 'lee@example.com',
            subject: '프로젝트 협업 제안',
            message: '프로젝트 협업에 관심이 있습니다. 연락처를 알려주세요.',
            createdAt: '2024-01-14T15:20:00',
            replied: true
          },
          {
            id: 3,
            name: '박민수',
            email: 'park@example.com',
            subject: '기술 질문',
            message: 'Vue.js와 React의 차이점에 대해 궁금합니다.',
            createdAt: '2024-01-13T09:15:00',
            replied: false
          }
        ]
        
        this.updateStats()
      } catch (error) {
        console.error('문의 로드 실패:', error)
      }
    },
    
    updateStats() {
      this.stats.totalInquiries = this.inquiries.length
      this.stats.newInquiries = this.inquiries.filter(inquiry => !inquiry.replied).length
      this.stats.repliedInquiries = this.inquiries.filter(inquiry => inquiry.replied).length
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
    
    replyInquiry(inquiry) {
      // TODO: 답변 기능 구현
      console.log('답변하기:', inquiry)
      alert('답변 기능은 추후 구현 예정입니다.')
    },
    
    async deleteInquiry(id) {
      if (confirm('정말로 이 문의를 삭제하시겠습니까?')) {
        try {
          // TODO: API 호출로 문의 삭제
          // await this.$http.delete(`/api/admin/inquiries/${id}`)
          
          this.inquiries = this.inquiries.filter(inquiry => inquiry.id !== id)
          this.updateStats()
          alert('문의가 삭제되었습니다.')
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
  min-height: 100vh;
  padding: 20px;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  overflow-x: hidden;
}

.inquiry-form-container {
  max-width: 1400px;
  width: 100%;
  margin: 0 auto;
  background: rgba(45, 45, 45, 0.95);
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.5);
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.inquiry-form-title {
  background: linear-gradient(135deg, #333333 0%, #555555 100%);
  color: white;
  margin: 0;
  padding: 30px;
  font-size: 2rem;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 15px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
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
  background: rgba(60, 60, 60, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 15px;
  transition: all 0.3s ease;
}

.stat-card:hover {
  background: rgba(70, 70, 70, 0.4);
  transform: translateY(-2px);
}

.stat-icon {
  width: 50px;
  height: 50px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
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
  color: white;
  margin-bottom: 5px;
}

.stat-label {
  color: #aaaaaa;
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
  border: 2px solid #555555;
  border-radius: 8px;
  background: rgba(60, 60, 60, 0.8);
  color: white;
  font-size: 1rem;
  transition: all 0.3s ease;
}

.search-input:focus {
  outline: none;
  border-color: #888888;
  background: rgba(70, 70, 70, 0.9);
}

.search-input::placeholder {
  color: #aaaaaa;
}

.filter-buttons {
  display: flex;
  gap: 10px;
}

.filter-btn {
  padding: 10px 20px;
  border: 2px solid #555555;
  border-radius: 8px;
  background: rgba(60, 60, 60, 0.8);
  color: white;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.9rem;
}

.filter-btn:hover {
  background: rgba(70, 70, 70, 0.9);
  border-color: #888888;
}

.filter-btn.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-color: #667eea;
}

/* 테이블 */
.inquiry-table-container {
  background: rgba(45, 45, 45, 0.8);
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.inquiry-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.9rem;
}

.inquiry-table th {
  background: linear-gradient(135deg, #333333 0%, #555555 100%);
  color: white;
  padding: 15px 12px;
  font-weight: 600;
  text-align: left;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.inquiry-table td {
  padding: 12px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  color: white;
}

.inquiry-table tr {
  transition: all 0.3s ease;
  cursor: pointer;
}

.inquiry-table tr:hover {
  background: rgba(100, 100, 100, 0.3);
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
  background: rgba(255, 193, 7, 0.2);
  color: #ffc107;
  border: 1px solid rgba(255, 193, 7, 0.3);
}

.status-replied {
  background: rgba(40, 167, 69, 0.2);
  color: #28a745;
  border: 1px solid rgba(40, 167, 69, 0.3);
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
  background: rgba(102, 126, 234, 0.2);
  color: #667eea;
}

.view-btn:hover {
  background: rgba(102, 126, 234, 0.3);
  transform: scale(1.05);
}

.reply-btn {
  background: rgba(40, 167, 69, 0.2);
  color: #28a745;
}

.reply-btn:hover {
  background: rgba(40, 167, 69, 0.3);
  transform: scale(1.05);
}

.delete-btn {
  background: rgba(220, 53, 69, 0.2);
  color: #dc3545;
}

.delete-btn:hover {
  background: rgba(220, 53, 69, 0.3);
  transform: scale(1.05);
}

/* 빈 상태 */
.no-inquiry {
  text-align: center;
  padding: 60px 20px;
  color: #aaaaaa;
}

.no-inquiry i {
  font-size: 3rem;
  margin-bottom: 20px;
  opacity: 0.5;
}

.no-inquiry h3 {
  margin-bottom: 10px;
  color: #cccccc;
}

/* 모달 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
}

.modal-content {
  background: rgba(45, 45, 45, 0.95);
  border-radius: 16px;
  max-width: 600px;
  width: 100%;
  max-height: 80vh;
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.modal-header {
  background: linear-gradient(135deg, #333333 0%, #555555 100%);
  color: white;
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
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
  color: white;
}

.detail-row {
  margin-bottom: 15px;
  display: flex;
  gap: 10px;
}

.detail-row label {
  font-weight: 600;
  min-width: 80px;
  color: #cccccc;
}

.message-content {
  background: rgba(60, 60, 60, 0.3);
  padding: 15px;
  border-radius: 8px;
  margin-top: 10px;
  white-space: pre-wrap;
  line-height: 1.5;
}

.modal-footer {
  padding: 20px;
  display: flex;
  gap: 10px;
  justify-content: flex-end;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
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
  background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
  color: white;
}

.btn-reply:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(40, 167, 69, 0.3);
}

.btn-cancel {
  background: rgba(108, 117, 125, 0.8);
  color: white;
}

.btn-cancel:hover {
  background: rgba(108, 117, 125, 1);
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
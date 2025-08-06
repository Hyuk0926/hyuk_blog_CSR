export default {
  // 네비게이션
  nav: {
    knowledge: '지식모음',
    projects: '프로젝트',
    about: '이력',
    contact: 'Contact',
    login: '로그인',
    register: '회원가입',
    logout: '로그아웃'
  },

  // 홈페이지
  home: {
    title: 'Takahara\'s Blog',
    subtitle: '개발자의 기술 블로그',
    welcome: '안녕하세요!',
    description: '개발 관련 지식과 프로젝트를 공유하는 블로그입니다.'
  },

  // 지식모음 페이지
  knowledge: {
    title: '지식모음',
    subtitle: '개발 관련 지식과 팁을 모아놓은 공간입니다.',
    categories: {
      backend: '백엔드',
      frontend: '프론트엔드',
      database: '데이터베이스',
      devops: 'DevOps',
      algorithm: '알고리즘'
    }
  },

  // 프로젝트 페이지
  projects: {
    personal: {
      title: '개인 프로젝트',
      blog: {
        period: '2025.06 ~ 진행중',
        title: 'Takahara\'s Blog (개인 기술 블로그)',
        desc: {
          1: '이력서/소개, 게시글 CRUD, 관리자 대시보드, 방문자수 통계, 다국어 지원, 문의 관리 등 다양한 기능',
          2: '데이터 영구 저장, 반응형 UI/UX, fragment 구조화',
          3: '최신 웹 트렌드 반영, 실시간 개선 및 유지보수'
        },
        role: '진행한 것: 전체 설계, 개발, 배포, 유지보수',
        result: '성과/배운점: Spring/Thymeleaf 실전 경험, 웹 아키텍처 설계, 실시간 피드백 반영, 유지보수성 높은 코드 작성'
      }
    },
    team: {
      title: '팀 프로젝트',
      collab: {
        period: '2025.09 ~ 2025.12',
        title: 'TeamCollaboration (팀 협업 플랫폼)',
        desc: {
          1: '실시간 협업/채팅, 프로젝트 관리(이슈, 일정), 파일 공유, 알림 시스템',
          2: 'CI/CD, 클라우드 배포, 코드 리뷰 및 협업 경험'
        },
        role: '저의 역할: 백엔드 API 설계/구현, DB 모델링, 배포 자동화',
        result: '성과/배운점: 협업 경험, 코드 리뷰, CI/CD, 실무 프로세스 경험, 커뮤니케이션 능력 향상',
        info: '팀원: 4명 (백엔드 2, 프론트엔드 1, 기획 1) / 협업툴: GitHub, Notion, Slack 등'
      }
    },
    link: {
      github: 'Github 보기'
    }
  },

  // 이력 페이지
  about: {
    title: '이력',
    subtitle: '개발자로서의 경력과 기술 스택을 소개합니다.',
    experience: '경력',
    education: '학력',
    skills: '기술 스택'
  },

  // Contact 페이지
  contact: {
    title: 'Contact',
    subtitle: '궁금한 점이나 문의사항이 있으시면 언제든 연락주세요.',
    form: {
      name: '이름',
      email: '이메일',
      subject: '제목',
      message: '메시지',
      send: '보내기'
    }
  },

  // 로그인 페이지
  login: {
    title: '로그인',
    subtitle: '좋아요와 댓글을 작성하려면 로그인이 필요합니다',
    username: '아이디',
    password: '비밀번호',
    loginButton: '로그인',
    loading: '로그인 중...',
    noAccount: '계정이 없으신가요?',
    register: '회원가입',
    backToHome: '← 메인 페이지로 돌아가기',
    capsLock: 'CapsLock이 켜져 있습니다!',
    passwordToggle: '비밀번호 보기'
  },

  // 회원가입 페이지
  register: {
    title: '회원가입',
    subtitle: '좋아요와 댓글 기능을 사용하려면 회원가입이 필요합니다',
    username: '아이디',
    password: '비밀번호',
    confirmPassword: '비밀번호 확인',
    nickname: '닉네임',
    email: '이메일',
    required: '*',
    registerButton: '회원가입',
    loading: '회원가입 중...',
    hasAccount: '이미 계정이 있으신가요?',
    login: '로그인',
    backToHome: '← 메인 페이지로 돌아가기',
    duplicateCheck: '중복확인',
    checkComplete: '확인완료',
    validation: {
      username: {
        required: '아이디를 입력해주세요.',
        format: '4-20자의 영문, 숫자, 언더스코어만 사용 가능합니다.',
        available: '사용 가능한 아이디입니다.',
        duplicate: '이미 사용 중인 아이디입니다.',
        checkError: '중복 확인 중 오류가 발생했습니다.'
      },
      password: {
        required: '비밀번호를 입력해주세요.',
        minLength: '비밀번호는 최소 6자 이상이어야 합니다.',
        available: '사용 가능한 비밀번호입니다.'
      },
      confirmPassword: {
        required: '비밀번호 확인을 입력해주세요.',
        mismatch: '비밀번호가 일치하지 않습니다.',
        match: '비밀번호가 일치합니다.'
      },
      nickname: {
        required: '닉네임을 입력해주세요.',
        length: '닉네임은 2-20자 사이여야 합니다.',
        available: '사용 가능한 닉네임입니다.',
        duplicate: '이미 사용 중인 닉네임입니다.',
        checkError: '중복 확인 중 오류가 발생했습니다.'
      },
      email: {
        format: '올바른 이메일 형식을 입력해주세요.',
        available: '올바른 이메일 형식입니다.',
        duplicate: '이미 사용 중인 이메일입니다.',
        checkError: '중복 확인 중 오류가 발생했습니다.'
      }
    },
    formError: '모든 필수 항목을 올바르게 입력해주세요.',
    serverError: '서버 오류가 발생했습니다.',
    success: '회원가입이 완료되었습니다. 로그인해주세요.'
  },

  // 공통
  common: {
    loading: '로딩 중...',
    error: '오류가 발생했습니다.',
    success: '성공했습니다.',
    confirm: '확인',
    cancel: '취소',
    save: '저장',
    edit: '수정',
    delete: '삭제',
    back: '뒤로',
    next: '다음',
    previous: '이전'
  }
} 
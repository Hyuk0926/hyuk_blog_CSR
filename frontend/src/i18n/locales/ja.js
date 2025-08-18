export default {
  // ナビゲーション
  nav: {
    knowledge: '知識集',
    projects: 'プロジェクト',
    resume: '履歴書',
    contact: 'Contact',
    login: 'ログイン',
    register: '会員登録',
    logout: 'ログアウト'
  },

  // ホームページ
  home: {
    title: 'Takahara\'s Blog',
    subtitle: '開発者の技術ブログ',
    welcome: 'こんにちは！',
    description: '開発関連の知識とプロジェクトを共有するブログです。'
  },

  // 知識集ページ
  knowledge: {
    title: '知識集',
    subtitle: '開発関連の知識とコツを集めたスペースです。',
    latestPosts: '最新記事',
    postCount: '件',
    searchPlaceholder: '記事を検索...',
    loading: '記事を読み込み中...',
    noPosts: '記事がありません。',
    retry: '再試行',
    loadError: '記事の読み込みに失敗しました。',
    searchError: '検索に失敗しました。',
    categoryError: 'カテゴリ別記事の読み込みに失敗しました。',
    categories: {
      all: 'すべて表示',
      backend: 'バックエンド',
      frontend: 'フロントエンド',
      database: 'データベース',
      devops: 'DevOps',
      algorithm: 'アルゴリズム'
    }
  },

  // プロジェクトページ
  projects: {
    personal: {
      title: '個人プロジェクト',
      blog: {
        period: '2025.06 ~ 進行中',
        title: 'Takahara\'s Blog (個人技術ブログ)',
        desc: {
          1: '履歴書/紹介、記事CRUD、管理者ダッシュボード、訪問者数統計、多言語対応、お問い合わせ管理など様々な機能',
          2: 'データ永続化、レスポンシブUI/UX、fragment構造化',
          3: '最新Webトレンド反映、リアルタイム改善とメンテナンス'
        },
        role: '担当したこと: 全体設計、開発、デプロイ、メンテナンス',
        result: '成果/学んだこと: Spring/Thymeleaf実戦経験、Webアーキテクチャ設計、リアルタイムフィードバック反映、保守性の高いコード作成'
      }
    },
    team: {
      title: 'チームプロジェクト',
      collab: {
        period: '2025.09 ~ 2025.12',
        title: 'TeamCollaboration (チーム協業プラットフォーム)',
        desc: {
          1: 'リアルタイム協業/チャット、プロジェクト管理(課題、スケジュール)、ファイル共有、通知システム',
          2: 'CI/CD、クラウドデプロイ、コードレビューと協業経験'
        },
        role: '私の役割: バックエンドAPI設計/実装、DBモデリング、デプロイ自動化',
        result: '成果/学んだこと: 協業経験、コードレビュー、CI/CD、実務プロセス経験、コミュニケーション能力向上',
        info: 'チームメンバー: 4名 (バックエンド2、フロントエンド1、企画1) / 協業ツール: GitHub、Notion、Slackなど'
      }
    },
    link: {
      github: 'Githubを見る'
    }
  },



  // 履歴書関連
  resume: {
    introduction: 'プロフィール',
    education: '学歴',
    school: '学校名',
    degree: '専攻/学位',
    period: '期間',
    skills: '技術スタック',
    studentLife: '学生生活(成長過程)',
    strengthsWeaknesses: '長所と短所',
    effortExperience: '人生で努力した経験',
    japanItMotivation: '日本IT就職志望動機',
    futurePlan: '将来の計画と抱負'
  },

  // Contactページ
  contact: {
    title: 'Contact',
    subtitle: 'ご質問やお問い合わせがございましたら、いつでもお気軽にご連絡ください。',
    form: {
      name: 'お名前',
      email: 'メールアドレス',
      subject: '件名',
      message: 'お問い合わせ内容',
      send: '送信する',
      placeholder: {
        name: 'お名前を入力してください',
        email: 'example@email.com',
        subject: '件名を入力してください',
        message: 'お問い合わせ内容を入力してください'
      }
    },
    success: 'お問い合わせを送信しました。ありがとうございます。',
    error: '送信に失敗しました。もう一度お試しください。'
  },

  // ログインページ
  login: {
    title: 'ログイン',
    subtitle: 'いいねとコメントを投稿するにはログインが必要です',
    username: 'ユーザーID',
    password: 'パスワード',
    loginButton: 'ログイン',
    loading: 'ログイン中...',
    noAccount: 'アカウントをお持ちでない方は？',
    register: '会員登録',
    backToHome: '← メインページに戻る',
    capsLock: 'CapsLockがオンになっています！',
    passwordToggle: 'パスワード表示'
  },

  // 会員登録ページ
  register: {
    title: '会員登録',
    subtitle: 'いいねとコメント機能を使用するには会員登録が必要です',
    username: 'ユーザーID',
    password: 'パスワード',
    confirmPassword: 'パスワード確認',
    nickname: 'ニックネーム',
    email: 'メールアドレス',
    required: '*',
    registerButton: '会員登録',
    loading: '会員登録中...',
    hasAccount: 'すでにアカウントをお持ちの方は？',
    login: 'ログイン',
    backToHome: '← メインページに戻る',
    duplicateCheck: '重複確認',
    checkComplete: '確認完了',
    validation: {
      username: {
        required: 'ユーザーIDを入力してください。',
        format: '4-20文字の英数字、アンダースコアのみ使用可能です。',
        available: '使用可能なユーザーIDです。',
        duplicate: 'すでに使用されているユーザーIDです。',
        checkError: '重複確認中にエラーが発生しました。'
      },
      password: {
        required: 'パスワードを入力してください。',
        minLength: 'パスワードは最低6文字以上である必要があります。',
        available: '使用可能なパスワードです。'
      },
      confirmPassword: {
        required: 'パスワード確認を入力してください。',
        mismatch: 'パスワードが一致しません。',
        match: 'パスワードが一致します。'
      },
      nickname: {
        required: 'ニックネームを入力してください。',
        length: 'ニックネームは2-20文字の間である必要があります。',
        available: '使用可能なニックネームです。',
        duplicate: 'すでに使用されているニックネームです。',
        checkError: '重複確認中にエラーが発生しました。'
      },
      email: {
        format: '正しいメールアドレス形式を入力してください。',
        available: '正しいメールアドレス形式です。',
        duplicate: 'すでに使用されているメールアドレスです。',
        checkError: '重複確認中にエラーが発生しました。'
      }
    },
    formError: 'すべての必須項目を正しく入力してください。',
    serverError: 'サーバーエラーが発生しました。',
    success: '会員登録が完了しました。ログインしてください。'
  },

  // 共通
  common: {
    loading: '読み込み中...',
    error: 'エラーが発生しました。',
    success: '成功しました。',
    confirm: '確認',
    cancel: 'キャンセル',
    save: '保存',
    edit: '編集',
    delete: '削除',
    back: '戻る',
    next: '次へ',
    previous: '前へ'
  }
} 
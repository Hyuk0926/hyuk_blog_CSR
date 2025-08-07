import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView
  },
  {
    path: '/knowledge',
    name: 'knowledge',
    component: () => import('../views/KnowledgeView.vue')
  },
  {
    path: '/knowledge/:id',
    name: 'post-detail',
    component: () => import('../views/PostDetailView.vue')
  },
  {
    path: '/projects',
    name: 'projects',
    // lazy loading for better performance
    component: () => import('../views/ProjectView.vue')
  },

  {
    path: '/contact',
    name: 'contact',
    component: () => import('../views/ContactView.vue')
  },
  {
    path: '/resume',
    name: 'resume',
    component: () => import('../views/ResumeView.vue')
  },
  {
    path: '/user/login',
    name: 'user-login',
    component: () => import('../views/UserLoginView.vue')
  },
  {
    path: '/user/register',
    name: 'user-register',
    component: () => import('../views/UserRegisterView.vue')
  },
  {
    path: '/admin/login',
    name: 'admin-login',
    component: () => import('../views/admin/LoginView.vue')
  },
      {
      path: '/admin/dashboard',
      name: 'admin-dashboard',
      component: () => import('../views/admin/DashboardView.vue')
    },
    {
      path: '/admin/posts/new',
      name: 'admin-post-new',
      component: () => import('../views/admin/PostFormView.vue')
    },
    {
      path: '/admin/posts/edit/:id',
      name: 'admin-post-edit',
      component: () => import('../views/admin/PostFormView.vue')
    },
    {
      path: '/admin/resume',
      name: 'admin-resume',
      component: () => import('../views/admin/ResumeView.vue')
    },
    {
      path: '/admin/inquiries',
      name: 'admin-inquiries',
      component: () => import('../views/admin/InquiryView.vue')
    }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router 
import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/',
    redirect: '/student/home'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/student',
    name: 'Student',
    component: () => import('@/views/student/Layout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: 'home',
        name: 'StudentHome',
        component: () => import('@/views/student/Home.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'articles',
        name: 'Articles',
        component: () => import('@/views/student/Articles.vue'),
        meta: { title: '心理科普' }
      },
      {
        path: 'articles/:id',
        name: 'ArticleDetail',
        component: () => import('@/views/student/ArticleDetail.vue'),
        meta: { title: '文章详情' }
      },
      {
        path: 'counselors',
        name: 'Counselors',
        component: () => import('@/views/student/Counselors.vue'),
        meta: { title: '咨询师团队' }
      },
      {
        path: 'counselors/:id',
        name: 'CounselorDetail',
        component: () => import('@/views/student/CounselorDetail.vue'),
        meta: { title: '咨询师详情' }
      },
      {
        path: 'appointments',
        name: 'Appointments',
        component: () => import('@/views/student/Appointments.vue'),
        meta: { title: '我的预约' }
      },
      {
        path: 'tests',
        name: 'Tests',
        component: () => import('@/views/student/Tests.vue'),
        meta: { title: '心理测评' }
      },
      {
        path: 'moments',
        name: 'Moments',
        component: () => import('@/views/student/Moments.vue'),
        meta: { title: '心灵树洞' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/student/Profile.vue'),
        meta: { title: '个人中心' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - 心理健康平台`
  }
  
  // 检查是否需要登录
  if (to.meta.requiresAuth && !userStore.token) {
    next('/login')
  } else if (to.path === '/login' && userStore.token) {
    next('/student/home')
  } else {
    next()
  }
})

export default router

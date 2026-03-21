import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/',
    redirect: '/login'
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
  },
  {
    path: '/counselor',
    name: 'Counselor',
    component: () => import('@/views/counselor/Layout.vue'),
    meta: { requiresAuth: true, role: 2 },
    children: [
      {
        path: 'dashboard',
        name: 'CounselorDashboard',
        component: () => import('@/views/counselor/Dashboard.vue'),
        meta: { title: '工作台' }
      },
      {
        path: 'schedule',
        name: 'CounselorSchedule',
        component: () => import('@/views/counselor/Schedule.vue'),
        meta: { title: '时间管理' }
      },
      {
        path: 'appointments',
        name: 'CounselorAppointments',
        component: () => import('@/views/counselor/Appointments.vue'),
        meta: { title: '预约管理' }
      },
      {
        path: 'notes',
        name: 'CounselorNotes',
        component: () => import('@/views/counselor/Notes.vue'),
        meta: { title: '咨询笔记' }
      },
      {
        path: 'profile',
        name: 'CounselorProfile',
        component: () => import('@/views/counselor/Profile.vue'),
        meta: { title: '个人中心' }
      }
    ]
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('@/views/admin/Layout.vue'),
    meta: { requiresAuth: true, role: 3 },
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/Dashboard.vue'),
        meta: { title: '数据看板' }
      },
      {
        path: 'counselors',
        name: 'AdminCounselors',
        component: () => import('@/views/admin/Counselors.vue'),
        meta: { title: '咨询师管理' }
      },
      {
        path: 'articles',
        name: 'AdminArticles',
        component: () => import('@/views/admin/Articles.vue'),
        meta: { title: '文章管理' }
      },
      {
        path: 'moments',
        name: 'AdminMoments',
        component: () => import('@/views/admin/Moments.vue'),
        meta: { title: '树洞审核' }
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/Users.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'statistics',
        name: 'AdminStatistics',
        component: () => import('@/views/admin/Statistics.vue'),
        meta: { title: '数据统计' }
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
    // 根据用户类型跳转到对应页面
    const userType = userStore.userInfo?.userType
    if (userType === 1) {
      next('/student/home')
    } else if (userType === 2) {
      next('/counselor/dashboard')
    } else if (userType === 3) {
      next('/admin/dashboard')
    } else {
      next('/student/home')
    }
  } else if (to.meta.role && userStore.userInfo?.userType !== to.meta.role) {
    // 角色验证
    next('/login')
  } else {
    next()
  }
})

export default router

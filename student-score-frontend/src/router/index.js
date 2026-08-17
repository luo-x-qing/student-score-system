import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView
  },
  {
    path: '/students',
    name: 'students',
    component: () => import('../views/StudentView.vue')
  },
  {
    path: '/subjects',
    name: 'subjects',
    component: () => import('../views/SubjectView.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router

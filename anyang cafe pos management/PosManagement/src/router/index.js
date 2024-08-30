
/**
 * router/index.ts
 *
 * Automatic routes for `./src/pages/*.vue`
 */

// Composables
import { createRouter, createWebHistory } from 'vue-router/auto'
import manage_meun from '../views/manage_menu.vue'
import manage_order from '../views/manage_order.vue'
import sale_record from '../views/sale_record.vue'

const routes = [
  {
    path:'/manageMenu',
    name:'메뉴 관리',
    component:manage_meun
  },
  {
    path:'/manageOrder',
    name:'주문 관리',
    component:manage_order
  },
  {
    path:'/saleRecord',
    name:'정산',
    component:sale_record
  },
]


const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

// Workaround for https://github.com/vitejs/vite/issues/11804
router.onError((err, to) => {
  if (err?.message?.includes?.('Failed to fetch dynamically imported module')) {
    if (!localStorage.getItem('vuetify:dynamic-reload')) {
      console.log('Reloading page to fix dynamic import error')
      localStorage.setItem('vuetify:dynamic-reload', 'true')
      location.assign(to.fullPath)
    } else {
      console.error('Dynamic import error, reloading page did not fix it', err)
    }
  } else {
    console.error(err)
  }
})

router.isReady().then(() => {
  localStorage.removeItem('vuetify:dynamic-reload')
})

export default router

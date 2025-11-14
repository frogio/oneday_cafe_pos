/**
 * plugins/vuetify.js
 *
 * Framework documentation: https://vuetifyjs.com`
 */

// Styles
import '@mdi/font/css/materialdesignicons.css'
import 'vuetify/styles'

// Composables
import { createVuetify } from 'vuetify'
// 👈 VDateInput을 'vuetify/labs/VDateInput'에서 import 합니다.
import { VDateInput } from 'vuetify/labs/VDateInput';

// https://vuetifyjs.com/en/introduction/why-vuetify/#feature-guides
export default createVuetify({
  components:{
    VDateInput,
  },
  theme: {
    defaultTheme: 'light',
  },
  locale:{
    locale:'ko',
  }
})

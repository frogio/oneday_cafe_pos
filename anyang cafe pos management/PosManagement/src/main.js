/**
 * main.js
 *
 * Bootstraps Vuetify and other plugins then mounts the App`
 */

// Plugins
import { registerPlugins } from '@/plugins'

// Components
import App from './App.vue'

// Composables
import { createApp } from 'vue'
import store from './store/store.js'
import vuetify from './plugins/vuetify'; // 👈 vuetify 인스턴스 import

const app = createApp(App)

registerPlugins(app)

app.use(store);
app.use(vuetify);

app.mount('#app')

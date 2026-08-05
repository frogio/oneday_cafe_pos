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
import VueSignaturePad from 'vue-signature-pad';
import store from "./store/store.js";

const app = createApp(App)

registerPlugins(app)

app.use(store);
app.use(VueSignaturePad);

app.mount('#app')

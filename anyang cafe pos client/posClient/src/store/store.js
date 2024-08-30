import { createStore } from "vuex";

const store = createStore({
    state(){
        return{
            IPAndPort:"192.168.1.7:5000",
        };
    },
    mutations:{},
    actions:{},
    getters:{}
});

export default store;
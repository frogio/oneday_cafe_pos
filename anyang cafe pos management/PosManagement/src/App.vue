<template>
  <v-card>
  <v-layout>
    <v-navigation-drawer
      v-model="drawer"
      temporary
    >        
      <v-list-item
        title="POS Management"
      ></v-list-item>

      <v-divider></v-divider>

      <v-list density="compact" nav>
        
        <router-link to ="/manageMenu">
          <v-list-item prepend-icon="mdi-forum" title="메뉴 관리" value="settings"></v-list-item>                 
        </router-link>
        <router-link to ="/manageOrder">
          <v-list-item prepend-icon="mdi-forum" title="주문 관리" value="settings"></v-list-item>                 
        </router-link>
        <router-link to ="/saleRecord">
          <v-list-item prepend-icon="mdi-forum" title="정산" value="settings"></v-list-item>                 
        </router-link>
        <router-link to ="/modifyRecord">
          <v-list-item prepend-icon="mdi-forum" title="정산 정정" value="settings"></v-list-item>                 
        </router-link>

      </v-list>
    </v-navigation-drawer>

    <v-main style="height:100vh">                        <!-- viewport에 100%로 적용 -->

      <v-app-bar :elevation="2" color="#000">            <!-- color #000로 적용 -->
          <v-btn icon="$menu" @click.stop="drawer = !drawer"></v-btn>
          <v-app-bar-title>{{ currentPage }}</v-app-bar-title>
      </v-app-bar>
      

      <router-view></router-view>

    </v-main>
  </v-layout>
</v-card>

</template>

<script lang="ts">

  export default{

    data(){
      return{
       drawer:null
      }
    },
    computed:{
      currentPage(){
        return this.$route.name;
      },
    }
  }


</script>

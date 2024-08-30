<template>

<div class="pos">
  <CategoryList class="category"
    @cur-category-drink="SelectCategory">
  </CategoryList>

  <DrinkList
    id="drink-list"
    class="drink-list"
    :cateInfo = "categoryInfo"
    :boxSize = "itemSize"
    @get-drink="GetDrink"
    >
  </DrinkList>

  <OrderList
    class="order-list"
    :list="orderList"
    :dataSetChanged="orderListChanged"
    @confirm-order="ConfirmDialog"
    @delete-order="DeleteOrder"
    >  
  </OrderList>

  <GetOrderDialog
        :itemInfo="selectedDrink"
        @order-info="GetOrderInfo">
  </GetOrderDialog>

  <v-dialog v-model="confirmDialog" max-width="700px" @click:outside="confirmDialog = false">
       <v-card>
        <v-card-title class="headline">음료를 주문하시겠습니까?</v-card-title>
           <div class="confirm-order-dialog">
              <p v-for="item in confirmedOrderItems">
                {{ item.msg }}
              </p>

              <div class="dialog-total-price">
                <p>{{ totalPrice }}원</p>
              </div>

            </div>
        <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="blue darken-1" text @click="ConfirmOrder">확인</v-btn>
            <v-btn color="blue darken-1" text @click="confirmDialog = false">취소</v-btn>
          </v-card-actions>
        </v-card>
    </v-dialog>

  
</div>

</template>

<style>
  .pos{
    display:flex;
    height:100vh;
  }

  .category{
    width:15%;
    height:100%;
  }

  .drink-list{
    width:50%;
    }
  .order-list{
    width:35%;
  }

  .confirm-order-dialog{
    padding:1.5rem;
    display:flex;
    justify-content:center;
    align-items:center;
    flex-direction:column;
  }

  .dialog-total-price{
    margin-top:1rem;
    font-size:1.5rem;

  }
</style>

<script>

import CategoryList from "./components/category_list.vue";
import DrinkList from "./components/drink_list.vue";
import GetOrderDialog from "./components/get_order_dialog.vue";
import OrderList from "./components/order_list.vue";

export default{
  components:{
    CategoryList,
    DrinkList,
    GetOrderDialog,
    OrderList,
  },
  data(){
    return{
      categoryInfo:null,
      itemSize:0,
      getOrderDialog:false,
      selectedDrink:null,
      orderList:[],
      orderListChanged:false,
    
      //confirmURL:"http://localhost:5000/receiveOrder",
      ws:null,
      confirmURL:"ws://" + this.$store.state.IPAndPort + "/",
      confirmDialog:false,
      confirmedOrderItems:[],
      totalPrice:0,
    }

  },
  methods:{

    SelectCategory(categoryDrink){
      this.categoryInfo = categoryDrink;
    },
    GetDivSize(){
        setTimeout(() => {              // 리스트가 렌더링이 다 될 떄까지 1초간 기다린다.
            const listDivSize = document.getElementById("drink-list")?.getBoundingClientRect();
            if(listDivSize != null)
              this.itemSize = listDivSize.width / 3;
        }, 300);
    },
    GetDrink(drink){
      this.getOrderDialog = !this.getOrderDialog;
      this.selectedDrink = {
        orderDialog:this.getOrderDialog,
        itemInfo:drink};
    },
    GetOrderInfo(orderInfo){
      this.orderList.push(orderInfo);
/*
      for(let i = 0; i < this.orderList.length; i++){
            console.log("selected Order : ");
            console.log("drinkName : " + orderInfo.drinkName);
            console.log("cupCount : " + orderInfo.cupCount);
            console.log("isHot : " + orderInfo.isHot);
            console.log("payOpt : " + orderInfo.payOpt);
            console.log("price : " + orderInfo.price);

      }
*/
      this.orderListChanged = !this.orderListChanged;
    },
    DeleteOrder(index){
      this.orderList.splice(index, 1);
      this.orderListChanged = !this.orderListChanged;
    },
    ConfirmDialog(orderItemInfo){
      this.confirmedOrderItems = orderItemInfo;
      this.totalPrice = 0;
      for(let i = 0; i < this.confirmedOrderItems.length; i++){
        if(this.confirmedOrderItems[i].price != "coupon")
          this.totalPrice += this.confirmedOrderItems[i].price;
      }
      this.confirmDialog = true;
    },
    ConfirmOrder(){
        this.ws.send(JSON.stringify({
            type:'order',
            order:this.orderList
        }));
        //console.log(this.orderList);
        this.orderList.length = 0;
        this.orderListChanged = !this.orderListChanged;
        this.confirmDialog = false;
    }
  },
  
  mounted(){
    this.GetDivSize();

    this.ws = new WebSocket(this.confirmURL);
    this.ws.onopen = () => {
        this.ws.send(JSON.stringify({
          type:'online',
          clientInfo:'receiveOrder'
        }));
      }

  }

}

</script>

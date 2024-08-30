<template>
    <div class="order-table" id="order-no-list" >
        <div class="scroll-list order-no-list" ref="list">
            <v-list lines="one">
                <v-list-item class ="scroll-list-item"
                    v-for="(order, index) in receivedOrder">
                    <v-card @click="GetOrder(order)" class="prevent-drag item" :key="index" :class="{'switching-background': index % 2 == 1 }">
                        {{ order.orderNo }}
                    </v-card>
                    <div class="sperator"></div>
                </v-list-item>
            </v-list>
        </div>
        <OrderTable
            :orders="selectedOrderList"
            @cancel-order="CancelOrder"
            @update-order-process="UpdatedOrderProcess"
            @complete-order="CompleteOrder"
            @cancel-drink="CancelDrink"
            class = "order-list">
        </OrderTable>
    </div>



</template>

<style>

.order-table{
    display:flex;
    width:100%;
    height:100vh;
}

.order-no-list{
    width:10%;
}

.order-list{
    width:90%;

}


</style>

<script>

import OrderTable from '../components/order_management/order_table.vue'

export default{
    components:{
        OrderTable,
    },
    data(){
        return{
            selectedOrderList:null,
            receivedOrder:null,
            ws:null,
        }
    },
    methods:{
        GetOrder(order){
            this.selectedOrderList = order;
            //console.log(this.selectedOrderList);
        },

        GetDivSize(){
            setTimeout(() => {              // 리스트가 렌더링이 다 될 떄까지 1초간 기다린다.
                const listDivSize = document.getElementById("order-no-list")?.getBoundingClientRect();
                if(listDivSize != null){
                    const categoryList = this.$refs.list;
                    if(categoryList != null)
                        categoryList.style.cssText = "height:" + (listDivSize.height - listDivSize.y) + "px!important";
                }
            }, 300);

        },

        ListReSize(){

            const listDivSize = document.getElementById("order-no-list")?.getBoundingClientRect();
            if(listDivSize != null){
                const categoryList = this.$refs.list;
                // 부모 Div로 부터 List가 가질 사이즈를 계산한다.
                if(categoryList != null)
                    categoryList.style.cssText = "height:" + (listDivSize.height - listDivSize.y) + "px!important";
            }
        },
        CancelOrder(orderNo){
            //console.log("isWorking?")
            this.receivedOrder = this.receivedOrder.filter(order => order.orderNo != orderNo);
            
            if(this.receivedOrder.length > 0 )                      // 주문이 아직도 남아있다면.
                this.selectedOrderList = this.receivedOrder[0];     // 첫번째 주문이 선택되도록 한다.
            else this.selectedOrderList = null;                     // 더이상 주문이 존재하지 않을 경우 null을 대입
            
            this.ws.send(JSON.stringify({
                    type:"deleteReq",
                    orderNo:orderNo
            }));

        },
        CancelDrink(drink){

            const orderNo = drink.orderNo;

            for(let i = 0; i < this.receivedOrder.length; i++){
                if(this.receivedOrder[i].orderNo == orderNo){
                    if(this.receivedOrder[i].orderList.length == 0){            // 현재 주문의 모든 음료가 취소되었을 경우
                        this.receivedOrder.splice(i, 1);                        // 주문번호를 삭제한다.
                        
                        if(this.receivedOrder.length > 0 )                      // 주문이 아직도 남아있다면.
                            this.selectedOrderList = this.receivedOrder[0];     // 첫번째 주문이 선택되도록 한다.

                    }
                    break;
                }
            }


            this.ws.send(JSON.stringify({
                type:"deleteDrinkReq",
                info:drink
            }));
        },

        UpdatedOrderProcess(orderState){
            this.ws.send(JSON.stringify({
                    type:"updateReq",
                    orderInfo:orderState
            }));
        },
        CompleteOrder(orderNo){
            this.receivedOrder = this.receivedOrder.filter(order => order.orderNo != orderNo);
            if(this.receivedOrder.length > 0 )                      // 주문이 아직도 남아있다면.
                this.selectedOrderList = this.receivedOrder[0];     // 첫번째 주문이 선택되도록 한다.
        }
    },
    mounted(){
        this.ws = new WebSocket("ws://" + this.$store.state.IPAndPort + "/");
        this.ws.onopen = () =>{
            this.ws.send(JSON.stringify({
                    type:'online',
                    clientInfo:'retrieveOrder'
            }));
        }

        this.ws.onmessage = (order) =>{

            if(this.receivedOrder == null){                             // 서버와 연결하여 최초로 데이터를 받아올 때
                const data = JSON.parse(order.data);
                if(Array.isArray(data) == false)                        // 단일 오브젝트만 받았을 경우
                    this.receivedOrder = [data];                        // 리스트 형식으로 강제한다.

                else 
                    this.receivedOrder = data;                          // 리스트 형식일 경우 그대로 대입한다.
            
                //this.selectedOrderList = this.receivedOrder[0];         // 주문 선택을 강제한다.
                console.log(this.receivedOrder);
            
            }
            else{
                this.receivedOrder.push(JSON.parse(order.data));        // 그렇지 않을 경우 push
                this.selectedOrderList = this.receivedOrder[0];         // 주문 선택을 강제한다.
                //console.log(this.receivedOrder);
            }                                                        
                
            
            //if(this.receivedOrder.length > 1){    // 연결되었을 때 주문이 존재하면 
                
                //console.log("isWorking?");
            //}
        }

        document.addEventListener('DOMContentLoaded', this.GetDivSize());
        window.addEventListener('resize',this.ListReSize);

    },
    
    beforeRouteLeave(){
        if(this.ws){
            this.ws.close();
        }
    }



}

</script>
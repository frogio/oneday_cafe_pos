<template>
    <div id="order-card-table" v-if="orderList != null && orderList.length > 0">
        <table>
            <tbody>
                <tr v-for="(rows, rowIndex) in processedTable" :key="renderAgain">
                    <td v-for="(order, index) in rows" >
                        <OrderCard
                            v-if="order != null"
                            :width="cardWidth"
                            :height="cardHeight"
                            :order="order"
                            :index="2 * rowIndex + index"
                            @drink-completed="IsCompletedDrink"
                            @cancel-drink="CancelDrink"
                            >

                        </OrderCard>               
                    </td>
                </tr>
            </tbody>
        </table>
        <div class="order-info-bar">
            <v-btn
                @click="CancelOrder"
                class="btn">
                주문 취소
            </v-btn>

            <p>
               {{ orderNo }}번 || {{ totalPrice }} 원
            </p>

            <v-btn
                @click="CompleteOrder"
                class="btn">
                주문 완료
            </v-btn>
        </div>

    </div>

    <div v-else class="empty-table">
        <p>주문이 더이상 존재하지 않습니다.</p>
    </div>

</template>
<style>

.order-info-bar{
    width:100%;
    height:48px;
    display:flex;
    justify-content:space-between;
    align-items:center;
}

.empty-table{
    display:flex;
    width:100%;
    height:100%;
    justify-content:center;
    align-items:center;
    font-size:larger;
    background:#f7f7f7;
}

.btn{
    background:black;
    color:whitesmoke;

}

</style>


<script>
import OrderCard from "./order_card.vue";

    export default{
        components:{  
            OrderCard,
        },
        watch:{
            orders(list){
                if(list == null){                                   // 주문이 더이상 없을 경우
                    this.orderList = null;
                    this.totalPrice = 0;
                    this.processedTable.length = 0;
                    this.renderAgain = !this.renderAgain;           // 강제 재 렌더링
                }
                else{
                    this.orderList = list.orderList;
                    this.orderNo = list.orderNo;
                    this.TableProcess();    
                }
                
            }
            

        },
        props:{
            orders:{
                required:true,
                renderAgain:false,
            },
        },
        data(){
            return{
                competeOrderUrl:"http://" + this.$store.state.IPAndPort + "/completeOrder",
                orderList:this.orders,
                orderNo:-1,
                totalPrice:0,
                processedTable:[],
                cardHeight:0,
                cardWidth:0,
            }

        },
        methods:{
            GetDivSize(){

                setTimeout(() => {              // 리스트가 렌더링이 다 될 떄까지 1초간 기다린다.
                    if(this.processedTable.length > 0){
                    const TableDivSize = document.getElementById("order-card-table")?.getBoundingClientRect();
                        if(TableDivSize != null){
                            this.cardHeight = (TableDivSize.height - 64 - 48) / this.processedTable.length;     // 64, 48는 메뉴바, 주문 정보바
                            this.cardWidth = TableDivSize.width / this.processedTable[0].length;
                            //console.log("Width, Height :" + this.cardWidth + ", " + this.cardHeight);
                        }
                }}, 300);
            },

            TableReSize(){

                if(this.processedTable.length > 0){
                    const TableDivSize = document.getElementById("order-card-table")?.getBoundingClientRect();
                        if(TableDivSize != null){
                            this.cardHeight = (TableDivSize.height - 64 - 48) / this.processedTable.length;     // 64, 48는 메뉴바, 주문 정보바
                            this.cardWidth = TableDivSize.width / this.processedTable[0].length;
                            //console.log("Width, Height :" + this.cardWidth + ", " + this.cardHeight);
                        }
                }
            },
            CancelOrder(){
                if(confirm("주문을 취소하시겠습니까?"))
                    this.$emit("cancel-order", this.orderNo);

            },
            CancelDrink(drinkIdx){
                console.log(drinkIdx);
                this.orderList.splice(drinkIdx, 1);                     // 리스트에서 음료 삭제

                if(this.orderList.length > 0)
                    this.TableProcess();
            
                let deletedDrink = {
                    orderNo:this.orderNo,
                    Idx:drinkIdx
                }

                this.$emit("cancel-drink", deletedDrink);
            
            },
            TableProcess(){
                    this.totalPrice = 0;
                    this.processedTable.length = 0;
                    for(let i = 0; i < this.orderList.length / 2; i++){
                        this.processedTable.push([]);
                        for(let k = 0; k < 2; k++){
                            if(this.orderList[2 * i + k] != undefined){
                                this.processedTable[i].push(this.orderList[2 * i + k]);
                                this.totalPrice += this.orderList[2 * i + k].price;
                            }
                        }
                    }
                    //console.log(this.processedTable);
                    const TableDivSize = document.getElementById("order-card-table")?.getBoundingClientRect();
                        if(TableDivSize != null){
                            this.cardHeight = (TableDivSize.height - 64 - 48) / this.processedTable.length;     // 64, 48는 메뉴바, 주문 정보바
                            this.cardWidth = TableDivSize.width / this.processedTable[0].length;
                            //console.log("Width, Height :" + this.cardWidth + ", " + this.cardHeight);
                    }
                    this.GetDivSize();
                    this.renderAgain = !this.renderAgain;           // 강제 재 렌더링
                
            },

            IsCompletedDrink(orderState){
                for(let idx = 0; idx < this.orderList.length; idx++){
                    if(orderState.index == idx)
                        this.orderList[idx].completed = orderState.isCompleted; 
                }
                const state = {
                    orderNo:this.orderNo,
                    orderList:this.orderList
                }
                
                this.$emit("update-order-process", state);
            },
            async CompleteOrder(){
                for(let i = 0; i < this.orderList.length; i++){
                    if(this.orderList[i].completed == false){
                        alert("아직 모든 음료가 준비되지 않았습니다. 음료를 모두 준비해주세요");
                        return;
                    }
                }
                
                try{
                    const payload = {
                        orderNo:this.orderNo,
                        orderList:this.orderList
                    }

                    const response = await fetch(this.competeOrderUrl,{
                        method:'POST',
                        headers:{
                            'Content-Type':'application/json'
                        },
                        body:JSON.stringify(payload)
                    });

                    if(!response.ok)
                        throw new Error("POST Transmission failed while add new category...");

                    else{
                        this.$emit("complete-order", this.orderNo);
                        this.orderList.length = 0;
                        this.processedTable.length = 0;
                        alert("주문 완료");
                    
                    }
                }           // try
                catch(err){
                    console.error("Error sending message : " + err);
                }       // catch

            }

        },

        mounted(){
            document.addEventListener('DOMContentLoaded', this.GetDivSize());
            window.addEventListener('resize',this.TableReSize);

        }

    }

</script>
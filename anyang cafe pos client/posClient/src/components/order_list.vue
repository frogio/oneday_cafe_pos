<template>
    <div class="list-container">
    <div class="total-price">
        {{ totalPrice }} 원
    </div>

    <v-divider></v-divider>
    <div class="scroll-list">
            <v-list lines="one">
                <v-list-item class ="scroll-list-item"
                    v-for="(order, index) in orderItemInfo">
                    <v-card class="prevent-drag item order-item" :key="index" :class="{'switching-background': index % 2 == 1 }">
                            <div class="order-info">
                                {{ order.msg }}
                            <p v-if="order.price == 'coupon'">
                                    쿠폰 사용
                            </p>
                            <p v-else>가격 : {{ order.price }}</p>
                            <p>메모 : {{ order.memo }}</p>
                        
                            </div>
                            <v-btn @click="DeleteOrder(index)">
                                삭제
                            </v-btn>
                    </v-card>
                    <div class="sperator"></div>
                </v-list-item>
            </v-list>
        </div>

    <v-btn class="complete-order" @click="ConfirmOrder" :disabled="orderList.length == 0">
        주문 완료
    </v-btn>
    </div>
    
</template>

<style>

.order-item{
    display:flex;
    justify-content:space-between;
}

.total-price{
    display:flex;
    width:100%;
    height:48px;
    justify-content:center;
    align-items:center;
    background:black;
    color:whitesmoke;
}

.complete-order{
    width:100%;
    height:48px;
    background:black;
    color:whitesmoke;
}

.order-info{
    display:flex;
    flex-direction:column;
}

.list-container{
    display:flex;
    flex-direction:column;
}

</style>

<script>

export default{
    watch:{
        dataSetChanged(changed){
            this.totalPrice = 0;
            this.orderItemInfo = [];
            for(let i = 0; i < this.orderList.length; i++){
                let order = {
                    msg:"",
                    price:0,
                };
                if(this.orderList[i].payOpt.includes('tumblr'))
                    order.msg += "텀블러 ";
                
                if(this.orderList[i].isHot)
                    order.msg += "Hot ";

                else 
                    order.msg += "Ice ";

                order.msg += this.orderList[i].drinkName + " ";
                order.msg += this.orderList[i].cupCount + "잔";
                
                if(this.orderList[i].payOpt.includes('coupon'))
                    order.price = "coupon";
                else
                    order.price = this.orderList[i].price;

                order.memo = this.orderList[i].drinkOpt;

                this.orderItemInfo.push(order);     // emit 되는 object
                this.totalPrice += this.orderList[i].price;
            }
        }

    },
    props:{
        list:{
            required:true,
        },
        dataSetChanged:{
            required:true,
        }
    },
    data(){
        return{
            orderList:this.list,
            orderItemInfo:[],
            totalPrice:0,
        }
    },
    methods:{
        ConfirmOrder(){
            this.$emit("confirm-order", this.orderItemInfo);
        },
        DeleteOrder(index){
            this.$emit("delete-order", index);
        }
    },

}

</script>
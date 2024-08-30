<template>
    <div :style="{height:cardHeight + 'px!important', width:cardWidth +'px!important'}">
        <v-card 
            class="order-card prevent-drag selectable"
            @click="CompletedDrink"
            :style="IsCompleted"
            >
            <p class="msg-font-size">{{ cardMsg }}</p>
            <p class="cupcount-size">{{ orderInfo.cupCount }} 잔</p>
            <p v-if="orderInfo.payOpt.includes('coupon')"
                class="notify-coupon">
                쿠폰 사용
            </p>
            <p class="cupcount-size"> {{ orderInfo.drinkOpt }} </p>
            <div class="selectable prevent-drag cancel-drink">
                <i class="fa-solid fa-xmark" @click.stop="CancelDrink"></i>  
            </div>
    
        </v-card>
    </div>

</template>

<style>

.cancel-drink{
    font-size:2rem;
    display:block;
    position:absolute;
    top:3%;
    left:93%;
}

.selectable {
    cursor: pointer;
    -webkit-user-select: none;
	-webkit-user-drag: none;
}

.prevent-drag{
    -webkit-user-select: none;
	-webkit-user-drag: none;
}

.order-card{
    display:flex;
    width:100%;
    height:100%;
    justify-content:center;
    align-items:center;
    flex-direction:column;
    position:relative;
}

.msg-font-size{
    font-size:2rem;
}

.cupcount-size{
    font-size:1.5rem;
}

.notify-coupon{
    font-size:1.3rem;
}

</style>

<script>

export default{

    watch:{
        width(width){
            this.cardWidth = width;
        },
        height(height){
            this.cardHeight = height;
        }
    },

    props:{
        order:{
            required:true,
        },
        width:{
            required:true,       
        },
        height:{
            required:true,
        },
        index:{
            required:true,
        }
    },
    data(){
        return{
            cardWidth:this.width,
            cardHeight:this.height,
            orderInfo:this.order,
            cardMsg:"",
            drinkIdx:this.index,
        }
    },
    methods:{
        CardMsg(){
            //{ "drinkName": "라떼는 말야!", "cupCount": 1, "isHot": false, "payOpt": [], "price": 2000, "completed": false }
            this.cardMsg += (this.orderInfo.payOpt.includes("tumblr") ? "텀블러 " : "")
                            + ((this.orderInfo.isHot) ? "Hot " : "Ice ")
                            + this.orderInfo.drinkName + " ";
        },
        CompletedDrink(){
            this.orderInfo.completed = !this.orderInfo.completed;
            const orderState = {
                index:this.drinkIdx,
                isCompleted:this.orderInfo.completed
            }

            this.$emit("drink-completed", orderState);
        },
        CancelDrink(){
            if(confirm("음료를 삭제하시겠습니까?"))
                this.$emit("cancel-drink", this.drinkIdx);
        }

    },

    mounted(){
        this.CardMsg();
    },
    computed:{
        IsCompleted(){
            return{
                background: this.orderInfo.completed ? 'yellow' : '' 
            }

        }
        
    }

}

</script>
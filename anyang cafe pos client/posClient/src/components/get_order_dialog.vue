<template>

    <v-dialog v-model="getOrderDialog" max-width="400px" @click:outside="getOrderDialog = false">
       <v-card>
        <v-card-title class="headline">{{ drinkInfo.drinkName }} 추가</v-card-title>
        <v-divider></v-divider>        
        <div class="cup-count">
            <v-btn class="inc-dec-btn circle-btn" @click="CupCount(-1)" :disabled="isCouponSelected">
                -
            </v-btn>
                
            <p v-if="isCouponSelected" class="align">1 잔</p>
            <p v-else class="align">{{ cupCount }} 잔</p>  
                          
            <v-btn class="inc-dec-btn circle-btn" @click="CupCount(1)" :disabled="isCouponSelected">
                +
            </v-btn>
        </div>

        <div class="temperature-div" v-if="drinkInfo.icePrice != 0 && drinkInfo.hotPrice != 0">
            <v-btn @click="SwitchTemperature('ice')" class="temperature-opt ice-btn" :disabled="isHot == false">
                Ice
            </v-btn>

            <v-btn @click="SwitchTemperature('hot')" class="temperature-opt hot-btn" :disabled="isHot">
                Hot
            </v-btn>

        </div>
        <div class="temperature-div" v-if="drinkInfo.icePrice == 0">
            Hot Only 
        </div>
        <div class="temperature-div" v-if="drinkInfo.hotPrice == 0">
            Ice Only
        </div>
        
        <v-card-text class="pay-opt-padding">
            <div class="pay-opt">
                <v-checkbox
                 v-for="option in payOpt"
                 :key="option.value"
                 :label="option.label"
                 :value="option.value"
                 v-model="selectedPayOpt"
                 @change="CheckTumblrOpt"
               ></v-checkbox>  
            </div>
        </v-card-text>

        <div class="drink-opt-div">
            <v-btn class = "drink-opt" @click="SelectDrinkOpt('진하게')"
                :class="{'selected-opt': drinkOpt.includes('진하게') }"
            >진하게</v-btn>
            <v-btn class = "drink-opt" @click="SelectDrinkOpt('연하게')"
                :class="{'selected-opt': drinkOpt.includes('연하게') }"
            >연하게</v-btn>
        </div>

        <div class="drink-opt-div"> 
            <p v-if="drinkOpt.length > 0">{{ selectedOpt }}</p>
            <p v-else>선택된 옵션 없음</p>
        </div>

        <div class="price-div">
            <p v-if="isCouponSelected">0 원</p>
            <p v-else>{{ price }} 원</p>
        </div>
        <!-- 메모 -->

        <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="blue darken-1" text @click="getOrderDialog = false">취소</v-btn>
            <v-btn color="blue darken-1" text @click="SelectDrink">확인</v-btn>
          </v-card-actions>
        </v-card>
    </v-dialog>


</template>

<style>

.selected-opt{
    background:black!important;
    color:whitesmoke!important;
}

.drink-opt-div{
    display:flex;
    justify-content:center;
    margin-bottom:1rem;
    height:2rem;
}

.drink-opt{
    border-radius:95%!important;
    margin:3px;
}

.headline{
    background:black;
    color:whitesmoke;
}

.v-messages{
    min-height:0px!important;
}

.pay-opt-padding:{
    padding: 0!important;
}

.align{
    padding-top:0.5rem;
    font-size:1.0rem;
}

.cup-count{
    display:flex;
    justify-content:center;
    margin-top:1rem;
}

.temperature-div{
    display:flex;
    justify-content:center;
    margin-top:2rem;
    margin-bottom:0.3rem;
}

.temperature-opt{
    margin-left:1rem;
    margin-right:1rem;
}

.pay-opt{
    display:flex;
    justify-content:center;
}

.circle-btn{
    min-width:36px!important;
    border-radius:50%!important;
    padding:0!important;
}

.ice-btn{
    background:#C7F5FC!important;
}

.hot-btn{
    background:#ff9A9A!important;
}

.inc-dec-btn{
    margin-left:1rem;
    margin-right:1rem;
    background:black!important;
    color:whitesmoke!important;
}
.v-input__details{
    min-height:0px!important;
}


.price-div{
    display:flex;
    width:100%;
    justify-content: center;
    font-size:2rem;
}

</style>

<script>

export default{
    watch:{
        itemInfo(item){
            this.cupCount = 1;
            this.isHot = false;
            this.selectedPayOpt = [];

            if(item.itemInfo.icePrice == 0)            // Hot Only 음료일 경우
                this.price = item.itemInfo.hotPrice;
            else if(item.itemInfo.hotPrice == 0)       // ice Only 음료일 경우
                this.price = item.itemInfo.icePrice;   
            else this.price = item.itemInfo.icePrice;   // 기본 select는 ice

            this.drinkOpt.length = 0;
            this.selectedOpt = "";
            this.drinkInfo = item.itemInfo;
            this.getOrderDialog = true;
        },
    },
    props:{
        getOrder:{
            required:true,
        },
        itemInfo:{
            required:true,
        }
    },
    data(){
        return{
            getOrderDialog:false,
            drinkInfo:this.itemInfo,
            
            cupCount:1,
            isHot:false,

            selectedPayOpt:[],
            payOpt: [
            { label: '텀블러', value: 'tumblr' },
            { label: '쿠폰', value: 'coupon' },
            ],
            drinkOpt:[],
            selectedOpt:"",

            price:0
        }
    },
    methods:{
        CupCount(count){
            let _count = this.cupCount + count;
            
            if(_count <= 0)
                return;
            
            this.cupCount = _count;
            this.CalculatePrice();
        },
        SwitchTemperature(temp){
            if(temp == 'ice'){
                this.isHot = false;
                this.CalculatePrice();
            }
            else if(temp == 'hot'){
                this.isHot = true;
                this.CalculatePrice();
            }
        },
        SelectDrink(){

            if(this.selectedPayOpt.includes('coupon')){
                this.cupCount = 1;  
                this.price = 0;
            }

            let orderInfo = {
                drinkName:this.drinkInfo.drinkName,
                cupCount:this.cupCount,
                isHot:this.isHot,
                payOpt:this.selectedPayOpt,
                price:this.price,
                drinkOpt:this.selectedOpt
            }

            this.getOrderDialog = false;
            this.$emit('order-info', orderInfo);
        },
        CheckTumblrOpt(){           // 텀블러 옵션을 선택했을 경우 가격 계산
            this.CalculatePrice();
        },
        CalculatePrice(){
            if(this.selectedPayOpt.includes('tumblr')){
                if(this.isHot)
                    this.price = this.drinkInfo.hotPrice * this.cupCount - 500 * this.cupCount;
                else
                    this.price = this.drinkInfo.icePrice * this.cupCount - 500 * this.cupCount;
            }else{
                if(this.isHot)
                    this.price = this.drinkInfo.hotPrice * this.cupCount;
                else
                    this.price = this.drinkInfo.icePrice * this.cupCount;
            }
        },
        SelectDrinkOpt(opt){
            
            if(this.drinkOpt.includes(opt) == false)
                this.drinkOpt.push(opt);
            else {
                for(let i = 0; i < this.drinkOpt.length; i++){
                    if(this.drinkOpt[i] == opt)
                        this.drinkOpt.splice(i, 1);
                }
            }
            let options = "";
            for(let i = 0; i < this.drinkOpt.length; i++)
                options += this.drinkOpt[i] + " ";
            this.selectedOpt = options;
        }
    },
    computed:{
        isCouponSelected(){
            return this.selectedPayOpt.includes('coupon');
        }
    },
}

</script>


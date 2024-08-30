<template>
    <div
        class="total-price"
    >총 {{ totalPrice }}원</div>

    <div style="height:100vh;" id="record-list" class="record-list">
        <div class="scroll-list" ref="list">

            <v-list lines="one" v-if="recordList != null && recordList.length > 0">
                <v-list-item class ="scroll-list-item"
                    v-for="(record, index) in recordList">
                    <v-card class="prevent-drag item" :key="index" :class="{'switching-background': index % 2 == 1 }">
                        <RecordCard
                            :record = "record"
                        ></RecordCard>
                    </v-card>
                    <div class="sperator"></div>
                </v-list-item>
            </v-list>

            <div
                class="empty-record" v-else>
                해당 날짜의 판매 기록이 존재하지 않습니다.
            </div>
        </div>    
    </div>

</template>

<style>
    .record-list{
        width:100%;
    }

    .empty-record{
        display:flex;
        height:100%;
        justify-content: center;
        align-items: center;
    }
    .total-price{
        display:flex;
        justify-content:center;
        align-items:center;
        height:40px;
    }

</style>

<script>

import RecordCard from './record-card.vue';

export default{
    components:{
        RecordCard,
    },
    data(){
        return{
            recordList:this.record,
            totalPrice:0
        }
    },
    watch:{
        record(rec){
            this.recordList = rec;
            this.GetTotalPrice();
        }
    },
    props:{
        record:{
            required:true,
        }
    },
    methods:{
        GetTotalPrice(){
                this.totalPrice = 0;
                for(let i = 0; i < this.recordList.length; i++){
                    if(this.recordList[i].price == "-1")
                        continue;
                    else
                        this.totalPrice += parseInt(this.recordList[i].price);
                }
            },

        GetDivSize(){

            setTimeout(() => {              // 리스트가 렌더링이 다 될 떄까지 1초간 기다린다.
                const recordDivSize = document.getElementById("record-list")?.getBoundingClientRect();
                if(recordDivSize != null){
                    const drinkList = this.$refs.list;
                    if(drinkList != null)
                        drinkList.style.cssText = "height:" + (recordDivSize.height - recordDivSize.y) + "px!important";
                }
            }, 300);

        },

        ListReSize(){

            const recordDivSize = document.getElementById("record-list")?.getBoundingClientRect();
            if(recordDivSize != null){
                const drinkList = this.$refs.list;
                // 부모 Div로 부터 List가 가질 사이즈를 계산한다.
                if(drinkList != null)
                    drinkList.style.cssText = "height:" + (recordDivSize.height - recordDivSize.y) + "px!important";
            }

        },
    },
    mounted(){
        document.addEventListener('DOMContentLoaded', this.GetDivSize());
        window.addEventListener('resize',this.ListReSize);

    }



}


</script>
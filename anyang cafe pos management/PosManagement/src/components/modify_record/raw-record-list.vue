<template>
    <div style="height:100vh;" id="raw-record-list" >
        <div class="scroll-list" ref="list">
        <v-list lines="one">
            <v-list-item class ="scroll-list-item"
                v-for="(rcd, index) in records">
                <v-card class="prevent-drag item" :key="index" :class="{'switching-background': index % 2 == 1 }">
                    <RawRecordCard
                        :record="rcd"
                        @delete-record="DeleteRecord"
                    >
                    </RawRecordCard>              
                </v-card>
                <div class="sperator"></div>
            </v-list-item>
        </v-list>
        </div>
    </div>

</template>

<script>
    import RawRecordCard from './raw-record-card.vue';


export default{
    components:{
        RawRecordCard,
    },
    props:{
        record:{
            required:true,
        }
    },
    watch:{
        record(rec){
            this.records = rec;
        }
    },
    data(){
        return{

            records:this.record,
        }
    },
    methods:{
        GetDivSize(){

            setTimeout(() => {              // 리스트가 렌더링이 다 될 떄까지 1초간 기다린다.
                const recordDivSize = document.getElementById("raw-record-list")?.getBoundingClientRect();
                if(recordDivSize != null){
                    const drinkList = this.$refs.list;
                    if(drinkList != null)
                        drinkList.style.cssText = "height:" + (recordDivSize.height - recordDivSize.y) + "px!important";
                }
            }, 300);

        },

        ListReSize(){

            const recordDivSize = document.getElementById("raw-record-list")?.getBoundingClientRect();
            if(recordDivSize != null){
                const drinkList = this.$refs.list;
                // 부모 Div로 부터 List가 가질 사이즈를 계산한다.
                if(drinkList != null)
                    drinkList.style.cssText = "height:" + (recordDivSize.height - recordDivSize.y) + "px!important";
            }

        },
        DeleteRecord(saleID){
            this.$emit('delete-record', saleID);
        }
        
    },
    mounted(){
        

        document.addEventListener('DOMContentLoaded', this.GetDivSize());
        window.addEventListener('resize',this.ListReSize);
        


    }


}

</script>
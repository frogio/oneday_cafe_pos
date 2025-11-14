<template>
    <div class="record-table">
        <div class="date-picker">
            <v-row class="date-align" dense>
                <v-col cols="12" md="3">
                    <v-date-input
                        label="시작일 선택"
                        prepend-icon=""
                        prepend-inner-icon="$calendar"
                        variant="solo"
                        v-model="startDate"
                        ></v-date-input>
                </v-col>
                <v-col cols="12" md="3">
                    <v-date-input
                        label="종료일 선택"
                        prepend-icon=""
                        prepend-inner-icon="$calendar"
                        variant="solo"
                        v-model="endDate"
                    ></v-date-input>
                        <!--  -->
                </v-col>
                <v-btn @click="GetRecord">
                    조회
                </v-btn>
            </v-row>
        </div>
        {{ testDate }}
        <div class="record">
            <RecordList
                :record="records">
            </RecordList>
        </div>
    </div>


</template>

<style>
    .date-picker{
        display:flex;
        flex-grow: 1;
    }
    .date-align{
        align-items: center;
        justify-content: center;
    }

    .record-table{
        width:100%;
        height:100%;
        display:flex;
        flex-direction:column;
        flex-grow: 3;
    }

    .record{
        width:100%;
    }

</style>

<script>

import RecordList from '../components/sale_record/record-list.vue';

    export default{
        components:{
            RecordList,
        },
        data(){
            return{
                saleRecordURL:"http://" + this.$store.state.IPAndPort + "/getSaleRecord",
                startDate:new Date(),
                endDate:new Date(),
                records:null,
                testDate:"",
            }

        },
        methods:{
            async GetRecord(){
                if(this.startDate.getTime() > this.endDate.getTime()){
                    alert("잘못된 날짜 범위입니다!");
                    return;
                }

                const startDate = this.customDateFormat(this.startDate);
                const endDate = this.customDateFormat(this.endDate);
                
                try{
                    const payload = {
                        startDate:startDate,
                        endDate:endDate
                    }

                    const response = await fetch(this.saleRecordURL,{
                        method:'POST',
                        headers:{
                            'Content-Type':'application/json'
                        },
                        body:JSON.stringify(payload)
                    });

                    if(!response.ok)
                        throw new Error("POST Transmission failed while add new category...");
                    else
                        this.records = await response.json();
                    }           // try
                catch(err){
                    console.error("Error sending message : " + err);
                }       // catch
                
            },


            customDateFormat(date){
                if (!date) return '';
                
                // Date 객체를 받아서 원하는 문자열로 변환
                const year = date.getFullYear();
                const month = String(date.getMonth() + 1).padStart(2, '0');
                const day = String(date.getDate()).padStart(2, '0');
                
                return `${year}-${month}-${day}`;
            }

        },
        mounted(){
            this.GetRecord();
        },


    }

</script>

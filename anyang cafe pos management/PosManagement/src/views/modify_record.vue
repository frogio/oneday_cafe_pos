<template>

    <div
        class="record-table">
        <v-date-picker
            class = "date-picker"
            v-model="date">
        </v-date-picker>
        <div
            class="record">
            <RawRecordList
                :key="renderAgain"
                :record="records"
                @delete-record="DeleteRecord">
            </RawRecordList>
        </div>
    </div>
</template>

<style>

</style>

<script>

import { render } from 'vue';
import RawRecordList from '../components/modify_record/raw-record-list.vue'

export default{
    components:{
        RawRecordList,
    },
    data(){
        return{
            date:new Date(),
            deleteRecordURL:"http://" + this.$store.state.IPAndPort + "/deleteRecord",
            rawRecordURL:"http://" + this.$store.state.IPAndPort + "/getRawSaleRecord",
            renderAgain:false,
            selectedDate:"",
            records:[],
        }
    },
    methods:{
        async GetRecord(){
            try{
                
                const payload = {
                    date:this.selectedDate,
                }
                const response = await fetch(this.rawRecordURL,{
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
                    for(let i = 0; i < this.records.length; i++){
                        //console.log(this.records[i]);
                        if(this.records[i].payOption.includes("tumblr"))
                            this.records[i].drinkName = "텀블러 " + this.records[i].drinkName;
                    }
                    this.records.sort((a, b) => {
                        return a.drinkName.localeCompare(b.drinkName);
                    });
                    this.renderAgain = !this.renderAgain;

                    //console.log(this.records);
                }           // try
                
                catch(err){
                    console.error("Error sending message : " + err);
                }       // catch
                
        },
        async DeleteRecord(saleID){
            if(confirm("판매 기록을 삭제하시겠습니까?")){
                try{
                const payload = {
                    saleID:saleID
                };
                
                const response = await fetch(this.deleteRecordURL,{
                    method:'POST',
                    headers:{
                        'Content-Type':'application/json'
                    },
                    body:JSON.stringify(payload)
                });
                
                if(!response.ok){
                    throw new Error("POST Transmission failed while add new category...");
                }else
                    this.records = this.records.filter(element => element.saleID != saleID);
                    this.renderAgain = !this.renderAgain;
                }   // try
                catch(err){
                    console.error("Error sending message : " + err);
                } // catch
                

            }

        }


    },
    watch:{
            date(date){
                let month = "" + (date.getMonth() + 1);

                if(month.length < 2)
                    month = "0" + (date.getMonth() + 1);
                
                let _date = "" + date.getDate();

                if(_date.length < 2)
                    _date = "0" + date.getDate();

                this.selectedDate = date.getFullYear() + '-' + month + '-' + _date;
                this.GetRecord();
        },

    },

    mounted(){
        let month = "" + (this.date.getMonth() + 1);
        
        if(month.length < 2)
            month = "0" + (this.date.getMonth() + 1);

        let date = "" + this.date.getDate();

        if(date.length < 2)
            date = "0" + this.date.getDate();

        this.selectedDate = this.date.getFullYear() + '-' + month + '-' + date;
        this.GetRecord();
    },


}


</script>
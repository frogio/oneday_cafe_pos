<template>
    <div
        class="record-table">
        <v-date-picker
            class = "date-picker"
            v-model="date">
        </v-date-picker>
        <div
            class="record">
            <RecordList
                :record="records"
            >
            </RecordList>
        </div>
    </div>


</template>

<style>
    .record-table{
        width:130%;
        height:100%;
        display:flex;
    }

    .date-picker{
        width:35%;
    }
    .record{
        width:65%;
    }

</style>

<script>

import RecordList from '../components/sale_record/record-list.vue';

    export default{
        components:{
            RecordList,
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
        data(){
            return{
                saleRecordURL:"http://" + this.$store.state.IPAndPort + "/getSaleRecord",
                date:new Date(),
                selectedDate:"",
                records:null,
            }

        },
        methods:{
            async GetRecord(){    
                try{
                    const payload = {
                        date:this.selectedDate,
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

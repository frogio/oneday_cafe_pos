<template>
    <div class="card">
        <div class="is-hot align">
            {{ isHot }}
        </div>
        <div class="drink-name align">
            {{ drinkName }}
        </div>
        <div class="count align">
            {{ count }}
        </div>
        <div class="price align">
            {{ price }}
        </div>
        <div class="pay-type align">
            {{ payType }}
        </div>
        <div class="option align">
            {{ option }}
        </div>


        <p class="delete-rename-btn prevent-drag selectable" @click="DeleteDrink()">삭제</p>
    </div>
</template>

<style>

.card{
    display:flex;
    justify-content:center;
}

.is-hot{
    width:30px;
}

.drink-name{
    width:500px;
}

.count{
    width:15px;
}

.price{
    width:100px;
}

.align{
    display:flex;
    justify-content: center;
}

</style>

<script>

export default{

    props:{
        record:{
            required:true,
        }
    },
    data(){
        return{
            saleID:this.record.saleID,
            isHot:(this.record.isHot == 0) ? "ICE" : "HOT",
            drinkName:`${(this.record.payOption.includes("tumblr") ? "텀블러 " : "") 
                    + this.record.drinkName}`,

            count:this.record.count,
            price:(this.record.price == "-1") ? "쿠폰" : this.record.price,
            option: `${(this.record.payOption.includes("syrup") ? "시럽 " : "") 
                    + (this.record.payOption.includes("shot") ? "샷 " : "")
                    + (this.record.payOption.includes("shot") || this.record.payOption.includes("syrup") ? "추가" : "") }`, 


            payType:(this.record.payType == 0) ? "현금" : "계좌이체"

        }
    },
    methods:{
        DeleteDrink(){
            this.$emit('delete-record', this.record.saleID);
        }
    }
    

}

</script>
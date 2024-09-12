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
            drinkName:this.record.drinkName,
            count:this.record.count,
            price:(this.record.price == "-1") ? "쿠폰" : this.record.price,
        }
    },
    methods:{
        DeleteDrink(){
            this.$emit('delete-record', this.record.saleID);
        }
    }

}

</script>
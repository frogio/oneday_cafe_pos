<template>
    <v-card class="drink prevent-drag selectable">
        
        <p class="drink-name">{{ item.drinkName }}</p> 
        <div v-if="item.icePrice != 0 && item.hotPrice != 0">
            <p class="price-ice">Ice {{ item.icePrice }}</p>
            <p class="price-hot">Hot {{ item.hotPrice }}</p>
        </div>
        
        <div v-if="item.icePrice == 0">
            <p> Hot Only {{ item.hotPrice }}</p>
        </div>

        <div v-if="item.hotPrice == 0">
            <p> Ice Only {{ item.icePrice }}</p>
        </div>
    </v-card>

</template>

<style>

.drink{
    display:flex;
    height:100%;
    justify-content: center;
    align-items: center;
    flex-direction: column;
    margin:5px;
    background-color: #f0f0f0;
}

.drink-name{
    font-size:1.2rem;
}

.price-ice{
    font-size:1rem;
}

.price-hot{
    font-size:1rem;
}

</style>

<script>

export default{

    props:{
        item:{
            required:true,
        }

    },
    data(){
        return{
            itemInfo:null,
        }
    },

}


</script>


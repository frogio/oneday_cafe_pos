<template>
    
    <div v-if="categoryInfo != null" class="scroll-list">
        <table class="drink-table">
            <tr v-for=" row in drinkTable" class="drink-row">
                <td v-for= " drink in row" class="drink-item">
                    <div 
                        v-if="drink != null"
                            :style="{height:itemSize + 'px!important', width:itemSize +'px!important'}">
                        <DrinkItem
                            :item="drink"
                            @click="GetDrink(drink)">
                        </DrinkItem>
                    </div>
                </td>
            </tr>
        </table>
    </div>
</template>
<style>
    .drink-table{
        width:100%;
    }
    
    .drink-row{
        width:100%;
    }

    .drink-item{
        width:33%;
    }

</style>

<script>

import DrinkItem from './drink_item.vue'

export default{
    components:{
        DrinkItem,
    },
    watch:{
        cateInfo(info){
            this.categoryInfo = info;
            //console.log("categoryInfo : "+ this.categoryInfo);
            
            this.drinkTable = [];
            for(let i = 0; i < this.categoryInfo.drinkByCategory.length / 3; i++){
                this.drinkTable.push([]);

                for(let k = 0; k < 3; k++)
                    this.drinkTable[i].push(this.categoryInfo.drinkByCategory[3 * i + k]);

            }
            //console.log(this.drinkTable);
        },
        boxSize(size){
            this.itemSize = size;
            //console.log("itemSize : " + this.itemSize);
        }
    },

    props:{
        cateInfo:{
            required:true,
        },
        boxSize:{
            required:true,
        }
    },
    data(){
        return{
            allDrinkURL:"http://" + this.$store.state.IPAndPort + "/getAllDrinks",
            categoryInfo:this.cateInfo,
            drinkTable:[],
            itemSize:0,
        }
    },
    methods:{
        async GetAllDrinks(){           // 모든 음료들을 가져옴
          const promise = await fetch(this.allDrinkURL);
          this.categoryInfo = await promise.json();
        },
        GetDrink(drink){
            this.$emit("get-drink", drink);
        }
    },
    async created(){
        await this.GetAllDrinks();
    }

}

</script>
<template>
    <v-list lines="one">
        <v-list-item class ="scroll-list-item"
            v-for="(category, index) in categoryList">
            <v-card @click="GetDrinksByCategory(category.categoryName)" class="prevent-drag item" :key="index" :class="{'switching-background': index % 2 == 1 }">
                <p>{{ category.categoryName }}</p>
            </v-card>
            <div class="sperator"></div>
        </v-list-item> 
    </v-list>
</template>

<script>

export default{
    props:{
        category:{
            required:true,
        }
    },

    data(){
        return {
            categoryURL:"http://" + this.$store.state.IPAndPort + "/getCategoryName/",
            getDrinkByCategoryURL:"http://" + this.$store.state.IPAndPort + "/getDrinksByCategory?categoryName=_categoryName",

            categoryList:[],
            curCategoryDrink:[],
            curCategory:"",
        }
        
    },
    methods:{
        
        async GetCategoryList(){       // 카테고리 목록을 가져옴
            const promise = await fetch(this.categoryURL);
            this.categoryList = await promise.json();
        },

        async GetDrinksByCategory(name){    // 선택된 카테고리의 음료를 가져옴

            this.curCategory = name;
            let url = this.getDrinkByCategoryURL;
            url = url.replace("_categoryName", this.curCategory);

            const promise = await fetch(url);
            this.curCategoryDrink = await promise.json();
            
            let emitCategoryDrink = {
                categoryName:this.curCategory,
                drinkByCategory:this.curCategoryDrink
            }
            this.$emit('cur-category-drink', emitCategoryDrink);
        },
    },
    mounted(){
        this.GetCategoryList();
    }

};

</script>

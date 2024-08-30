<template>
    <div class="manage-menu">
        <categoryList :category="categoryNames"
                    @cur-category-drink="SelectCategory"
                    @get-category-list="GetCategoryName"
                    @delete-category="GetAllDrinks"
                    class="category-list">
                    </categoryList>
        <drinkList :drinks="drinkList.drinkByCategory"
                    @popup-add-dialog="PopupAddDialog"
                    @popup-update-dialog="PopupUpdateDialog" 
                    class="drink-list"></drinkList>
    </div>
    <AddDrinkDialog 
            :addDialog="addDrinkDialog"
            :categories="categoryName"
            @add-drink="AddDrink"
            >
    </AddDrinkDialog>

    <UpdateDrinkDialog 
            :updateDialog="updateDrinkDialog"
            :prevDrinkInfo="drinkInfo"
            :categories="categoryName"
            @update-drink="UpdateDrink"
            >
    </UpdateDrinkDialog>

</template>

<script>
import categoryList from "../components/menu_management/category_list.vue";
import drinkList from "../components/menu_management/drink_list.vue"
import AddDrinkDialog from '../components/menu_management/add_drink_dialog.vue';
import UpdateDrinkDialog from '../components/menu_management/update_drink_dialog.vue'

export default{
    components:{
        UpdateDrinkDialog,
        AddDrinkDialog,
        categoryList,
        drinkList,
    },

    data(){
        return{
            getDrinkByCategoryURL:"http://" + this.$store.state.IPAndPort + "/getDrinksByCategory?categoryName=_categoryName",
            allDrinkURL:"http://" + this.$store.state.IPAndPort + "/getAllDrinks",
            addDrinkDialog:true,
            updateDrinkDialog:true,
            drinkInfo:null,
            
            drinkList:[],
            categoryName:[],
        }
    },
    methods:{
        async GetDrinksByCategory(name){    // 선택된 카테고리의 음료를 가져옴

            let url = this.getDrinkByCategoryURL;
            url = url.replace("_categoryName", name);

            const promise = await fetch(url);
            this.drinkList.categoryName = name;
            this.drinkList.drinkByCategory = await promise.json();

        },

        SelectCategory(categoryDrink){
            this.drinkList = categoryDrink; 
        },

        async GetAllDrinks(){           // 모든 음료들을 가져옴
            const promise = await fetch(this.allDrinkURL);
            this.drinkList.drinkByCategory = await promise.json();
        }, // AddCategory

        PopupAddDialog(){
            this.addDrinkDialog = !this.addDrinkDialog;
        },

        PopupUpdateDialog(drink){
            this.updateDrinkDialog = !this.updateDrinkDialog;
            this.drinkInfo = drink;
        },

        GetCategoryName(categoryList){

            this.categoryName = [];
            for(let i = 0; i < categoryList.length; i++)
                this.categoryName.push(categoryList[i].categoryName);
        
        },
        
        AddDrink(drink){
            this.GetDrinksByCategory(drink.categoryName);
        },

        UpdateDrink(drink){
            this.GetDrinksByCategory(drink.categoryName);
        }
 

    },

    mounted(){
        this.GetAllDrinks();
    }


};

</script>
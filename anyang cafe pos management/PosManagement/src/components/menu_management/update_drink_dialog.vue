<template>

    <v-dialog v-model="updateDrinkDialog" max-width="700px" @click:outside="updateDrinkDialog = false">
       <v-card>
        <v-card-title class="headline">음료 추가</v-card-title>
        <v-text-field label="음료 이름"
            v-model="newDrinkName"
        ></v-text-field>
        
        <div class="price-div">
            
            <v-text-field label="Ice 가격"
                v-model="icePrice"
            ></v-text-field>

            <v-text-field label="Hot 가격"
                v-model="hotPrice"
            ></v-text-field>
        </div>

        <v-select
            v-model="selectedCategory"
            :items="categoryName"
            item-text="categoryName"
            item-value="cateID"
            label="카테고리 선택"
        >   
        </v-select>
  
        <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="blue darken-1" text @click="updateDrinkDialog = false">취소</v-btn>
            <v-btn color="blue darken-1" text @click="UpdateDrink">저장</v-btn>
          </v-card-actions>
        </v-card>
    </v-dialog>


</template>

<style>

.price-div{
    display:flex;
}

</style>

<script>

export default{
    watch:{
        updateDialog(dialog){
            this.updateDrinkDialog = true;
        },
        prevDrinkInfo(drink){
            
            this.prevDrinkName = drink.drinkName; 
            this.newDrinkName = drink.drinkName;
            this.icePrice = drink.icePrice;
            this.hotPrice = drink.hotPrice;
            this.selectedCategory = drink.categoryName;
        
        },
        categories(names){
            this.categoryName = names;
        }
    },
    props:{
        updateDialog:{
            required:true,
        },
        prevDrinkInfo:{
            required:true,
        },
        categories:{
            required:true,
        }
    },
    data(){
        return{
            updateDrinkDialog:false,
            updateDrinkURL:"http://" + this.$store.state.IPAndPort + "/updateDrink",
            categoryName:[],

            prevDrinkName:"",
            newDrinkName:"",
            icePrice:"",
            hotPrice:"",
            selectedCategory:null,
        }

    },
    methods:{
        async UpdateDrink(){
            try{
                const payload = {
                    prevName: this.prevDrinkName,
                    newName: this.newDrinkName,
                    icePrice: parseInt(this.icePrice),
                    hotPrice: parseInt(this.hotPrice),
                    categoryName:this.selectedCategory
                }

                if(this.selectedCategory == null){
                    alert("음료의 카테고리를 선택해 주세요");
                    return;
                }

                const response = await fetch(this.updateDrinkURL,{
                    method:'POST',
                    headers:{
                        'Content-Type':'application/json'
                    },
                    body:JSON.stringify(payload)
                })
                if(!response.ok)
                    throw new Error("POST Transmission failed while add new category...");
                                
                else{
                    this.$emit("update-drink", payload);
                    this.updateDrinkDialog = false;
                }
            }           // try
            catch(err){
                console.error("Error sending message : " + err);
            }       // catch
        },
    },
}

</script>


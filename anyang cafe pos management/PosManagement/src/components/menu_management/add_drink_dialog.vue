<template>

    <v-dialog v-model="addDrinkDialog" max-width="700px" @click:outside="addDrinkDialog = false">
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
            <v-btn color="blue darken-1" text @click="addDrinkDialog = false">취소</v-btn>
            <v-btn color="blue darken-1" text @click="AddDrinkToCategory">저장</v-btn>
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
        addDialog(dialog){
            this.addDrinkDialog = true;
        },
        categories(names){
            this.categoryName = names;
        }
    },
    props:{
        addDialog:{
            required:true,
        },
        categories:{
            required:true,
        }
    },
    data(){
        return{
            addDrinkDialog:false,
            addDrinkURL:"http://" + this.$store.state.IPAndPort + "/addDrink",
            categoryName:[],

            newDrinkName:"",
            icePrice:"",
            hotPrice:"",
            selectedCategory:null,
        }

    },
    methods:{
        async AddDrinkToCategory(){

            try{
                const payload = {
                    drinkName: this.newDrinkName,
                    icePrice: parseInt(this.icePrice),
                    hotPrice: parseInt(this.hotPrice),
                    categoryName:this.selectedCategory
                }

                const response = await fetch(this.addDrinkURL,{
                    method:'POST',
                    headers:{
                        'Content-Type':'application/json'
                    },
                    body:JSON.stringify(payload)
                });
                
                if(!response.ok)
                    throw new Error("POST Transmission failed while add new category...");
                
                else if((await response.text()).toString() == "drinkName was duplicated!"){
                    alert("음료 이름이 중복되었습니다.");
                    return;
                }
                
                else{
                    this.$emit("add-drink", payload);
                    this.addDrinkDialog = false;
                }
            }           // try
            catch(err){
                console.error("Error sending message : " + err);
            }       // catch
        },
    },

}

</script>


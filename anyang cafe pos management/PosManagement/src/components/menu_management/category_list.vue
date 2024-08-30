<template>
    <div style="height:100vh;" id="category-list" >
        <div class="scroll-list" ref="list">
        <v-dialog v-model="addCategoryDialog" max-width="500px" @click:outside="addCategoryDialog = false">
            <v-card>
             <v-card-title class="headline">카테고리 추가</v-card-title>
             <v-text-field label="카테고리 이름"
                 v-model="newCategoryName"
             ></v-text-field>
            
             <v-card-actions>
                 <v-spacer></v-spacer>
                 <v-btn color="blue darken-1" text @click="addCategoryDialog = false">취소</v-btn>
                 <v-btn color="blue darken-1" text @click="AddCategory">저장</v-btn>
               </v-card-actions>
             </v-card>
         </v-dialog>
     
         <v-dialog v-model="renameCategoryDialog" max-width="500px" @click:outside="renameCategoryDialog = false">
            <v-card>
             <v-card-title class="headline">카테고리 변경</v-card-title>
             <v-text-field label="새 카테고리 이름"
                 v-model="newCategoryName"
             ></v-text-field>
            
             <v-card-actions>
                 <v-spacer></v-spacer>
                 <v-btn color="blue darken-1" text @click="renameCategoryDialog = false">취소</v-btn>
                 <v-btn color="blue darken-1" text @click="RenameCategoryFetch">저장</v-btn>
               </v-card-actions>
             </v-card>
         </v-dialog>

        <v-list lines="one">
            <v-list-item class ="scroll-list-item"
                v-for="(category, index) in categoryList">
                <v-card @click="GetDrinksByCategory(category.categoryName)" class="prevent-drag item" :key="index" :class="{'switching-background': index % 2 == 1 }">
                    <div class="item-btn-div">
                            <p>{{ category.categoryName }}</p>
                        <div class="item-btn-div">
                            <p class="delete-rename-btn" @click.stop="DeleteCategory(category)">삭제</p>&nbsp
                            <p class="delete-rename-btn" @click.stop="RenameCategory(category)">변경</p>
                        </div>
                    </div>
                </v-card>
                <div class="sperator"></div>
            </v-list-item>
            <v-btn class="add-category-btn" @click="addCategoryDialog = true">
                카테고리 추가
            </v-btn>    
        </v-list>
        </div>
    </div>

 

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
            addCategoryURL:"http://" + this.$store.state.IPAndPort + "/addCategoryName",
            deleteCategoryURL:"http://" + this.$store.state.IPAndPort + "/deleteCategoryName",
            deleteDrinkByCategoryURL:"http://" + this.$store.state.IPAndPort + "/deleteDrinkByCategory",
            renameCategoryURL:"http://" + this.$store.state.IPAndPort + "/renameCategoryName",
            // category CRUD

            getDrinkByCategoryURL:"http://" + this.$store.state.IPAndPort + "/getDrinksByCategory?categoryName=_categoryName",


            categoryList:[],
            curCategoryDrink:[],
            curCategory:"",

            addCategoryDialog:false,
            renameCategoryDialog:false,
            prevCategoryName:"",
            newCategoryName:"",
        }
        
    },
    methods:{
        
        async GetCategoryList(){       // 카테고리 목록을 가져옴
            const promise = await fetch(this.categoryURL);
            this.categoryList = await promise.json();
            this.$emit("get-category-list", this.categoryList);
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

        async DeleteCategory(category){ // 카테고리 제거
            
            await this.GetDrinksByCategory(category.categoryName);
            // fetch간 동기를 맞추기 위해 await keyword를 사용한다.
            // 먼저 선택한 카테고리에 등록된 음료들을 받고

            //console.log("Drinks by Category : " + this.curCategoryDrinks);      // ----- 2
            if(this.curCategoryDrink.length > 0 ){      // 선택된 카테고리에 하나라도 음료가 등록되어 있을 경우.
                if(confirm("카테고리에 등록된 음료가 있습니다. 음료와 카테고리 전부 삭제하시겠습니까?")){
                    try{
                        const payload = {
                            cateID:category.cateID,
                            categoryName:category.categoryName,
                        };
                        const response = await fetch(this.deleteDrinkByCategoryURL,{
                            method:'POST',
                            headers:{
                                'Content-Type':'application/json'
                            },
                            body:JSON.stringify(payload)
                        });

                        if(!response.ok)
                            throw new Error("POST Transmission failed while delete category...");
                        else{   
                            this.GetCategoryList();
                            this.$emit("delete-category");
                        }

                        }  // try
                    catch(err){
                        console.error("Error sending message : " + err);
                    } // catch


                }   // if confirm
            }       // if

            else if(confirm(category.categoryName + " 카테고리를 삭제하시겠습니까?")){   // 카테고리에 하나의 음료도 등록되어 있지 않을 경우

                    try{
                        const payload = {
                            categoryName:category.categoryName
                        };

                        const response = await fetch(this.deleteCategoryURL,{
                            method:'POST',
                            headers:{
                                'Content-Type':'application/json'
                            },
                            body:JSON.stringify(payload)
                        });

                        if(!response.ok)
                            throw new Error("POST Transmission failed while delete category...");
                        else {
                            //this.categoryList = this.categoryList.filter(name => name.categoryName != category.categoryName);
                            this.GetCategoryList();
                            this.$emit("delete-category");
                        } 
                    }           // try
                    catch(err){
                        console.error("Error sending message : " + err);
                    }       // catch
            
                }       // else if
                       
        },   // Delete Category
        async AddCategory(){            // 카테고리 추가
            this.addCategoryDialog = false;

            for(let i = 0; i < this.categoryList.length; i++){
                if(this.categoryList[i].categoryName == this.newCategoryName){
                    alert("중복된 카테고리명입니다.");
                    return;
                }
            }       // 카테고리명 중복 체크

            this.categoryList.push(
                { 
                    cateID:'dummy',
                    categoryName:this.newCategoryName
                });

            try{
                const payload = {
                    categoryName:this.newCategoryName
                };
                
                const response = await fetch(this.addCategoryURL,{
                    method:'POST',
                    headers:{
                        'Content-Type':'application/json'
                    },
                    body:JSON.stringify(payload)
                });
                
                if(!response.ok){
                    throw new Error("POST Transmission failed while add new category...");
                }else{
                    this.GetCategoryList();
                    this.GetDrinksByCategory(this.newCategoryName);
                    // 업데이트 된 카테고리를 서버로부터 새로 받아온다.
                } 
            }   // try
            catch(err){
                console.error("Error sending message : " + err);
            } // catch

        },
        RenameCategory(category){
                this.prevCategoryName = category.categoryName; 
                this.newCategoryName = category.categoryName;
                this.renameCategoryDialog = true;
        },
        async RenameCategoryFetch(){
            for(let i = 0; i < this.categoryList.length; i++){
                if(this.newCategoryName == this.categoryList[i].categoryName){
                    alert("동일한 이름의 카테고리가 존재합니다.");
                    return;
                }
            }
            try{
                    const payload = {
                        newName:this.newCategoryName,
                        prevName:this.prevCategoryName,
                    };
                    const response = await fetch(this.renameCategoryURL,{
                        method:'POST',
                        headers:{
                            'Content-Type':'application/json'
                        },
                        body:JSON.stringify(payload)
                    });
                    if(!response.ok)
                        throw new Error("POST Transmission failed while add new category...");
                    else {
                        // 업데이트 성공 후
                        this.GetCategoryList();
                        this.GetDrinksByCategory(this.newCategoryName);
                        // 카테고리를 다시 불러오고 다이얼로그를 닫음
                        this.renameCategoryDialog = false;
                    }
                }           // try
                catch(err){
                    console.error("Error sending message : " + err);
                }       // catch
        },

        GetDivSize(){

            setTimeout(() => {              // 리스트가 렌더링이 다 될 떄까지 1초간 기다린다.
                const listDivSize = document.getElementById("category-list")?.getBoundingClientRect();
                if(listDivSize != null){
                    const categoryList = this.$refs.list;
                    if(categoryList != null)
                        categoryList.style.cssText = "height:" + (listDivSize.height - listDivSize.y) + "px!important";
                }
            }, 300);

        },

        ListReSize(){

            const listDivSize = document.getElementById("category-list")?.getBoundingClientRect();
            if(listDivSize != null){
                const categoryList = this.$refs.list;
                // 부모 Div로 부터 List가 가질 사이즈를 계산한다.
                if(categoryList != null)
                    categoryList.style.cssText = "height:" + (listDivSize.height - listDivSize.y) + "px!important";
            }

        },

    },
    mounted(){
        this.GetCategoryList();
        document.addEventListener('DOMContentLoaded', this.GetDivSize());
        window.addEventListener('resize',this.ListReSize);
    }

};

</script>

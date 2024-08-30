<template>

    <div style="height:100vh;" id="drink-list" >
        <div class="scroll-list" ref="list">
            <v-btn class="add-drink-btn" @click="PopupAddDialog">
                    음료 추가
            </v-btn>    
            <v-list lines="one">
                <v-list-item class ="scroll-list-item"
                    v-for="(drink, index) in drinkList">
                    <v-card class="prevent-drag item" :key="index" :class="{'switching-background': index % 2 == 1 }">
                        <div class="item-btn-div">
                            <div>
                                <p>{{ drink.drinkName }}</p></br>
                                <p class="price-hot">Hot  {{ drink.hotPrice }}</p>
                                <p class="price-ice">Ice  {{ drink.icePrice }}</p></br>
                            </div>
                            <div class="item-btn-div">
                                    <p class="delete-rename-btn prevent-drag selectable" @click="DeleteDrink(drink)">삭제</p>&nbsp
                                    <p class="delete-rename-btn prevent-drag selectable" @click="UpdateDrink(drink)">변경</p>
                            </div>
                        </div>
                    </v-card>
                    <div class="sperator"></div>
                </v-list-item>
                    
            </v-list>
        </div>    
    </div>

</template>

<style>

.drink-list{
    min-height:0px!important;
}

.price-hot{
    font-size:0.8rem;
    /*color:red;*/
}

.price-ice{
    font-size:0.8rem;
    /*color:skyblue;*/
}

</style>

<script>
export default{
    props:{
        drinks:{
            required:true
        }
    },
    watch:{
        drinks(d){
            this.drinkList = d;
        }

    },
    data(){
        return{
            deleteDrinkURL:"http://" + this.$store.state.IPAndPort + "/deleteDrink",
            drinkList:this.drinks,
        }
    },
    methods:{
        
        GetDivSize(){

            setTimeout(() => {              // 리스트가 렌더링이 다 될 떄까지 1초간 기다린다.
                const listDivSize = document.getElementById("drink-list")?.getBoundingClientRect();
                if(listDivSize != null){
                    const drinkList = this.$refs.list;
                    if(drinkList != null)
                        drinkList.style.cssText = "height:" + (listDivSize.height - listDivSize.y) + "px!important";
                }
            }, 300);

        },

        ListReSize(){

            const listDivSize = document.getElementById("drink-list")?.getBoundingClientRect();
            if(listDivSize != null){
                const drinkList = this.$refs.list;
                // 부모 Div로 부터 List가 가질 사이즈를 계산한다.
                if(drinkList != null)
                    drinkList.style.cssText = "height:" + (listDivSize.height - listDivSize.y) + "px!important";
            }

        },
        async DeleteDrink(drink){
            if(confirm("음료를 삭제하시겠습니까?")){
                try{
                const payload = {
                    drinkName:drink.drinkName
                };
                
                const response = await fetch(this.deleteDrinkURL,{
                    method:'POST',
                    headers:{
                        'Content-Type':'application/json'
                    },
                    body:JSON.stringify(payload)
                });
                
                if(!response.ok){
                    throw new Error("POST Transmission failed while add new category...");
                }else
                    this.drinkList = this.drinkList.filter(name => name.drinkName != drink.drinkName);
                }   // try
                catch(err){
                    console.error("Error sending message : " + err);
                } // catch
                

            }
        },
        UpdateDrink(drink){
            this.$emit("popup-update-dialog", drink);
        },

        PopupAddDialog(){
            this.$emit('popup-add-dialog');
        }

    },
    mounted(){

        document.addEventListener('DOMContentLoaded', this.GetDivSize());
        window.addEventListener('resize',this.ListReSize);

    }



}


</script>
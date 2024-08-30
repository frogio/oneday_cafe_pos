const express = require('express');
const app = express();
const cors = require('cors');
const port = 5000;
const pool = require('./DBManager');

app.listen(port, () => {
    console.log('Server listening on port ' + port);
});


const corsOptions = {           // CORS options
    origin:'*',
    methods: ['GET', 'POST'],
    allowedHeaders: ['Content-Type'],
    credential: true,
    optionsSuccessStatus: 200
};


const QUERY_INSERT_CATEGORY_NAME = "INSERT INTO menu_category values(null, \"_categoryName\");";
const QUERY_SELECT_CATEGORY_NAME = "SELECT * FROM menu_category WHERE cateID != 0;";
const QUERY_DELETE_CATEGORY_NAME = "DELETE FROM menu_category WHERE categoryName=\"_categoryName\"";
const QUERY_RENAME_CATEGORY_NAME = "UPDATE menu_category SET categoryName=\"_newName\" WHERE categoryName=\"_prevName\"";
const QUERY_GET_CATEGORY_NAME_PK = "SELECT cateID FROM menu_category WHERE categoryName=\"_categoryName\"";

app.use(cors(corsOptions));
app.use(express.json());
app.use(express.urlencoded({ extended:true }));


// 카테고리 추가
app.post('/addCategoryName', async(req, res) =>{ 
    const { categoryName } = req.body;
    console.log("received category name from menu manager...");
    console.log("received name : " + categoryName);
	
    let conn;
    try{

        console.log("try to connect to db...");
        conn = await pool.getConnection();
        console.log("connection success...");

        let query = QUERY_INSERT_CATEGORY_NAME;
        query = query.replace("_categoryName", categoryName);

        console.log("Excute query " + query);
        await conn.query(query);
        console.log(categoryName + " insert was succeeded in menu_category table");
        res.send('OK');

    }catch(err){
        console.log("error occured while connect");
        console.log("error message : " + err);

    }finally{
        if(conn) conn.release();
    }

});

// 카테고리 조회
app.get('/getCategoryName', async(req, res) =>{
    let conn;

    try{

        console.log("try to connect to db...");
        conn = await pool.getConnection();
        console.log("connection success...");

        console.log("get category list...");
        let query = QUERY_SELECT_CATEGORY_NAME;
        console.log("Execute query " + query);
        const names = await conn.query(query);
        res.json(names);

    }catch(err){
        console.log("error occured while connect");
        console.log("error message : " + err);

    }finally{
        if(conn) conn.release();
    }


});

// 카테고리 삭제
app.post('/deleteCategoryName', async(req, res) =>{
    
    const { categoryName } = req.body;
    console.log("received category name from menu manager...");
    console.log("received name : " + categoryName);

    let conn;
    try{

        console.log("try to connect to db...");
        conn = await pool.getConnection();
        console.log("connection success...");

        let query = QUERY_DELETE_CATEGORY_NAME;
        query = query.replace("_categoryName", categoryName);
        
        console.log("Execute query " + query);
        await conn.query(query);
        console.log(categoryName + " delete was succeeded in menu_category table");
        res.send('OK');

    }catch(err){
        console.log("error occured while connect");
        console.log("error message : " + err);

    }finally{
        if(conn) conn.release();
    }

});

// 카테고리 이름 변경
app.post('/renameCategoryName', async(req, res) =>{
    
    const { prevName, newName } = req.body;
    console.log("received category name from menu manager...");
    console.log("received prev name : " + prevName);
    console.log("received new name : " + newName);

    let conn;
    try{

        console.log("try to connect to db...");
        conn = await pool.getConnection();
        console.log("connection success...");

        let query = QUERY_RENAME_CATEGORY_NAME;
        query = query.replace("_newName", newName).replace("_prevName", prevName);

        console.log("Execute query " + query);
        await conn.query(query);
        console.log(prevName + " change into " + newName + " was succeeded in menu_category table");
        res.send('OK');

    }catch(err){
        console.log("error occured while connect");
        console.log("error message : " + err);

    }finally{
        if(conn) conn.release();
    }

});


const QUERY_INSERT_DRINK = "INSERT INTO DRINK VALUES(null, \"_drinkName\", \"_icePrice\", \"_hotPrice\", \"fk_cateID\")";
const QUERY_CHK_DUPLICATED_DRINK = "SELECT * FROM drink WHERE drinkName=\"_drinkName\" AND FK_MENU_CATEGORY_cateID != 0";


const QUERY_GET_ALL_DRINK = "SELECT * FROM DRINK WHERE FK_MENU_CATEGORY_cateID != 0";
const QUERY_GET_DRINK_BY_CATEGORY = `SELECT drinkName, icePrice, hotPrice FROM DRINK a, 
                                        MENU_CATEGORY b WHERE a.FK_MENU_CATEGORY_cateID = b.cateID 
                                        AND b.categoryName =\"_categoryName\";`

const QUERY_UPDATE_DRINK = `UPDATE DRINK SET drinkName = \"_newName\",
                                             icePrice = _icePrice,
                                             hotPrice = _hotPrice,
                                             FK_MENU_CATEGORY_cateID = _categoryID
                                             WHERE drinkName = \"_prevName\"
											 AND FK_MENU_CATEGORY_cateID != 0`;

const QUERY_DELETE_DRINK = "UPDATE DRINK SET FK_MENU_CATEGORY_cateID = 0 WHERE drinkName = \"_drinkName\"";
const QUERY_DELETE_DRINK_BY_CATEGORY = "UPDATE DRINK SET FK_MENU_CATEGORY_cateID = 0 WHERE FK_MENU_CATEGORY_cateID = _categoryID";


// 음료 추가
app.post('/addDrink', async (req, res) =>{

    const { drinkName, icePrice, hotPrice, categoryName } = req.body;

    let conn;
    try{
        console.log("try to connect to db...");
        conn = await pool.getConnection();
        console.log("connection success...");

		let chkDuplicated = QUERY_CHK_DUPLICATED_DRINK;
		chkDuplicated = chkDuplicated.replace("_drinkName", drinkName);
		const chkResult = await conn.query(chkDuplicated);

		if(chkResult.length > 0){
			console.log("error occured while insert!");
			console.log(drinkName + " was duplicated!");
			return res.send("drinkName was duplicated!");
		}		// 중복 등록 방지
		
        let insertQuery = QUERY_INSERT_DRINK;

        let getPKQuery = QUERY_GET_CATEGORY_NAME_PK;
        getPKQuery = getPKQuery.replace("_categoryName", categoryName);
        console.log("Execute query " + getPKQuery);

        const ID = await conn.query(getPKQuery);
		
        insertQuery = insertQuery.replace("_drinkName", drinkName,).
                                    replace("_icePrice", icePrice).
                                    replace("_hotPrice", hotPrice).
                                    replace("fk_cateID", ID[0].cateID);
        
        console.log("Execute query " + insertQuery);
        await conn.query(insertQuery);
        console.log(drinkName + " insert was succeeded to drink table");
        res.send('OK');

    }catch(err){
        console.log("error occured while connect");
        console.log("error message : " + err);

    }finally{
        if(conn) conn.release();
    }
});


// 모든 음료 조회
app.get('/getAllDrinks', async(req, res) =>{

    let conn;
    try{

        console.log("try to connect to db...");
        conn = await pool.getConnection();
        console.log("connection success...");

        console.log("get all drink info from drink table");
        let query = QUERY_GET_ALL_DRINK;
        console.log("Execute query " + query);
        const drinks = await conn.query(query);
        res.json(drinks);

    }catch(err){
        console.log("error occured while connect");
        console.log("error message : " + err);

    }finally{
        if(conn) conn.release();
    }

});

// 카테고리별로 음료 조회
app.get('/getDrinksByCategory', async(req, res) =>{

    const categoryName = req.query.categoryName;
    let conn;
    try{

        console.log("try to connect to db...");
        conn = await pool.getConnection();
        console.log("connection success...");

        console.log("get drink info by category from drink table...");
        let query = QUERY_GET_DRINK_BY_CATEGORY;
        query = query.replace("_categoryName", categoryName);
        console.log("Execute query" + query);

        const drinks = await conn.query(query);
        res.json(drinks);

    }catch(err){
        console.log("error occured while connect");
        console.log("error message : " + err);

    }finally{
        if(conn) conn.release();
    }

});

app.post('/deleteDrinkByCategory', async(req,res) =>{
    const { cateID, categoryName } = req.body;
    let conn;
    
    try{

        console.log("try to connect to db...");
        conn = await pool.getConnection();
        console.log("connection success...");

        console.log("delete drink data releated with current category from drink table");
        let drink_query = QUERY_DELETE_DRINK_BY_CATEGORY;
        drink_query = drink_query.replace("_categoryID", cateID);
        console.log("Execute query " + drink_query);
        await conn.query(drink_query);
        // 카테고리에 등록되어있는 음료부터 삭제 후

        console.log("delete current category");
        let category_query = QUERY_DELETE_CATEGORY_NAME;
        category_query = category_query.replace("_categoryName", categoryName);
        console.log("Execute query " + category_query);
        await conn.query(category_query);
        // 카테고리를 삭제        

        console.log(categoryName + " category delete was succeeded");
        res.send('OK');

    }catch(err){
        console.log("error occured while connect");
        console.log("error message : " + err);

    }finally{
        if(conn) conn.release();
    }

	
});

// 음료 삭제
app.post('/deleteDrink', async(req, res) =>{

    const { drinkName } = req.body;
    let conn;
    try{

        console.log("try to connect to db...");
        conn = await pool.getConnection();
        console.log("connection success...");

        console.log("delete drink from drink table...");
        let query = QUERY_DELETE_DRINK;
        query = query.replace("_drinkName", drinkName);
        console.log("Execute query " + query);
        await conn.query(query);
        
        console.log(drinkName + " delete was succeeded");
        res.send('OK');

    }catch(err){
        console.log("error occured while connect");
        console.log("error message : " + err);

    }finally{
        if(conn) conn.release();
    }

});

// 음료 정보 변경
app.post('/updateDrink', async(req, res) =>{

    const { prevName, newName, icePrice, hotPrice, categoryName } = req.body;
    let conn;
    try{

        console.log("try to connect to db...");
        conn = await pool.getConnection();
        console.log("connection success...");

		console.log("get " + categoryName +" Foregin key...");
		
        let getPKQuery = QUERY_GET_CATEGORY_NAME_PK;
        getPKQuery = getPKQuery.replace("_categoryName", categoryName);
        console.log("Execute query " + getPKQuery);

        const ID = await conn.query(getPKQuery);

        console.log("update drink info...");
        let query = QUERY_UPDATE_DRINK;
        query = query.replace("_prevName", prevName).
                        replace("_newName", newName).
                        replace("_icePrice", icePrice).
                        replace("_hotPrice", hotPrice).
                        replace("_categoryID", ID[0].cateID);
        
        console.log("Execute query " + query);
        await conn.query(query);
        
        console.log(prevName + " update was succeeded");
        res.send('OK');

    }catch(err){
        console.log("error occured while connect");
        console.log("error message : " + err);

    }finally{
        if(conn) conn.release();
    }
    
});

let orderMap = new Map();
let currentOrderNo = 1;

app.post('/receiveOrder',async(req,res) =>{
    const { orderList } = req.body;
	console.log("received order from orderClient");
	
    console.log(orderList);
	orderMap.set(currentOrderNo++, orderList);
    
	res.send('OK');
});

app.get('/getOrder', async(req, res) =>{
	
	let orderList = [];
	for( [orderNo, list] of orderMap){
		orderList.push({
			orderNo:orderNo,
			orderList:list
		});
	}
	res.json(orderList);
	
});

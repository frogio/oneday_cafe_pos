const express = require('express');
const http = require('http');
const WebSocket = require('ws');

const app = express();
const cors = require('cors');
const ip = "192.168.1.7";
const port = 5000;
const pool = require('./DBManager');
const path = require('path');

const server = http.createServer(app);
const wss = new WebSocket.Server({ server });

const RECEIVE_ORDER = 0, RETRIEVE_ORDER = 1;
let isOnline = [0, 0]; 
const clients = new Map();
// 0번 웹소켓이 주문 접수 클라이언트, 1번 웹소켓이 주문 조회 클라이언트

let orderMap = new Map();
let currentOrderNo = 1;

app.use(express.static(path.join(__dirname, "PosClient")));
app.use(express.static(path.join(__dirname, "PosManagement")));

app.get('/posClient', (req, res) =>{
	res.sendFile(path.join(__dirname, "PosClient", "index.html"));

});

app.get('/PosManagement', (req, res) =>{
	res.sendFile(path.join(__dirname, "PosManagement", "index.html"));

});

app.get('/manageMenu', (req, res) =>{
	res.sendFile(path.join(__dirname, "PosManagement", "index.html"));

});

app.get('/manageOrder', (req, res) =>{
	res.sendFile(path.join(__dirname, "PosManagement", "index.html"));

});

app.get('/saleRecord', (req, res) =>{
	res.sendFile(path.join(__dirname, "PosManagement", "index.html"));

});

wss.on('connection', (ws) =>{
    ws.on('message', (message) => {
        try{
/*          data 확인
            const data = JSON.parse(message);
            for(let i = 0; i < data.length; i++){
                console.log("drinkName : " + data[i].drinkName);
                console.log("cupCount : " + data[i].cupCount);
                console.log("price : " + data[i].price);
                console.log("isHot : " + data[i].isHot );
                console.log("payOpt : " );
                for(let k = 0; k < data[i].payOpt.length; k++)
                    console.log(" " + data[i].payOpt[k]);

            }
*/
			const data = JSON.parse(message);
			
			if(data.type === 'online'){
				
				// 주문 접수 클라이언트 연결
				if(data.clientInfo === 'receiveOrder' && isOnline[RECEIVE_ORDER] == 0){
					//clients.push(ws);
					clients.set(RECEIVE_ORDER, ws);
					console.log(data.clientInfo + " client is online");
					isOnline[RECEIVE_ORDER] = 1;
				}
				// 주문 조회 클라이언트 연결
				else if(data.clientInfo === 'retrieveOrder' && isOnline[RETRIEVE_ORDER] == 0){
					clients.set(RETRIEVE_ORDER, ws);
					const targetClient = clients.get(RETRIEVE_ORDER);
					console.log(data.clientInfo + " client is online");
					
					// 주문 조회 클라이언트와 연결되었을 때, 받아놓았던 주문들을 전송한다.
					//let orderList = Array.from(map, ([key, value]) => ({ key, value }));
					let orderList = [];
					orderMap.forEach((value, key) =>{
						orderList.push({
							orderNo:key,
							orderList:value
						});
					});
					
					/*
					let orderList = [];
					for( [orderNo, list] of orderMap){
						orderList.push({
							orderNo:orderNo,
							orderList:list
						});s
					}*/
					if(orderList.length != 0)
						targetClient.send(JSON.stringify(orderList));
					isOnline[RETRIEVE_ORDER] = 1;				
				}
				
				else console.log(data.clientInfo + " is already online!");
	
			}
			// 새로운 주문일 때, 주문 조회에 전송한다.
			else if(data.type === 'order'){
				const targetClient = clients.get(RETRIEVE_ORDER);
				console.log("receive order from client");
			
				for(let i = 0; i < data.order.length; i++){
					
					data.order[i].completed = false;
					// 음료가 다 완성되었는지 여부를 확인하는 속성을 하나 더 추가한다.
					console.log(data.order[i]);
		/*		
					console.log("drinkName : " + data.order[i].drinkName);
					console.log("cupCount : " + data.order[i].cupCount);
					console.log("price : " + data.order[i].price);
					console.log("isHot : " + data.order[i].isHot );
					console.log("payOpt : " );
					for(let k = 0; k < data.order[i].payOpt.length; k++)
						console.log(" " + data.order[i].payOpt[k]);
		*/
				}		
				
				
				
				orderMap.set(currentOrderNo, data.order);
				
				if(isOnline[RETRIEVE_ORDER] == 1)				// 주문 조회 클라이언트가 online일 때만 send
					targetClient.send(JSON.stringify({
							orderNo:currentOrderNo,
							orderList:data.order
						}));
				currentOrderNo++;
			}
			else if(data.type === 'deleteReq'){
				const deleteNo = data.orderNo;
				console.log("received deleteReq from client...");
				orderMap.delete(deleteNo);
				console.log("deleted order no : " + deleteNo);
				
				console.log("current ordermap status : ");
				orderMap.forEach((value, key) =>{
					console.log("Order No : " + key);
					console.log("orderList : " + value);
				});	
			}
			else if(data.type === "deleteDrinkReq"){
				
				const orderNo = data.info.orderNo;
				const drinkIdx = data.info.Idx;
				console.log("received deletedDrinkReq from client...");

				orderMap.forEach((value, key) =>{
					if(key == orderNo){
						value.splice(drinkIdx, 1);
						console.log(value);
						if(value.length == 0)			// 모든 음료가 지워졌을 경우
							orderMap.delete(orderNo);	// 맵에서 삭제
					}
				});
	
			}
			
			else if(data.type === 'updateReq'){
				const updateNo = data.orderInfo.orderNo;
				//console.log(data.orderInfo);
				
				orderMap.set(updateNo, data.orderInfo.orderList);
			}
			

        }
        catch(error){
            console.log(error);
        }
    });
    
    ws.on('close', () =>{
		if(ws === clients.get(RECEIVE_ORDER)){
			clients.delete(RECEIVE_ORDER);
			console.log("receiveOrder client is disconnected");
			isOnline[RECEIVE_ORDER] = 0;
		}
		else if(ws === clients.get(RETRIEVE_ORDER)){
			clients.delete(RETRIEVE_ORDER);
			console.log("retrieveOrder client is disconnected");
			isOnline[RETRIEVE_ORDER] = 0;
		}
    });
});

/*
server.listen(port, ()=>{
    console.log("Server listen on Port " + port);
});
*/
server.listen(port, ip, ()=>{
    console.log("Server listen on Port " + port);
});
/*
app.listen(port, () => {
    console.log('Example app listening on port ' + port);
});
*/

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

/*
+-------------------+-------------+------+-----+---------+----------------+
| Field             | Type        | Null | Key | Default | Extra          |
+-------------------+-------------+------+-----+---------+----------------+
| saleID            | int(11)     | NO   | PRI | NULL    | auto_increment |
| date              | varchar(50) | YES  |     | NULL    |                |
| count             | int(11)     | YES  |     | NULL    |                |
| isHot             | int(11)     | YES  |     | NULL    |                |
| payOption         | varchar(50) | YES  |     | NULL    |                |
| FK_DRINK_recordID | int(11)     | YES  | MUL | NULL    |                |
+-------------------+-------------+------+-----+---------+----------------+*/

const QUERY_INSERT_SALE_RECORD = "INSERT INTO SALE_RECORD VALUES(null, \"_date\", _count, _isHot, \"_payOption\", _FK_DRINK_ID)";
const QUERY_GET_DRINK_PK = "SELECT drinkID FROM DRINK WHERE FK_MENU_CATEGORY_cateID != 0 AND drinkName = \"_drinkName\"";

app.post('/completeOrder',async(req,res) =>{
    const { orderNo, orderList } = req.body;

	console.log("received completed order...");
	
	let conn;
	try{
		
		console.log("try to connect to db...");
		conn = await pool.getConnection();
		console.log("connection success...");

		console.log("orderNo : " + orderNo);
		console.log("ordeInfo : ");
	
		const date = new Date();
		let month = "" + (date.getMonth() + 1);

		if(month.length < 2)
			month = '0' + (date.getMonth() + 1);

		let _date = "" + date.getDate();
		
		if(_date.length < 2)
			_date = '0' + date.getDate();
		
		let today = date.getFullYear() + '-' + month + '-' + _date;
		
		for(let i = 0; i < orderList.length; i++){
	
			console.log("drinkName : " + orderList[i].drinkName);
			console.log("cupCount : " + orderList[i].cupCount);
			console.log("price : " + orderList[i].price);
			console.log("isHot : " + orderList[i].isHot );
			console.log("payOpt : " );
			
			let payOpt = "";
			
			for(let k = 0; k < orderList[i].payOpt.length; k++){
				console.log(" " + orderList[i].payOpt[k]);
				payOpt += orderList[i].payOpt[k] + " ";
			}
	
			let query = QUERY_INSERT_SALE_RECORD;
			
			query = query.replace("_date", today);
			query = query.replace("_count", orderList[i].cupCount);
			query = query.replace("_isHot", (orderList[i].isHot) ? 1 : 0);
			query = query.replace("_payOption", payOpt);
			//query = query.
			
			let getPKQuery = QUERY_GET_DRINK_PK;
			getPKQuery = getPKQuery.replace("_drinkName", orderList[i].drinkName);
			console.log("Execute query " + getPKQuery);
			const ID = await conn.query(getPKQuery);
			query = query.replace("_FK_DRINK_ID", "" + ID[0].drinkID);
			

			console.log("Excute query " + query);
			await conn.query(query);
			
		}
		orderMap.delete(orderNo);			// 주문이 완료되었으므로 맵에서 삭제
		res.send('OK');
		
	}catch(err){
		console.log("error occured while connect");
		console.log("error message : " + err);
	
	}finally{
		if(conn) conn.release();
	}
    //console.log(orderList);*/
});


const QUERY_DRINK_SALE_RECORD = `SELECT
									e.isHot,
									drinkName,
									count,
									case 
									when(e.payOption LIKE \"%coupon%\")
										then -1
										
									when(isHot=1)
										then if (e.payOption LIKE \"%tumblr%\",
											count * (d.hotPrice - 500),		/*if*/
											count * d.hotPrice)				/*else*/
									
									when(isHot=0)
										then if (e.payOption LIKE \"%tumblr%\",
											count * (d.icePrice - 500),		/*if*/
											count * d.icePrice)				/*else*/
									
									end as price
									FROM
										(SELECT sum(count) as count, isHot, rcd.payOption FROM SALE_RECORD rcd, DRINK d
											WHERE rcd.date = \"_date\"
											AND rcd.isHot = _isHot
											AND rcd.FK_DRINK_recordID = d.drinkID
											AND rcd.FK_DRINK_recordID = _drinkID
											AND rcd.payOption = \"_payOption\") e, DRINK d where d.drinkID = _drinkID`;

const QUERY_GET_ALL_DRINK_PK = "SELECT drinkID FROM drink";

app.post('/getSaleRecord',async(req,res) =>{
	const { date } = req.body;
	
	console.log("get sale record on " + date);
	
	let conn;
	
	try{
		
		console.log("try to connect to db...");
		conn = await pool.getConnection();
		console.log("connection success...");
		
		let getPKQuery = QUERY_GET_ALL_DRINK_PK;
		
		console.log("Execute query " + getPKQuery);
		const ID = await conn.query(getPKQuery);

		let records = [];
		
		for(let isHot = 0; isHot < 2; isHot++){
			for(let payOpt = 0; payOpt < 4; payOpt++){
				let query = QUERY_DRINK_SALE_RECORD;
				query = query.replace("_date", date);
				query = query.replace("_isHot", "" + isHot);
				
				let optQuery = query;
				let finalQuery;
				switch(payOpt){
					case 0:			// 일반 결제 옵션
					
					optQuery = optQuery.replace("_payOption", "");
					for(let i = 0; i < ID.length; i++){
						finalQuery = optQuery.replace("_drinkID", "" + ID[i].drinkID);
						finalQuery = finalQuery.replace("_drinkID", "" + ID[i].drinkID);
						const result = await conn.query(finalQuery);
						records.push(result[0]);
					}
					
					break;
					case 1:			// tumblr
					
					optQuery = optQuery.replace("_payOption", "tumblr ");
					for(let i = 0; i < ID.length; i++){
						finalQuery = optQuery.replace("_drinkID", "" + ID[i].drinkID);
						finalQuery = finalQuery.replace("_drinkID", "" + ID[i].drinkID);
						const result = await conn.query(finalQuery);
						result[0].drinkName = "텀블러 " + result[0].drinkName;
						records.push(result[0]);
					}
					
					
					break;
					case 2:			// coupon
					
					optQuery = optQuery.replace("_payOption", "coupon ");
					for(let i = 0; i < ID.length; i++){
						finalQuery = optQuery.replace("_drinkID", "" + ID[i].drinkID);
						finalQuery = finalQuery.replace("_drinkID", "" + ID[i].drinkID);
						const result = await conn.query(finalQuery);
						records.push(result[0]);
					}
					
					break;
					case 3:			// tumblr coupon
					
					optQuery = optQuery.replace("_payOption", "tumblr coupon ");
					for(let i = 0; i < ID.length; i++){
						finalQuery = optQuery.replace("_drinkID", "" + ID[i].drinkID);
						finalQuery = finalQuery.replace("_drinkID", "" + ID[i].drinkID);
						const result = await conn.query(finalQuery);
						result[0].drinkName = "텀블러 " + result[0].drinkName;
						records.push(result[0]);
					}
					
					
					break;
					
				}
			}
		}
	
		records = records.filter(record => record.price != null);
		res.json(records);
		
	}catch(err){
		console.log("error occured while connect");
		console.log("error message : " + err);
		
	}finally{
		if(conn) conn.release();	
	}
});

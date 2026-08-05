const DBHandle = require('mariadb');

const pool = DBHandle.createPool({
    host:"",
    user:"",
    password:"",
    database:"",
    port:3306,
    connectionLimit:5
});

module.exports = pool;
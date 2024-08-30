const DBHandle = require('mariadb');

const pool = DBHandle.createPool({
    host:"",
    user:"",
    password:"",
    database:"",
    port:,
    connectionLimit:5
});

module.exports = pool;
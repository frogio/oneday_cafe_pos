const DBHandle = require('mariadb');

const pool = DBHandle.createPool({
    host:"localhost",
    user:"root",
    password:"frog1122",
    database:"anyangcafe",
    port:3306,
    connectionLimit:5
});

module.exports = pool;
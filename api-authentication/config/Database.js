import {Sequelize} from "sequelize";

const db = new Sequelize('c23ps021_db','root','123789456',{
    host: '34.101.196.100',
    dialect: 'mysql'
});

export default db;
// 'use strict';

const fs = require('fs');
const path = require('path');
const Sequelize = require('sequelize');
const process = require('process');
const basename = path.basename(__filename);
const env = process.env.NODE_ENV || 'development';
const dbConfig = require('../config/db.config.js')[env];
const db = {};

let sequelize;
if (dbConfig.use_env_variable) {
  sequelize = new Sequelize(process.env[dbConfig.use_env_variable], dbConfig);
} else {
  sequelize = new Sequelize(dbConfig.DB, dbConfig.USER, dbConfig.PASSWORD, dbConfig);
}

const modelFiles = fs
  .readdirSync(__dirname)
  .filter(file => (
    file.indexOf('.') !== 0 &&
    file !== basename &&
    file.slice(-3) === '.js' &&
    file.indexOf('.test.js') === -1
  ));

modelFiles.forEach(file => {
  const model = require(path.join(__dirname, file))(sequelize, Sequelize.DataTypes);
  db[model.name] = model;
});

Object.keys(db).forEach(modelName => {
  if (db[modelName].associate) {
    db[modelName].associate(db);
  }
});

db.Sequelize = Sequelize;
db.sequelize = sequelize;

// Adapted from the new structure
db.User = require('./user.model.js')(sequelize, Sequelize);
db.Role = require('./role.model.js')(sequelize, Sequelize);

db.Role.belongsToMany(db.User, {
  through: 'user_roles',
  foreignKey: 'roleId',
  otherKey: 'userId',
});
db.User.belongsToMany(db.Role, {
  through: 'user_roles',
  foreignKey: 'userId',
  otherKey: 'roleId',
});

db.ROLES = ['user', 'admin'];

module.exports = db;

// 'use strict';

const { Model } = require('sequelize');

module.exports = (sequelize, DataTypes) => {
  class User extends Model {
    static associate(models) {
      User.belongsToMany(models.Role, {
        through: 'user_roles',
        foreignKey: 'userId',
        otherKey: 'roleId',
        as: 'roles',
      });
    }
  }

  User.init(
    {
      username: DataTypes.STRING,
      email: DataTypes.STRING,
      password: DataTypes.STRING,
      dob: DataTypes.DATE,
      phone: DataTypes.STRING,
      greeting: DataTypes.STRING,
      resetToken: DataTypes.STRING,
      resetTokenExpires: DataTypes.DATE,
      emailVerified: {
        type: DataTypes.BOOLEAN,
        defaultValue: false,
      },
      emailVerificationToken: DataTypes.STRING,
    },
    {
      sequelize,
      modelName: 'User',
      tableName: 'users'
    }
  );

  return User;
};



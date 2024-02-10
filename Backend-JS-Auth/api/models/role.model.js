const { Model } = require('sequelize');

module.exports = (sequelize, DataTypes) => {
  class Role extends Model {
    static associate(models) {
      Role.belongsToMany(models.User, {
        through: 'user_roles',
        foreignKey: 'roleId',
        otherKey: 'userId',
      });
    }
  }

  Role.init(
    {
      name: DataTypes.STRING,
    },
    {
      sequelize,
      modelName: 'Role',
      tableName: 'roles'
    }
  );

  return Role;
};


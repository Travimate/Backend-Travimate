'use strict';

const bcrypt = require('bcryptjs');

module.exports = {
  up: async (queryInterface, Sequelize) => {
    const adminRole = await queryInterface.rawSelect('Roles', {
      where: { name: 'admin' },
    }, ['id']);

    const userRole = await queryInterface.rawSelect('Roles', {
      where: { name: 'user' },
    }, ['id']);

    await queryInterface.bulkInsert('Users', [
      {
        username: 'adminUser',
        email: 'adminuser123@example.com',
        password: bcrypt.hashSync('adminUser_123', 8),
        dob: new Date(),
        phone: '087656725356',
        greeting: "nyonya",
        roleId: adminRole,
        createdAt: new Date(),
        updatedAt: new Date(),
      },
      {
        username: 'regularUser',
        email: 'regularuser123@example.com',
        password: bcrypt.hashSync('regularUser_123', 8),
        dob: new Date(),
        phone: '087656725356',
        greeting: "nyonya",
        roleId: userRole,
        createdAt: new Date(),
        updatedAt: new Date(),
      },
      // Add more seeded user data as needed
    ], {});
  },

  down: async (queryInterface, Sequelize) => {
    await queryInterface.bulkDelete('Users', null, {});
  },
  order: 2 
};


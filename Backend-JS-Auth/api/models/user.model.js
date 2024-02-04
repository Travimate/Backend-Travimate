module.exports = (sequelize, Sequelize) => {
  const User = sequelize.define("users", {
    username: {
      type: Sequelize.STRING,
    },
    email: {
      type: Sequelize.STRING,
    },
    password: {
      type: Sequelize.STRING,
    },
    dob: {
      type: Sequelize.DATE,
    },
    phone: {
      type: Sequelize.STRING,
    },
    greeting: {
      type: Sequelize.STRING,
    },
    resetToken: {
      type: Sequelize.STRING,
    },
    resetTokenExpires: {
      type: Sequelize.DATE,
    },
    emailVerified: {
      type: Sequelize.BOOLEAN,
      defaultValue: false,
    },
    emailVerificationToken: {
      type: Sequelize.STRING,
    },
  });

  return User;
};

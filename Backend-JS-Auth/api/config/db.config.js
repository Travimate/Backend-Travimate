module.exports = {
  development: {
    HOST: "localhost",
    USER: "postgres",
    PASSWORD: "root",
    DB: "travi_development",
    dialect: "postgres",
    pool: {
      max: 5,
      min: 0,
      acquire: 30000,
      idle: 10000,
    },
  },
  test: {
    HOST: "localhost",
    USER: "postgres",
    PASSWORD: "root",
    DB: "travi_test",
    dialect: "postgres",
    pool: {
      max: 5,
      min: 0,
      acquire: 30000,
      idle: 10000,
    },
  },
  production: {
    HOST: "localhost",
    USER: "postgres",
    PASSWORD: "root",
    DB: "travi_production",
    dialect: "postgres",
    pool: {
      max: 5,
      min: 0,
      acquire: 30000,
      idle: 10000,
    },
  },
};


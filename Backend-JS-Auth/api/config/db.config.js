module.exports = {
  development: {
    HOST: "localhost",
    USER: "postgres",
    PASSWORD: "root",
    DB: "travimate",
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
    USER: "fajar",
    PASSWORD: "123",
    PORT:"5432",
    DB: "travimate",
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
    USER: "fajar",
    PASSWORD: "123",
    PORT:"5432",
    DB: "travimate",
    dialect: "postgres",
    pool: {
      max: 5,
      min: 0,
      acquire: 30000,
      idle: 10000,
    },
  },
};


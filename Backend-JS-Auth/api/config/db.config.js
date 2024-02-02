module.exports = {
  HOST: "localhost", // or the IP address of your PostgreSQL server
  PORT: 5432,        // the port on which your PostgreSQL server is running
  USER: "fajar",
  PASSWORD: "123",
  DB: "travimate",
  dialect: "postgres",
  pool: {
    max: 5,
    min: 0,
    acquire: 30000,
    idle: 10000,
  },
};

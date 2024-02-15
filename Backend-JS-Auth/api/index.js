const express = require("express");
const bodyParser = require("body-parser");
const cors = require("cors");
const morgan = require("morgan");
const swagger = require("./../swagger"); // Import the Swagger configuration
const { sequelize, Role } = require("./models");
const app = express();
// const corsOptions = {
//   origin: "http://localhost:8081",
// };
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));
app.use(cors());
app.use(morgan("dev"));
const dotenv = require("dotenv");
dotenv.config();
const PORT = process.env.PORT || 8081;

// database

sequelize.sync().then(() => {
  console.log("Database synchronized");
  initial();
});

// db.sequelize.sync();
// force: true will drop the table if it already exists
// db.sequelize.sync({ force: true }).then(() => {
//   console.log("Drop and Resync Database with { force: true }");
//   initial();
// });

// simple route
app.get("/", (req, res) => {
  res.json({ message: "Express API is Ready" });
});
// routes
require("./routes/auth.routes")(app);
require("./routes/user.routes")(app);

swagger(app);

app.listen(PORT, () => {
  console.log(`Server is running on port ${PORT}.`);
});

function initial() {
  Role.findOrCreate({
    where: { id: 1 },
    defaults: { name: "user" },
  });

  Role.findOrCreate({
    where: { id: 2 },
    defaults: { name: "admin" },
  });
}

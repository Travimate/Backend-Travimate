const express = require("express");
const bodyParser = require("body-parser");
const cors = require("cors");
const swagger = require('./../swagger'); // Import the Swagger configuration
const app = express();
const corsOptions = {
  origin: "http://localhost:8081",
};
app.use(cors(corsOptions));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

// database
const db = require("./models");
const Role = db.role;
const PORT = process.env.PORT || 8080;

// db.sequelize.sync();
// force: true will drop the table if it already exists
db.sequelize.sync({ force: true }).then(() => {
  console.log("Drop and Resync Database with { force: true }");
  initial();
});
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
  Role.create({
    id: 1,
    name: "user",
  });
  Role.create({
    id: 2,
    name: "admin",
  });
}

const { authJwt } = require("../middleware");
const users = require("../controllers/user.controller");

module.exports = function (app) {
  app.use(function (req, res, next) {
    res.header(
      "Access-Control-Allow-Headers",
      "x-access-token, Origin, Content-Type, Accept"
    );
    next();
  });

  app.get(
    "/api/users/",
    [authJwt.verifyToken, authJwt.isAdmin, authJwt.authCheck],
    users.findAll
  );
  app.get(
    "/api/users/:id",
    [authJwt.verifyToken, authJwt.isAdmin, authJwt.authCheck],
    users.findOne
  );
  // Update a User with id (for the user to update their own account)
  app.put(
    "/api/users/:id",
    [authJwt.verifyToken, authJwt.isUser, authJwt.authCheck],
    users.update
  );
  // Delete a User with id (for admin)
  app.delete(
    "/api/users/:id",
    [authJwt.verifyToken, authJwt.isAdmin, authJwt.authCheck],
    users.delete
  );
  app.get("/api/auth/me", [authJwt.verifyToken], users.authMe);
};

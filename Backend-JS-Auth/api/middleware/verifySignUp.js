const db = require("../models");
const ROLES = db.ROLES;
const User = db.user;

const checkDuplicateUsernameOrEmail = (req, res, next) => {
  // Username
  if (!req.body.username || req.body.username.trim() === "") {
    res.status(400).send({
      message: "Aksi gagal! Username harus diisi!",
    });
    return;
  }
  
  User.findOne({
    where: {
      username: req.body.username,
    },
  }).then((user) => {
    if (user) {
      res.status(400).send({
        message: "Aksi gagal! Username telah dipakai!",
      });
      return;
    }
    // Email
    if (!req.body.email || req.body.email.trim() === "") {
        res.status(400).send({
          message: "Aksi gagal! Email tidak dapat kosong!",
        });
        return;
      }
    // Validate email format
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(req.body.email)) {
      res.status(400).send({
        message: "Aksi gagal! Format email tidak valid!",
      });
      return;
    }
    User.findOne({
      where: {
        email: req.body.email,
      },
    }).then((user) => {
      if (user) {
        res.status(400).send({
          message: "Aksi gagal! Email telah dipakai!",
        });
        return;
      }
      next();
    });
  });
};

const checkRolesExisted = (req, res, next) => {
  if (req.body.roles) {
    for (let i = 0; i < req.body.roles.length; i++) {
      if (!ROLES.includes(req.body.roles[i])) {
        res.status(400).send({
          message: "Aksi Gagal! Role tidak tersedia = " + req.body.roles[i],
        });
        return;
      }
    }
  }
  next();
};

const validatePassword = (req, res, next) => {
    // Password validation
    if (!req.body.password || req.body.password.trim() === "") {
      res.status(400).send({
        message: "Aksi gagal! Password tidak boleh kosong!",
      });
      return;
    }
  
    // Password length check
    if (req.body.password.length < 6) {
      res.status(400).send({
        message: "Aksi gagal! Password harus terdiri dari minimal 6 karakter!",
      });
      return;
    }
  
    // Password complexity check
    const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?& _])[A-Za-z\d@$!%*?&_ ]/;
    if (!passwordRegex.test(req.body.password)) {
      res.status(400).send({
        message: "Aksi gagal! Passowrd harus memiliki minimal satu huruf kecil, satu huruf besar, sati angka, dan satu karakter spesial!.",
      });
      return;
    }
  
    next();
  };

const verifySignUp = {
  checkDuplicateUsernameOrEmail: checkDuplicateUsernameOrEmail,
  checkRolesExisted: checkRolesExisted,
  validatePassword: validatePassword,
};

module.exports = verifySignUp;

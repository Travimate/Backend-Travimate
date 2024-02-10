const db = require("../models");
const ROLES = db.ROLES;
const User = db.User;
const Role = db.Role;

const checkDuplicateUsernameOrEmail = (req, res, next) => {
  // Username
  const username = req.body.username;
  const email = req.body.email;

  if (!username || username.trim() === "") {
    return res.status(400).send({
      message: "Aksi gagal! Username harus diisi!",
    });
  }

  User.findOne({
    where: {
      username: username,
    },
  }).then((user) => {
    if (user) {
      return res.status(400).send({
        message: "Aksi gagal! Username telah dipakai!",
      });
    }

    // Email
    if (!email || email.trim() === "") {
      return res.status(400).send({
        message: "Aksi gagal! Email tidak dapat kosong!",
      });
    }

    // Validate email format
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
      return res.status(400).send({
        message: "Aksi gagal! Format email tidak valid!",
      });
    }

    User.findOne({
      where: {
        email: email,
      },
      include: [Role], 
    }).then((user) => {
      if (user) {
        return res.status(400).send({
          message: "Aksi gagal! Email telah dipakai!",
        });
      }
      next();
    });
  });
};


const checkRolesExisted = (req, res, next) => {
  if (req.body.roles) {
    for (let i = 0; i < req.body.roles.length; i++) {
      if (!ROLES.includes(req.body.roles[i])) {
        return res.status(400).send({
          message: "Aksi Gagal! Role tidak tersedia = " + req.body.roles[i],
        });
      }
    }
  }
  next();
};


const validatePassword = (req, res, next) => {
  // Password validation
  const password = req.body.password;

  if (!password || password.trim() === "") {
    return res.status(400).send({
      message: "Aksi gagal! Password tidak boleh kosong!",
    });
  }

  // Password length check
  if (password.length < 6) {
    return res.status(400).send({
      message: "Aksi gagal! Password harus terdiri dari minimal 6 karakter!",
    });
  }

  // Password complexity check
  const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?& _])[A-Za-z\d@$!%*?&_ ]/;
  if (!passwordRegex.test(password)) {
    return res.status(400).send({
      message: "Aksi gagal! Password harus memiliki minimal satu huruf kecil, satu huruf besar, sati angka, dan satu karakter spesial!.",
    });
  }

  next();
};

const verifySignUp = {
  checkDuplicateUsernameOrEmail,
  checkRolesExisted,
  validatePassword,
};

module.exports = verifySignUp;

const db = require("../models");
const config = require("../config/auth.config");
const jwt = require("jsonwebtoken");
const nodemailer = require('nodemailer');
const bcrypt = require("bcryptjs");
const { v4: uuidv4 } = require('uuid');
const crypto = require('crypto');
const User = db.user;
const Role = db.role;
const Op = db.Sequelize.Op;
const { authJwt } = require("../middleware");
const dotenv = require('dotenv');
dotenv.config();

const transporter = nodemailer.createTransport({
  service: process.env.EMAIL_SERVICE,
  auth: {
    user: process.env.EMAIL_SENDER,
    pass: process.env.EMAIL_PASSWORD,
  },
});

exports.forgotPassword = (req, res) => {
  const { email } = req.body;

  User.findOne({ where: { email } })
    .then((user) => {
      if (!user) {
        return res.status(404).send({ message: "Pengguna tidak ditemukan!" });
      }
      // Generate a unique reset token
      const resetToken = uuidv4();
      // Save the resetToken and expiration time to the user in your database
      user.resetToken = resetToken;
      user.resetTokenExpires = Date.now() + 3600000; // Token expires in 1 hour

      user.save().then(() => {
        const resetLink = `${process.env.BASE_URL}/reset-password?token=${resetToken}`;

        const mailOptions = {
          from: process.env.EMAIL_SENDER,
          to: user.email,
          subject: "Reset Password",
          text: `Anda menerima email ini karena mengajukan reset password. Tolong klik tautan berikut untuk mereset password akun anda: ${resetLink}`,
          html: `<p>Anda menerima email ini karena mengajukan reset password. Tolong klik tautan berikut untuk mereset password akun anda:</p><a href="${resetLink}">${resetLink}</a>`,
        };

        transporter.sendMail(mailOptions, (error, info) => {
          if (error) {
            console.log(error);
            return res.status(500).send({ message: "Gagal mengirim email!" });
          }

          console.log("Email telah dikirimkan: " + info.response);
          res.send({ message: "Email berhasil dikirimkan", resetToken });
        });
      });
    })
    .catch((err) => {
      console.log(err);
      res.status(500).send({ message: "Kesalahan server internal!" });
    });
};

exports.resetPassword = (req, res) => {
  const { token, newPassword } = req.body;

  User.findOne({
    where: {
      resetToken: token,
      resetTokenExpires: { [db.Sequelize.Op.gte]: Date.now() },
    },
  })
    .then((user) => {
      if (!user) {
        return res.status(400).send({ message: "Token salah atau kadaluarsa!" });
      }

      // Update user's password
      const hashedPassword = bcrypt.hashSync(newPassword, 8);
      user.password = hashedPassword;
      user.resetToken = null;
      user.resetTokenExpires = null;

      user.save().then(() => {
        res.status(200).json({ message: "Password berhasil diubah!" });
      });
    })
    .catch((err) => {
      console.log(err);
      res.status(500).send({ message: "Kesalahan server internal!" });
    });
};

exports.signup = (req, res) => {
  User.create({
    username: req.body.username,
    email: req.body.email,
    password: bcrypt.hashSync(req.body.password, 8),
    dob: req.body.dob,
    phone: req.body.phone,
    greeting: req.body.greeting,
    emailVerificationToken: uuidv4(),
  })
    .then((user) => {
      sendVerificationEmail(user);
      if (req.body.roles) {
        Role.findAll({
          where: {
            name: {
              [Op.or]: req.body.roles,
            },
          },
        }).then((roles) => {
          user.setRoles(roles).then(() => {
            res.send({ message: "User berhasil didaftarkan!" });
          });
        });
      } else {
        // default, user role = 1
        user.setRoles([1]).then(() => {
          res.send({ message: "User berhasil didaftarkan!" });
        });
      }
    })
    .catch((err) => {
      res.status(500).send({ message: err.message });
    });
};

function sendVerificationEmail(user) {
  const verificationLink = `${process.env.BASE_URL}/api/auth/verify-email?token=${user.emailVerificationToken}`;

  const mailOptions = {
    from: process.env.EMAIL_SENDER,
    to: user.email,
    subject: "Verifikasi Email",
    text: `Klik tautan berikut untuk memverifikasi email anda: ${verificationLink}`,
    html: `<p>Klik tautan berikut untuk memverifikasi email anda:</p><a href="${verificationLink}">${verificationLink}</a>`,
  };

  transporter.sendMail(mailOptions, (error, info) => {
    if (error) {
      console.log(error);
      // Handle error
    }
    console.log("Email telah dikirimkan: " + info.response);
  });
}

exports.verifyEmail = (req, res) => {
  const { token } = req.query;

  User.findOne({
    where: { emailVerificationToken: token },
  })
    .then((user) => {
      if (!user) {
        return res.status(404).send({ message: "Token verifikasi tidak valid!" });
      }

      user.emailVerified = true;
      user.emailVerificationToken = null;
      user.save();

      res.status(200).send({ message: "Email berhasil diverifikasi!" });
    })
    .catch((err) => {
      res.status(500).send({ message: err.message });
    });
};

exports.signin = (req, res) => {
  User.findOne({
    where: {
      username: req.body.username,
    },
  })
    .then((user) => {
      if (!user) {
        return res.status(404).send({ message: "User tidak ditemukan!" });
      }
      let passwordIsValid = bcrypt.compareSync(
        req.body.password,
        user.password
      );
      if (!passwordIsValid) {
        return res.status(401).send({
          accessToken: null,
          message: "Passsword tidak valid!",
        });
      }
      let token = jwt.sign({ id: user.id }, config.secret, {
        expiresIn: 86400, // 24 hours
      });

      let authorities = [];
      user.getRoles().then((roles) => {
        for (let i = 0; i < roles.length; i++) {
          authorities.push("ROLE_" + roles[i].name.toUpperCase());
        }
        res.status(200).send({
          id: user.id,
          username: user.username,
          email: user.email,
          dob: user.dob,
          phone: user.phone,
          greeting: user.greeting,
          roles: authorities,
          accessToken: token,
          emailVerified: user.emailVerified,
          emailVerificationToken: user.emailVerificationToken
        });
      });
    })
    .catch((err) => {
      res.status(500).send({ message: err.message });
    });
};

exports.logout = (req, res) => {
    const token = req.headers["x-access-token"];
    if (!token) {
      return res.status(401).send({ message: "Tidak ada token yang disediakan!" });
    }

    // Invalidate the token by adding it to the blacklist
    authJwt.invalidateToken(token);
  
    res.status(200).send({ message: "Logout berhasil!" });
  };

const db = require("../models");
const config = require("../config/auth.config");
const jwt = require("jsonwebtoken");
const nodemailer = require("nodemailer");
const bcrypt = require("bcryptjs");
const { v4: uuidv4 } = require("uuid");
const crypto = require("crypto");
const User = db.user;
const Role = db.role;
const Op = db.Sequelize.Op;
const { authJwt } = require("../middleware");
const dotenv = require("dotenv");
dotenv.config();

const transporter = nodemailer.createTransport({
  service: "gmail",
  auth: {
    user: "bgsp_aditya@student.uns.ac.id",
    pass: "kkxv jrgx ivfb zrdr",
  },
});

// Function to generate a unique reset token
const generateResetToken = () => uuidv4();

function sendVerificationEmail(user) {
  const verificationLink = `http://localhost:8081/api/auth/verify-email?token=${user.emailVerificationToken}`;

  const mailOptions = {
    from: "bgsp_aditya@student.uns.ac.id",
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

exports.forgotPassword = async (req, res) => {
  const { email } = req.body;

  try {
    const user = await db.User.findOne({ where: { email } });

    if (!user) {
      return res.status(404).send({ message: "Pengguna tidak ditemukan!" });
    }

    // Generate a unique reset token
    const resetToken = generateResetToken();

    // Save the resetToken and expiration time to the user in your database
    user.resetToken = resetToken;
    user.resetTokenExpires = Date.now() + 3600000; // Token expires in 1 hour

    await user.save();

    const resetLink = `http://localhost:8081/reset-password?token=${resetToken}`;

    const mailOptions = {
      from: "bgsp_aditya@student.uns.ac.id",
      to: user.email,
      subject: "Reset Password",
      text: `Anda menerima email ini karena mengajukan reset password. Tolong klik tautan berikut untuk mereset password akun anda: ${resetLink}`,
      html: `<p>Anda menerima email ini karena mengajukan reset password. Tolong klik tautan berikut untuk mereset password akun anda:</p><a href="${resetLink}">${resetLink}</a>`,
    };

    await transporter.sendMail(mailOptions);

    res.send({ message: "Email berhasil dikirimkan", resetToken });
  } catch (err) {
    console.log(err);
    res.status(500).send({ message: "Kesalahan server internal!" });
  }
};

exports.resetPassword = async (req, res) => {
  const { token, newPassword } = req.body;

  try {
    const user = await db.User.findOne({
      where: {
        resetToken: token,
        resetTokenExpires: { [Op.gte]: Date.now() },
      },
    });

    if (!user) {
      return res.status(400).send({ message: "Token salah atau kadaluarsa!" });
    }

    // Update user's password
    const hashedPassword = bcrypt.hashSync(newPassword, 8);
    user.password = hashedPassword;
    user.resetToken = null;
    user.resetTokenExpires = null;

    await user.save();

    res.status(200).json({ message: "Password berhasil diubah!" });
  } catch (err) {
    console.log(err);
    res.status(500).send({ message: "Kesalahan server internal!" });
  }
};

exports.signup = async (req, res) => {
  const { username, email, password, dob, phone, greeting, roles } = req.body;

  try {
    const user = await db.User.create({
      username,
      email,
      password: bcrypt.hashSync(password, 8),
      dob,
      phone,
      greeting,
      emailVerificationToken: uuidv4(),
    });

    sendVerificationEmail(user);

    if (roles && roles.length > 0) {
      const foundRoles = await db.Role.findAll({
        where: {
          name: {
            [Op.or]: roles,
          },
        },
      });

      await user.setRoles(foundRoles);
    } else {
      // Default, user role = 1
      const defaultRole = await db.Role.findOne({
        where: { name: "user" },
      });

      await user.setRoles([defaultRole]);
    }

    res.send({ message: "User berhasil didaftarkan!" });
  } catch (err) {
    res.status(500).send({ message: err.message });
  }
};

exports.verifyEmail = async (req, res) => {
  const { token } = req.query;

  try {
    const user = await db.User.findOne({
      where: { emailVerificationToken: token },
    });

    if (!user) {
      return res.status(404).send({ message: "Token verifikasi tidak valid!" });
    }

    user.emailVerified = true;
    user.emailVerificationToken = null;
    await user.save();

    res.status(200).send({ message: "Email berhasil diverifikasi!" });
  } catch (err) {
    res.status(500).send({ message: err.message });
  }
};

exports.signin = async (req, res) => {
  const { username, password } = req.body;

  try {
    const user = await db.User.findOne({ where: { username } });

    if (!user) {
      return res.status(404).send({ message: "User tidak ditemukan!" });
    }

    const passwordIsValid = bcrypt.compareSync(password, user.password);

    if (!passwordIsValid) {
      return res.status(401).send({
        accessToken: null,
        message: "Passsword tidak valid!",
      });
    }
    const roles = await user.getRoles();
    const authorities = Array.isArray(roles)
      ? roles.map((role) => `ROLE_${role.name.toUpperCase()}`)
      : [];

    const token = jwt.sign({ id: user.id }, config.secret, {
      expiresIn: 8400, // 24 hours
    });

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
      emailVerificationToken: user.emailVerificationToken,
    });
  } catch (err) {
    res.status(500).send({ message: err.message });
  }
};

exports.logout = (req, res) => {
  const token = req.headers["x-access-token"];

  if (!token) {
    return res
      .status(401)
      .send({ message: "Tidak ada token yang disediakan!" });
  }

  // Invalidate the token by adding it to the blacklist
  authJwt.invalidateToken(token);

  res.status(200).send({ message: "Logout berhasil!" });
};
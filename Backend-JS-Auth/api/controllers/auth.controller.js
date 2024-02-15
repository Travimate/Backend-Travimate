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
    user: "travimate.synrgy@gmail.com",
    pass: "rurf lsps hnkj rtla",
  },
});

const generateResetToken = () => uuidv4();

function sendVerificationEmail(user) {
  const verificationLink = `${process.env.BASE_URL}/api/auth/verify-email?token=${user.emailVerificationToken}`;

  const mailOptions = {
    from: "travimate.synrgy@gmail.com",
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
      return res.status(404).send({ status: 404, message: "Pengguna tidak ditemukan!" });
    }

    // Generate a unique reset token
    const resetToken = generateResetToken();

    // Save the resetToken and expiration time to the user in your database
    user.resetToken = resetToken;
    user.resetTokenExpires = Date.now() + 3600000; // Token expires in 1 hour

    await user.save();

    const resetLink = `http://localhost:8081/reset-password?token=${resetToken}`;

    const mailOptions = {
      from: "travimate.synrgy@gmail.com",
      to: user.email,
      subject: "Reset Password",
      text: `Anda menerima email ini karena mengajukan reset password. Tolong klik tautan berikut untuk mereset password akun anda: ${resetLink}`,
      html: `<p>Anda menerima email ini karena mengajukan reset password. Tolong klik tautan berikut untuk mereset password akun anda:</p><a href="${resetLink}">${resetLink}</a>`,
    };

    await transporter.sendMail(mailOptions);

    res.send({ message: "Email berhasil dikirimkan", resetToken });
  } catch (err) {
    console.log(err);
    res.status(500).send({ status: 500, message: "Kesalahan server internal!" });
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
      return res.status(400).send({ status: 400, message: "Token salah atau kadaluarsa!" });
    }

    // Update user's password
    const hashedPassword = bcrypt.hashSync(newPassword, 8);
    user.password = hashedPassword;
    user.resetToken = null;
    user.resetTokenExpires = null;

    await user.save();

    res.status(200).json({ status: 200, message: "Password berhasil diubah!" });
  } catch (err) {
    console.log(err);
    res.status(500).send({ status: 500, message: "Kesalahan server internal!" });
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
      pp: "https://res.cloudinary.com/dmta1mm4p/image/upload/v1707805375/slbzq9etce6o00r2mkw4.png",
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

    res.status(200).send({ status: 200, message: "User berhasil didaftarkan!" });
  } catch (err) {
    res.status(500).send({ status: 500, message: err.message });
  }
};

exports.verifyEmail = async (req, res) => {
  const { token } = req.query;

  try {
    const user = await db.User.findOne({
      where: { emailVerificationToken: token },
    });

    if (!user) {
      return res.status(404).send({ status: 404, message: "Token verifikasi tidak valid!" });
    }

    user.emailVerified = true;
    user.emailVerificationToken = null;
    await user.save();

    res.status(200).send({ status: 200, message: "Email berhasil diverifikasi!" });
  } catch (err) {
    res.status(500).send({ status: 500, message: err.message });
  }
};

exports.signin = async (req, res) => {
  const { email, password } = req.body;

  try {
    const user = await db.User.findOne({ where: { email } });

    if (!user) {
      return res.status(404).send({ status: 404, message: "User tidak ditemukan!" });
    }

    const passwordIsValid = bcrypt.compareSync(password, user.password);

    if (!passwordIsValid) {
      return res.status(401).send({
        status: 401,
        accessToken: null,
        message: "Passsword tidak valid!",
      });
    }
    const roles = await user.getRoles();
    const authorities = Array.isArray(roles)
      ? roles.map((role) => `ROLE_${role.name.toUpperCase()}`)
      : [];

    const token = jwt.sign(
      {
        id: user.id,
        authorities: authorities,
      },
      config.secret,
      {
        expiresIn: 8400, // 24 hours
      }
    );

    res.status(200).send({
      status: 200,
      // id: user.id,
      username: user.username,
      email: user.email,
      dob: user.dob,
      phone: user.phone,
      greeting: user.greeting,
      pp: user.pp,
      roles: authorities,
      accessToken: token,
      emailVerified: user.emailVerified,
      emailVerificationToken: user.emailVerificationToken,
    });
  } catch (err) {
    res.status(500).send({ status: 500, message: err.message });
  }
};

exports.logout = (req, res) => {
  const token = req.headers["x-access-token"];

  if (!token) {
    return res
      .status(401)
      .send({ status: 401, message: "Tidak ada token yang disediakan!" });
  }

  // Invalidate the token by adding it to the blacklist
  authJwt.invalidateToken(token);

  res.status(200).send({ status: 200, message: "Logout berhasil!" });
};

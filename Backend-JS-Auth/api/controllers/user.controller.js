const db = require("../models");
const User = db.user;
const Role = db.role;
const Op = db.Sequelize.Op;
const jwt = require("jsonwebtoken");
const config = require("../config/auth.config.js");

exports.findAll = async (req, res) => {
  try {
    const data = await db.User.findAll();
    res.send(data);
  } catch (err) {
    res.status(500).send({
      message: err.message || "Terjadi error saat mengambil data user!",
    });
  }
};

exports.authMe = async (req, res) => {
  try {
    const user = await db.User.findByPk(req.userId);

    if (!user) {
      return res.status(404).json({ message: "User tidak ditemukan!" });
    }

    const currentTime = Math.floor(Date.now() / 1000);

    if (req.user.exp > currentTime) {
      const remainingTime = req.user.exp - currentTime;
      res.status(200).json({
        id: user.id,
        username: user.username,
        email: user.email,
        dob: user.dob,
        phone: user.phone,
        greeting: user.greeting,
        roles: user.roles,
        remainingTime,
      });
    } else {
      res.status(401).json({ message: "Token kadaluarsa. Silakan login kembali!" });
    }
  } catch (err) {
    console.error(err);
    res.status(500).json({ message: "Internal Server Error" });
  }
};

exports.findOne = async (req, res) => {
  const id = req.params.id;

  try {
    const data = await db.User.findByPk(id);
    res.send(data);
  } catch (err) {
    res.status(500).send({
      message: "Terjadi error saat mengambil data user dengan id=" + id,
    });
  }
};


exports.update = async (req, res) => {
  const id = req.params.id;

  // Ensure that the logged-in user can only update their own account
  if (req.userId != id) {
    return res.status(403).send({
      message: "Anda hanya dapat mengatur akun anda sendiri!",
    });
  }

  try {
    const [num] = await db.User.update(req.body, {
      where: { id: id },
    });

    if (num === 1) {
      res.send({
        message: "Data user berhasil diubah!",
      });
    } else {
      res.send({
        message: `Tidak dapat mengatur data user dengan id=${id}. Mungkin user tidak ditemukan atau request kosong`,
      });
    }
  } catch (err) {
    res.status(500).send({
      message: "Gagal mengubah data user dengan id=" + id,
    });
  }
};

exports.delete = async (req, res) => {
  const id = req.params.id;

  try {
    const num = await db.User.destroy({
      where: { id: id },
    });

    if (num === 1) {
      res.send({
        message: "Data user berhasil dihapus!",
      });
    } else {
      res.send({
        message: `Gagal menghapus data user dengan id=${id}. Mungkin user tidak ditemukan!`,
      });
    }
  } catch (err) {
    res.status(500).send({
      message: "Gagal menghapus data user dengan id=" + id,
    });
  }
};
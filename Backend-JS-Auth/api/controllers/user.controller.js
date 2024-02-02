const db = require("../models");
const User = db.user;
const Op = db.Sequelize.Op;

exports.findAll = (req, res) => {
  User.findAll()
    .then((data) => {
      res.send(data);
    })
    .catch((err) => {
      res.status(500).send({
        message: err.message || "Terjadi error saat mengambil data user!",
      });
    });
};
exports.findOne = (req, res) => {
  const id = req.params.id;
  User.findByPk(id)
    .then((data) => {
      res.send(data);
    })
    .catch((err) => {
      res.status(500).send({
        message: "Terjadi error saat mengambil data user dengan id=" + id,
      });
    });
};

exports.update = (req, res) => {
  const id = req.params.id;
  // Ensure that the logged-in user can only update their own account
  if (req.userId != id) {
    return res.status(403).send({
      message: "Anda hanya dapat mengatur akun andaa sendiri!",
    });
  }
  User.update(req.body, {
    where: { id: id },
  })
    .then((num) => {
      if (num == 1) {
        res.send({
          message: "Data user berhasil diubah!",
        });
      } else {
        res.send({
          message: `Tidak dapat mengatur data user dengan id =${id}. Mungkin user tidak ditemukan atau request kosong`,
        });
      }
    })
    .catch((err) => {
      res.status(500).send({
        message: "Gagal mengubah data user dengan id=" + id,
      });
    });
};

exports.delete = (req, res) => {
  const id = req.params.id;
  User.destroy({
    where: { id: id },
  })
    .then((num) => {
      if (num == 1) {
        res.send({
          message: "Data user berhasil dihapus!",
        });
      } else {
        res.send({
          message: `Gagal menghapus data user dengan id=${id}. Mungkin user tidak ditemukan!`,
        });
      }
    })
    .catch((err) => {
      res.status(500).send({
        message: "Gagal menghapus data user dengan id=" + id,
      });
    });
};

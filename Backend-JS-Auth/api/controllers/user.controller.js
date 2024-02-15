const db = require("../models");
const User = db.user;
const Role = db.role;
const Op = db.Sequelize.Op;
const jwt = require("jsonwebtoken");
const cloudinary = require("cloudinary").v2;
const multer = require('multer');
const upload = multer();

const config = require("../config/auth.config.js");

// Initialize Cloudinary
// cloudinary.config({
//   cloud_name: process.env.CLOUDINARY_CLOUD_NAME,
//   api_key: process.env.CLOUDINARY_API_KEY,
//   api_secret: process.env.CLOUDINARY_API_SECRET
// });  
cloudinary.config({ 
  cloud_name: 'dmta1mm4p', 
  api_key: '831129951586995', 
  api_secret: 'NN7J5tqix4MEzgO92EaxSbGe_jo' 
});

exports.findAll = async (req, res) => {
  try {
    const data = await db.User.findAll();
    res.send(data);
  } catch (err) {
    res.status(500).send({
      status: 500, message: err.message || "Terjadi error saat mengambil data user!",
    });
  }
};

exports.authMe = async (req, res) => {
  try {
    const user = await db.User.findByPk(req.userId);

    if (!user) {
      return res.status(404).json({ status: 404, message: "User tidak ditemukan!" });
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
        pp: user.pp,
        roles: user.roles,
        remainingTime,
      });
    } else {
      res.status(401).json({ status: 401, message: "Token kadaluarsa. Silakan login kembali!" });
    }
  } catch (err) {
    console.error(err);
    res.status(500).json({ status: 500, message: "Internal Server Error" });
  }
};

exports.findOne = async (req, res) => {
  const id = req.params.id;

  try {
    const data = await db.User.findByPk(id);
    res.send(data);
  } catch (err) {
    res.status(500).send({
      status: 500,
      message: "Terjadi error saat mengambil data user dengan id=" + id,
    });
  }
};

exports.update = async (req, res) => {
  const id = req.params.id;

  // Ensure that the logged-in user can only update their own account
  if (req.userId != id) {
    return res.status(403).send({
      status: 403,
      message: "Anda hanya dapat mengatur akun anda sendiri!",
    });
  }

  try {
    let ppUrl = null; // Initialize with null

    // Check if a file was uploaded
    if (req.file) {
      const result = await new Promise((resolve, reject) => {
        cloudinary.uploader.upload_stream({ folder: 'profile_picture' }, (error, result) => {
          if (error) {
            reject(error);
          } else {
            resolve(result);
          }
        }).end(req.file.buffer);
      });
      ppUrl = result.secure_url; // Update profile picture URL
    } else {
      // Fetch the user's current profile picture URL from the database
      const user = await db.User.findByPk(id);
      if (user) {
        ppUrl = user.pp; // Use the existing profile picture URL
      }
    }
    // Check if req.body exists (JSON data)
    const requestBody = req.body ? req.body : {};

    const [num] = await db.User.update({
      ...requestBody, 
      pp: ppUrl
    },
    {
      where: { id: id }
    },
    );

    if (num === 1) {
      res.status(200).send({
        status: 200,
        message: "Data user berhasil diubah!",
      });
    } else {
      res.status(401).send({
        status: 401,
        message: `Tidak dapat mengatur data user dengan id=${id}. Mungkin user tidak ditemukan atau request kosong`,
      });
    }
  } catch (err) {
    console.log(err.message);
    res.status(500).send({
      status: 500,
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
      res.status(200).send({
        status: 200,
        message: "Data user berhasil dihapus!",
      });
    } else {
      res.status(401).send({
        status: 401,
        message: `Gagal menghapus data user dengan id=${id}. Mungkin user tidak ditemukan!`,
      });
    }
  } catch (err) {
    res.status(500).send({
      status: 500,
      message: "Gagal menghapus data user dengan id=" + id,
    });
  }
};
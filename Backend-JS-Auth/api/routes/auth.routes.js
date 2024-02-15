/**
 * @swagger
 * components:
 *   schemas:
 *     User:
 *       type: object
 *       required:
 *         - username
 *         - email
 *         - password
 *       properties:
 *         id:
 *           type: integer
 *           description: The auto-generated id of the user
 *         username:
 *           type: string
 *           description: The username of user
 *         email:
 *           type: string
 *           description: The email of user
 *         password:
 *           type: string
 *           description: The password provided by user. Password must be at least 6 character, have uppercase, have lowercase, have special character, and have number
 *         dob:
 *           type: string
 *           format: date
 *           description: The date of birth of user
 *         phone:
 *           type: string
 *           description: The phone number of user
 *         greeting:
 *           type: string
 *           description: The greeting of every user
 *         roles:
 *           type: array
 *           items:
 *             type: string
 *           description: The role of user
 *       example:
 *         username: bytadit123
 *         email: bytadit@gmail.com
 *         password: Bytadit_123
 *         dob: 2024-01-16
 *         phone: 085123455566
 *         greeting: nyonya
 *         roles: ["admin"]
 *     Reset:
 *       type: object
 *       required:
 *         - token
 *         - newPassword
 *       properties:
 *         token:
 *           type: string
 *           description: Token lupa password
 *         newPassword:
 *           type: string
 *           description: Password baru
 *       example:
 *         token: inputGeneratedToken
 *         newPassword: Passbaru_123
 */
/**
 * @swagger
 * /api/auth/signup:
 *   post:
 *     summary: Mendaftarkan user baru
 *     tags: [Authentication]
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/User'
 *     responses:
 *       200:
 *         description: Registrasi user berhasil
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/User'
 *       500:
 *         description: Terjadi kesalahan server
 * /api/auth/signin:
 *   post:
 *     summary: Masuk ke akun user
 *     tags: [Authentication]
 *     requestBody:
 *       required: true
 *       content: 
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/User'
 *     responses:
 *       200:
 *         description: User masuk akun.
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/User'
 *       500:
 *         description: Kesalahan server!
 * /api/users:
 *   get:
 *     summary: Melihat seluruh data user oleh admin
 *     tags: [User Management]
 *     parameters:
 *       - in: header
 *         name: x-access-token
 *         schema:
 *           type: string
 *         required: true
 *         description: Token login user
 *     responses:
 *       200:
 *         description: Daftar seluruh user
 *         content:
 *           application/json:
 *             schema:
 *               type: array
 *               items:
 *                 $ref: '#/components/schemas/User'
 * /api/auth/me:
 *   get:
 *     summary: Melihat profil pribadi, dan mengecek apakah token expired
 *     tags: [Authentication]
 *     parameters:
 *       - in: header
 *         name: x-access-token
 *         schema:
 *           type: string
 *         required: true
 *         description: Token login user
 *     responses:
 *       200:
 *         description: Informasi Login User
 *         content:
 *           application/json:
 *             schema:
 *               type: array
 *               items:
 *                 $ref: '#/components/schemas/User'
 * /api/auth/verify-email?token={emailVerificationToken}:
 *   get:
 *     summary: Verifikasi email
 *     tags: [Authentication]
 *     parameters:
 *       - in: path
 *         name: emailVerificationToken
 *         schema:
 *           type: string
 *         required: true
 *         description: Token verifikasi email
 *     responses:
 *       200:
 *         description: Verifikasi email menggunakan token
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/User'
 *       404:
 *         description: Email tidak dapat terverifikasi
 * /api/auth/forgot-password:
 *   post:
 *     summary: Lupa password user
 *     tags: [Authentication]
 *     requestBody:
 *       required: true
 *       content: 
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/User'
 *     responses:
 *       200:
 *         description: User lupa password
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/User'
 *       500:
 *         description: Kesalahan server!
 * /api/auth/reset-password:
 *   post:
 *     summary: Reset password user
 *     tags: [Authentication]
 *     requestBody:
 *       required: true
 *       content: 
 *         application/json:
 *           schema:
 *             $ref: '#/components/schemas/Reset'
 *     responses:
 *       200:
 *         description: Password user telah
 *         content:
 *           application/json:
 *             schema:
 *               $ref: '#/components/schemas/Reset'
 *       500:
 *         description: Kesalahan server!
 * /api/auth/logout:
 *   post:
 *     summary: Keluar dari akun user
 *     tags: [Authentication]
 *     parameters:
 *       - in: header
 *         name: x-access-token
 *         schema:
 *           type: string
 *         required: true
 *         description: Token login user
 *     responses:
 *       200:
 *         description: User telah keluar dari akun
 *         content:
 *           application/json:
 *             schema:
 *               type: array
 *               items:
 *                 $ref: '#/components/schemas/User'
 * /api/users/{id}:
 *   get:
 *    summary: Lihat data user by id
 *    tags: [User Management]
 *    parameters:
 *       - in: header
 *         name: x-access-token
 *         schema:
 *           type: string
 *         required: true
 *         description: Token login user
 *       - in: path
 *         name: id
 *         schema:
 *           type: string
 *         required: true
 *         description: Id user
 *    requestBody:
 *      required: true
 *      content:
 *        application/json:
 *          schema:
 *            $ref: '#/components/schemas/User'
 *    responses:
 *      200:
 *        description: Data user berhasil ditemukan
 *        content:
 *          application/json:
 *            schema:
 *              $ref: '#/components/schemas/User'
 *      404:
 *        description: Data user tidak ditemukan
 *      500:
 *        description: Terjadi kesalahan
 *   put:
 *    summary: Update data user by id
 *    tags: [User Management]
 *    parameters:
 *       - in: header
 *         name: x-access-token
 *         schema:
 *           type: string
 *         required: true
 *         description: Token login user
 *       - in: path
 *         name: id
 *         schema:
 *           type: string
 *         required: true
 *         description: Id user
 *    requestBody:
 *      required: true
 *      content:
 *        application/json:
 *          schema:
 *            $ref: '#/components/schemas/User'
 *    responses:
 *      200:
 *        description: Data user berhasil diupdate
 *        content:
 *          application/json:
 *            schema:
 *              $ref: '#/components/schemas/User'
 *      404:
 *        description: Data user tidak ditemukan
 *      500:
 *        description: Terjadi kesalahan
 *   delete:
 *     summary: Hapus data user
 *     tags: [User Management]
 *     parameters:
 *       - in: header
 *         name: x-access-token
 *         schema:
 *           type: string
 *         required: true
 *         description: Token login user
 *       - in: path
 *         name: id
 *         schema:
 *           type: string
 *         required: true
 *         description: Id user
 *     responses:
 *       200:
 *         description: Data user berhasil dihapus
 *       404:
 *         description: Data user tidak ditemukan
 */


const { verifySignUp, authJwt } = require("../middleware");
const controller = require("../controllers/auth.controller");

module.exports = function (app) {
  app.use(function (req, res, next) {
    res.header(
      "Access-Control-Allow-Headers",
      "x-access-token, Origin, Content-Type, Accept"
    );
    next();
  });

  app.post(
    "/api/auth/signup",
    [
      verifySignUp.checkDuplicateUsernameOrEmail,
      verifySignUp.checkRolesExisted,
      verifySignUp.validatePassword,
    ],
    controller.signup
  );
  app.get("/api/auth/verify-email", controller.verifyEmail);
  app.post("/api/auth/forgot-password", controller.forgotPassword);
  app.post("/api/auth/reset-password", controller.resetPassword);

  app.post("/api/auth/signin", controller.signin);
  // Logout route
  app.post("/api/auth/logout", [authJwt.authCheck], controller.logout);
};

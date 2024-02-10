const { execSync } = require("child_process");
const path = require("path");

const sequelizeConfigPath = path.join(
  __dirname,
  "api",
  "config",
  "config.json"
);
const migrationsPath = path.join(__dirname, "api", "migrations"); // Adjusted path here
const seedersPath = path.join(__dirname, "api", "seeders");

// Check if the database exists
try {
  execSync(`npx sequelize db:create --config "${sequelizeConfigPath}"`);
  console.log("Database created successfully.");
  // Run migrations if needed
  try {
    execSync(
      `npx sequelize db:migrate --config "${sequelizeConfigPath}" --migrations-path "${migrationsPath}"`
    );
    console.log("Migrations executed successfully.");
    try {
      execSync(
        `npx sequelize db:seed:all --config "${sequelizeConfigPath}" --seeders-path "${seedersPath}"`
      );
      console.log("Seeders executed successfully.");
    } catch (error) {
      console.log("Seeders not executed.");
    }
  } catch (error) {
    console.error("Error running migrations:", error.stderr.toString());
    process.exit(1); // Exit the process with an error code
  }
} catch (error) {
  console.log("Database already exists.");
  try {
    execSync(
      `npx sequelize db:migrate --config "${sequelizeConfigPath}" --migrations-path "${migrationsPath}"`
    );
    console.log("Migrations executed successfully.");
    try {
      execSync(
        `npx sequelize db:seed:all --config "${sequelizeConfigPath}" --seeders-path "${seedersPath}"`
      );
      console.log("Seeders executed successfully.");
    } catch (error) {
      console.log("Seeders not executed.");
    }
  } catch (error) {
    console.error("Error running migrations:", error.stderr.toString());
    process.exit(1); // Exit the process with an error code
  }
}

require("./api/index.js");

// Check if migrations exist
// Check if migrations exist
// try {
//   execSync(
//     `npx sequelize db:migrate:status --config "${sequelizeConfigPath}" --migrations-path "${migrationsPath}"`
//   );
//   console.log("Migrations exist.");
// } catch (error) {
//   if (error.status === 1) {
//     console.log("No migrations found.");
//   } else {
//     console.error("Error checking migrations:", error.stderr.toString());
//     process.exit(1); // Exit the process with an error code
//   }
// }

// // Run migrations if needed
// try {
//   execSync(
//     `npx sequelize db:migrate --config "${sequelizeConfigPath}" --migrations-path "${migrationsPath}"`
//   );
//   console.log("Migrations executed successfully.");
// } catch (error) {
//   console.error("Error running migrations:", error.stderr.toString());
//   process.exit(1); // Exit the process with an error code
// }

// Check if seeders exist and run seeders
// try {
//   execSync(
//     `npx sequelize db:seed:all --config "${sequelizeConfigPath}" --seeders-path "${seedersPath}"`
//   );
//   console.log("Seeders executed successfully.");
// } catch (error) {
//   console.log("Seeders not executed.");
// }

// Start your application using nodemon

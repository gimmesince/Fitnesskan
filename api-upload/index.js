const cors = require("cors");
const express = require("express");
const app = express();

let corsOptions = {
  origin: "https://backend-upload-dot-c23-ps021-387009.et.r.appspot.com",
};

app.use(cors(corsOptions));

const initRoutes = require("./src/routes");

app.use(express.urlencoded({ extended: true }));
initRoutes(app);

const port = 8080;
app.listen(port, () => {
  console.log(`Running at:${port}`);
});
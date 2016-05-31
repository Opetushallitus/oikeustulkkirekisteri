var webpackDevServer = require("webpack-dev-server");
var webpack = require('webpack');
var config = require("./webpack.config.js");
var compiler = webpack(config);
var server = new webpackDevServer(compiler, {
    hot: true,
    proxy: {
        '/oikeustulkkirekisteri-service/*': {
            target: 'http://localhost:8080',
            secure: false
        }
    }
});
server.listen(9000);

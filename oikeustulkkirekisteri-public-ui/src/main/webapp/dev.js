var webpackDevServer = require("webpack-dev-server");
var webpack = require('webpack');
var config = require("./webpack.config.js");
var compiler = webpack(config);
var server = new webpackDevServer(compiler, {
    proxy: {
        '/oikeustulkkirekisteri-service/*': {
            target: 'http://localhost:8080',
            secure: false
        }
    },
    progress: true,
    inline: true,
    watchOptions: {
        aggregateTimeout: 300,
        poll: 1000
    },
    stats: { colors: true }
});
server.listen(9010);

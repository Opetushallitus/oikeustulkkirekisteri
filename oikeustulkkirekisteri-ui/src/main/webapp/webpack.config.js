module.exports = {
    entry: {
        "app.min": "./js/entry.js",
        "lib.min": "./js/lib.js"
    },
    devtool: "source-map",
    output: {
        path: __dirname + "/dist",
        filename: "[name].js"
    },
    module: {
        loaders: [
            { test: /\.css$/, loader: "style!css" }
        ]
    },
    proxy: {
        '/oikeustulkkirekisteri-service/*': {
            target: 'http://localhost:8080/oikeustulkkirekisteri-service/',
            secure: false,
            bypass: function (req, res, proxyOptions) {
                if (req.headers.accept.indexOf('html') !== -1) {
                    console.log('Skipping proxy for browser request.');
                    return '/index.html';
                }
            }
        }
    }
};
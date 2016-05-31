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
    }
};
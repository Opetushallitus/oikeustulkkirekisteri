var path = require('path'),
    webpack = require('webpack'),
    ExtractTextPlugin = require("extract-text-webpack-plugin");

module.exports = {
    entry: {
        'app.min': ['./js/entry.js'],
        'lib.min': ['./js/lib.js']
    },
    devtool: 'source-map',
    output: {
        path: (__dirname||'.')+'/dist',
        filename: '[name].js',
        chunkFilename: '[id].js',
        publicPath: '.'
    },
    module: {
        loaders: [
            { test: /\.ts$/, loader: 'ts-loader' },
            { test: /\.css$/, loader: ExtractTextPlugin.extract("style", "css")},
            { test: /\.scss$/, loader: ExtractTextPlugin.extract("style", "css!sass")},
            { test: /\.(png|woff|woff2|eot|ttf|svg)$/, loader: 'url-loader?limit=100000' }
        ]
    },
    plugins: [ 
        new webpack.HotModuleReplacementPlugin(),
        new webpack.NoErrorsPlugin(),
        new ExtractTextPlugin("styles.min.css"),
        new webpack.ProvidePlugin({
            _: 'lodash'
        })
    ]
};
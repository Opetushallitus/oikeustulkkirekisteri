var path = require('path'),
    webpack = require('webpack');

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
            { test: /\.css$/, loader: 'style!css' },
            { test: /\.ts$/, loader: 'typescript-loader' },
            { test: /\.scss$/, loader: "style!css!sass"},
            { test: /\.(png|woff|woff2|eot|ttf|svg)$/, loader: 'url-loader?limit=100000' }
        ]
    },
    plugins: [ 
        new webpack.HotModuleReplacementPlugin(),
        new webpack.NoErrorsPlugin()
    ]
};
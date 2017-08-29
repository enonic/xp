var extractTextPlugin = require('extract-text-webpack-plugin');

module.exports = {
    entry: {
        home: './src/main/resources/assets/js/home/main.js',
        launcher: './src/main/resources/assets/js/launcher/main.js'
    },
    output: {
        path: __dirname + '/build/resources/main/assets/bundle',
        filename: '[name].js'
    },
    resolve: {
        extensions: ['.js']
    },
    module: {
        rules: [{
            test: /.less$/,
            loader: extractTextPlugin.extract({
                fallback: 'style-loader',
                use: ['css-loader', 'less-loader']
            })
        }, {
            test: /\.(png|woff|woff2|eot|ttf|svg)$/,
            loader: 'file-loader',
            options: {
                name: 'images/[name].[ext]'
            }
        }]
    },
    plugins: [
        new extractTextPlugin('[name].css')
    ],
    devtool: 'source-map'
};

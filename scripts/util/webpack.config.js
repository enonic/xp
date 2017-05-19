const RelativeErrorsWebpackPlugin = require('./relativeErrorsWebpackPlugin');

module.exports = {
    entry: './src/main/js/main.ts',
    output: {
        filename: './build/resources/main/assets/app/bundle.js'
    },
    resolve: {
        extensions: ['.ts', '.js']
    },
    module: {
        rules: [{
            test: /\.ts$/,
            loader: 'ts-loader',
            exclude: /node_modules/,
        }]
    },
    plugins: [
        RelativeErrorsWebpackPlugin
    ],
    devtool: 'source-map'
};

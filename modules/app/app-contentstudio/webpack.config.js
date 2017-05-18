const RelativeErrorsWebpackPlugin = require('../../../scripts/util/relativeErrorsWebpackPlugin');

module.exports = {
    entry: './src/main/js/main.ts',
    output: {
        filename: './build/resources/main/assets/app/bundle.js'
    },
    resolve: {
        extensions: ['.ts', '.js']
    },
    module: {
        loaders: [
            {test: /\.tsx?$/, loader: "ts-loader"}
        ]
    },
    plugins: [
        RelativeErrorsWebpackPlugin
    ],
    devtool: 'source-map'
};

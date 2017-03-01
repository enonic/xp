module.exports = {
    entry: './src/main/js/main.ts',
    output: {
        filename: './build/app/bundle.js'
    },
    resolve: {
        extensions: ['.ts', '.js']
    },
    module: {
        loaders: [
            {test: /\.tsx?$/, loader: "ts-loader"}
        ]
    },
    devtool: 'source-map'
};

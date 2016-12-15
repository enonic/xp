var forOwn = require("lodash.forown");
var CONFIG = require("../config");
var path = require("path");

module.exports = function (tasks) {
    var entry = {};

    forOwn(tasks, function (task) {
        var basePath = task.assets ? CONFIG.assets.src : CONFIG.root.src;
        entry[task.name] = "." + path.join('/', basePath, task.src);
    });

    // output
    var root = path.normalize(path.join('/', CONFIG.root.dest));
    var output = {filename: "." + path.join(root, CONFIG.tasks.js.webpack.dest)};

    return {
        entry: entry,
        output: output,
        resolve: {
            extensions: ['', '.js', '.ts']
        },
        devtool: 'source-map',
        module: {
            loaders: [
                {
                    test: /\.ts$/,
                    exclude: /(node_modules|bower_components)/,
                    loader: 'ts-loader'
                }
            ]
        },
        stats: {
            colors: true
        },
        progress: false
    };
};

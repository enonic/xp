import CONFIG from "../config";
import path from "path";

export default (tasks) => {
    // entry
    const entry = {};
    for (const name in tasks) {
        const task = tasks[name];
        const basePath = task.assets ? CONFIG.assets.src : CONFIG.root.src;
        entry[task.name] = `.${path.join('/', basePath, task.src)}`;
    }

    // output
    const output = {filename: `.${path.join('/', CONFIG.root.dest, '/apps/[name]/js/_all.js')}`};

    const config = {
        entry,
        output,
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

    return config;
};

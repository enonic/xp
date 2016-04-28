/**
 * Common variables.
 */
var baseDir = 'src/main/resources/web/admin';
var assetsDir = 'src/main/resources/assets';

/**
 * Clean all build files.
 */
var clean = {
    all: {
        src: [baseDir + '/**/_all.*']
    }
};

/**
 * Concat commons lib file.
 */
var directives = {
    common: {
        src: baseDir + '/common/lib/_module.js',
        dest: baseDir + '/common/lib/_all.js'
    }
};

/**
 * Less for all modules.
 */
var less = {
    all: {
        options: {
            sourceMap: true,
            relativeUrls: true
        },
        files: {}
    }
};

/**
 * Add less file paths.
 */
less.all.files[baseDir + '/common/styles/_all.css'] = baseDir + '/common/styles/_module.less';
less.all.files[baseDir + '/live-edit/styles/_all.css'] = baseDir + '/live-edit/styles/_module.less';
less.all.files[baseDir + '/common/styles/_home.css'] = baseDir + '/common/styles/apps/home/home.less';
less.all.files[baseDir + '/common/styles/api/util/htmlarea/html-editor.css'] =
    baseDir + '/common/styles/api/util/htmlarea/html-editor.module.less';
less.all.files[assetsDir + '/styles/_launcher.css'] = baseDir + '/common/styles/apps/launcher/launcher.less';

/**
 * Typescript configuration.
 */
var ts = {
    common: {
        src: baseDir + '/common/js/_module.ts',
        out: baseDir + '/common/js/_all.js',
        options: {
            declaration: true
        }
    },
    live_edit: {
        src: baseDir + '/live-edit/js/_module.ts',
        out: baseDir + '/live-edit/js/_all.js',
        options: {
            declaration: false
        }
    }
};

/**
 * Webpack configuration.
 */
var webpack = {
    all: {
        entry: {
            "home": "./src/main/resources/assets/js/home/main.js",
            "login": "./src/main/resources/web/admin/apps/login/js/main.ts",
            "launcher": "./src/main/resources/assets/js/launcher/main.js",
            "applications": "./src/main/resources/web/admin/apps/applications/js/main.ts",
            "user-manager": "./src/main/resources/web/admin/apps/user-manager/js/main.ts",
            "content-studio": "./src/main/resources/web/admin/apps/content-studio/js/main.ts"
        },
        output: {
            filename: "./target/resources/main/web/admin/apps/[name]/js/_all.js"
        },
        resolve: {
            extensions: ['', '.js', '.ts']
        },
        devtool: 'source-map',
        module: {
            loaders: [
                {
                    test: /\.ts/,
                    loaders: ['ts-loader'],
                    exclude: /node_modules/
                }
            ]
        },
        stats: {
            colors: false
        },
        progress: false
    }
};

/**
 * Newer configuration to speed up build.
 */
var newer = {
    common: {
        src: [baseDir + '/common/js/**'],
        dest: baseDir + '/common/js/_all.js',
        options: {
            tasks: ["ts:common"]
        }
    },
    live_edit: {
        src: [baseDir + '/live-edit/js/**'],
        dest: baseDir + '/live-edit/js/_all.js',
        options: {
            tasks: ["ts:live_edit"]
        }
    }
};

/**
 * Grunt configuration.
 */
module.exports = function (grunt) {

    grunt.initConfig({
        clean: clean,
        directives: directives,
        less: less,
        ts: ts,
        webpack: webpack,
        newer: newer
    });

    // Add grunt plugins
    grunt.loadNpmTasks('grunt-ts');
    grunt.loadNpmTasks('grunt-directives');
    grunt.loadNpmTasks('grunt-contrib-less');
    grunt.loadNpmTasks('grunt-contrib-clean');
    grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.loadNpmTasks("grunt-newer-explicit");
    grunt.loadNpmTasks("grunt-webpack");

    // Register aliases
    grunt.registerTask('default', 'all');
    grunt.registerTask('all', ['less', 'newer', 'directives', 'webpack']);
    grunt.registerTask('all_no_ts', ['less', 'directives']);

};

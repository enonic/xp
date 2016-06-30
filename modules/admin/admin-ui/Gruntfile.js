var DIR = {
    src: 'src/main/resources/web/admin',
    dest: 'target/resources/main/web/admin',
    assets: 'target/resources/main/assets',
};


var clean = {
    all: {
        src: [DIR.src + '/**/_all.*']
    }
};


// Concat commons lib file.
var directives = {
    common: {
        src: DIR.src + '/common/lib/_module.js',
        dest: DIR.src + '/common/lib/_all.js'
    }
};


var less = {
    all: {
        options: {
            sourceMap: true,
            relativeUrls: true
        },
        files: {}
    }
};

var autoprefixer = {
    all: {
        options: {
            browsers: ['last 2 versions', 'ie 11'],
            map: {
                inline: false,
                sourcesContent: true
            }
        },
        files: {}
    }
};


less.all.files[DIR.dest + '/common/styles/_all.css'] = DIR.src + '/common/styles/_module.less';
less.all.files[DIR.dest + '/live-edit/styles/_all.css'] = DIR.src + '/live-edit/styles/_module.less';
less.all.files[DIR.dest + '/common/styles/_home.css'] = DIR.src + '/common/styles/apps/home/home.less';
less.all.files[DIR.dest + '/common/styles/api/util/htmlarea/html-editor.css'] =
    DIR.src + '/common/styles/api/util/htmlarea/html-editor.module.less';
less.all.files[DIR.assets + '/styles/_launcher.css'] = DIR.src + '/common/styles/apps/launcher/launcher.less';

for (var keys in less.all.files) {
    autoprefixer.all.files[keys] = keys;
}


// Typescript
var ts = {
    common: {
        src: DIR.src + '/common/js/_module.ts',
        out: DIR.src + '/common/js/_all.js',
        options: {
            declaration: true
        }
    },
    live_edit: {
        src: DIR.src + '/live-edit/js/_module.ts',
        out: DIR.src + '/live-edit/js/_all.js',
        options: {
            declaration: false
        }
    }
};


var webpack = {
    all: {
        entry: {
            "home": "./src/main/resources/assets/js/home/main.js",
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


var newer = {
    common: {
        src: [DIR.src + '/common/js/**'],
        dest: DIR.src + '/common/js/_all.js',
        options: {
            tasks: ["ts:common"]
        }
    },
    live_edit: {
        src: [DIR.src + '/live-edit/js/**'],
        dest: DIR.src + '/live-edit/js/_all.js',
        options: {
            tasks: ["ts:live_edit"]
        }
    }
};


module.exports = function (grunt) {
    grunt.initConfig({
        clean: clean,
        directives: directives,
        less: less,
        autoprefixer: autoprefixer,
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
    grunt.loadNpmTasks("grunt-autoprefixer");
    grunt.loadNpmTasks("grunt-webpack");

    // Register aliases
    grunt.registerTask('default', 'all');
    grunt.registerTask('all', ['css', 'newer', 'directives', 'webpack']);
    grunt.registerTask('all_no_ts', ['css', 'directives']);
    grunt.registerTask('css', ['less', 'autoprefixer']);
};

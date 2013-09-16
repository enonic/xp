module.exports = function (grunt) {

    grunt.loadNpmTasks('grunt-ts');
    grunt.loadNpmTasks('grunt-contrib-watch');
    grunt.loadNpmTasks('grunt-karma');

    grunt.initConfig({

            ts: {
                api: {
                    src: ['src/main/js/main.ts'],
                    out: 'target/generated/all.js',
                    options: {
                        // target: 'es5',
                        sourcemap: true,
                        declaration: true
                    }
                }
            },

            karma: {
                options: {
                    browsers: ['PhantomJS'],
                    files: [
                        'target/generated/**/*.js',
                        'src/test/**/*.js'
                    ],
                    frameworks: ['jasmine'],
                    preprocessors: {
                        '**/main/**/*.js': 'coverage'
                    },
                    reporters: ['dots', 'coverage'],
                    coverageReporter: {
                        type: 'html',
                        dir: 'target/coverage/'
                    }
                },
                dev: {
                    singleRun: true
                }
            }

        }
    );

    grunt.registerTask('all', ['ts', 'karma']);

};

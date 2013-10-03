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
                },
                test: {
                    src: ['src/test/js/*.ts'],
                    outDir: 'target/generated/tests/'
                }
            },

            karma: {
                options: {
                    browsers: ['PhantomJS'],
                    files: [
                        'target/generated/tests/**/*.js'
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

    grunt.registerTask('all', ['ts']);
    grunt.registerTask('test', ['ts:test', 'karma'])
};

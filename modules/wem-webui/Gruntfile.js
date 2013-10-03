module.exports = function (grunt) {

    grunt.loadNpmTasks('grunt-ts');
    grunt.loadNpmTasks('grunt-contrib-watch');
    grunt.loadNpmTasks('grunt-karma');
    grunt.loadNpmTasks('grunt-contrib-clean');

    grunt.initConfig({

            clean: ['target'],

            ts: {
                api: {
                    src: ['../wem-webapp/src/main/webapp/admin2/api/js/main.ts'],
                    out: 'target/main/js/api.js',
                    options: {
                        sourcemap: false,
                        declaration: false
                    }
                }
            },

            karma: {
                options: {
                    browsers: ['PhantomJS'],
                    files: [
                        '../wem-webapp/src/main/webapp/admin/resources/lib/ext/ext-all.js',
                        '../wem-webapp/src/main/webapp/admin2/resources/lib/jquery-2.0.2.js',
                        '../wem-webapp/src/main/webapp/admin2/resources/lib/mousetrap.min.js',
                        '../wem-webapp/src/main/webapp/admin2/resources/lib/signals.js',
                        '../wem-webapp/src/main/webapp/admin2/resources/lib/hasher.js',
                        '../wem-webapp/src/main/webapp/admin2/resources/lib/crossroads.js',
                        'target/main/js/api.js',
                        'src/test/js/**/*.js'
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
                    autoWatch: true
                },
                ci: {
                    singleRun: true
                }
            }

        }
    );

    grunt.registerTask('all', ['ts']);
    grunt.registerTask('test', ['clean', 'ts', 'karma:ci'])
    grunt.registerTask('test-dev', ['clean', 'ts', 'karma:dev'])
};

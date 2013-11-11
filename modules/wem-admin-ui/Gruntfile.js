module.exports = function (grunt) {

    var webappDir = '../wem-webapp/src/main/webapp/admin';

    grunt.loadNpmTasks('grunt-contrib-clean');
    grunt.loadNpmTasks('grunt-contrib-copy');
    grunt.loadNpmTasks('grunt-contrib-less');
    grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.loadNpmTasks('grunt-ts');
    grunt.loadNpmTasks('grunt-directives');

    grunt.initConfig({

            clean: {
                target: {
                    src: ['target']
                }
            },

            copy: {
                common: {
                    files: [
                        {
                            expand: true,
                            cwd: (webappDir + '/resources/css'),
                            src: ['**/*.css'],
                            dest: 'src/main/common/styles/'
                        },
                        {
                            expand: true,
                            cwd: (webappDir + '/resources/less'),
                            src: ['**/*.less'],
                            dest: 'src/main/common/styles/'
                        },
                        {
                            expand: true,
                            cwd: (webappDir + '/resources/images'),
                            src: ['**'],
                            dest: 'src/main/common/images/'
                        },
                        {
                            expand: true,
                            cwd: (webappDir + '/resources/font'),
                            src: ['**'],
                            dest: 'src/main/common/font/'
                        },
                        {
                            expand: true,
                            cwd: (webappDir + '/resources/lib'),
                            src: ['**'],
                            dest: 'src/main/common/lib/'
                        },
                        {
                            expand: true,
                            cwd: (webappDir + '/api/js'),
                            src: ['**'],
                            dest: 'src/main/common/js/'
                        }
                    ]
                },
                app_launcher: {
                    files: [
                        {
                            expand: true,
                            cwd: (webappDir + '/apps/app-launcher/js'),
                            src: ['**'],
                            dest: 'src/main/apps/app-launcher/js/'
                        }
                    ]
                },
                content_manager: {
                    files: [
                        {
                            expand: true,
                            cwd: (webappDir + '/apps/content-manager/js'),
                            src: ['**'],
                            dest: 'src/main/apps/content-manager/js/'
                        }
                    ]
                },
                schema_manager: {
                    files: [
                        {
                            expand: true,
                            cwd: (webappDir + '/apps/schema-manager/js'),
                            src: ['**'],
                            dest: 'src/main/apps/schema-manager/js/'
                        }
                    ]
                },
                space_manager: {
                    files: [
                        {
                            expand: true,
                            cwd: (webappDir + '/apps/space-manager/js'),
                            src: ['**'],
                            dest: 'src/main/apps/space-manager/js/'
                        }
                    ]
                }
            },

            module: {
                common: {
                    dir: 'src/main/common'
                },
                space_manager: {
                    dir: 'src/main/common'
                }
            }
        }
    );

    grunt.registerMultiTask('module', 'Build an admin module', function () {

        var dir = this.data.dir;
        var target = this.target;
        var config = {};

        var styleFile = dir + '/styles/_module.less';
        if (grunt.file.exists(styleFile)) {
            config = {
                files: {
                }
            };

            config.files[dir + '/styles/_all.css'] = styleFile;
            grunt.config.set('less.' + target, config);

            grunt.log.writeln('Adding less:' + target + ' task');
            grunt.task.run('less');
        }

        var libFile = dir + '/lib/_module.js';
        if (grunt.file.exists(libFile)) {
            config = {
                src: libFile,
                dest: dir + '/lib/_all.js'
            };

            grunt.config.set('directives.' + target, config);

            grunt.log.writeln('Adding directives:' + target + ' task');
            grunt.task.run('directives');
        }

        var tsFile = dir + '/ts/_module.ts';
        if (grunt.file.exists(tsFile)) {
            config = {
                src: tsFile,
                out: dir + '/ts/_all.js',
                options: {
                    sourcemap: true,
                    declaration: true
                }
            };

            grunt.config.set('ts.' + target, config);

            grunt.log.writeln('Adding ts:' + target + ' task');
            grunt.task.run('ts');
        }
    });

    grunt.registerTask('all', ['clean', 'less', 'concat', 'ts']);
};

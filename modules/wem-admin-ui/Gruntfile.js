module.exports = function (grunt) {

    var webappDir = '../wem-webapp/src/main/webapp/admin';

    grunt.loadNpmTasks('grunt-contrib-clean');
    grunt.loadNpmTasks('grunt-contrib-copy');
    grunt.loadTasks('grunt');

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
                    dir: 'src/main/common',
                    ts: {
                        sourcemap: true,
                        declaration: true
                    }
                }
            }


        }
    );

    grunt.registerTask('all', ['clean', 'less', 'concat', 'ts']);
};

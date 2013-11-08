module.exports = function (grunt) {

    var webappDir = '../wem-webapp/src/main/webapp/admin';

    grunt.loadNpmTasks('grunt-contrib-clean');
    grunt.loadNpmTasks('grunt-contrib-copy');
    grunt.loadNpmTasks('grunt-contrib-less');
    grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.loadNpmTasks('grunt-ts');

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

            less: {
                common: {
                    files: {
                        "target/main/common/styles/all.css": "src/main/common/styles/main.less"
                    }
                }
            },

            concat: {
                common: {
                    src: [
                        'src/main/common/lib/ext/ext-all.js',
                        'src/main/common/lib/plupload/js/plupload.full.js',
                        'src/main/common/lib/jquery-2.0.2.js',
                        'src/main/common/lib/jquery-ui-1.10.3.custom.min.js',
                        'src/main/common/lib/jquery.ui.live-draggable.js',
                        'src/main/common/lib/jquery.simulate.js',
                        'src/main/common/lib/codemirror/codemirror.js',
                        'src/main/common/lib/codemirror/addon/loadmode.js',
                        'src/main/common/lib/signals.js',
                        'src/main/common/lib/hasher.js',
                        'src/main/common/lib/crossroads.js',
                        'src/main/common/lib/slickgrid/lib/jquery.event.drag-2.2.js',
                        'src/main/common/lib/slickgrid/lib/jquery.event.drop-2.2.js',
                        'src/main/common/lib/slickgrid/slick.core.js',
                        'src/main/common/lib/slickgrid/slick.grid.js',
                        'src/main/common/lib/slickgrid/slick.dataview.js',
                        'src/main/common/lib/slickgrid/slick.remotemodel.js',
                        'src/main/common/lib/slickgrid/slick.rowselectionmodel.js',
                        'src/main/common/lib/slickgrid/slick.checkboxselectcolumn.js',
                    ],
                    dest: 'target/main/common/lib/all.js'
                }
            },

            ts: {
                common: {
                    src: 'src/main/common/js/main.ts',
                    out: 'target/main/common/js/all.js',
                    options: {
                        sourcemap: true,
                        declaration: true
                    }
                },
                app_launcher: {
                    src: 'src/main/apps/app-launcher/js/main.ts',
                    out: 'target/main/apps/app-launcher/js/all.js',
                    options: {
                        sourcemap: true
                    }
                }
            }
        }
    );

    grunt.registerTask('all', ['clean',  'less', 'concat', 'ts']);
};

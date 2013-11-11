module.exports = function (grunt) {

    grunt.loadNpmTasks('grunt-ts');
    grunt.loadNpmTasks('grunt-contrib-watch');
    grunt.loadNpmTasks('grunt-contrib-less');
    grunt.loadNpmTasks('grunt-contrib-concat');

    grunt.initConfig({

        less: {
            common: {
                files: {
                    'src/main/webapp/admin/resources/styles/_all.css': 'src/main/webapp/admin/resources/styles/main.less'
                }
            },
            live_edit: {
                files: {
                    "src/main/webapp/admin/live-edit/css/less/_all.css": "src/main/webapp/admin/live-edit/css/less/live-edit.less"
                }
            }
        },

        concat: {
            common: {
                src: [
                    'src/main/webapp/admin/resources/lib/ext/ext-all.js',
                    'src/main/webapp/admin/resources/lib/plupload/js/plupload.full.js',
                    'src/main/webapp/admin/resources/lib/jquery-2.0.2.js',
                    'src/main/webapp/admin/resources/lib/jquery-ui-1.10.3.custom.min.js',
                    'src/main/webapp/admin/resources/lib/jquery.ui.live-draggable.js',
                    'src/main/webapp/admin/resources/lib/jquery.simulate.js',
                    'src/main/webapp/admin/resources/lib/codemirror/codemirror.js',
                    'src/main/webapp/admin/resources/lib/codemirror/addon/loadmode.js',
                    'src/main/webapp/admin/resources/lib/signals.js',
                    'src/main/webapp/admin/resources/lib/hasher.js',
                    'src/main/webapp/admin/resources/lib/crossroads.js',
                    'src/main/webapp/admin/resources/lib/slickgrid/lib/jquery.event.drag-2.2.js',
                    'src/main/webapp/admin/resources/lib/slickgrid/lib/jquery.event.drop-2.2.js',
                    'src/main/webapp/admin/resources/lib/slickgrid/slick.core.js',
                    'src/main/webapp/admin/resources/lib/slickgrid/slick.grid.js',
                    'src/main/webapp/admin/resources/lib/slickgrid/slick.dataview.js',
                    'src/main/webapp/admin/resources/lib/slickgrid/slick.remotemodel.js',
                    'src/main/webapp/admin/resources/lib/slickgrid/slick.rowselectionmodel.js',
                    'src/main/webapp/admin/resources/lib/slickgrid/slick.checkboxselectcolumn.js',
                    'src/main/webapp/admin/resources/lib/mousetrap.min.js'
                ],
                dest: 'src/main/webapp/admin/resources/lib/_all.js'
            }
        },

        ts: {
            api: {
                src: ['src/main/webapp/admin/api/js/_module.ts'],
                out: 'src/main/webapp/admin/api/js/_all.js'
            },
            space_manager: {
                src: ['src/main/webapp/admin/apps/space-manager/js/_module.ts'],
                out: 'src/main/webapp/admin/apps/space-manager/js/_all.js',
                options: {
                    sourcemap: true
                }
            },
            content_manager: {
                src: ['src/main/webapp/admin/apps/content-manager/js/_module.ts'],
                out: 'src/main/webapp/admin/apps/content-manager/js/_all.js',
                options: {
                    sourcemap: true
                }
            },
            schema_manager: {
                src: ['src/main/webapp/admin/apps/schema-manager/js/_module.ts'],
                out: 'src/main/webapp/admin/apps/schema-manager/js/_all.js',
                options: {
                    sourcemap: true
                }
            },
            app_launcher: {
                src: ['src/main/webapp/admin/apps/app-launcher/js/_module.ts'],
                out: 'src/main/webapp/admin/apps/app-launcher/js/_all.js',
                options: {
                    sourcemap: true
                }
            },
            live_edit: {
                src: ['src/main/webapp/admin/live-edit/js/_module.ts'],
                out: 'src/main/webapp/admin/live-edit/js/_all.js',
                options: {
                    sourcemap: true
                }
            }
        },

        watch: {
            files: ['src/main/webapp/admin/**/*.ts', 'src/test/webapp/admin/**/*.ts'],
            tasks: ['ts']
        }

    });

    /**
     * Alias tasks
     */
    grunt.registerTask('default', 'watch');
    grunt.registerTask('all', ['less', 'concat', 'ts']);
    grunt.registerTask('cm', [
        'ts:api',
        'ts:content_manager'
    ]);
    grunt.registerTask('sp', [
        'ts:api',
        'ts:space_manager'
    ]);
    grunt.registerTask('sc', [
        'ts:api',
        'ts:schema_manager'
    ]);
    grunt.registerTask('al', [
        'ts:api',
        'ts:app_launcher'
    ]);
    grunt.registerTask('live_edit_build_all', [
        'ts:live_edit',
        'less:live_edit'
    ]);
};

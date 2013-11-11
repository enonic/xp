module.exports = function (grunt) {

    grunt.loadTasks('grunt');

    grunt.initConfig({

        module: {
            common: {
                dir: 'src/main/webapp/admin/common',
                ts: {
                    sourcemap: true,
                    declaration: true
                }
            },
            space_manager: {
                dir: 'src/main/webapp/admin/apps/space-manager',
                ts: {
                    sourcemap: true
                }
            },
            content_manager: {
                dir: 'src/main/webapp/admin/apps/content-manager',
                ts: {
                    sourcemap: true
                }
            },
            schema_manager: {
                dir: 'src/main/webapp/admin/apps/schema-manager',
                ts: {
                    sourcemap: true
                }
            },
            app_launcher: {
                dir: 'src/main/webapp/admin/apps/app-launcher',
                ts: {
                    sourcemap: true
                }
            },
            live_edit: {
                dir: 'src/main/webapp/admin/live-edit',
                ts: {
                    sourcemap: true
                }
            }
        }
    });

    /**
     * Alias tasks
     */
    grunt.registerTask('default', 'all');
    grunt.registerTask('all', ['module']);
    grunt.registerTask('cm', ['module:content_manager']);
    grunt.registerTask('sp', ['module:space_manager']);
    grunt.registerTask('sc', ['module:schema_manager']);
    grunt.registerTask('al', ['module:app_launcher']);
    grunt.registerTask('le', ['module:live_edit']);
};

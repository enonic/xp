module.exports = function (grunt) {

    grunt.loadTasks('grunt');

    grunt.initConfig({

        module: {
            common: {
                module: 'admin/common',
                options: {
                    sourcemap: true,
                    declaration: true
                }
            },
            space_manager: {
                module: 'admin/apps/space-manager',
                options: {
                    sourcemap: true
                }
            },
            content_manager: {
                module: 'admin/apps/content-manager',
                options: {
                    sourcemap: true
                }
            },
            schema_manager: {
                module: 'admin/apps/schema-manager',
                options: {
                    sourcemap: true
                }
            },
            module_manager: {
                module: 'admin/apps/module-manager',
                options: {
                    sourcemap: true
                }
            },
            template_manager: {
                module: 'admin/apps/template-manager',
                options: {
                    sourcemap: true
                }
            },
            app_launcher: {
                module: 'admin/apps/app-launcher',
                options: {
                    sourcemap: true
                }
            },
            live_edit: {
                module: 'admin/live-edit',
                options: {
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
    grunt.registerTask('md', ['module:module_manager']);
    grunt.registerTask('tm', ['module:template_manager']);
    grunt.registerTask('al', ['module:app_launcher']);
    grunt.registerTask('le', ['module:live_edit']);
};

module.exports = function (grunt) {

    grunt.loadNpmTasks('grunt-ts');
    grunt.loadNpmTasks('grunt-contrib-watch');
    grunt.loadNpmTasks('grunt-contrib-less');
    grunt.loadNpmTasks('grunt-contrib-cssmin');

    grunt.initConfig({

        ts: {
            api: {
                src: ['src/main/webapp/admin/api/js/main.ts'],
                out: 'src/main/webapp/admin/api/js/api.js',
                options: {
                    // target: 'es5',
                    sourcemap: true,
                    declaration: true
                }
            },
            space_manager: {
                src: ['src/main/webapp/admin/apps/space-manager/js/main.ts'],
                out: 'src/main/webapp/admin/apps/space-manager/js/all.js',
                options: {
                    // target: 'es5',
                    sourcemap: true
                }
            },
            content_manager: {
                src: ['src/main/webapp/admin/apps/content-manager/js/main.ts'],
                out: 'src/main/webapp/admin/apps/content-manager/js/all.js',
                options: {
                    // target: 'es5',
                    sourcemap: true
                }
            },
            schema_manager: {
                src: ['src/main/webapp/admin/apps/schema-manager/js/main.ts'],
                out: 'src/main/webapp/admin/apps/schema-manager/js/all.js',
                options: {
                    // target: 'es5',
                    sourcemap: true
                }
            },
            app_launcher: {
                src: ['src/main/webapp/admin/apps/app-launcher/js/main.ts'],
                out: 'src/main/webapp/admin/apps/app-launcher/js/all.js',
                options: {
                    // target: 'es5',
                    sourcemap: true
                }
            },
            live_edit: {
                src: ['src/main/webapp/admin/live-edit/js/Main.ts'],
                out: 'src/main/webapp/admin/live-edit/js/all.js',
                options: {
                    // target: 'es5',
                    sourcemap: true
                }
            }
        },

        less: {
            live_edit: {
                options: {
                    compress: true
                },
                files: {
                    "src/main/webapp/admin2/live-edit/css/live-edit.css": "src/main/webapp/admin/live-edit/css/less/live-edit.less"
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
    grunt.registerTask('all', ['ts']);
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

module.exports = function (grunt) {

    grunt.loadNpmTasks('grunt-ts');
    grunt.loadNpmTasks('grunt-contrib-watch');
    grunt.loadNpmTasks('grunt-contrib-less');
    grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.loadNpmTasks('grunt-directives');

    grunt.initConfig({

        less: {
            common: {
                files: {
                    'src/main/webapp/admin/resources/styles/_all.css': 'src/main/webapp/admin/resources/styles/_module.less'
                }
            },
            live_edit: {
                files: {
                    "src/main/webapp/admin/live-edit/styles/_all.css": "src/main/webapp/admin/live-edit/styles/_module.less"
                }
            }
        },

        directives: {
            common: {
                src: 'src/main/webapp/admin/resources/lib/_module.js',
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
    grunt.registerTask('all', ['less', 'directives', 'ts']);
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

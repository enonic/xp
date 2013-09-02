module.exports = function (grunt) {

    grunt.loadNpmTasks('grunt-ts');
    grunt.loadNpmTasks('grunt-typescript');
    grunt.loadNpmTasks('grunt-contrib-watch');
    grunt.loadNpmTasks('grunt-contrib-sass');
    grunt.loadNpmTasks('grunt-contrib-cssmin');

    grunt.initConfig({

        // Use typescript for LiveEdit
        typescript: {
            live_edit: {
                src: ['src/main/webapp/admin2/live-edit/js/Main.ts'],
                dest: 'src/main/webapp/admin2/live-edit/js/all.js',
                options: {
                    // target: 'es5',
                    sourcemap: true
                }
            }
        },

        ts: {
            api: {
                src: ['src/main/webapp/admin2/api/js/main.ts'],
                out: 'src/main/webapp/admin2/api/js/api.js',
                options: {
                    // target: 'es5',
                    sourcemap: true,
                    declaration: true
                }
            },
            api_test: {
                src: ['src/test/webapp/admin2/api/js/**/*.ts'],
                out: 'src/test/webapp/admin2/api/js/api-test.js',
                options: {
                    // target: 'es5',
                    sourcemap: true,
                    declaration: true
                }
            },
            space_manager: {
                src: ['src/main/webapp/admin2/apps/space-manager/js/main.ts'],
                out: 'src/main/webapp/admin2/apps/space-manager/js/all.js',
                options: {
                    // target: 'es5',
                    sourcemap: true
                }
            },
            content_manager: {
                src: ['src/main/webapp/admin2/apps/content-manager/js/main.ts'],
                out: 'src/main/webapp/admin2/apps/content-manager/js/all.js',
                options: {
                    // target: 'es5',
                    sourcemap: true
                }
            },
            schema_manager: {
                src: ['src/main/webapp/admin2/apps/schema-manager/js/main.ts'],
                out: 'src/main/webapp/admin2/apps/schema-manager/js/all.js',
                options: {
                    // target: 'es5',
                    sourcemap: true
                }
            },
            app_launcher: {
                src: ['src/main/webapp/admin2/apps/app-launcher/js/main.ts'],
                out: 'src/main/webapp/admin2/apps/app-launcher/js/all.js',
                options: {
                    // target: 'es5',
                    sourcemap: true
                }
            }
        },

        sass: {
            live_edit: {
                files: {
                    'src/main/webapp/admin2/live-edit/css/live-edit.css': 'src/main/webapp/admin2/live-edit/css/sass/live-edit.scss'
                }
            }
        },

        cssmin: {
            live_edit: {
                src: 'src/main/webapp/admin2/live-edit/css/live-edit.css',
                dest: 'src/main/webapp/admin2/live-edit/css/live-edit.min.css'
            }
        },

        watch: {
            files: ['src/main/webapp/admin2/**/*.ts', 'src/test/webapp/admin2/**/*.ts'],
            tasks: ['ts']
        }

    });

    /**
     * Alias tasks
     */
    grunt.registerTask('default', 'watch');
    grunt.registerTask('all', ['ts', 'typescript']);
    grunt.registerTask('live_edit_build_all',
        [
            'typescript:live_edit',
            'sass:live_edit',
            'cssmin:live_edit'
        ]
    );
};

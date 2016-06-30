const CONFIG = {
    gulpTasks: 'gulp/tasks/',
    root: {
        src: 'src/main/resources/web/admin',
        dest: 'target/resources/main/web/admin',
        assets: 'target/resources/main/assets'
    },
    tasks: {
        css: {
            autoprefixer: {
                browsers: ['last 3 versions', 'ie 11']
            },
            maps: {},
            files: {
                common: {src: '/common/styles/_module.less', dest: '/common/styles/_all.css'},
                live: {src: '/live-edit/styles/_module.less', dest: '/live-edit/styles/_all.css'},
                home: {src: '/common/styles/apps/home/home.less', dest: '/common/styles/_home.css'},
                editor: {
                    src: '/common/styles/api/util/htmlarea/html-editor.module.less',
                    dest: '/common/styles/api/util/htmlarea/html-editor.css'
                },
                launcher: {src: '/common/styles/apps/launcher/launcher.less', dest: '/styles/_launcher.css', assets: true}
            }
        },
        directives: {
            src: '/common/lib/_include.js',
            dest: '/common/lib/_all.js'
        },
        clean: {
            pattern: '/**/_all.*',
            cleanDot: true
        }
    }
};

export default CONFIG;
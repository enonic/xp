var baseDir = 'src/main/resources/web/admin';

module.exports = {

    options: {
        sourceMap: true
    }

};

add('common', 'common', true);
add('content_manager', 'apps/content-studio', false);
add('applications', 'apps/applications', false);
add('user_manager', 'apps/user-manager', false);
// add('login', 'apps/login', false);
add('live_edit', 'live-edit', false);

function add(name, path, declaration) {

    module.exports[name] = {
        src: baseDir + '/' + path + '/js/_module.ts',
        out: baseDir + '/' + path + '/js/_all.js',
        options: {
            declaration: declaration
        }
    };

}

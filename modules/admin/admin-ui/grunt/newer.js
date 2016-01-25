var baseDir = 'src/main/resources/web/admin';

add('common', 'common', []);
add('content_manager', 'apps/content-studio', ['common']);
add('applications', 'apps/applications', ['common']);
add('user_manager', 'apps/user-manager', ['common']);
add('login', 'apps/login', ['common']);
add('live_edit', 'live-edit', ['common']);

function add(name, path, dependencies) {

    var srcList = [baseDir + '/' + path + '/js/**'];
    for (var i = 0; i < dependencies.length; i++) {
        srcList.push(baseDir + '/' + dependencies[i] + '/js/**')
    }

    module.exports[name + "_ts"] = {
        src: srcList,
        dest: baseDir + '/' + path + '/js/_all.js',
        options: {
            tasks: ["ts:" + name]
        }
    };

}

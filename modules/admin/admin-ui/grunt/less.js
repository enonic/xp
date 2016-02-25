var baseDir = 'src/main/resources/web/admin';

add('common', 'common');
add('live_edit', 'live-edit');
addHome();

function add(name, path) {

    module.exports[name] = {
        src: baseDir + '/' + path + '/styles/_module.less',
        dest: baseDir + '/' + path + '/styles/_all.css',
        options: {
            sourceMap: true,
            relativeUrls: true
        }
    };
}

function addHome() {

    module.exports['home'] = {
        src: baseDir + '/common/styles/apps/home/_module.less',
        dest: baseDir + '/common/styles/_home.css',
        options: {
            sourceMap: false,
            relativeUrls: true
        }
    };
}

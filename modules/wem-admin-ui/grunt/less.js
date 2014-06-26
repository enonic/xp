var baseDir = 'src/main/resources/web/admin';

add('common', 'common');
add('live_edit', 'live-edit');

function add(name, path) {

    module.exports[name] = {
        src: baseDir + '/' + path + '/styles/_module.less',
        dest: baseDir + '/' + path + '/styles/_all.css',
        options: {
            sourceMap: true
        }
    };

}

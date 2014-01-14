var baseDir = 'src/main/webapp/admin';

add('common', 'common');
add('live_edit', 'live-edit');

function add(name, path) {

    module.exports[name] = {
        src: baseDir + '/' + path + '/styles/_module.less',
        dest: baseDir + '/' + path + '/styles/_all.css'
    };

}

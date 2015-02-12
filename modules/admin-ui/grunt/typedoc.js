var baseDir = 'src/main/resources/web/admin';

module.exports = {

    common: {
        options: {
            out: './target/typedoc',
            name: 'Enonic XP Admin UI Api'
        },
        src: [baseDir + '/common/js/_module.ts']
    }

};

var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

let resource = 'css: "assets/styles.css"\n' +
               'image:\n' +
               '- name: "editor-width-auto"\n' +
               '  displayName:\n' +
               '    text: "Override ${width}"\n' +
               '    i18n: "editor-width-auto-text"\n' +
               '- name: "editor-style-cinema"\n' +
               '  displayName:\n' +
               '    text: "Cinema"\n' +
               '    i18n: "editor-style-cinema-text"\n' +
               '  aspectRatio: "21:9"\n' +
               '  filter: "pixelate(10)"';


// BEGIN
// Create virtual styles.
var result = schemaLib.createStyles({
    application: 'myapp',
    resource

});

log.info('Created styles: ' + result.application);

// END


assert.assertJsonEquals({
    application: 'myapp',
    cssPath: 'assets/styles.css',
    modifiedTime: '2021-09-25T10:00:00Z',
    resource: 'css: "assets/styles.css"\n' +
              'image:\n' +
              '- name: "editor-width-auto"\n' +
              '  displayName:\n' +
              '    text: "Override ${width}"\n' +
              '    i18n: "editor-width-auto-text"\n' +
              '- name: "editor-style-cinema"\n' +
              '  displayName:\n' +
              '    text: "Cinema"\n' +
              '    i18n: "editor-style-cinema-text"\n' +
              '  aspectRatio: "21:9"\n' +
              '  filter: "pixelate(10)"',
    elements: [
        {
            element: 'image',
            displayName: 'Override ${width}',
            name: 'editor-width-auto'
        },
        {
            element: 'image',
            displayName: 'Cinema',
            name: 'editor-style-cinema'
        }
    ]
}, result);


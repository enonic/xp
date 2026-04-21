var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

let resource = 'kind: "Style"\n' +
               'styles:\n' +
               '- name: "editor-width-auto"\n' +
               '  type: "Image"\n' +
               '  label:\n' +
               '    text: "Override ${width}"\n' +
               '    i18n: "editor-width-auto-text"\n' +
               '- name: "editor-style-cinema"\n' +
               '  type: "Image"\n' +
               '  label:\n' +
               '    text: "Cinema"\n' +
               '    i18n: "editor-style-cinema-text"\n' +
               '  aspectRatio: "21:9"\n' +
               '  filter: "pixelate(10)"';


// BEGIN
// Update virtual styles.
var result = schemaLib.updateStyles({
    application: 'myapp',
    resource

});

log.info('Updated styles: ' + result.application);

// END


assert.assertJsonEquals({
    application: 'myapp',
    modifiedTime: '2021-09-25T10:00:00Z',
    resource: 'kind: "Style"\n' +
              'styles:\n' +
              '- name: "editor-width-auto"\n' +
              '  type: "Image"\n' +
              '  label:\n' +
              '    text: "Override ${width}"\n' +
              '    i18n: "editor-width-auto-text"\n' +
              '- name: "editor-style-cinema"\n' +
              '  type: "Image"\n' +
              '  label:\n' +
              '    text: "Cinema"\n' +
              '    i18n: "editor-style-cinema-text"\n' +
              '  aspectRatio: "21:9"\n' +
              '  filter: "pixelate(10)"',
    elements: [
        {
            label: 'Override ${width}',
            name: 'editor-width-auto'
        },
        {
            label: 'Cinema',
            name: 'editor-style-cinema'
        }
    ]
}, result);


var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Fetch dynamic styles.
var result = schemaLib.getStyles({
    application: 'myapp'
});

log.info('Fetched styles: myapp');

// END


assert.assertJsonEquals({
    application: 'myapp',
    modifiedTime: '2021-02-25T10:44:33.170079900Z',
    resource: 'kind: "Style"\n' +
              'styles:\n' +
              '- name: "mystyle"\n' +
              '  type: "Image"\n' +
              '  label:\n' +
              '    text: "Style display name"\n' +
              '    i18n: "style.display"\n' +
              '  aspectRatio: "16:9"\n' +
              '  filter: "sharpen()"\n' +
              '- name: "plain"\n' +
              '  type: "Image"\n' +
              '  label: "Plain"\n',
    elements: [
        {
            label: 'Style display name',
            name: 'mystyle',
            type: 'Image',
            aspectRatio: '16:9',
            filter: 'sharpen()'
        },
        {
            label: 'Plain',
            name: 'plain',
            type: 'Image'
        }
    ]
}, result);


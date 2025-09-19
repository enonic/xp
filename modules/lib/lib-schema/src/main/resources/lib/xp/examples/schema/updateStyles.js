var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

let resource = '<?xml version="1.0" encoding="UTF-8"?>' +
               '<styles css="assets/styles.css" xmlns="urn:enonic:xp:model:1.0">' +
               '<image name="editor-width-auto">' +
               '<display-name i18n="editor-width-auto-text">Override ${width}</display-name>' +
               '</image>' +
               '<image name="editor-style-cinema">' +
               '<display-name i18n="editor-style-cinema-text">Cinema</display-name>' +
               '<aspect-ratio>21:9</aspect-ratio>' +
               '<filter>pixelate(10)</filter>' +
               '</image>' +
               '</styles>';


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
    cssPath: 'assets/styles.css',
    modifiedTime: '2021-09-25T10:00:00Z',
    resource: '<?xml version=\"1.0\" encoding=\"UTF-8\"?>' +
              '<styles css=\"assets/styles.css\" xmlns=\"urn:enonic:xp:model:1.0\">' +
              '<image name=\"editor-width-auto\">' +
              '<display-name i18n=\"editor-width-auto-text\">Override ${width}</display-name>' +
              '</image><image name=\"editor-style-cinema\">' +
              '<display-name i18n=\"editor-style-cinema-text\">Cinema</display-name>' +
              '<aspect-ratio>21:9</aspect-ratio><filter>pixelate(10)</filter>' +
              '</image>' +
              '</styles>',
    elements: [
        {
            displayName: 'Override ${width}',
            name: 'editor-width-auto'
        },
        {
            displayName: 'Cinema',
            name: 'editor-style-cinema'
        }
    ]
}, result);


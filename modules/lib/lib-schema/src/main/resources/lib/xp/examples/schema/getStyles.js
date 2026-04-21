var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Fetch virtual styles.
var result = schemaLib.getStyles({
    application: 'myapp'
});

log.info('Fetched styles: myapp');

// END


assert.assertJsonEquals({
    application: 'myapp',
    modifiedTime: '2021-02-25T10:44:33.170079900Z',
    resource: '<styles><some-data></some-data></styles>',
    elements: [
        {
            label: 'Style display name',
            name: 'mystyle'
        }
    ]
}, result);


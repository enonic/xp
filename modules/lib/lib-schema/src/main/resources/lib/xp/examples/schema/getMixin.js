var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Fetch dynamic mixin type.
var result = schemaLib.getMixin({
    name: 'myapp:mydata'
});

log.info('Fetched mixin: ' + result.name);

// END


assert.assertJsonEquals({
    name: 'media:cameraInfo',
    title: 'Photo Info',
    titleI18nKey: 'media.cameraInfo.displayName',
    modifiedTime: '1970-01-06T03:07:14.242Z',
    resource: 'kind: "Mixin"\n' +
              'title:\n' +
              '  text: "Photo Info"\n' +
              '  i18n: "media.cameraInfo.displayName"\n',
    type: 'MIXIN',
    form: [],
    config: {}
}, result);


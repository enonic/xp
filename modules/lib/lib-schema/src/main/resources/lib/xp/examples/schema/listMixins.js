var schemaLib = require('/lib/xp/schema');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// Fetch dynamic mixins.
var result = schemaLib.listMixins({
    application: 'myapp'
});

log.info('Fetched mixins: ' + result.map((mixin) => mixin.name).join(','));

// END


assert.assertJsonEquals([
    {
        name: 'myapp:mixin1',
        title: 'My mixin display name',
        description: 'My mixin description',
        modifiedTime: '2010-01-01T10:00:00Z',
        resource: 'kind: "Mixin"\n' +
                  'title: "My mixin display name"\n' +
                  'description: "My mixin description"\n',
        type: 'MIXIN',
        form: [],
        config: {}
    },
    {
        name: 'myapp:mixin2',
        title: 'Other mixin',
        modifiedTime: '2012-01-01T10:00:00Z',
        resource: 'kind: "Mixin"\n' +
                  'title: "Other mixin"\n',
        type: 'MIXIN',
        form: [],
        config: {}
    }
], result);

var appLib = require('/lib/xp/app');
var assert = require('/lib/xp/testing');

/* global log*/

// BEGIN
// List virtual apps.
var result = appLib.list();

log.info('Listed apps: ' + result.map((app) => app.key).join(', '));

// END


assert.assertJsonEquals([
    {
        key: 'app1',
        displayName: 'app display name',
        vendorName: 'vendor name',
        vendorUrl: 'https://vendor.url',
        url: 'https://myapp.url',
        version: '1.0.0',
        minSystemVersion: '2.0.0',
        maxSystemVersion: '3.0.0',
        modifiedTime: '2020-09-25T10:00:00Z',
        started: true,
        description: 'my app description',
        icon: 'data:image/png;base64, AAE='
    },
    {
        key: 'app2',
        displayName: 'app display name 2',
        vendorName: 'vendor name 2',
        vendorUrl: 'https://vendor2.url',
        url: 'https://myapp2.url',
        version: '4.1.2',
        minSystemVersion: '5.3.11',
        maxSystemVersion: '3.0.6',
        modifiedTime: '2021-09-25T10:00:00Z',
        started: false,
        description: 'my app description 2'
    }
], result);


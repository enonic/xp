var adminLib = require('/lib/xp/admin');

// BEGIN
// Get all admin tools accessible to the current user
var result = adminLib.getTools();
log.info('Admin tools: ' + JSON.stringify(result, null, 2));
// END

// BEGIN
// Expected result:
var expected = [
    {
        "key": "com.enonic.xp.app.main:home",
        "name": "Home",
        "description": "Home dashboard",
        "icon": "<svg>...</svg>",
        "systemApp": true
    },
    {
        "key": "com.enonic.xp.app.contentstudio:main",
        "name": "Content Studio",
        "description": "Manage your content",
        "icon": "<svg>...</svg>",
        "systemApp": true
    }
];
// END

var authLib = require('/lib/xp/auth');

// BEGIN
// Creates an id provider.
var createIdProviderResult = authLib.createIdProvider({
    key: 'myIdProvider',
    displayName: 'My id provider',
    description: 'My id provider description',
    idProviderConfig: {
        applicationKey: 'com.enonic.app.myidprovider',
        config: {
            loginUrl: 'https://example.com/login'
        }
    },
    permissions: [
        {
            principal: 'role:system.admin',
            access: 'ADMINISTRATOR'
        }
    ]
});
// END

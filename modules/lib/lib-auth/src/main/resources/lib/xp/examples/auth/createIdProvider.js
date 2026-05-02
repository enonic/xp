var authLib = require('/lib/xp/auth');

var createIdProviderResult = authLib.createIdProvider({
    key: 'myIdProvider',
    displayName: 'My id provider',
    description: 'My id provider description',
    idProviderConfig: {
        applicationKey: 'com.enonic.app.myidprovider',
        config: {
            title: 'Login'
        }
    },
    permissions: [
        {
            principal: 'role:system.admin',
            access: 'ADMINISTRATOR'
        }
    ]
});

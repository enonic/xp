var Harness = Siesta.Harness.Browser.ExtJS;

Harness.configure( {
    title: 'Admin Test Suite',

    loaderPath: {
        'App': 'app/account/js',
        'Common': 'common/js',
        'Main': 'app/main/js',
        'Admin': 'resources/app'
    },

    testClass: AdminTestUtil,

    preload: [
        'resources/lib/ext/resources/css/ext-all.css',
        'resources/css/main.css',
        'resources/css/icons.css',
        'resources/css/user-preview.css',
        'resources/css/user-preview-panel.css',
        'resources/css/BoxSelect.css',
        'resources/app/view/XTemplates.js',
        'resources/lib/ext/ext-all.js'
    ]
} );

Harness.start(
        {
            group: 'Common',
            items: [
                'tests/common/test_TabPanel.js'
            ]
        },
        {
            group: 'Account',
            items: [
                'tests/account/test_Sanity.js',
                'tests/account/test_AccountModel.js',
                'tests/account/test_ChangePasswordWindow.js',
                'tests/account/test_DoublePasswordField.js',
                'tests/account/test_TabPanel.js'
            ]
        }
);


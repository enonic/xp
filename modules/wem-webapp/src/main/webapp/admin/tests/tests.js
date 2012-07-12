var Harness = Siesta.Harness.Browser.ExtJS;

Harness.configure({
    title: 'Admin Test Suite',
    defaultTimeout: 30000,
    loaderPath: {
        'App': 'app/account/js',
        'Common': 'common/js',
        'Main': 'app/main/js',
        'Admin': 'resources/app',
        'Test': 'tests'
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
});

Harness.start(
    {
        group: 'Common',
        items: [
            'tests/common/test_TabPanel.js',
            'tests/common/test_Diff.js',
            'tests/common/test_WizardPanel.js'
        ]
    },
    {
        group: 'Account',
        items: [
            'tests/account/test_Sanity.js',
            'tests/account/test_AccountModel.js',
            'tests/account/test_ChangePasswordWindow.js',
            'tests/account/test_DoublePasswordField.js',
            'tests/account/test_EditUserFormPanel.js'
        ]
    },
    {
        group: 'Live Edit',
        items: [
            {
                group: 'JS library conflicts',
                items: [
                    {
                        url: 'tests/liveedit/conflicts/test_jQueryNoConflict.js',
                        hostPageUrl: 'tests/liveedit/conflicts/test_jQueryNotConflict_host.html'
                    },
                    {
                        url: 'tests/liveedit/conflicts/test_mootoolsNoConflict.js',
                        hostPageUrl: 'tests/liveedit/conflicts/test_mootoolsNoConflict_host.html'
                    },
                    {
                        url: 'tests/liveedit/conflicts/test_prototypeNoConflict.js',
                        hostPageUrl: 'tests/liveedit/conflicts/test_prototypeNoConflict_host.html'
                    }
                ]
            },
            {
                group: 'Util',
                items: [
                    {
                        url: 'tests/liveedit/util/test_getBoxModel.js',
                        hostPageUrl: 'tests/liveedit/util/host.html'
                    },
                    {
                        url: 'tests/liveedit/util/test_getElementPagePosition.js',
                        hostPageUrl: 'tests/liveedit/util/host.html'
                    },
                    {
                        url: 'tests/liveedit/util/test_getClosestPageElementFromPoint.js',
                        hostPageUrl: 'tests/liveedit/util/host.html'
                    }
                ]
            }
        ]
    }
);


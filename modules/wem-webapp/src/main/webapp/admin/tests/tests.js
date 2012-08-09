var Harness = Siesta.Harness.Browser.ExtJS;

Harness.configure({
    title: 'Admin Test Suite',
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
        'resources/lib/ext/ext-all-debug.js'
    ]
});

Harness.start(
    {
        group: 'Common',
        items: [
            'tests/common/test_TabPanel.js',
            'tests/common/test_Diff.js',
            'tests/common/test_WizardPanel.js',
            'tests/common/test_TreeGridPanel.js',
            'tests/common/test_BaseDialogWindow.js'
        ]
    },
    {
        group: 'Account',
        items: [
            {
                group: 'Model',
                items: [
                    'tests/account/test_AccountModel.js',
                    'tests/account/test_CountryModel.js',
                    'tests/account/test_UserstoreConfigModel.js',
                    'tests/account/test_GroupModel.js',
                    'tests/account/test_LanguageModel.js',
                    'tests/account/test_LocaleModel.js',
                    'tests/account/test_TimezoneModel.js',
                    'tests/account/test_UserFieldModel.js'
                ]
            },
            'tests/account/test_Sanity.js',
            'tests/account/test_ChangePasswordWindow.js',
            'tests/account/test_DoublePasswordField.js',
            'tests/account/test_EditUserFormPanel.js',
            'tests/account/test_UserFormField.js'
        ]
    },
    {
        group: 'Content Manager',
        items: [
            'tests/contentManager/test_ContentManagerModel.js'
        ]
    },
    {
        group: 'Data Designer',
        items: [
            'tests/datadesigner/test_ContentTypeModel.js'
        ]
    },
    {
        group: 'Userstore',
        items: [
            'tests/userstore/test_UserstoreConfigModel.js',
            'tests/userstore/test_UserstoreConnectorModel.js'
        ]
    },
    {
        group: 'Live Edit',
        items: [
            {
                group: 'JS library conflicts',
                items: [
                    {
                        url: 'tests/liveedit/noconflict/test_jQueryNoConflict.js',
                        hostPageUrl: 'tests/liveedit/noconflict/test_jQueryNoConflict_host.html'
                    },
                    {
                        url: 'tests/liveedit/noconflict/test_mootoolsNoConflict.js',
                        hostPageUrl: 'tests/liveedit/noconflict/test_mootoolsNoConflict_host.html'
                    },
                    {
                        url: 'tests/liveedit/noconflict/test_prototypeNoConflict.js',
                        hostPageUrl: 'tests/liveedit/noconflict/test_prototypeNoConflict_host.html'
                    }
                ]
            },
            {
                group: 'Util',
                items: [
                    {
                        url: 'tests/liveedit/util/test_getBoxModel.js',
                        hostPageUrl: 'tests/liveedit/util/host.html'
                    }
                ]
            }
        ]
    }
);


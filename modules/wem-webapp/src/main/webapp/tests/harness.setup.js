var Harness = Siesta.Harness.Browser.ExtJS;

Harness.configure({
    title: 'Admin Test Suite',
    loaderPath: {
        'App': '../admin/app/account/js',
        'Common': '../admin/common/js',
        'Main': '../admin/app/main/js',
        'Admin': '../admin/resources/app',
        'Test': '../tests'
    },

    testClass: AdminTestUtil,

    preload: [
        { text : 'window.CONFIG = ' + JSON.stringify(window.CONFIG) },
        '../admin/resources/lib/ext/resources/css/ext-all.css',
        '../admin/resources/css/main.css',
        '../admin/resources/css/icons.css',
        '../admin/resources/css/user-preview.css',
        '../admin/resources/css/user-preview-panel.css',
        '../admin/resources/css/BoxSelect.css',
        '../admin/resources/app/view/XTemplates.js',
        '../admin/resources/lib/ext/ext-all-debug.js',
        '../admin/resources/app/lib/UriHelper.js'
    ]
});

Harness.start(
    {
        group: 'Common',
        items: [
            'common/test_TabPanel.js',
            'common/test_Diff.js',
            'common/test_WizardPanel.js',
            'common/test_TreeGridPanel.js',
            'common/test_BaseDialogWindow.js'
        ]
    },
    {
        group: 'Account',
        items: [
            {
                group: 'Model',
                items: [
                    'account/test_AccountModel.js',
                    'account/test_CountryModel.js',
                    'account/test_UserstoreConfigModel.js',
                    'account/test_GroupModel.js',
                    'account/test_LanguageModel.js',
                    'account/test_LocaleModel.js',
                    'account/test_TimezoneModel.js',
                    'account/test_UserFieldModel.js'
                ]
            },
            'account/test_Sanity.js',
            'account/test_ChangePasswordWindow.js',
            'account/test_DoublePasswordField.js',
            'account/test_EditUserFormPanel.js',
            'account/test_UserFormField.js'
        ]
    },
    {
        group: 'Content Manager',
        items: [
            'contentManager/test_ContentManagerModel.js'
        ]
    },
    {
        group: 'Data Designer',
        items: [
            'datadesigner/test_ContentTypeModel.js'
        ]
    },
    {
        group: 'Userstore',
        items: [
            'userstore/test_UserstoreConfigModel.js',
            'userstore/test_UserstoreConnectorModel.js'
        ]
    },
    {
        group: 'Live Edit',
        items: [
            {
                group: 'JS library conflicts',
                items: [
                    {
                        url: 'liveedit/noconflict/test_jQueryNoConflict.js',
                        hostPageUrl: 'liveedit/noconflict/test_jQueryNoConflict_host.html'
                    },
                    {
                        url: 'liveedit/noconflict/test_mootoolsNoConflict.js',
                        hostPageUrl: 'liveedit/noconflict/test_mootoolsNoConflict_host.html'
                    },
                    {
                        url: 'liveedit/noconflict/test_prototypeNoConflict.js',
                        hostPageUrl: 'liveedit/noconflict/test_prototypeNoConflict_host.html'
                    }
                ]
            },
            {
                group: 'Util',
                items: [
                    {
                        url: 'liveedit/util/test_Util.js',
                        hostPageUrl: 'liveedit/util/host.html'
                    }
                ]
            },
            {
                group: 'Core',
                items: [
                    {
                        url: 'liveedit/core/test_HtmlElementReplacer.js',
                        hostPageUrl: 'liveedit/core/test_htmlElementReplacer.html'
                    }
                ]
            }
        ]
    }
);


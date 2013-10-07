Ext.application({
    name: 'App',

    controllers: [
        'Admin.controller.Controller',
        'Admin.controller.NotifyUserController',
        'Admin.controller.TopBarController',
        'Admin.controller.account.Controller',
        'Admin.controller.account.GridPanelController',
        'Admin.controller.account.BrowseToolbarController',
        'Admin.controller.account.DetailPanelController',
        'Admin.controller.account.FilterPanelController',
        'Admin.controller.account.EditUserPanelController',
        'Admin.controller.account.UserWizardController',
        'Admin.controller.account.GroupWizardController',
        'Admin.controller.account.UserPreviewController',
        'Admin.controller.account.GroupPreviewController'
    ],

    requires: [
        'Admin.MessageBus',
        'Admin.NotificationManager',
        'Admin.view.TabPanel'
    ],

    launch: function () {
        Ext.create('Ext.container.Viewport', {
            layout: 'fit',
            cls: 'admin-viewport',

            items: [
                {
                    xtype: 'cmsTabPanel',
                    appName: 'Accounts',
                    appIconCls: 'icon-metro-accounts-24',
                    items: [
                        {
                            id: 'tab-browse',
                            title: 'Browse',
                            closable: false,
                            border: false,
                            xtype: 'panel',
                            layout: 'border',
                            tabConfig: {
                                hidden: true
                            },
                            items: [
                                {
                                    region: 'west',
                                    xtype: 'accountFilter',
                                    width: 200
                                },
                                {
                                    region: 'center',
                                    xtype: 'accountShow'
                                }
                            ]
                        }
                    ]
                }
            ]
        });
    }

});
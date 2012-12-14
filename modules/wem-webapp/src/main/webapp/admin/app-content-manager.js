Ext.application({
    name: 'App',

    controllers: [
        'Admin.controller.Controller',
        'Admin.controller.TopBarController',
        'Admin.controller.contentManager.GridPanelController',
        'Admin.controller.contentManager.DetailPanelController',
        'Admin.controller.contentManager.FilterPanelController',
        'Admin.controller.contentManager.BrowseToolbarController',
        'Admin.controller.contentManager.ContentWizardController',
        'Admin.controller.contentManager.ContentPreviewController',
        'Admin.controller.contentManager.DialogWindowController'
    ],

    requires: [
        'Admin.MessageBus',
        'Admin.view.FeedbackBox',
        'Admin.view.TabPanel'
    ],

    launch: function () {

        Ext.create('Ext.container.Viewport', {
            layout: 'fit',
            cls: 'admin-viewport',

            items: [
                {
                    xtype: 'cmsTabPanel',
                    appName: 'Content Manager',
                    appIconCls: 'icon-metro-content-manager-24',
                    items: [
                        {
                            id: 'tab-browse',
                            title: 'Browse',
                            closable: false,
                            xtype: 'panel',
                            layout: 'border',
                            items: [
                                {
                                    region: 'west',
                                    xtype: 'contentFilter',
                                    width: 200
                                },
                                {
                                    region: 'center',
                                    xtype: 'contentShow'
                                }
                            ]
                        }
                    ]
                }
            ]
        });

        Ext.create('widget.feedbackBox');

    }
});
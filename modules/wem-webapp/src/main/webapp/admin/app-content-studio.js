Ext.application({
    name: 'App',

    controllers: [
        'Admin.controller.Controller',
        'Admin.controller.TopBarController',
        'Admin.controller.contentStudio.BrowseController',
        'Admin.controller.contentStudio.FilterPanelController',
        'Admin.controller.contentStudio.ContentTypeWizardController',
        'Admin.controller.contentStudio.DialogWindowController'
    ],

    requires: [
        'Admin.MessageBus',
        'Admin.view.FeedbackBox',
        'Admin.view.TabPanel'
    ],

    launch: function () {
        Ext.create('Ext.container.Viewport', {
            region: 'center',
            title: 'Browse',
            layout: 'border',
            padding: 0,
            items: [
                {
                    region: 'center',
                    xtype: 'cmsTabPanel',
                    appName: 'Content Studio',
                    appIconCls: 'icon-metro-content-studio-24',
                    items: [
                        {
                            id: 'tab-browse',
                            title: 'Browse',
                            closable: false,
                            layout: 'border',
                            border: false,
                            items: [
                                {
                                    region: 'west',
                                    xtype: 'contentStudioFilter',
                                    width: 182,
                                    minSize: 100,
                                    maxSize: 500,
                                    margins: '3 0 5 5'

                                },
                                {
                                    region: 'center',
                                    layout: 'border',
                                    border: false,
                                    margins: '3 5 5 0',
                                    items: [
                                        {
                                            region: 'center',
                                            xtype: 'contentTypeTreeGridPanel',
                                            flex: 2
                                        },
                                        {
                                            region: 'south',
                                            xtype: 'contentTypeDetailPanel',
                                            collapsible: true,
                                            border: true,
                                            split: true,
                                            flex: 1
                                        }
                                    ]
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
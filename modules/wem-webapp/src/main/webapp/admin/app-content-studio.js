Ext.application({
    name: 'App',

    controllers: [
        'Admin.controller.Controller',
        'Admin.controller.TopBarController',
        'Admin.controller.contentStudio.GridPanelController',
        'Admin.controller.contentStudio.BrowseController',
        'Admin.controller.contentStudio.FilterPanelController',
        'Admin.controller.contentStudio.WizardController',
        'Admin.controller.contentStudio.ContentTypeWizardController',
        'Admin.controller.contentStudio.MixinWizardController',
        'Admin.controller.contentStudio.RelationshipTypeWizardController',
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
                            tabConfig: {
                                hidden: true
                            },
                            border: false,
                            items: [
                                {
                                    region: 'west',
                                    xtype: 'contentStudioFilter',
                                    width: 182,
                                    minSize: 100,
                                    maxSize: 500
                                },
                                {
                                    region: 'center',
                                    layout: 'border',
                                    border: false,
                                    items: [
                                        {
                                            region: 'center',
                                            xtype: 'contentTypeTreeGridPanel',
                                            border: false,
                                            flex: 1
                                        },
                                        {
                                            region: 'south',
                                            xtype: 'contentTypeDetailPanel',
                                            collapsible: true,
                                            border: false,
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
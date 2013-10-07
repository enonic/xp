Ext.application({
    name: 'App',

    controllers: [
        'Admin.controller.Controller',
        'Admin.controller.TopBarController',
        'Admin.controller.schemaManager.Controller',
        'Admin.controller.schemaManager.GridPanelController',
        'Admin.controller.schemaManager.BrowseController',
        'Admin.controller.schemaManager.FilterPanelController',
        'Admin.controller.schemaManager.WizardController',
        'Admin.controller.schemaManager.ContentTypeWizardController',
        'Admin.controller.schemaManager.MixinWizardController',
        'Admin.controller.schemaManager.RelationshipTypeWizardController',
        'Admin.controller.schemaManager.DialogWindowController'
    ],

    requires: [
        'Admin.MessageBus',
        'Admin.NotificationManager',
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
                    appName: 'Schema Manager',
                    appIconCls: 'icon-metro-schema-manager-24',
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
                                    xtype: 'schemaManagerFilter',
                                    width: 200,
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
    }
});
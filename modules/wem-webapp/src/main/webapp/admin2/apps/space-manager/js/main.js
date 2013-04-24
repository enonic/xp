Ext.application({
    name: 'spaceAdmin',

    controllers: [
        'Admin.controller.FilterPanelController',
        'Admin.controller.GridPanelController',
        'Admin.controller.BrowseToolbarController',
        'Admin.controller.DetailPanelController',
        'Admin.controller.DetailToolbarController',
        'Admin.controller.DialogWindowController',
        'Admin.controller.WizardController'
    ],

    stores: [
        'Admin.store.SpaceStore'
    ],

    requires: [
        'Admin.view.TabPanel',
        'Admin.view.FilterPanel',
        'Admin.view.BrowseToolbar',
        'Admin.view.TreeGridPanel',
        'Admin.view.DetailPanel',
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
                    appName: 'Space Admin',
                    appIconCls: 'icon-metro-space-admin-24',
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
                                    xtype: 'spaceFilter',
                                    width: 200
                                },
                                {
                                    region: 'center',
                                    xtype: 'container',
                                    layout: 'border',
                                    items: [
                                        {
                                            region: 'north',
                                            xtype: 'spaceBrowseToolbar'
                                        },
                                        {
                                            region: 'center',
                                            xtype: 'spaceTreeGrid',
                                            flex: 1
                                        },
                                        {
                                            region: 'south',
                                            split: true,
                                            collapsible: true,
                                            header: false,
                                            xtype: 'spaceDetail',
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
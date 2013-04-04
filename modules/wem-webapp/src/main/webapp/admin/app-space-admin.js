Ext.application({
    name: 'spaceAdmin',

    controllers: [
        'Admin.controller.spaceAdmin.FilterPanelController',
        'Admin.controller.spaceAdmin.GridPanelController',
        'Admin.controller.spaceAdmin.BrowseToolbarController',
        'Admin.controller.spaceAdmin.DetailPanelController',
        'Admin.controller.spaceAdmin.DetailToolbarController',
        'Admin.controller.spaceAdmin.DialogWindowController',
        'Admin.controller.spaceAdmin.WizardController'
    ],

    stores: [
        'Admin.store.spaceAdmin.SpaceStore',
        'Admin.store.spaceAdmin.SpaceTreeStore'
    ],

    requires: [
        'Admin.view.TabPanel',
        'Admin.view.spaceAdmin.FilterPanel',
        'Admin.view.spaceAdmin.BrowseToolbar',
        'Admin.view.spaceAdmin.TreeGridPanel',
        'Admin.view.spaceAdmin.DetailPanel',
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
                                    width: 200,
                                    html: 'west'
                                },
                                {
                                    region: 'center',
                                    xtype: 'container',
                                    layout: 'border',
                                    html: 'center',
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
///<reference path='../../../api/js/api.d.ts' />

///<reference path='../../../api/js/lib/JsonRpcProvider.ts' />
///<reference path='../../../api/js/lib/RemoteService.ts' />

///<reference path='lib/ux/toggleslide/Thumb.ts' />
///<reference path='lib/ux/toggleslide/ToggleSlide.ts' />

///<reference path='handler/DeleteContentHandler.ts' />
///<reference path='view/DeleteContentWindow.ts' />
///<reference path='view/wizard/form/FormComponent.ts' />
///<reference path='view/wizard/form/FormItemSetComponent.ts' />
///<reference path='view/wizard/form/input/BaseInputComponent.ts' />
///<reference path='view/wizard/form/input/TextLineComponent.ts' />

///<reference path='plugin/PersistentGridSelectionPlugin.ts' />
///<reference path='plugin/GridToolbarPlugin.ts' />

///<reference path='model/ContentModel.ts' />
///<reference path='model/ContentTypeModel.ts' />

///<reference path='store/ContentStore.ts' />
///<reference path='store/ContentTreeStore.ts' />
///<reference path='store/ContentTypeStore.ts' />
///<reference path='store/ContentTypeTreeStore.ts' />

///<reference path='lib/UriHelper.ts' />
///<reference path='lib/DateHelper.ts' />

///<reference path='view/AdminImageButton.ts' />
///<reference path='view/TopBarMenuItem.ts' />
///<reference path='view/TopBarMenu.ts' />
///<reference path='view/TopBar.ts' />
///<reference path='view/TabPanel.ts' />
///<reference path='view/BaseFilterPanel.ts' />
///<reference path='view/FilterPanel.ts' />
///<reference path='view/ShowPanel.ts' />
///<reference path='view/BrowseToolbar.ts' />
///<reference path='view/BaseContextMenu.ts' />
///<reference path='view/ContextMenu.ts' />
///<reference path='view/BaseTreeGridPanel.ts' />
///<reference path='view/TreeGridPanel.ts' />
///<reference path='view/DetailToolbar.ts' />
///<reference path='view/LivePreview.ts' />
///<reference path='view/DropDownButton.ts' />
///<reference path='view/BaseContextMenu.ts' />
///<reference path='view/IframeContainer.ts' />
///<reference path='view/BaseDetailPanel.ts' />
///<reference path='view/DetailPanel.ts' />

///<reference path='view/BaseDialogWindow.ts' />
///<reference path='view/NewContentWindow.ts' />


///<reference path='controller/BaseController.ts' />
///<reference path='controller/Controller.ts' />
///<reference path='controller/TopBarController.ts' />
///<reference path='controller/GridPanelController.ts' />
///<reference path='controller/DetailPanelController.ts' />
///<reference path='controller/FilterPanelController.ts' />
///<reference path='controller/BrowseToolbarController.ts' />
///<reference path='controller/DetailToolbarController.ts' />
///<reference path='controller/ContentController.ts' />
///<reference path='controller/ContentWizardController.ts' />
///<reference path='controller/ContentPreviewController.ts' />
///<reference path='controller/DialogWindowController.ts' />

declare var Ext;
declare var Admin;
declare var CONFIG;

Ext.application({
    name: 'CM',

    appFolder: 'resources/app',

    controllers: [
        'Admin.controller.BaseController',
        'Admin.controller.Controller',
        'Admin.controller.TopBarController',
        'Admin.controller.GridPanelController',
        'Admin.controller.DetailPanelController',
        'Admin.controller.FilterPanelController',
        'Admin.controller.BrowseToolbarController',
        'Admin.controller.DetailToolbarController',
        'Admin.controller.ContentWizardController',
        'Admin.controller.ContentPreviewController',
        'Admin.controller.DialogWindowController'
    ],

    /*    requires: [
     'Admin.MessageBus',
     'Admin.NotificationManager',
     'Admin.view.TabPanel'
     ],*/

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
                            tabConfig: {
                                hidden: true
                            },
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
    }
});
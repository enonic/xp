///<reference path='../../../api/js/ExtJs.d.ts' />
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

///<reference path='store/ContentStore.ts' />
///<reference path='store/ContentTreeStore.ts' />
///<reference path='store/ContentTypeStore.ts' />
///<reference path='store/ContentTypeTreeStore.ts' />

///<reference path='lib/UriHelper.ts' />
///<reference path='lib/DateHelper.ts' />
///<reference path='lib/Sortable.ts' />

///<reference path='view/BaseDialogWindow.ts' />
///<reference path='view/BaseDetailPanel.ts' />
///<reference path='view/AdminImageButton.ts' />
///<reference path='view/TopBarMenuItem.ts' />
///<reference path='view/TopBarMenu.ts' />
///<reference path='view/TopBar.ts' />
///<reference path='view/TabPanel.ts' />
///<reference path='view/BaseFilterPanel.ts' />
///<reference path='view/FilterPanel.ts' />
///<reference path='view/ShowPanel.ts' />
///<reference path='view/BrowseToolbar.ts' />
///<reference path='view/ContextMenu.ts' />
///<reference path='view/BaseTreeGridPanel.ts' />
///<reference path='view/TreeGridPanel.ts' />
///<reference path='view/DetailToolbar.ts' />
///<reference path='view/LivePreview.ts' />
///<reference path='view/DropDownButton.ts' />
///<reference path='view/IframeContainer.ts' />
///<reference path='view/DetailPanel.ts' />
///<reference path='view/NewContentWindow.ts' />
///<reference path='view/FileUploadWindow.ts' />
///<reference path='view/AutosizeTextField.ts' />
///<reference path='view/WizardHeader.ts' />
///<reference path='view/WizardLayout.ts' />
///<reference path='view/WizardPanel.ts' />

/// <reference path='view/wizard/form/FormItemOccurrencesHandler.ts' />
/// <reference path='view/wizard/form/ImagePopupDialog.ts' />
/// <reference path='view/wizard/form/FormGenerator.ts' />
/// <reference path='view/wizard/form/FieldSetLayout.ts' />
/// <reference path='view/wizard/form/FormComponent.ts' />
/// <reference path='view/wizard/form/FormItemContainer.ts' />
/// <reference path='view/wizard/form/FormItemSet.ts' />
/// <reference path='view/wizard/form/FormItemSetComponent.ts' />
/// <reference path='view/wizard/form/FormItemSetContainer.ts' />
/// <reference path='view/wizard/form/InputContainer.ts' />

/// <reference path='view/wizard/form/input/Base.ts' />
/// <reference path='view/wizard/form/input/BaseInputComponent.ts' />
/// <reference path='view/wizard/form/input/HtmlArea.ts' />
/// <reference path='view/wizard/form/input/HtmlArea-temp.ts' />
/// <reference path='view/wizard/form/input/Image.ts' />
/// <reference path='view/wizard/form/input/ImageSelector.ts' />
/// <reference path='view/wizard/form/input/Relationship.ts' />
/// <reference path='view/wizard/form/input/TextArea.ts' />
/// <reference path='view/wizard/form/input/TextLine.ts' />
/// <reference path='view/wizard/form/input/TextLineComponent.ts' />

///<reference path='view/wizard/ContentWizardToolbar.ts' />
///<reference path='view/wizard/WizardToolbarMenu.ts' />
///<reference path='view/wizard/ContentDataPanel.ts' />
///<reference path='view/wizard/ContentWizardPanel.ts' />
///<reference path='view/wizard/ContentLiveEditPanel.ts' />


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
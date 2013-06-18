///<reference path='../../../api/js/ExtJs.d.ts' />
///<reference path='../../../api/js/api.d.ts' />

///<reference path='event/DeletedEvent.ts' />
///<reference path='event/BaseSpaceModelEvent.ts' />
///<reference path='event/DeletePromptEvent.ts' />
///<reference path='event/GridSelectionChangeEvent.ts' />
///<reference path='event/GridDeselectEvent.ts' />
///<reference path='event/ShowContextMenuEvent.ts' />
///<reference path='event/NewSpaceEvent.ts' />
///<reference path='event/OpenSpaceEvent.ts' />
///<reference path='event/EditSpaceEvent.ts' />
///<reference path='event/SaveSpaceEvent.ts' />
///<reference path='event/CloseActiveSpacePanelEvent.ts' />

///<reference path='SpaceContext.ts' />

///<reference path='SpaceActions.ts' />
///<reference path='SpaceAppBrowsePanel.ts' />

///<reference path='wizard/SpaceWizardActions.ts' />
///<reference path='wizard/SpaceWizardContext.ts' />
///<reference path='wizard/SpaceWizardToolbar2.ts' />

///<reference path='plugin/PersistentGridSelectionPlugin.ts' />
///<reference path='plugin/GridToolbarPlugin.ts' />
///<reference path='plugin/fileupload/FileUploadGrid.ts' />
///<reference path='plugin/fileupload/PhotoUploadButton.ts' />
///<reference path='plugin/fileupload/PhotoUploadWindow.ts' />

///<reference path='model/SpaceModel.ts' />

///<reference path='view/WizardLayout.ts' />
///<reference path='view/WizardHeader.ts' />
///<reference path='view/WizardPanel.ts' />

///<reference path='view/BaseActionMenu.ts' />
///<reference path='view/ActionMenu.ts' />
///<reference path='view/ActionMenu2.ts' />
///<reference path='view/DetailToolbar.ts' />
///<reference path='view/DetailPanel.ts' />

///<reference path='view/DeleteSpaceDialog.ts' />

///<reference path='view/TreeGridPanel.ts' />

///<reference path='view/ContextMenu.ts' />
///<reference path='view/ContextMenuGridPanel.ts' />

///<reference path='view/wizard/SpaceWizardToolbar.ts' />
///<reference path='view/wizard/SpaceStepPanel.ts' />
///<reference path='view/wizard/SpaceWizardPanel.ts' />
///<reference path='wizard/SpaceWizardPanel2.ts' />

///<reference path='view/AdminImageButton.ts' />
///<reference path='view/TopBarMenuItem.ts' />
///<reference path='view/TopBarMenu.ts' />
///<reference path='view/TopBar.ts' />
///<reference path='view/TabPanel.ts' />
///<reference path='view/FilterPanel.ts' />

///<reference path='view/BrowseToolbar.ts' />
///<reference path='appbar/SpaceAppBar.ts' />

///<reference path='controller/Controller.ts' />
///<reference path='controller/SpaceController.ts' />

///<reference path='controller/FilterPanelController.ts' />
///<reference path='controller/GridPanelController.ts' />
///<reference path='controller/BrowseToolbarController.ts' />
///<reference path='controller/DetailPanelController.ts' />
///<reference path='controller/DetailToolbarController.ts' />
///<reference path='controller/WizardController.ts' />

///<reference path='appbar/SpaceAppBarTabMenuItem.ts' />
///<reference path='appbar/SpaceAppBarTabMenu.ts' />
///<reference path='appbar/SpaceAppBar.ts' />
///<reference path='SpaceAppTabPanelController.ts' />
///<reference path='SpaceAppPanel.ts' />


declare var Ext;
declare var Admin;
declare var CONFIG;

module app {

    // Application id for uniquely identifying app
    export var id = 'space-manager';

}

module components {
    export var detailPanel:app_ui.SpaceDetailPanel;
    export var gridPanel:app_ui.TreeGridPanel;
    export var tabPanel:app_ui.TabPanel;
}


Ext.application({
    name: 'spaceAdmin',

    controllers: [
        'Admin.controller.FilterPanelController',
        'Admin.controller.GridPanelController',
        'Admin.controller.BrowseToolbarController',
        'Admin.controller.DetailPanelController',
        'Admin.controller.DetailToolbarController',
        'Admin.controller.WizardController'
    ],

    stores: [],

    launch: function () {

        // TODO: var spaceAppPanel = new app.SpaceAppPanel();
        var spaceAppMainPanel = new app.SpaceAppBrowsePanel();

        var tabPanel = components.tabPanel = app_ui.TabPanel.init({
            appName: 'Space Admin',
            appIconCls: 'icon-metro-space-admin-24'
        }).getExtEl();

        tabPanel.add(spaceAppMainPanel.ext);

        var viewPort = new Ext.container.Viewport({
            layout: 'fit',
            cls: 'admin-viewport'
        });

        viewPort.add(tabPanel);

        var deleteSpaceDialog:app_ui.DeleteSpaceDialog = new app_ui.DeleteSpaceDialog();
        app_event.DeletePromptEvent.on((event) => {
            deleteSpaceDialog.setSpacesToDelete(event.getModels());
            deleteSpaceDialog.open();
        });

    }
});

app.SpaceContext.init();
app.SpaceActions.init();
app.SpaceAppTabPanelController.init();


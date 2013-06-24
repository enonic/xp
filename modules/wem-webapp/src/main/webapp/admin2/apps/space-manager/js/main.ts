///<reference path='../../../api/js/ExtJs.d.ts' />
///<reference path='../../../api/js/Mousetrap.d.ts' />
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

///<reference path='wizard/SpaceWizardActions.ts' />
///<reference path='wizard/SpaceWizardContext.ts' />
///<reference path='wizard/SpaceWizardToolbar2.ts' />
///<reference path='wizard/SpaceWizardPanel2.ts' />

///<reference path='browse/SpaceBrowseActions.ts' />
///<reference path='browse/SpaceBrowseToolbar.ts' />
///<reference path='browse/SpaceActionMenu.ts' />
///<reference path='browse/SpaceDetailPanel.ts' />
///<reference path='browse/SpaceAppBrowsePanel.ts' />
///<reference path='browse/SpaceTreeGridContextMenu.ts' />
///<reference path='browse/SpaceTreeGridPanel.ts' />

///<reference path='plugin/PersistentGridSelectionPlugin.ts' />
///<reference path='plugin/GridToolbarPlugin.ts' />
///<reference path='plugin/fileupload/FileUploadGrid.ts' />
///<reference path='plugin/fileupload/PhotoUploadButton.ts' />
///<reference path='plugin/fileupload/PhotoUploadWindow.ts' />

///<reference path='model/SpaceModel.ts' />

///<reference path='view/WizardLayout.ts' />
///<reference path='view/WizardHeader.ts' />
///<reference path='view/WizardPanel.ts' />
///<reference path='view/wizard/SpaceWizardToolbar.ts' />
///<reference path='view/wizard/SpaceStepPanel.ts' />
///<reference path='view/wizard/SpaceWizardPanel.ts' />
///<reference path='view/AdminImageButton.ts' />
///<reference path='view/FilterPanel.ts' />

///<reference path='controller/Controller.ts' />
///<reference path='controller/SpaceController.ts' />
///<reference path='controller/FilterPanelController.ts' />
///<reference path='controller/GridPanelController.ts' />
///<reference path='controller/WizardController.ts' />

///<reference path='appbar/SpaceAppBar.ts' />
///<reference path='appbar/SpaceAppBarActions.ts' />
///<reference path='appbar/SpaceAppBarTabMenuItem.ts' />
///<reference path='appbar/SpaceAppBarTabMenu.ts' />
///<reference path='appbar/SpaceAppBar.ts' />

///<reference path='SpaceDeleteDialog.ts' />
///<reference path='SpaceAppPanel.ts' />


declare var Ext;
declare var Admin;
declare var CONFIG;

module app {

    // Application id for uniquely identifying app
    export var id = 'space-manager';

}

module components {
    export var detailPanel:app_browse.SpaceDetailPanel;
    export var gridPanel:app_browse.SpaceTreeGridPanel;
}


Ext.application({
    name: 'spaceAdmin',

    controllers: [
        'Admin.controller.FilterPanelController',
        'Admin.controller.GridPanelController',
        'Admin.controller.WizardController'
    ],

    stores: [],

    launch: function () {

        var appBar = new app_appbar.SpaceAppBar();
        var appPanel = new app.SpaceAppPanel(appBar);

        document.body.appendChild(appBar.getHTMLElement());
        document.body.appendChild(appPanel.getHTMLElement());

        appPanel.init();

        var deleteSpaceDialog = new app.SpaceDeleteDialog();
        app_event.DeletePromptEvent.on((event) => {
            deleteSpaceDialog.setSpacesToDelete(event.getModels());
            deleteSpaceDialog.open();
        });

    }
});

app.SpaceContext.init();
app_browse.SpaceBrowseActions.init();


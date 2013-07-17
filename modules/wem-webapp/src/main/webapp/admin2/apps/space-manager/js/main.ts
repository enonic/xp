///<reference path='../../../api/js/ExtJs.d.ts' />
///<reference path='../../../api/js/Mousetrap.d.ts' />
///<reference path='../../../api/js/api.d.ts' />

///<reference path='model/SpaceModel.ts' />

///<reference path='view/WizardLayout.ts' />
///<reference path='view/WizardHeader.ts' />
///<reference path='view/WizardPanel.ts' />
///<reference path='view/wizard/SpaceWizardToolbar.ts' />
///<reference path='view/wizard/SpaceStepPanel.ts' />
///<reference path='view/wizard/SpaceWizardPanel.ts' />
///<reference path='view/AdminImageButton.ts' />
///<reference path='view/FilterPanel.ts' />

/*///<reference path='app/event/SaveSpaceEvent.ts' />
///<reference path='app/event/DeletedEvent.ts' />
///<reference path='app/event/BaseSpaceModelEvent.ts' />
///<reference path='app/event/SpaceDeletePromptEvent.ts' />
///<reference path='app/event/GridSelectionChangeEvent.ts' />
///<reference path='app/event/GridDeselectEvent.ts' />
///<reference path='app/event/ShowContextMenuEvent.ts' />
///<reference path='app/event/NewSpaceEvent.ts' />
///<reference path='app/event/OpenSpaceEvent.ts' />
///<reference path='app/event/EditSpaceEvent.ts' />
///<reference path='app/event/CloseActiveSpacePanelEvent.ts' />*/

///<reference path='app/wizard/SpaceWizardEvents.ts' />
///<reference path='app/wizard/SpaceWizardActions.ts' />
/*///<reference path='app/wizard/SpaceWizardContext.ts' />*/
///<reference path='app/wizard/SpaceWizardToolbar.ts' />
///<reference path='app/wizard/SpaceForm.ts' />
///<reference path='app/wizard/SpaceWizardPanel.ts' />

///<reference path='app/browse/SpaceBrowseEvents.ts' />
///<reference path='app/browse/SpaceBrowseActions.ts' />
///<reference path='app/browse/SpaceBrowseToolbar.ts' />
///<reference path='app/browse/SpaceActionMenu.ts' />
///<reference path='app/browse/SpaceBrowseItemPanel.ts' />
///<reference path='app/browse/SpaceBrowsePanel.ts' />
///<reference path='app/browse/SpaceTreeGridContextMenu.ts' />
///<reference path='app/browse/SpaceTreeGridPanel.ts' />
///<reference path='app/browse/SpaceItemStatisticsPanel.ts' />
///<reference path='app/browse/SpaceViewActions.ts' />
///<reference path='app/browse/SpaceItemViewToolbar.ts' />
///<reference path='app/browse/SpaceItemViewPanel.ts' />

///<reference path='app/delete/DeletedEvent.ts' />
///<reference path='app/delete/SpaceDeleteDialog.ts' />

///<reference path='app/SpaceAppBar.ts' />
/*///<reference path='app/SpaceAppBarActions.ts' />*/
///<reference path='app/SpaceAppBarTabMenuItem.ts' />
///<reference path='app/SpaceAppBarTabMenu.ts' />
///<reference path='app/SpaceAppBar.ts' />
///<reference path='app/SpaceContext.ts' />
///<reference path='app/SpaceAppPanel.ts' />

///<reference path='controller/Controller.ts' />
///<reference path='controller/SpaceController.ts' />
///<reference path='controller/FilterPanelController.ts' />
///<reference path='controller/GridPanelController.ts' />
///<reference path='controller/WizardController.ts' />


declare var Ext;
declare var Admin;
declare var CONFIG;

module app {

    // Application id for uniquely identifying app
    export var id = 'space-manager';

}

module components {
    export var detailPanel:app_browse.SpaceBrowseItemPanel;
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

        var appBar = new app.SpaceAppBar();
        var appPanel = new app.SpaceAppPanel(appBar);

        api_dom.Body.get().appendChild(appBar);
        api_dom.Body.get().appendChild(appPanel);

        appPanel.init();

        var deleteSpaceDialog = new app.SpaceDeleteDialog();
        app_browse.SpaceDeletePromptEvent.on((event) => {
            deleteSpaceDialog.setSpacesToDelete(event.getModels());
            deleteSpaceDialog.open();
        });

        var spaceGridContextMenu = new app_browse.SpaceTreeGridContextMenu();
        spaceGridContextMenu.hide();
        app_browse.ShowContextMenuEvent.on((event) => {
            spaceGridContextMenu.showAt(event.getX(), event.getY());
        })

    }
});

app.SpaceContext.init();
app_browse.SpaceBrowseActions.init();


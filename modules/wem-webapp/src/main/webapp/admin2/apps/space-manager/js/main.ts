///<reference path='../../../api/js/ExtJs.d.ts' />
///<reference path='../../../api/js/Mousetrap.d.ts' />
///<reference path='../../../api/js/api.d.ts' />

///<reference path='model/SpaceModel.ts' />

///<reference path='app/wizard/SpaceWizardEvents.ts' />
///<reference path='app/wizard/SpaceWizardActions.ts' />
///<reference path='app/wizard/SpaceWizardToolbar.ts' />
///<reference path='app/wizard/SpaceForm.ts' />
///<reference path='app/wizard/SpaceWizardPanel.ts' />

///<reference path='app/browse/SpaceBrowseEvents.ts' />
///<reference path='app/browse/SpaceBrowseActions.ts' />
///<reference path='app/browse/SpaceBrowseToolbar.ts' />
///<reference path='app/browse/SpaceBrowseItemPanel.ts' />
///<reference path='app/browse/SpaceBrowsePanel.ts' />
///<reference path='app/browse/SpaceTreeGridContextMenu.ts' />
///<reference path='app/browse/grid/SpaceGridStore.ts' />
///<reference path='app/browse/grid/SpaceTreeStore.ts' />
///<reference path='app/browse/SpaceTreeGridPanel.ts' />

///<reference path='app/browse/filter/SpaceBrowseFilterEvents.ts' />
///<reference path='app/browse/filter/SpaceBrowseFilterPanel.ts' />

///<reference path='app/view/SpaceItemStatisticsPanel.ts' />
///<reference path='app/view/SpaceViewActions.ts' />
///<reference path='app/view/SpaceItemViewToolbar.ts' />
///<reference path='app/view/SpaceItemViewPanel.ts' />

///<reference path='app/delete/DeletedEvent.ts' />
///<reference path='app/delete/SpaceDeleteDialog.ts' />

///<reference path='app/SpaceAppBar.ts' />
///<reference path='app/SpaceAppBarTabMenuItem.ts' />
///<reference path='app/SpaceAppBarTabMenu.ts' />
///<reference path='app/SpaceAppBar.ts' />
///<reference path='app/SpaceAppPanel.ts' />


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
    }
});


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

window.onload = () => {
    var appBar = new api_app.AppBar("Space Admin", new api_app.AppBarTabMenu("SpaceAppBarTabMenu"));
    var appPanel = new app.SpaceAppPanel(appBar);

    api_dom.Body.get().appendChild(appBar);
    api_dom.Body.get().appendChild(appPanel);

    appPanel.init();

    var deleteSpaceDialog = new app.SpaceDeleteDialog();
    app_browse.SpaceDeletePromptEvent.on((event) => {
        deleteSpaceDialog.setSpacesToDelete(event.getModels());
        deleteSpaceDialog.open();
    });
};


declare var Ext;
declare var Admin;
declare var CONFIG;

window.onload = () => {
    var appBar = new api_app.AppBar("Module Manager", new api_app.AppBarTabMenu("ModuleAppBarTabMenu"));
    var appPanel = new app.ModuleAppPanel(appBar);

    api_dom.Body.get().appendChild(appBar);
    api_dom.Body.get().appendChild(appPanel);

    appPanel.init();

    if (window.parent["appLoaded"]) {
        window.parent["appLoaded"](getAppName());
    }
};

module components {
    export var detailPanel:app_browse.ModuleBrowseItemPanel;
    export var gridPanel:app_browse.ModuleTreeGridPanel;
}

function getAppName():string {
    return jQuery(window.frameElement).data("wem-app");
}

function route(path:api_rest.Path) {
    var action = path.getElement(0);

    switch (action) {
    case 'edit':
        console.log("edit");
        break;
    case 'view' :
        console.log("view");
        break;
    default:
        new api_app.ShowAppBrowsePanelEvent().fire();
        break;
    }
}
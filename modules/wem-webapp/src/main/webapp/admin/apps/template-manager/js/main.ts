declare var Ext;
declare var Admin;
declare var CONFIG;

module components {
    export var detailPanel:app_browse.TemplateBrowseItemPanel;
    export var gridPanel:app_browse.TemplateTreeGridPanel;
}

window.onload = () => {
    var appBar = new api_app.AppBar("Template Manager", new api_app.AppBarTabMenu("TemplateAppBarTabMenu"));
    var appPanel = new app.TemplateAppPanel(appBar);

    api_dom.Body.get().appendChild(appBar);
    api_dom.Body.get().appendChild(appPanel);

    appPanel.init();

    if (window.parent["appLoaded"]) {
        window.parent["appLoaded"](getAppName());
    }
};

function getAppName():string {
    return jQuery(window.frameElement).data("wem-app");
}
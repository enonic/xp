
declare var Ext;
declare var Admin;
declare var CONFIG;

module components {
    export var browseToolbar:app_browse.ContentBrowseToolbar;
    export var contextMenu:app_browse.ContentTreeGridContextMenu;
    export var gridPanel:app_browse.ContentTreeGridPanel;
    export var detailPanel:app_browse.ContentBrowseItemPanel;
}

window.onload = () => {
    var appBar = new api_app.AppBar("Content Manager", new api_app.AppBarTabMenu("ContentAppBarTabMenu"));
    var appPanel = new app.ContentAppPanel(appBar);

    api_dom.Body.get().appendChild(appBar);
    api_dom.Body.get().appendChild(appPanel);

    appPanel.init();

    var contentDeleteDialog = new app_delete.ContentDeleteDialog();
    app_browse.ContentDeletePromptEvent.on((event) => {
        contentDeleteDialog.setContentToDelete(event.getModels());
        contentDeleteDialog.open();
    });

    var newContentDialog = new app_new.NewContentDialog();
    app_browse.ShowNewContentDialogEvent.on((event) => {

        var parentContent:api_content.ContentSummary = event.getParentContent();

        new api_content.GetContentByIdRequest(parentContent.getId()).send().
            done((jsonResponse:api_rest.JsonResponse) => {
                var newParentContent = new api_content.Content(jsonResponse.getJson());
                newContentDialog.setParentContent(newParentContent);
                newContentDialog.open();
            });
    });
    if (window.parent["appLoaded"]) {
        window.parent["appLoaded"](getAppName());
    }
};

function getAppName():string {
    return jQuery(window.frameElement).data("wem-app");
}

function route(path:api_rest.Path) {
    var action = path.getElement(0);

    switch (action) {
        case 'edit':
            var id = path.getElement(1);
            if (id) {
                var getContentByIdPromise = new api_content.GetContentByIdRequest(id).send();
                jQuery.when(getContentByIdPromise).then((contentResponse:api_rest.JsonResponse) => {
                    new app_browse.EditContentEvent([new api_content.ContentSummary(contentResponse.getJson())]).fire();
                });
            }
            break;
        case 'view' :
            var id = path.getElement(1);
            if (id) {
                var getContentByIdPromise = new api_content.GetContentByIdRequest(id).send();
                jQuery.when(getContentByIdPromise).then((contentResponse:api_rest.JsonResponse) => {
                    new app_browse.OpenContentEvent([new api_content.ContentSummary(contentResponse.getJson())]).fire();
                });
            }
            break;
        default:
            new api_app.ShowAppBrowsePanelEvent().fire();
            break;
    }
}
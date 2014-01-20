declare var Ext: Ext_Packages;
declare var Admin;
declare var CONFIG;

module components {
    export var browseToolbar: app.browse.ContentBrowseToolbar;
    export var contextMenu: app.browse.ContentTreeGridContextMenu;
    export var gridPanel: app.browse.ContentTreeGridPanel;
    export var detailPanel: app.browse.ContentBrowseItemPanel;
}

window.onload = () => {
    var appBar = new api.app.AppBar("Content Manager", new api.app.AppBarTabMenu());
    var appPanel = new app.ContentAppPanel(appBar);

    api.dom.Body.get().appendChild(appBar);
    api.dom.Body.get().appendChild(appPanel);

    appPanel.init();

    var contentDeleteDialog = new app.remove.ContentDeleteDialog();
    app.browse.ContentDeletePromptEvent.on((event) => {
        contentDeleteDialog.setContentToDelete(event.getModels());
        contentDeleteDialog.open();
    });

    var newContentDialog = new app.create.NewContentDialog();
    app.browse.ShowNewContentDialogEvent.on((event) => {

        var parentContent: api.content.ContentSummary = event.getParentContent();

        if (parentContent != null) {
            new api.content.GetContentByIdRequest(parentContent.getContentId()).send().
                done((jsonResponse: api.rest.JsonResponse<api.content.json.ContentJson>) => {
                    var newParentContent = new api.content.Content(jsonResponse.getResult());
                    newContentDialog.setParentContent(newParentContent);
                    newContentDialog.open();
                });
        }
        else {
            newContentDialog.setParentContent(null);
            newContentDialog.open();
        }
    });
    if (window.parent["appLoaded"]) {
        window.parent["appLoaded"](getAppName());
    }

    window.onmessage = (e:MessageEvent) => {
        if( e.data.appLauncherEvent ) {
            var eventType:api.app.AppLauncherEventType = api.app.AppLauncherEventType[<string>e.data.appLauncherEvent];
            if( eventType ==  api.app.AppLauncherEventType.Show ) {
                appPanel.activateCurrentKeyBindings();
            }
        }
    }
};

function getAppName(): string {
    return jQuery(window.frameElement).data("wem-app");
}

function route(path: api.rest.Path) {
    var action = path.getElement(0);

    switch (action) {
    case 'edit':
        var id = path.getElement(1);
        if (id) {
            var getContentByIdPromise = new api.content.GetContentByIdRequest(new api.content.ContentId(id)).send();
            jQuery.when(getContentByIdPromise).then((contentResponse: api.rest.JsonResponse<api.content.json.ContentJson>) => {
                new app.browse.EditContentEvent([new api.content.Content(contentResponse.getResult())]).fire();
            });
        }
        break;
    case 'view' :
        var id = path.getElement(1);
        if (id) {
            var getContentByIdPromise = new api.content.GetContentByIdRequest(new api.content.ContentId(id)).send();
            jQuery.when(getContentByIdPromise).then((contentResponse: api.rest.JsonResponse<api.content.json.ContentJson>) => {
                new app.browse.ViewContentEvent([new api.content.Content(contentResponse.getResult())]).fire();
            });
        }
        break;
    default:
        new api.app.ShowAppBrowsePanelEvent().fire();
        break;
    }
}
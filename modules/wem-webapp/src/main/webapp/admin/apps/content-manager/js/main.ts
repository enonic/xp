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
    var application:api.app.Application = api.app.Application.getApplication();

    var appBar = new api.app.AppBar(application);
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
            new api.content.GetContentByIdRequest(parentContent.getContentId()).sendAndParse().
                done((newParentContent: api.content.Content) => {
                    newContentDialog.setParentContent(newParentContent);
                    newContentDialog.open();
                });
        }
        else {
            newContentDialog.setParentContent(null);
            newContentDialog.open();
        }
    });
    application.setLoaded(true);

    window.onmessage = (e: MessageEvent) => {
        if (e.data.appLauncherEvent) {
            var eventType: api.app.AppLauncherEventType = api.app.AppLauncherEventType[<string>e.data.appLauncherEvent];
            if (eventType == api.app.AppLauncherEventType.Show) {
                appPanel.activateCurrentKeyBindings();
            }
        }
    }
};

function route(path: api.rest.Path) {
    var action = path.getElement(0);

    switch (action) {
    case 'edit':
        var id = path.getElement(1);
        if (id) {
            var getContentByIdPromise = new api.content.GetContentByIdRequest(new api.content.ContentId(id)).sendAndParse().
                done((content: api.content.Content) => {
                    new app.browse.EditContentEvent([content]).fire();
                });
        }
        break;
    case 'view' :
        var id = path.getElement(1);
        if (id) {
            var getContentByIdPromise = new api.content.GetContentByIdRequest(new api.content.ContentId(id)).sendAndParse().
                done((content: api.content.Content) => {
                    new app.browse.ViewContentEvent([content]).fire();
                });
        }
        break;
    default:
        new api.app.ShowAppBrowsePanelEvent().fire();
        break;
    }
}
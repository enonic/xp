declare var Ext: Ext_Packages;
declare var Admin;
declare var CONFIG;

module components {
    export var browseToolbar: app.browse.ContentBrowseToolbar;
    export var contextMenu: app.browse.ContentTreeGridContextMenu;
    export var gridPanel: app.browse.ContentTreeGridPanel;
    export var detailPanel: app.browse.ContentBrowseItemPanel;
}

function startApplication() {
    var application: api.app.Application = api.app.Application.getApplication();

    var appBar = new api.app.AppBar(application);
    var appPanel = new app.ContentAppPanel(appBar, application.getPath());

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

    var publishDialog = new app.wizard.PublishContentDialog();
    application.setLoaded(true);

    window.onmessage = (e: MessageEvent) => {
        if (e.data.appLauncherEvent) {
            var eventType: api.app.AppLauncherEventType = api.app.AppLauncherEventType[<string>e.data.appLauncherEvent];
            if (eventType == api.app.AppLauncherEventType.Show) {
                appPanel.activateCurrentKeyBindings();
            }
        }
    }
}
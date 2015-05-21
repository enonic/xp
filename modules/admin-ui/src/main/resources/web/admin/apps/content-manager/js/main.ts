declare var CONFIG;

module components {
    export var detailPanel: app.browse.ContentBrowseItemPanel;
}

function initToolTip() {
    var ID = "tooltip", CLS_ON = "tooltip_ON", FOLLOW = true,
        DATA = "_tooltip", OFFSET_X = 0, OFFSET_Y = 20,
        showAt = function (e) {
            var ntop = e.pageY + OFFSET_Y, nleft = e.pageX + OFFSET_X;
            wemjq("#" + ID).html(api.util.StringHelper.escapeHtml(wemjq(e.target).data(DATA))).css({
                position: "absolute", top: ntop, left: nleft
            }).show();
        };
    wemjq(document).on("mouseenter", "*[title]", function (e) {
        wemjq(this).data(DATA, wemjq(this).attr("title"));
        wemjq(this).removeAttr("title").addClass(CLS_ON);
        wemjq("<div id='" + ID + "' />").appendTo("body");
        showAt(e);
    });
    wemjq(document).on("mouseleave", "." + CLS_ON, function (e) {
        wemjq(this).attr("title", wemjq(this).data(DATA)).removeClass(CLS_ON);
        wemjq("#" + ID).remove();
    });
    if (FOLLOW) { wemjq(document).on("mousemove", "." + CLS_ON, showAt); }
}

function startApplication() {

    var application: api.app.Application = api.app.Application.getApplication();

    var body = api.dom.Body.get();

    var appBar = new api.app.bar.AppBar(application);
    var appPanel = new app.ContentAppPanel(appBar, application.getPath());

    body.appendChild(appBar);
    body.appendChild(appPanel);

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
                then((newParentContent: api.content.Content) => {

                    // TODO: remove pyramid of doom
                    if (parentContent.hasParent() && parentContent.getType().isTemplateFolder()) {
                        new api.content.GetContentByPathRequest(parentContent.getPath().getParentPath()).
                            sendAndParse().then((grandParent: api.content.Content) => {

                                newContentDialog.setParentContent(newParentContent);
                                newContentDialog.open();
                            }).catch((reason: any) => {
                                api.DefaultErrorHandler.handle(reason);
                            }).done();
                    }
                    else {
                        newContentDialog.setParentContent(newParentContent);
                        newContentDialog.open();
                    }
                }).catch((reason: any) => {
                    api.DefaultErrorHandler.handle(reason);
                }).done();
        }
        else {
            newContentDialog.setParentContent(null);
            newContentDialog.open();
        }
    });

    initToolTip();

    var sortDialog = new app.browse.SortContentDialog();
    var moveDialog = new app.browse.MoveContentDialog();
    var editPermissionsDialog = new app.wizard.EditPermissionsDialog();
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
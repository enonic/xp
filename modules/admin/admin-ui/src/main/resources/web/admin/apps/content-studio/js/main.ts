declare var CONFIG;

module components {
    export var detailPanel: app.browse.ContentBrowseItemPanel;
}

var application = (function () {
    var application = new api.app.Application('content-studio', 'Content Studio', 'CM', 'content-studio');
    application.setPath(api.rest.Path.fromString(app.Router.getPath()));
    application.setWindow(window);
    this.serverEventsListener = new api.app.ServerEventsListener([application]);

    var messageId;
    this.lostConnectionDetector = new api.system.LostConnectionDetector();
    this.lostConnectionDetector.setAuthenticated(true);
    this.lostConnectionDetector.onConnectionLost(() => {
        api.notify.NotifyManager.get().hide(messageId);
        messageId = api.notify.showError("Lost connection to server - Please wait until connection is restored", false);
    });
    this.lostConnectionDetector.onSessionExpired(() => {
        api.notify.NotifyManager.get().hide(messageId);
        window.location.href = api.util.UriHelper.getToolUri("");
    });
    this.lostConnectionDetector.onConnectionRestored(() => {
        api.notify.NotifyManager.get().hide(messageId);
    });

    return application;
})();

function getApplication(id: string): api.app.Application {
    return application;
}

function initToolTip() {
    var ID = api.StyleHelper.getCls("tooltip", api.StyleHelper.COMMON_PREFIX),
        CLS_ON = "tooltip_ON", FOLLOW = true,
        DATA = "_tooltip", OFFSET_X = 0, OFFSET_Y = 20,
        pageX = 0, pageY = 0,
        showAt = function (e) {
            var ntop = pageY + OFFSET_Y, nleft = pageX + OFFSET_X;
            var tooltipText = api.util.StringHelper.escapeHtml(wemjq(e.target).data(DATA));
            var tooltipWidth = tooltipText.length * 7.5;
            var windowWidth = wemjq(window).width();
            if (nleft + tooltipWidth >= windowWidth) {
                nleft = windowWidth - tooltipWidth;
            }
            wemjq("#" + ID).html(tooltipText).css({
                position: "absolute", top: ntop, left: nleft
            }).show();
        };
    wemjq(document).on("mouseenter", "*[title]", function (e) {
        wemjq(this).data(DATA, wemjq(this).attr("title"));
        wemjq(this).removeAttr("title").addClass(CLS_ON);
        wemjq("<div id='" + ID + "' />").appendTo("body");
        if (e.pageX) {
            pageX = e.pageX;
        }
        if (e.pageY) {
            pageY = e.pageY;
        }
        showAt(e);
    });
    wemjq(document).on("mouseleave click", "." + CLS_ON, function (e) {
        if (wemjq(this).data(DATA)) {
            wemjq(this).attr("title", wemjq(this).data(DATA));
        }
        wemjq(this).removeClass(CLS_ON);
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
        contentDeleteDialog.
            setContentToDelete(event.getModels()).
            setYesCallback(event.getYesCallback()).
            setNoCallback(event.getNoCallback()).
            open();
    });

    app.browse.ContentPublishPromptEvent.on((event) => {
        var contentPublishDialog = new app.publish.ContentPublishDialog();
        contentPublishDialog.setSelectedContents(event.getModels());
        contentPublishDialog.initAndOpen();
    });

    var newContentDialog = new app.create.NewContentDialog();
    app.browse.ShowNewContentDialogEvent.on((event) => {

        var parentContent: api.content.ContentSummary = event.getParentContent()
            ? event.getParentContent().getContentSummary() : null;

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

    api.util.AppHelper.preventDragRedirect();

    var sortDialog = new app.browse.SortContentDialog();
    var moveDialog = new app.browse.MoveContentDialog();
    var editPermissionsDialog = new app.wizard.EditPermissionsDialog();
    application.setLoaded(true);
    this.serverEventsListener.start();
    this.lostConnectionDetector.startPolling();

    window.onmessage = (e: MessageEvent) => {
        if (e.data.appLauncherEvent) {
            var eventType: api.app.AppLauncherEventType = api.app.AppLauncherEventType[<string>e.data.appLauncherEvent];
            if (eventType == api.app.AppLauncherEventType.Show) {
                appPanel.activateCurrentKeyBindings();
            }
        }
    };

    api.content.event.ContentServerEventsHandler.getInstance().start();
}
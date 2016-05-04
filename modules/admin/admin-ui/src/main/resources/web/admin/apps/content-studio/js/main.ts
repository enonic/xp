import "./api.ts";
import {Router} from "./app/Router";
import {ContentAppPanel} from "./app/ContentAppPanel";
import {ContentDeletePromptEvent} from "./app/browse/ContentDeletePromptEvent";
import {ContentPublishPromptEvent} from "./app/browse/ContentPublishPromptEvent";
import {ContentDeleteDialog} from "./app/remove/ContentDeleteDialog";
import {ContentPublishDialog} from "./app/publish/ContentPublishDialog";
import {NewContentDialog} from "./app/create/NewContentDialog";
import {ShowNewContentDialogEvent} from "./app/browse/ShowNewContentDialogEvent";
import {SortContentDialog} from "./app/browse/SortContentDialog";
import {MoveContentDialog} from "./app/browse/MoveContentDialog";
import {EditPermissionsDialog} from "./app/wizard/EditPermissionsDialog";

declare var CONFIG;

/*
 module components {
 export var detailPanel: app.browse.ContentBrowseItemPanel;
 }
 */

function getApplication(): api.app.Application {
    var application = new api.app.Application('content-studio', 'Content Studio', 'CM', 'content-studio');
    application.setPath(api.rest.Path.fromString(Router.getPath()));
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

    var application: api.app.Application = getApplication();

    var body = api.dom.Body.get();

    var appBar = new api.app.bar.AppBar(application);
    var appPanel = new ContentAppPanel(appBar, application.getPath());

    body.appendChild(appBar);
    body.appendChild(appPanel);

    var contentDeleteDialog = new ContentDeleteDialog();
    ContentDeletePromptEvent.on((event) => {
        contentDeleteDialog.setContentToDelete(event.getModels()).setYesCallback(event.getYesCallback()).setNoCallback(
            event.getNoCallback()).open();
    });

    ContentPublishPromptEvent.on((event) => {
        var contentPublishDialog = new ContentPublishDialog();
        contentPublishDialog.setSelectedContents(event.getModels());
        contentPublishDialog.initAndOpen();
    });

    var newContentDialog = new NewContentDialog();
    ShowNewContentDialogEvent.on((event) => {

        var parentContent: api.content.ContentSummary = event.getParentContent()
            ? event.getParentContent().getContentSummary() : null;

        if (parentContent != null) {
            new api.content.GetContentByIdRequest(parentContent.getContentId()).sendAndParse().then(
                (newParentContent: api.content.Content) => {

                    // TODO: remove pyramid of doom
                    if (parentContent.hasParent() && parentContent.getType().isTemplateFolder()) {
                        new api.content.GetContentByPathRequest(parentContent.getPath().getParentPath()).sendAndParse().then(
                            (grandParent: api.content.Content) => {

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

    var sortDialog = new SortContentDialog();
    var moveDialog = new MoveContentDialog();
    var editPermissionsDialog = new EditPermissionsDialog();
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

window.onload = function () {
    startApplication();
};

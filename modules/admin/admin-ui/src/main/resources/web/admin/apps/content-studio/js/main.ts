import "./api.ts";
import {Router} from "./app/Router";
import {ContentAppPanel} from "./app/ContentAppPanel";
import {ContentDeletePromptEvent} from "./app/browse/ContentDeletePromptEvent";
import {ContentPublishPromptEvent} from "./app/browse/ContentPublishPromptEvent";
import {ContentUnpublishPromptEvent} from "./app/browse/ContentUnpublishPromptEvent";
import {ContentDeleteDialog} from "./app/remove/ContentDeleteDialog";
import {ContentPublishDialog} from "./app/publish/ContentPublishDialog";
import {ContentUnpublishDialog} from "./app/publish/ContentUnpublishDialog";
import {NewContentDialog} from "./app/create/NewContentDialog";
import {ShowNewContentDialogEvent} from "./app/browse/ShowNewContentDialogEvent";
import {SortContentDialog} from "./app/browse/SortContentDialog";
import {MoveContentDialog} from "./app/browse/MoveContentDialog";
import {EditPermissionsDialog} from "./app/wizard/EditPermissionsDialog";
import {ContentWizardPanelParams} from "./app/wizard/ContentWizardPanelParams";
import {ContentWizardPanel} from "./app/wizard/ContentWizardPanel";
import {ContentEventsListener} from "./app/ContentEventsListener";
import {ContentEventsProcessor} from "./app/ContentEventsProcessor";
import UriHelper = api.util.UriHelper;
import ContentTypeName = api.schema.content.ContentTypeName;
import ContentId = api.content.ContentId;
import AppBarTabId = api.app.bar.AppBarTabId;
import ContentNamedEvent = api.content.event.ContentNamedEvent;
import PropertyChangedEvent = api.PropertyChangedEvent;
import ContentIconUrlResolver = api.content.util.ContentIconUrlResolver;
import Content = api.content.Content;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import ShowBrowsePanelEvent = api.app.ShowBrowsePanelEvent;
import ImgEl = api.dom.ImgEl;
import LostConnectionDetector = api.system.LostConnectionDetector;

declare var CONFIG;

/*
 module components {
 export var detailPanel: app.browse.ContentBrowseItemPanel;
 }
 */

function getApplication(): api.app.Application {
    let application = new api.app.Application('content-studio', 'Content Studio', 'CM', 'content-studio');
    application.setPath(api.rest.Path.fromString(Router.getPath()));
    application.setWindow(window);

    return application;
}

function startLostConnectionDetector(): LostConnectionDetector {
    let messageId;
    let lostConnectionDetector = new LostConnectionDetector();
    lostConnectionDetector.setAuthenticated(true);
    lostConnectionDetector.onConnectionLost(() => {
        api.notify.NotifyManager.get().hide(messageId);
        messageId = api.notify.showError("Lost connection to server - Please wait until connection is restored", false);
    });
    lostConnectionDetector.onSessionExpired(() => {
        api.notify.NotifyManager.get().hide(messageId);
        window.location.href = api.util.UriHelper.getToolUri("");
    });
    lostConnectionDetector.onConnectionRestored(() => {
        api.notify.NotifyManager.get().hide(messageId);
    });

    lostConnectionDetector.startPolling();
    return lostConnectionDetector;
}

function initToolTip() {
    const ID = api.StyleHelper.getCls("tooltip", api.StyleHelper.COMMON_PREFIX);
    const CLS_ON = "tooltip_ON";
    const FOLLOW = true;
    const DATA = "_tooltip";
    const OFFSET_X = 0;
    const OFFSET_Y = 20;

    let pageX = 0;
    let pageY = 0;

    const showAt = function (e: any) {
        const top = pageY + OFFSET_Y;
        let left = pageX + OFFSET_X;

        const tooltipText = api.util.StringHelper.escapeHtml(wemjq(e.currentTarget || e.target).data(DATA));
        if (!tooltipText) { //if no text then probably hovering over children of original element that has title attr
            return;
        }

        const tooltipWidth = tooltipText.length * 7.5;
        const windowWidth = wemjq(window).width();
        if (left + tooltipWidth >= windowWidth) {
            left = windowWidth - tooltipWidth;
        }
        wemjq("#" + ID).html(tooltipText).css({
            position: "absolute", top, left
        }).show();
        };
    wemjq(document).on("mouseenter", "*[title]:not([disabled]):visible", function (e: any) {
        wemjq(window).data(DATA, wemjq(window).attr("title"));
        wemjq(window).removeAttr("title").addClass(CLS_ON);
        wemjq("<div id='" + ID + "' />").appendTo("body");
        if (e.pageX) {
            pageX = e.pageX;
        }
        if (e.pageY) {
            pageY = e.pageY;
        }
        showAt(e);
    });
    wemjq(document).on("mouseleave click", "." + CLS_ON, function (e: any) {
        if (wemjq(window).data(DATA)) {
            wemjq(window).attr("title", wemjq(window).data(DATA));
        }
        wemjq(window).removeClass(CLS_ON);
        wemjq("#" + ID).remove();
    });
    if (FOLLOW) { wemjq(document).on("mousemove", "." + CLS_ON, showAt); }
}

function updateTabTitle(title: string) {
    wemjq('title').html(`${title} / Content Studio`);
}

function shouldUpdateFavicon(contentTypeName: ContentTypeName): boolean {
    // Chrome currently doesn't support SVG favicons which are served for not image contents
    return contentTypeName.isImage() || navigator.userAgent.search("Chrome") === -1;
}

let faviconCache: {[url: string]: Element} = {};

function clearFavicon() {
    // save current favicon hrefs
    wemjq('link[rel*=icon][sizes]').each((index, link) => {
        let href = link.getAttribute('href');
        faviconCache[href] = link;
        link.setAttribute('href', ImgEl.PLACEHOLDER);
    });
}

function updateFavicon(content: Content, iconUrlResolver: ContentIconUrlResolver) {
    let resolver = iconUrlResolver.setContent(content).setCrop(false);
    let shouldUpdate = shouldUpdateFavicon(content.getType());
    for (let href in faviconCache) {
        if (faviconCache.hasOwnProperty(href)) {
            let link = faviconCache[href];
            if (shouldUpdate) {
                let sizes = link.getAttribute('sizes').split('x');
                if (sizes.length > 0) {
                    try {
                        resolver.setSize(parseInt(sizes[0], 10));
                    } catch (e) { /* empty */ }
                }
                link.setAttribute('href', resolver.resolve());
            } else {
                link.setAttribute('href', href);
            }
            delete faviconCache[href];
        }
    }
}

function preLoadApplication() {
    let application: api.app.Application = getApplication();
    let wizardParams = ContentWizardPanelParams.fromApp(application);
    if (wizardParams) {
        clearFavicon();
    }
}

function startApplication() {

    let application: api.app.Application = getApplication();

    let serverEventsListener = new api.app.ServerEventsListener([application]);
    serverEventsListener.start();

    let connectionDetector = startLostConnectionDetector();

    let wizardParams = ContentWizardPanelParams.fromApp(application);
    if (wizardParams) {
        startContentWizard(wizardParams, connectionDetector);
    } else {
        startContentApplication(application);
    }

    initToolTip();

    api.util.AppHelper.preventDragRedirect();

    let contentDeleteDialog = new ContentDeleteDialog();
    ContentDeletePromptEvent.on((event) => {
        contentDeleteDialog
            .setContentToDelete(event.getModels())
            .setYesCallback(event.getYesCallback())
            .setNoCallback(event.getNoCallback())
            .open();
    });

    let contentPublishDialog = new ContentPublishDialog();
    ContentPublishPromptEvent.on((event) => {
        contentPublishDialog
            .setContentToPublish(event.getModels())
            .setIncludeChildItems(event.isIncludeChildItems(), true)
            .open();
    });

    let contentUnpublishDialog = new ContentUnpublishDialog();
    ContentUnpublishPromptEvent.on((event) => {
        contentUnpublishDialog
            .setContentToUnpublish(event.getModels())
            .open();
    });

    let editPermissionsDialog = new EditPermissionsDialog();

    application.setLoaded(true);

    api.content.event.ContentServerEventsHandler.getInstance().start();
}

function startContentWizard(wizardParams: ContentWizardPanelParams, connectionDetector: LostConnectionDetector) {
    let wizard = new ContentWizardPanel(wizardParams);
    let iconUrlResolver = new ContentIconUrlResolver();

    wizard.onDataLoaded(content => {
        let contentType = (<ContentWizardPanel>wizard).getContentType();
        updateTabTitle(content.getDisplayName() || api.content.ContentUnnamed.prettifyUnnamed(contentType.getDisplayName()));
        updateFavicon(content, iconUrlResolver);
    });
    wizard.onWizardHeaderCreated(() => {
        // header will be ready after rendering is complete
        wizard.getWizardHeader().onPropertyChanged((event: api.PropertyChangedEvent) => {
            if (event.getPropertyName() === "displayName") {
                let contentType = (<ContentWizardPanel>wizard).getContentType(),
                    name = <string>event.getNewValue() || api.content.ContentUnnamed.prettifyUnnamed(contentType.getDisplayName());

                updateTabTitle(name);
            }
        });
    });

    const beforeUnload = event => {
        if (wizard.isContentDeleted() || !connectionDetector.isConnected() || !connectionDetector.isAuthenticated()) {
            return;
        }
        if (wizard.hasUnsavedChanges()) {
            let message = 'Wizard has unsaved changes. Continue without saving ?';
            // Hack for IE. returnValue is boolean
            const e: any = event || window.event || { returnValue: '' };
            e['returnValue'] = message;
            return message;
        } else {
            // do close to notify everybody
            wizard.close(false);
        }
    };

    wizard.onClosed((event) => {
        if (!event.isCheckCanClose()) {
            api.dom.WindowDOM.get().unBeforeUnload(beforeUnload);
        }
        window.close();
    });

    api.dom.WindowDOM.get().onBeforeUnload(beforeUnload);

    api.content.event.EditContentEvent.on(ContentEventsProcessor.handleEdit);

    api.dom.Body.get().addClass('wizard-page').appendChild(wizard);
}

function startContentApplication(application: api.app.Application) {
    let body = api.dom.Body.get(),
        appBar = new api.app.bar.AppBar(application),
        appPanel = new ContentAppPanel(application.getPath());

    let clientEventsListener = new ContentEventsListener();
    clientEventsListener.start();

    body.appendChild(appBar);
    body.appendChild(appPanel);

    let newContentDialog = new NewContentDialog();
    ShowNewContentDialogEvent.on((event) => {

        let parentContent: api.content.ContentSummary = event.getParentContent()
            ? event.getParentContent().getContentSummary() : null;

        if (parentContent != null) {
            new api.content.resource.GetContentByIdRequest(parentContent.getContentId()).sendAndParse().then(
                (newParentContent: api.content.Content) => {

                    // TODO: remove pyramid of doom
                    if (parentContent.hasParent() && parentContent.getType().isTemplateFolder()) {
                        new api.content.resource.GetContentByPathRequest(parentContent.getPath().getParentPath()).sendAndParse().then(
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

    let sortDialog = new SortContentDialog();
    let moveDialog = new MoveContentDialog();
}

preLoadApplication();

window.onload = function () {
    startApplication();
};

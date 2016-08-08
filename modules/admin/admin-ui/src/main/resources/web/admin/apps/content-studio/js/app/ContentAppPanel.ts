import "../api.ts";
import {NewContentEvent} from "./create/NewContentEvent";
import {ContentWizardPanel} from "./wizard/ContentWizardPanel";
import {ViewContentEvent} from "./browse/ViewContentEvent";
import {SortContentEvent} from "./browse/SortContentEvent";
import {MoveContentEvent} from "./browse/MoveContentEvent";
import {ContentBrowsePanel} from "./browse/ContentBrowsePanel";
import {ContentItemViewPanel} from "./view/ContentItemViewPanel";
import {OpenSortDialogEvent} from "./browse/OpenSortDialogEvent";
import {OpenMoveDialogEvent} from "./browse/OpenMoveDialogEvent";
import {ContentWizardPanelParams} from "./wizard/ContentWizardPanelParams";

import ContentSummary = api.content.ContentSummary;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import Content = api.content.Content;
import ContentId = api.content.ContentId;
import ContentNamedEvent = api.content.event.ContentNamedEvent;
import ContentUpdatedEvent = api.content.event.ContentUpdatedEvent;
import AppBarTabId = api.app.bar.AppBarTabId;
import AppBarTabMenuItem = api.app.bar.AppBarTabMenuItem;
import AppBarTabMenuItemBuilder = api.app.bar.AppBarTabMenuItemBuilder;
import ShowBrowsePanelEvent = api.app.ShowBrowsePanelEvent;

export class ContentAppPanel extends api.app.BrowseAndWizardBasedAppPanel<ContentSummaryAndCompareStatus> {

    private mask: api.ui.mask.LoadMask;

    constructor(appBar: api.app.bar.AppBar, path?: api.rest.Path) {

        super({
            appBar: appBar
        });

        this.mask = new api.ui.mask.LoadMask(this);

        this.handleGlobalEvents();

        this.route(path);
    }

    addWizardPanel(tabMenuItem: AppBarTabMenuItem, wizardPanel: api.app.wizard.WizardPanel<Content>) {
        super.addWizardPanel(tabMenuItem, wizardPanel);

        wizardPanel.onRendered((event) => {
            // header will be ready after rendering is complete
            wizardPanel.getWizardHeader().onPropertyChanged((event: api.PropertyChangedEvent) => {
                if (event.getPropertyName() === "displayName") {
                    var contentType = (<ContentWizardPanel>wizardPanel).getContentType(),
                        name = <string>event.getNewValue() || api.content.ContentUnnamed.prettifyUnnamed(contentType.getDisplayName());

                    tabMenuItem.setLabel(name, !<string>event.getNewValue(), false);
                }
            });
        });

        var contentWizardPanel = <ContentWizardPanel>wizardPanel;

        contentWizardPanel.onDataLoaded((content) => {
            tabMenuItem.markInvalid(!content.isValid());
        });

        contentWizardPanel.onValidityChanged((event: api.ValidityChangedEvent) => {
            tabMenuItem.markInvalid(!contentWizardPanel.isValid());
        });
    }

    private route(path?: api.rest.Path) {
        var action = path ? path.getElement(0) : undefined;

        switch (action) {
        case 'edit':
            var id = path.getElement(1);
            if (id) {
                api.content.resource.ContentSummaryAndCompareStatusFetcher.fetch(new ContentId(id)).done(
                    (content: ContentSummaryAndCompareStatus) => {
                        new api.content.event.EditContentEvent([content]).fire();
                    });
            }
            break;
        case 'view' :
            var id = path.getElement(1);
            if (id) {
                api.content.resource.ContentSummaryAndCompareStatusFetcher.fetch(new ContentId(id)).done(
                    (content: ContentSummaryAndCompareStatus) => {
                        new ViewContentEvent([content]).fire();
                    });
            }
            break;
        default:
            new ShowBrowsePanelEvent().fire();
            break;
        }
    }

    private handleGlobalEvents() {
        NewContentEvent.on((event) => {
            this.handleNewContent(event);
        });

        ViewContentEvent.on((event) => {
            this.handleView(event);
        });

        api.content.event.EditContentEvent.on((event) => {
            this.handleEdit(event);
        });

        ShowBrowsePanelEvent.on((event) => {
            this.handleBrowse(event);
        });

        ContentUpdatedEvent.on((event) => {
            this.handleUpdated(event);
        });

        SortContentEvent.on((event) => {
            this.handleSort(event);
        });

        MoveContentEvent.on((event) => {
            this.handleMove(event);
        });
    }

    private handleUpdated(event: ContentUpdatedEvent) {
        // do something when content is updated
    }

    private handleBrowse(event: ShowBrowsePanelEvent) {
        var browsePanel: api.app.browse.BrowsePanel<ContentSummaryAndCompareStatus> = this.getBrowsePanel();
        if (!browsePanel) {
            this.addBrowsePanel(new ContentBrowsePanel());
        } else {
            this.selectPanelByIndex(this.getPanelIndex(browsePanel));
        }
    }

    private handleNewContent(newContentEvent: NewContentEvent) {

        var contentTypeSummary = newContentEvent.getContentType();
        var tabId = AppBarTabId.forNew(contentTypeSummary.getName());
        var tabMenuItem = this.getAppBarTabMenu().getNavigationItemById(tabId);

        if (tabMenuItem != null) {

            this.selectPanel(tabMenuItem);

        } else {

            var wizardParams = new ContentWizardPanelParams()
                .setTabId(tabId)
                .setContentTypeName(contentTypeSummary.getContentTypeName())
                .setParentContent(newContentEvent.getParentContent())
                .setCreateSite(newContentEvent.getContentType().isSite());

            var wizard = new ContentWizardPanel(wizardParams);

            wizard.onDataLoaded((loadedContent: Content) => {
                var newTabId = AppBarTabId.forNew(loadedContent.getContentId().toString());
                tabMenuItem.setTabId(newTabId);
                wizard.setTabId(newTabId);
            });

            wizard.onContentNamed(this.handleContentNamedEvent.bind(this));

            tabMenuItem = new AppBarTabMenuItemBuilder()
                .setLabel(api.content.ContentUnnamed.prettifyUnnamed(contentTypeSummary.getDisplayName()))
                .setTabId(tabId).setCloseAction(wizard.getCloseAction())
                .build();

            this.addWizardPanel(tabMenuItem, wizard);

            if (newContentEvent.getContentType().isSite() && this.getBrowsePanel()) {
                var content: Content = newContentEvent.getParentContent();
                if (!!content) { // refresh site's node
                    this.getBrowsePanel().getTreeGrid().refreshNodeById(content.getId());
                }
            }

        }
    }


    private handleEdit(event: api.content.event.EditContentEvent) {

        event.getModels().forEach((content: ContentSummaryAndCompareStatus) => {

            if (!content || !content.getContentSummary()) {
                return;
            }

            var contentSummary = content.getContentSummary(),
                contentTypeName = contentSummary.getType();

            var closeViewPanelMenuItem = this.resolveTabMenuItemForContentBeingViewed(contentSummary);
            var tabMenuItem = this.resolveTabMenuItemForContentBeingEdited(contentSummary);

            if (tabMenuItem != null) {
                this.selectPanel(tabMenuItem);
            } else {

                var tabId = AppBarTabId.forEdit(contentSummary.getId());

                var wizardParams = new ContentWizardPanelParams()
                    .setTabId(tabId)
                    .setContentTypeName(contentTypeName)
                    .setContentSummary(contentSummary);

                var wizard = new ContentWizardPanel(wizardParams);

                if (closeViewPanelMenuItem != null) {
                    this.getAppBarTabMenu().deselectNavigationItem();
                    this.getAppBarTabMenu().removeNavigationItem(closeViewPanelMenuItem);
                    this.removePanelByIndex(closeViewPanelMenuItem.getIndex());
                }

                var name = contentSummary.getDisplayName();
                if (api.util.StringHelper.isBlank(name)) {
                    wizard.onDataLoaded((loadedContent) => {
                        tabMenuItem.setLabel(api.content.ContentUnnamed.prettifyUnnamed(wizard.getContentType().getDisplayName()));
                    })
                }

                tabMenuItem = new AppBarTabMenuItemBuilder()
                    .setLabel(name)
                    .setMarkUnnamed(!contentSummary.getDisplayName())
                    .setMarkInvalid(!contentSummary.isValid())
                    .setTabId(tabId)
                    .setEditing(true)
                    .setCloseAction(wizard.getCloseAction()).build();

                this.addWizardPanel(tabMenuItem, wizard);

                var viewTabId = AppBarTabId.forView(contentSummary.getId());
                var viewTabMenuItem = this.getAppBarTabMenu().getNavigationItemById(viewTabId);
                if (viewTabMenuItem != null) {
                    this.removePanelByIndex(viewTabMenuItem.getIndex());
                }

            }
        });
    }

    private handleView(event: ViewContentEvent) {

        var contents: ContentSummaryAndCompareStatus[] = event.getModels();
        contents.forEach((content: ContentSummaryAndCompareStatus) => {
            if (!content || !content.getContentSummary()) {
                return;
            }

            var tabMenuItem = this.resolveTabMenuItemForContentBeingEditedOrViewed(content.getContentSummary());

            if (tabMenuItem) {
                this.selectPanel(tabMenuItem);
            } else {
                var tabId = AppBarTabId.forView(content.getId());
                var contentItemViewPanel = new ContentItemViewPanel();

                tabMenuItem = new AppBarTabMenuItemBuilder().setLabel(content.getDisplayName()).setMarkInvalid(
                    !content.getContentSummary().isValid()).setTabId(tabId).setCloseAction(contentItemViewPanel.getCloseAction()).build();

                if (!content.getDisplayName()) {
                    new api.schema.content.GetContentTypeByNameRequest(content.getContentSummary().getType()).sendAndParse().then(
                        (contentType: api.schema.content.ContentType) => {
                            tabMenuItem.setLabel(api.content.ContentUnnamed.prettifyUnnamed(contentType.getDisplayName()), true);
                        }).done();
                }

                var contentItem = new api.app.view.ViewItem(content)
                    .setDisplayName(content.getDisplayName())
                    .setPath(content.getPath().toString())
                    .setIconUrl(new api.content.util.ContentIconUrlResolver().setContent(content.getContentSummary()).resolve());

                contentItemViewPanel.setItem(contentItem);

                this.addViewPanel(tabMenuItem, contentItemViewPanel);
            }
        });
    }

    private handleSort(event: SortContentEvent) {

        var contents: ContentSummaryAndCompareStatus[] = event.getModels();
        new OpenSortDialogEvent(contents[0]).fire();
    }

    private handleMove(event: MoveContentEvent) {

        var contents: ContentSummaryAndCompareStatus[] = event.getModels();
        new OpenMoveDialogEvent(contents.map(content => content.getContentSummary())).fire();
    }

    private handleContentNamedEvent(event: ContentNamedEvent) {

        var wizard = event.getWizard(),
            tabMenuItem = this.getAppBarTabMenu().getNavigationItemById(wizard.getTabId());
        // update tab id so that new wizard for the same content type can be created
        var newTabId = AppBarTabId.forEdit(event.getContent().getId());
        tabMenuItem.setTabId(newTabId);
        wizard.setTabId(newTabId);
    }

    private resolveTabMenuItemForContentBeingEditedOrViewed(content: ContentSummary): AppBarTabMenuItem {
        var result = this.resolveTabMenuItemForContentBeingEdited(content);
        if (!result) {
            result = this.resolveTabMenuItemForContentBeingViewed(content)
        }
        return result;
    }

    private resolveTabMenuItemForContentBeingEdited(content: ContentSummary): AppBarTabMenuItem {
        if (!!content) {

            var tabId = this.getAppBarTabMenu().getNavigationItemById(AppBarTabId.forEdit(content.getId()));
            if (tabId) {
                return tabId;
            }
        }
        return null;
    }

    private resolveTabMenuItemForContentBeingViewed(content: ContentSummary): AppBarTabMenuItem {
        if (!!content) {
            var tabId = this.getAppBarTabMenu().getNavigationItemById(AppBarTabId.forView(content.getId()));
            if (tabId) {
                return tabId;
            }
        }

        return null;
    }
}

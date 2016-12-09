import "../../api.ts";
import {MobileDetailsPanel} from "./detail/MobileDetailsSlidablePanel";
import {ContentItemPreviewPanel} from "./ContentItemPreviewPanel";
import {MobileDetailsPanelToggleButton} from "./detail/button/MobileDetailsPanelToggleButton";
import {MobileContentBrowseToolbar} from "../browse/MobileContentBrowseToolbar";
import {ContentTreeGridActions} from "../browse/action/ContentTreeGridActions";
import {DetailsView} from "./detail/DetailsView";

import ViewItem = api.app.view.ViewItem;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import StringHelper = api.util.StringHelper;
import ResponsiveManager = api.ui.responsive.ResponsiveManager;
import ResponsiveItem = api.ui.responsive.ResponsiveItem;

export class MobileContentItemStatisticsPanel extends api.app.view.ItemStatisticsPanel<api.content.ContentSummaryAndCompareStatus> {

    private itemHeader: api.dom.DivEl = new api.dom.DivEl("mobile-content-item-statistics-header");
    private headerLabel: api.dom.H6El = new api.dom.H6El();
    private subHeaderLabel: api.dom.SpanEl = new api.dom.SpanEl();

    private previewPanel: ContentItemPreviewPanel;
    private detailsPanel: MobileDetailsPanel;
    private detailsToggleButton: MobileDetailsPanelToggleButton;

    private toolbar: MobileContentBrowseToolbar;

    constructor(browseActions: ContentTreeGridActions, detailsView: DetailsView) {
        super("mobile-content-item-statistics-panel");

        this.setDoOffset(false);

        this.initHeader();

        this.initPreviewPanel();

        this.initDetailsPanel(detailsView);

        this.initDetailsPanelToggleButton();

        this.initToolbar(browseActions);

        this.onRendered(() => {
            this.slideAllOut();
        });

        ResponsiveManager.onAvailableSizeChanged(this, (item: ResponsiveItem) => {
            this.slideAllOut();
        });
    }

    private initToolbar(browseActions: ContentTreeGridActions) {
        this.toolbar = new MobileContentBrowseToolbar({
            newContentAction: browseActions.SHOW_NEW_CONTENT_DIALOG_ACTION,
            editContentAction: browseActions.EDIT_CONTENT,
            publishAction: browseActions.PUBLISH_CONTENT,
            unpublishAction: browseActions.UNPUBLISH_CONTENT,
            duplicateAction: browseActions.DUPLICATE_CONTENT,
            deleteAction: browseActions.DELETE_CONTENT,
            sortAction: browseActions.SORT_CONTENT,
            moveAction: browseActions.MOVE_CONTENT
        });
        this.appendChild(this.toolbar);
    }

    private initHeader() {
        this.itemHeader.appendChild(this.headerLabel);
        this.itemHeader.appendChild(this.subHeaderLabel);
        var backButton = new api.dom.DivEl("mobile-details-panel-back-button");
        backButton.onClicked((event) => {
            this.slideAllOut();
        });
        this.itemHeader.appendChild(backButton);

        this.appendChild(this.itemHeader);

    }

    private initDetailsPanel(detailsView: DetailsView) {
        this.detailsPanel = new MobileDetailsPanel(detailsView);
        this.appendChild(this.detailsPanel);
    }

    private initDetailsPanelToggleButton() {
        this.detailsToggleButton = new MobileDetailsPanelToggleButton(this.detailsPanel, () => {
            this.calcAndSetDetailsPanelTopOffset();
        });
        this.itemHeader.appendChild(this.detailsToggleButton);
    }

    private initPreviewPanel() {
        this.previewPanel = new ContentItemPreviewPanel();
        this.previewPanel.setDoOffset(false);
        this.appendChild(this.previewPanel);
    }

    setItem(item: ViewItem<ContentSummaryAndCompareStatus>) {
        if (!this.getItem() || !this.getItem().equals(item)) {
            super.setItem(item);
            this.detailsPanel.setItem(!!item ? item.getModel() : null);
            if (item) {
                this.setName(this.makeDisplayName(item));
                this.setStatus(this.makeCompareStatus(item));
            }
        }
    }

    private makeDisplayName(item: ViewItem<ContentSummaryAndCompareStatus>): string {
        let localName = item.getModel().getType().getLocalName() || "";
        return StringHelper.isEmpty(item.getDisplayName())
            ? api.content.ContentUnnamed.prettifyUnnamed(localName)
            : item.getDisplayName();
    }

    private makeCompareStatus(item: ViewItem<ContentSummaryAndCompareStatus>): string {
        let compareStatus = item.getModel().getCompareStatus();
        return api.content.CompareStatusFormatter.formatStatus(compareStatus);
    }

    getDetailsPanel(): MobileDetailsPanel {
        return this.detailsPanel;
    }

    getPreviewPanel(): ContentItemPreviewPanel {
        return this.previewPanel;
    }

    private setName(name: string) {
        this.headerLabel.setHtml(name);
    }

    private setStatus(status: string) {
        this.subHeaderLabel.getHTMLElement().setAttribute("class", "");
        this.subHeaderLabel.addClass(status.toLowerCase().replace(" ", "-"));
        this.subHeaderLabel.setHtml(status);
    }

    slideAllOut() {
        this.slideOut();
        this.detailsPanel.slideOut();
        this.detailsToggleButton.removeClass("expanded");
    }

    // hide
    slideOut() {
        this.getEl().setRightPx(-this.getEl().getWidthWithBorder());
        api.dom.Body.get().getHTMLElement().classList.remove("mobile-statistics-panel");
    }

    // show
    slideIn() {
        api.dom.Body.get().getHTMLElement().classList.add("mobile-statistics-panel");
        this.getEl().setRightPx(0);
    }

    private calcAndSetDetailsPanelTopOffset() {
        this.detailsPanel.getEl().setTopPx(this.itemHeader.getEl().getHeightWithMargin());
    }
}
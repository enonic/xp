import '../../api.ts';
import {MobileDetailsPanel} from './detail/MobileDetailsSlidablePanel';
import {ContentItemPreviewPanel} from './ContentItemPreviewPanel';
import {MobileDetailsPanelToggleButton} from './detail/button/MobileDetailsPanelToggleButton';
import {ContentTreeGridActions} from '../browse/action/ContentTreeGridActions';
import {DetailsView} from './detail/DetailsView';
import {MobilePreviewFoldButton} from './MobilePreviewFoldButton';

import ViewItem = api.app.view.ViewItem;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import StringHelper = api.util.StringHelper;
import ResponsiveManager = api.ui.responsive.ResponsiveManager;
import ResponsiveItem = api.ui.responsive.ResponsiveItem;
import FoldButton = api.ui.toolbar.FoldButton;

export class MobileContentItemStatisticsPanel extends api.app.view.ItemStatisticsPanel<api.content.ContentSummaryAndCompareStatus> {

    private itemHeader: api.dom.DivEl = new api.dom.DivEl('mobile-content-item-statistics-header');
    private headerLabel: api.dom.H6El = new api.dom.H6El('mobile-header-title');
    private subHeaderLabel: api.dom.SpanEl = new api.dom.SpanEl();

    private previewPanel: ContentItemPreviewPanel;
    private detailsPanel: MobileDetailsPanel;
    private detailsToggleButton: MobileDetailsPanelToggleButton;

    private foldButton: MobilePreviewFoldButton;

    constructor(browseActions: ContentTreeGridActions, detailsView: DetailsView) {
        super('mobile-content-item-statistics-panel');

        this.setDoOffset(false);

        this.createFoldButton(browseActions);

        this.initHeader();

        this.initPreviewPanel();

        this.initDetailsPanel(detailsView);

        this.initDetailsPanelToggleButton();

        this.initListeners();
    }

    private initListeners() {

        let reloadItemPublishStateChange = (contents: ContentSummaryAndCompareStatus[]) => {
            let thisContentId = this.getItem().getModel().getId();

            let contentSummary: ContentSummaryAndCompareStatus = contents.filter((content) => {
                return thisContentId === content.getId();
            })[0];

            if (contentSummary) {
                this.setItem(ViewItem.fromContentSummaryAndCompareStatus(contentSummary));
            }
        };

        let serverEvents = api.content.event.ContentServerEventsHandler.getInstance();

        serverEvents.onContentPublished(reloadItemPublishStateChange);
        serverEvents.onContentUnpublished(reloadItemPublishStateChange);

        this.onRendered(() => {
            this.slideAllOut();
        });

        ResponsiveManager.onAvailableSizeChanged(this, (item: ResponsiveItem) => {
            this.slideAllOut();
        });
    }

    private createFoldButton(browseActions: ContentTreeGridActions) {
        this.foldButton = new MobilePreviewFoldButton([
            browseActions.UNPUBLISH_CONTENT,
            browseActions.PUBLISH_CONTENT,
            browseActions.MOVE_CONTENT,
            browseActions.SORT_CONTENT,
            browseActions.DELETE_CONTENT,
            browseActions.DUPLICATE_CONTENT,
            browseActions.EDIT_CONTENT,
            browseActions.SHOW_NEW_CONTENT_DIALOG_ACTION
        ], this.itemHeader);
    }

    private initHeader() {
        this.itemHeader.appendChild(this.headerLabel);
        this.itemHeader.appendChild(this.subHeaderLabel);

        this.itemHeader.appendChild(this.foldButton);

        let backButton = new api.dom.DivEl('mobile-details-panel-back-button');
        backButton.onClicked((event) => {
            this.foldButton.collapse();
            this.slideAllOut();
            event.stopPropagation();
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
            this.foldButton.collapse();
            this.calcAndSetDetailsPanelTopOffset();
        });
        this.itemHeader.appendChild(this.detailsToggleButton);
    }

    private initPreviewPanel() {
        this.previewPanel = new ContentItemPreviewPanel();
        this.previewPanel.setDoOffset(false);
        this.previewPanel.addClass('mobile');
        this.appendChild(this.previewPanel);
    }

    setItem(item: ViewItem<ContentSummaryAndCompareStatus>) {
        if (!this.getItem() || !this.getItem().equals(item)) {
            super.setItem(item);
            this.foldButton.collapse();
            this.detailsPanel.setItem(!!item ? item.getModel() : null);
            if (item) {
                this.setName(this.makeDisplayName(item));
                this.setStatus(this.makeCompareStatus(item));
            }
        }
    }

    private makeDisplayName(item: ViewItem<ContentSummaryAndCompareStatus>): string {
        let localName = item.getModel().getType().getLocalName() || '';
        return StringHelper.isEmpty(item.getDisplayName())
            ? api.content.ContentUnnamed.prettifyUnnamed(localName)
            : item.getDisplayName();
    }

    private makeCompareStatus(item: ViewItem<ContentSummaryAndCompareStatus>): string {
        return api.content.CompareStatusFormatter.formatStatusFromContent(item.getModel());
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
        this.subHeaderLabel.getHTMLElement().setAttribute('class', '');
        this.subHeaderLabel.addClass(status.toLowerCase().replace(' ', '-'));
        this.subHeaderLabel.setHtml(status);
    }

    slideAllOut() {
        this.slideOut();
        this.detailsPanel.slideOut();
        this.detailsToggleButton.removeClass('expanded');
    }

    // hide
    slideOut() {
        this.getEl().setRightPx(-this.getEl().getWidthWithBorder());
        api.dom.Body.get().getHTMLElement().classList.remove('mobile-statistics-panel');
    }

    // show
    slideIn() {
        api.dom.Body.get().getHTMLElement().classList.add('mobile-statistics-panel');
        this.getEl().setRightPx(0);
    }

    private calcAndSetDetailsPanelTopOffset() {
        this.detailsPanel.getEl().setTopPx(this.itemHeader.getEl().getHeightWithMargin());
    }
}

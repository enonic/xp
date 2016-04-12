module app.view {

    import TabMenuItemBuilder = api.ui.tab.TabMenuItemBuilder;
    import DetailsPanel = app.view.detail.DetailsPanel;
    import ViewItem = api.app.view.ViewItem;
    import ContentSummary = api.content.ContentSummary;
    import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
    import StringHelper = api.util.StringHelper;
    import ResponsiveManager = api.ui.responsive.ResponsiveManager;
    import ResponsiveItem = api.ui.responsive.ResponsiveItem;
    import MobileContentTreeGridActions = app.browse.action.MobileContentTreeGridActions;
    import MobileContentBrowseToolbar = app.browse.MobileContentBrowseToolbar;
    import MobileDetailsPanelToggleButton = app.view.detail.button.MobileDetailsPanelToggleButton;

    export class MobileContentItemStatisticsPanel extends api.app.view.ItemStatisticsPanel<api.content.ContentSummaryAndCompareStatus> {

        private itemHeader: api.dom.DivEl = new api.dom.DivEl("mobile-content-item-statistics-header");
        private headerLabel: api.dom.SpanEl = new api.dom.SpanEl();

        private previewPanel: ContentItemPreviewPanel;
        private detailsPanel: DetailsPanel = DetailsPanel.create().
            setUseSplitter(false).
            setUseViewer(false).
            setSlideFrom(app.view.detail.SLIDE_FROM.BOTTOM).
            build();
        private detailsToggleButton: MobileDetailsPanelToggleButton;

        private mobileBrowseActions: MobileContentTreeGridActions;
        private toolbar: MobileContentBrowseToolbar;

        constructor(mobileBrowseActions: MobileContentTreeGridActions) {
            super("mobile-content-item-statistics-panel");

            this.mobileBrowseActions = mobileBrowseActions;

            this.setDoOffset(false);

            this.initHeader();

            this.initPreviewPanel();

            this.initDetailsPanel();

            this.initToolbar();

            this.onRendered(() => {
                this.slideAllOut();
            });

            ResponsiveManager.onAvailableSizeChanged(this, (item: ResponsiveItem) => {
                this.slideAllOut();
            });
        }

        private initToolbar() {
            this.toolbar = new MobileContentBrowseToolbar(this.mobileBrowseActions);
            this.appendChild(this.toolbar);
        }

        private initHeader() {
            this.itemHeader.appendChild(this.headerLabel);
            this.detailsToggleButton = new MobileDetailsPanelToggleButton(this.detailsPanel, () => {
                this.calcAndSetDetailsPanelTopOffset();
            });
            var backButton = new api.dom.DivEl("back-button");
            backButton.onClicked((event) => {
                this.slideAllOut();
            });
            this.itemHeader.appendChild(backButton);
            this.itemHeader.appendChild(this.detailsToggleButton);

            this.appendChild(this.itemHeader);

        }

        private initDetailsPanel() {
            this.detailsPanel.addClass("mobile");
            this.appendChild(this.detailsPanel);
        }

        private initPreviewPanel() {
            this.previewPanel = new ContentItemPreviewPanel();
            this.previewPanel.setDoOffset(false);
            this.appendChild(this.previewPanel);
        }

        setItem(item: ViewItem<ContentSummaryAndCompareStatus>) {
            if (!this.getItem() || !this.getItem().equals(item)) {
                super.setItem(item);
                this.previewPanel.setItem(item);
                this.detailsPanel.setItem(item ? item.getModel() : null);
                if (item) {
                    this.setName(this.makeDisplayName(item));
                }
            }
            this.slideIn();
        }

        private makeDisplayName(item: ViewItem<ContentSummaryAndCompareStatus>): string {
            let localName = item.getModel().getType().getLocalName() || "";
            return StringHelper.isEmpty(item.getDisplayName())
                ? api.content.ContentUnnamed.prettifyUnnamed(localName)
                : item.getDisplayName();
        }

        getDetailsPanel(): DetailsPanel {
            return this.detailsPanel;
        }

        setName(name: string) {
            this.headerLabel.setHtml(name);
        }

        slideAllOut() {
            this.slideOut();
            this.detailsPanel.slideOut();
            this.detailsToggleButton.removeClass("expanded");
        }

        slideOut() {
            this.getEl().setRightPx(-this.getEl().getWidthWithBorder());
        }

        slideIn() {
            //this.calcAndSetDetailsPanelTopOffset();
            this.getEl().setRightPx(0);
        }

        private calcAndSetDetailsPanelTopOffset() {
            this.detailsPanel.getEl().setTopPx(this.itemHeader.getEl().getHeightWithMargin());
        }
    }

}
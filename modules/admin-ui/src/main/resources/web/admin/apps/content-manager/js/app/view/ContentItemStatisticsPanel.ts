module app.view {

    import Panel = api.ui.panel.Panel;
    import TabMenuItemBuilder = api.ui.tab.TabMenuItemBuilder;
    import WidgetsPanelToggleButton = app.view.detail.DetailsPanelToggleButton;
    import DetailsPanel = app.view.detail.DetailsPanel;
    import WidgetView = app.view.detail.WidgetView;

    export class ContentItemStatisticsPanel extends api.app.view.ItemStatisticsPanel<api.content.ContentSummary> {

        private previewPanel: ContentItemPreviewPanel;

        private detailsPanel: DetailsPanel;

        constructor() {
            super("content-item-statistics-panel");

            this.previewPanel = new ContentItemPreviewPanel();
            this.previewPanel.setDoOffset(false);
            this.appendChild(this.previewPanel);

            this.initDetailsPanel();
        }

        private initDetailsPanel() {
            this.detailsPanel = new DetailsPanel();
            this.appendChild(new WidgetsPanelToggleButton(this.detailsPanel));
            this.appendChild(this.detailsPanel);
        }

        setItem(item: api.app.view.ViewItem<api.content.ContentSummary>) {
            if (this.getItem() != item) {
                super.setItem(item);
                this.previewPanel.setItem(item);
                this.detailsPanel.setItem(item);
            }
        }

    }

}

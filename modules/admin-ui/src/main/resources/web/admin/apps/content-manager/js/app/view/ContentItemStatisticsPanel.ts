module app.view {

    import Panel = api.ui.panel.Panel;
    import TabMenuItemBuilder = api.ui.tab.TabMenuItemBuilder;
    import WidgetsPanelToggleButton = app.view.detail.DetailPanelToggleButton;
    import DetailPanel = app.view.detail.DetailPanel;
    import WidgetView = app.view.detail.WidgetView;

    export class ContentItemStatisticsPanel extends api.app.view.ItemStatisticsPanel<api.content.ContentSummary> {

        private previewPanel: ContentItemPreviewPanel;

        private detailPanel: DetailPanel;

        constructor() {
            super("content-item-statistics-panel");

            this.previewPanel = new ContentItemPreviewPanel();
            this.previewPanel.setDoOffset(false);
            this.appendChild(this.previewPanel);

            this.initWidgetsPanel();
        }

        private initWidgetsPanel() {
            this.detailPanel = new DetailPanel();
            this.appendChild(new WidgetsPanelToggleButton(this.detailPanel));
            this.appendChild(this.detailPanel);
        }

        setItem(item: api.app.view.ViewItem<api.content.ContentSummary>) {
            if (this.getItem() != item) {
                super.setItem(item);
                this.previewPanel.setItem(item);
                this.detailPanel.setItem(item);
            }
        }

    }

}

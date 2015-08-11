module app.view {

    import Panel = api.ui.panel.Panel;
    import TabMenuItemBuilder = api.ui.tab.TabMenuItemBuilder;
    import WidgetsPanelToggleButton = app.view.widget.WidgetsPanelToggleButton;
    import WidgetsPanel = app.view.widget.WidgetsPanel;
    import WidgetView = app.view.widget.WidgetView;

    export class ContentItemStatisticsPanel extends api.app.view.ItemStatisticsPanel<api.content.ContentSummary> {

        private previewPanel: ContentItemPreviewPanel;

        private widgetsPanel: WidgetsPanel;

        constructor() {
            super("content-item-statistics-panel");

            this.previewPanel = new ContentItemPreviewPanel();
            this.previewPanel.setDoOffset(false);
            this.appendChild(this.previewPanel);

            this.initWidgetsPanel();
        }

        private initWidgetsPanel() {
            this.widgetsPanel = new WidgetsPanel();
            this.appendChild(new WidgetsPanelToggleButton(this.widgetsPanel));
            this.appendChild(this.widgetsPanel);
        }

        setItem(item: api.app.view.ViewItem<api.content.ContentSummary>) {
            if (this.getItem() != item) {
                super.setItem(item);
                this.previewPanel.setItem(item);
                this.widgetsPanel.setItem(item);
            }
        }

    }

}

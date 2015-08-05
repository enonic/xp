module app.view {

    import Panel = api.ui.panel.Panel;
    import TabMenuItemBuilder = api.ui.tab.TabMenuItemBuilder;
    import WidgetsPanelToggleButton = app.view.widget.WidgetsPanelToggleButton;
    import WidgetsPanel = app.view.widget.WidgetsPanel;
    import Widget = app.view.widget.Widget;

    export class ContentItemStatisticsPanel extends api.app.view.ItemStatisticsPanel<api.content.ContentSummary> {

        private previewPanel: ContentItemPreviewPanel;

        private versionsPanel: ContentItemVersionsPanel;

        private widgetsPanel: WidgetsPanel;

        constructor() {
            super("content-item-statistics-panel");

            this.previewPanel = new ContentItemPreviewPanel();
            this.previewPanel.setDoOffset(false);
            this.appendChild(this.previewPanel);

            this.versionsPanel = new ContentItemVersionsPanel();

            this.initWidgetsPanel();
        }

        private initWidgetsPanel() {
            this.widgetsPanel = new WidgetsPanel();
            this.appendChild(new WidgetsPanelToggleButton(this.widgetsPanel));
            this.appendChild(this.widgetsPanel);
        }

        private initWidgetsForItem() {
            if (this.widgetsPanel) {
                this.widgetsPanel.removeWidgets();
            }

            this.widgetsPanel.setName(this.getItem().getDisplayName());

            var testWidget1 = new Widget("Version history"),
                testWidget2 = new Widget("Widget Y"),
                testWidgetContent2 = new api.dom.DivEl();

            testWidgetContent2.setHtml("Some test contents");

            testWidget1.setWidgetContents(this.versionsPanel);
            testWidget2.setWidgetContents(testWidgetContent2);

            this.widgetsPanel.addWidget(testWidget1);
            this.widgetsPanel.addWidget(testWidget2);

            this.widgetsPanel.onPanelSizeChanged(() => {
                this.versionsPanel.ReRenderActivePanel();
            });

        }

        setItem(item: api.app.view.ViewItem<api.content.ContentSummary>) {
            if (this.getItem() != item) {
                super.setItem(item);
                this.previewPanel.setItem(item);
                this.versionsPanel.setItem(item);
            }
            this.initWidgetsForItem();
        }

    }

}

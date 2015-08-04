module app.view {

    import Panel = api.ui.panel.Panel;
    import TabMenuItemBuilder = api.ui.tab.TabMenuItemBuilder;
    import WidgetsPanelToggleButton = app.view.widget.WidgetsPanelToggleButton;
    import WidgetsPanel = app.view.widget.WidgetsPanel;
    import Widget = app.view.widget.Widget;

    export class ContentItemStatisticsPanel extends api.app.view.MultiItemStatisticsPanel<api.content.ContentSummary> {

        private previewPanel: ContentItemPreviewPanel;

        private versionsPanel: ContentItemVersionsPanel;

        private widgetsPanel: WidgetsPanel;

        constructor() {
            super("content-item-statistics-panel");

            this.previewPanel = new ContentItemPreviewPanel();
            this.addNavigablePanel((<TabMenuItemBuilder>new TabMenuItemBuilder().setLabel("Preview")).build(), this.previewPanel, true);

            this.versionsPanel = new ContentItemVersionsPanel();
            this.addNavigablePanel((<TabMenuItemBuilder>new TabMenuItemBuilder().setLabel("Version History")).build(), this.versionsPanel);

            this.getTabMenu().onNavigationItemSelected((event: api.ui.NavigatorEvent) => {
                this.onTabSelected(event.getItem());
            });

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

            var testWidget1 = new Widget("Widget X"),
                testWidget2 = new Widget("Widget Y"),
                testWidgetContent1 = new api.dom.DivEl(),
                testWidgetContent2 = new api.dom.DivEl();

            testWidgetContent1.setHtml("Some test contents");
            testWidgetContent2.setHtml("Some test contents");

            testWidget1.setWidgetContents(testWidgetContent1);
            testWidget2.setWidgetContents(testWidgetContent2);

            this.widgetsPanel.addWidget(testWidget1);
            this.widgetsPanel.addWidget(testWidget2);

        }


        setItem(item: api.app.view.ViewItem<api.content.ContentSummary>) {
            if (this.getItem() != item) {
                super.setItem(item);
                switch (this.getTabMenu().getSelectedIndex()) {
                case 0:
                    this.previewPanel.setItem(item);
                    break;
                case 1:
                    this.versionsPanel.setItem(item);
                    break;
                }
            }

            this.initWidgetsForItem();
        }

        private onTabSelected(navigationItem: api.ui.NavigationItem) {
            var item = this.getItem();
            switch (navigationItem.getIndex()) {
            case 0:
                this.getHeader().hide();
                if (this.previewPanel.getItem() != item) {
                    this.previewPanel.setItem(item);
                }
                break;
            case 1:
                this.getHeader().hide();
                if (this.versionsPanel.getItem() != item) {
                    this.versionsPanel.setItem(item);
                }
                break;
            default:
                this.getHeader().show();
            }

        }

    }

}

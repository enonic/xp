module app.view {

    import Panel = api.ui.panel.Panel;
    import TabMenuItemBuilder = api.ui.tab.TabMenuItemBuilder;

    export class ContentItemStatisticsPanel extends api.app.view.MultiItemStatisticsPanel<api.content.ContentSummary> {

        private previewPanel: ContentItemPreviewPanel;

        private versionsPanel: ContentItemVersionsPanel;

        constructor() {
            super("content-item-statistics-panel");

            this.previewPanel = new ContentItemPreviewPanel();
            this.addNavigablePanel((<TabMenuItemBuilder>new TabMenuItemBuilder().setLabel("Preview")).build(), this.previewPanel, true);

            this.versionsPanel = new ContentItemVersionsPanel();
            this.addNavigablePanel((<TabMenuItemBuilder>new TabMenuItemBuilder().setLabel("Version History")).build(), this.versionsPanel);

            this.getTabMenu().onNavigationItemSelected((event: api.ui.NavigatorEvent) => {
                this.onTabSelected(event.getItem());
            });
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

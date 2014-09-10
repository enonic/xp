module app.view {

    import Panel = api.ui.panel.Panel;
    import TabMenuItemBuilder = api.ui.tab.TabMenuItemBuilder;

    export class ContentItemStatisticsPanel extends api.app.view.ItemStatisticsPanel<api.content.ContentSummary> {

        private previewPanel: ContentItemPreviewPanel;

        private analyticsPanel: ContentItemAnalyticsPanel;

        constructor() {
            super();

            this.previewPanel = new ContentItemPreviewPanel();
            this.addNavigablePanel(new TabMenuItemBuilder().setLabel("Preview").build(),
                this.previewPanel);

            this.analyticsPanel = new ContentItemAnalyticsPanel();
            this.addNavigablePanel(new TabMenuItemBuilder().setLabel("Google Analytics").build(), this.analyticsPanel);

            this.addNavigablePanel(new TabMenuItemBuilder().setLabel("Details").build(), new Panel());
            this.addNavigablePanel(new TabMenuItemBuilder().setLabel("Relationships").build(), new Panel());
            this.addNavigablePanel(new TabMenuItemBuilder().setLabel("Version History").build(), new Panel());
            this.addNavigablePanel(new TabMenuItemBuilder().setLabel("SEO").build(), new Panel());

            this.getTabMenu().onNavigationItemSelected((event: api.ui.NavigatorEvent) => {
                this.onTabSelected(event.getItem());
            });

            var firstShowListener = (event: api.dom.ElementShownEvent) => {
                this.showPanel(0);
                this.unShown(firstShowListener);
            }
            this.onShown(firstShowListener);
        }

        setItem(item: api.app.view.ViewItem<api.content.ContentSummary>) {
            super.setItem(item);
            if (this.getTabMenu().getSelectedIndex() == 0) {
                this.previewPanel.setItem(item);
            }
        }

        private onTabSelected(item: api.ui.NavigationItem) {
            if (item.getIndex() == 0) {
                this.getHeader().hide();
                if (this.getBrowseItem()) {
                    this.previewPanel.setItem(this.getBrowseItem());
                }
            } else {
                this.getHeader().show();
            }
        }

    }

}

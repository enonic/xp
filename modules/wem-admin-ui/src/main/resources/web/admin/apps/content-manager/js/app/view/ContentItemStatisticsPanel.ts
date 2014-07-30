module app.view {

    import Panel = api.ui.panel.Panel;

    export class ContentItemStatisticsPanel extends api.app.view.ItemStatisticsPanel<api.content.ContentSummary> {

        private previewPanel: ContentItemPreviewPanel;

        private analyticsPanel: ContentItemAnalyticsPanel;

        constructor() {
            super();

            this.previewPanel = new ContentItemPreviewPanel();
            this.addNavigablePanel(new api.ui.tab.TabMenuItem("Preview"), this.previewPanel);

            this.analyticsPanel = new ContentItemAnalyticsPanel();
            this.addNavigablePanel(new api.ui.tab.TabMenuItem("Google Analytics"), this.analyticsPanel);

            this.addNavigablePanel(new api.ui.tab.TabMenuItem("Details"), new Panel());
            this.addNavigablePanel(new api.ui.tab.TabMenuItem("Relationships"), new Panel());
            this.addNavigablePanel(new api.ui.tab.TabMenuItem("Version History"), new Panel());
            this.addNavigablePanel(new api.ui.tab.TabMenuItem("SEO"), new Panel());

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

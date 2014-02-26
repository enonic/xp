module app.view {

    export class ContentItemStatisticsPanel extends api.app.view.ItemStatisticsPanel<api.content.ContentSummary> {

        constructor() {
            super();

            this.addNavigablePanel(new api.ui.tab.TabMenuItem("Preview"), new api.ui.Panel());
            this.addNavigablePanel(new api.ui.tab.TabMenuItem("Google Analytics"), new api.ui.Panel());
            this.addNavigablePanel(new api.ui.tab.TabMenuItem("Details"), new api.ui.Panel());
            this.addNavigablePanel(new api.ui.tab.TabMenuItem("Relationships"), new api.ui.Panel());
            this.addNavigablePanel(new api.ui.tab.TabMenuItem("Version History"), new api.ui.Panel());
            this.addNavigablePanel(new api.ui.tab.TabMenuItem("SEO"), new api.ui.Panel());
            this.showPanel(0);
        }

    }

}

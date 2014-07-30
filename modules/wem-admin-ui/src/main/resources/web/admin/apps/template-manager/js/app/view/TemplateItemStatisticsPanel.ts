module app.view {

    export class TemplateItemStatisticsPanel extends api.app.view.ItemStatisticsPanel<api.content.TemplateSummary> {

        private previewPanel: TemplateItemPreviewPanel;

        constructor() {
            super();

            this.previewPanel = new TemplateItemPreviewPanel();

            this.addNavigablePanel(new api.ui.tab.TabMenuItem("Preview"), this.previewPanel);
            this.getTabMenu().hide();

            var firstShowListener = (event: api.dom.ElementShownEvent) => {
                this.showPanel(0);
                this.unShown(firstShowListener);
            };
            this.onShown(firstShowListener);
        }

        setItem(item: api.app.view.ViewItem<api.content.TemplateSummary>) {
            super.setItem(item);
            this.previewPanel.setItem(item);
        }

    }

}

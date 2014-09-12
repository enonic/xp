module app.view {

    export class TemplateItemStatisticsPanel extends api.app.view.MultiItemStatisticsPanel<app.browse.TemplateSummary> {

        private previewPanel: TemplateItemPreviewPanel;

        constructor() {
            super("template-item-statistics-panel");

            this.previewPanel = new TemplateItemPreviewPanel();
            this.addNavigablePanel(new api.ui.tab.TabMenuItemBuilder().setLabel("Preview").build(), this.previewPanel);
            this.getTabMenu().hide();

            var firstShowListener = (event: api.dom.ElementShownEvent) => {
                this.showPanel(0);
                this.unShown(firstShowListener);
            };
            this.onShown(firstShowListener);
        }

        setItem(item: api.app.view.ViewItem<app.browse.TemplateSummary>) {
            super.setItem(item);
            this.previewPanel.setItem(item);
        }

    }

}

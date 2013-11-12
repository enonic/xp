module app_contextwindow {
    export class InspectorPanel extends api_ui.DeckPanel {
        private detailPanel:DetailPanel;
        private selectPanel:SelectPanel;
        private imageSelectPanel:app_contextwindow_image.ImageSelectPanel;
        private imageInspectPanel:app_contextwindow_image.ImageInspectPanel;

        constructor(contextWindow:ContextWindow) {
            super();

            this.detailPanel = new DetailPanel(contextWindow);
            this.selectPanel = new SelectPanel(contextWindow);
            this.imageSelectPanel = new app_contextwindow_image.ImageSelectPanel(contextWindow);
            this.imageInspectPanel = new app_contextwindow_image.ImageInspectPanel(contextWindow);


            this.addPanel(this.detailPanel);
            this.addPanel(this.selectPanel);
            this.addPanel(this.imageSelectPanel);
            this.addPanel(this.imageInspectPanel);

            ComponentSelectEvent.on((event) => {
                switch (event.getComponent().componentType.typeName) {
                    case 'image':
                        this.showPanel(this.getPanelIndex(this.imageSelectPanel))
                        break;
                    default:
                        event.getComponent().isEmpty() ? this.showPanel(this.getPanelIndex(this.selectPanel)) : this.showPanel(this.getPanelIndex(this.detailPanel));
                }
            });

        }
    }
}
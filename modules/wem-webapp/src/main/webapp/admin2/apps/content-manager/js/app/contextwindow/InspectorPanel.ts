module app_contextwindow {
    export class InspectorPanel extends api_ui.DeckPanel {
        private detailPanel:DetailPanel;
        private selectPanel:SelectPanel;

        constructor(contextWindow:ContextWindow) {
            super();

            this.detailPanel = new DetailPanel(contextWindow);
            this.selectPanel = new SelectPanel(contextWindow);

            this.addPanel(this.detailPanel);
            this.addPanel(this.selectPanel);

            ComponentSelectEvent.on((event) => {
                if (event.getComponent().isEmpty()) {
                    this.showPanel(this.getPanelIndex(this.selectPanel));
                } else {
                    this.showPanel(this.getPanelIndex(this.detailPanel));
                }
            });

        }
    }
}
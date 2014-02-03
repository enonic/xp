module app.contextwindow {

    import SiteTemplate = api.content.site.template.SiteTemplate;

    export interface InspectorPanelConfig {

        liveEditWindow:any;

        siteTemplate:SiteTemplate;

    }

    export class InspectorPanel extends api.ui.DeckPanel {

        private detailPanel: DetailPanel;
        private selectPanel: SelectPanel;
        private imageSelectPanel: app.contextwindow.image.ImageSelectPanel;

        constructor(config: InspectorPanelConfig) {
            super();

            this.detailPanel = new DetailPanel({
                siteTemplate: config.siteTemplate,
            });

            this.selectPanel = new SelectPanel({
                liveEditWindow: config.liveEditWindow});

            this.imageSelectPanel = new app.contextwindow.image.ImageSelectPanel({
                liveEditWindow: config.liveEditWindow});


            this.addPanel(this.detailPanel);
            this.addPanel(this.selectPanel);
            this.addPanel(this.imageSelectPanel);

            SelectComponentEvent.on((event) => {

                switch (event.getComponent().componentType.typeName) {
                case 'image':
                    this.showPanel(this.getPanelIndex(this.detailPanel));
                    break;
                default:
                    event.getComponent().isEmpty() ? this.showPanel(this.getPanelIndex(this.selectPanel))
                        : this.showPanel(this.getPanelIndex(this.detailPanel));
                }
            });
        }
    }
}
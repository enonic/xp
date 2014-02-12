module app.contextwindow {

    import SiteTemplate = api.content.site.template.SiteTemplate;

    export interface InspectionPanelConfig {

        liveEditWindow:any;
        siteTemplate:SiteTemplate;
        liveFormPanel:app.wizard.LiveFormPanel;
    }

    export class InspectionPanel extends api.ui.DeckPanel {

        private imageInspectionPanel: ImageInspectionPanel;
        private selectPanel: SelectPanel;
        private imageSelectPanel: app.contextwindow.image.ImageSelectPanel;
        private liveFormPanel: app.wizard.LiveFormPanel;

        constructor(config: InspectionPanelConfig) {
            super();

            this.imageInspectionPanel = new ImageInspectionPanel({
                siteTemplate: config.siteTemplate,
                liveFormPanel: config.liveFormPanel
            });

            this.selectPanel = new SelectPanel({
                liveEditWindow: config.liveEditWindow});

            this.imageSelectPanel = new app.contextwindow.image.ImageSelectPanel({
                liveEditWindow: config.liveEditWindow});


            this.addPanel(this.imageInspectionPanel);
            this.addPanel(this.selectPanel);
            this.addPanel(this.imageSelectPanel);

            SelectComponentEvent.on((event) => {

                switch (event.getComponent().componentType.typeName) {
                case 'image':
                    this.showPanel(this.getPanelIndex(this.imageInspectionPanel));
                    break;
                default:
                    event.getComponent().isEmpty() ? this.showPanel(this.getPanelIndex(this.selectPanel))
                        : this.showPanel(this.getPanelIndex(this.imageInspectionPanel));
                }
            });
        }
    }
}
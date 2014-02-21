module app.contextwindow {

    import SiteTemplate = api.content.site.template.SiteTemplate;
    import ImageComponent = api.content.page.image.ImageComponent
    import PartComponent = api.content.page.part.PartComponent;
    import LayoutComponent = api.content.page.layout.LayoutComponent;

    export interface InspectionPanelConfig {

        liveEditWindow:any;
        siteTemplate:SiteTemplate;
        liveFormPanel:app.wizard.LiveFormPanel;
    }

    export class InspectionPanel extends api.ui.DeckPanel {

        private noSelectionPanel: NoSelectionInspectionPanel;
        private imageInspectionPanel: ImageInspectionPanel;
        private partInspectionPanel: PartInspectionPanel;
        private layoutInspectionPanel: LayoutInspectionPanel;
        private contentInspectionPanel: ContentInspectionPanel;
        private pageInspectionPanel: PageInspectionPanel;
        private regionInspectionPanel: RegionInspectionPanel;
        private liveFormPanel: app.wizard.LiveFormPanel;

        constructor(config: InspectionPanelConfig) {
            super();

            this.noSelectionPanel = new NoSelectionInspectionPanel();
            this.imageInspectionPanel = new ImageInspectionPanel(config.liveFormPanel, config.siteTemplate);
            this.partInspectionPanel = new PartInspectionPanel(config.liveFormPanel, config.siteTemplate);
            this.layoutInspectionPanel = new LayoutInspectionPanel(config.liveFormPanel, config.siteTemplate);
            this.contentInspectionPanel = new ContentInspectionPanel();
            this.pageInspectionPanel = new PageInspectionPanel();
            this.regionInspectionPanel = new RegionInspectionPanel();

            this.addPanel(this.imageInspectionPanel);
            this.addPanel(this.partInspectionPanel);
            this.addPanel(this.layoutInspectionPanel);
            this.addPanel(this.contentInspectionPanel);
            this.addPanel(this.regionInspectionPanel);
            this.addPanel(this.pageInspectionPanel);
            this.addPanel(this.noSelectionPanel);

            this.clearSelection();
        }

        private showInspectionPanel(panel: api.ui.Panel) {
            this.showPanel(this.getPanelIndex(panel));
        }

        public clearSelection() {
            this.showInspectionPanel(this.noSelectionPanel);
        }

        public inspectPage(page: api.content.Content, pageTemplate: api.content.page.PageTemplate, pageDescriptor: api.content.page.PageDescriptor) {
            this.pageInspectionPanel.setPage(page, pageTemplate, pageDescriptor);
            this.showInspectionPanel(this.pageInspectionPanel);
        }

        public inspectRegion(region: api.content.page.region.Region) {
            this.regionInspectionPanel.setRegion(region);
            this.showInspectionPanel(this.regionInspectionPanel);
        }

        public inspectContent(content: api.content.Content) {
            this.contentInspectionPanel.setContent(content);
            this.showInspectionPanel(this.contentInspectionPanel);
        }

        public inspectComponent(component: api.content.page.PageComponent) {
            if (component instanceof ImageComponent) {
                this.imageInspectionPanel.setImageComponent(<ImageComponent>component);
                this.showInspectionPanel(this.imageInspectionPanel);

            } else if (component instanceof PartComponent) {
                this.partInspectionPanel.setPartComponent(<PartComponent>component);
                this.showInspectionPanel(this.partInspectionPanel);

            } else if (component instanceof api.content.page.layout.LayoutComponent) {
                this.layoutInspectionPanel.setLayoutComponent(<LayoutComponent>component);
                this.showInspectionPanel(this.layoutInspectionPanel);
            } else {
                console.warn('Unsupported component in InspectionPanel', component);
            }
        }
    }
}
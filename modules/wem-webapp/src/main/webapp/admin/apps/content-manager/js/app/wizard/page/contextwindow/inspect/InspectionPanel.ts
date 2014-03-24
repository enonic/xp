module app.wizard.page.contextwindow.inspect {

    import RootDataSet = api.data.RootDataSet;
    import LiveFormPanel = app.wizard.page.LiveFormPanel;
    import SiteTemplate = api.content.site.template.SiteTemplate;
    import Content = api.content.Content;
    import ContentTypeName = api.schema.content.ContentTypeName;
    import PageTemplateKey = api.content.page.PageTemplateKey;
    import PageTemplate = api.content.page.PageTemplate;
    import PageDescriptor = api.content.page.PageDescriptor;
    import Region = api.content.page.region.Region;
    import PageComponent = api.content.page.PageComponent
    import ImageComponent = api.content.page.image.ImageComponent
    import PartComponent = api.content.page.part.PartComponent;
    import LayoutComponent = api.content.page.layout.LayoutComponent;

    export interface InspectionPanelConfig {

        liveEditWindow:any;
        siteTemplate:SiteTemplate;
        liveFormPanel:LiveFormPanel;
        contentType:ContentTypeName;
    }

    export class InspectionPanel extends api.ui.DeckPanel {

        private noSelectionPanel: NoSelectionInspectionPanel;
        private imageInspectionPanel: ImageInspectionPanel;
        private partInspectionPanel: PartInspectionPanel;
        private layoutInspectionPanel: LayoutInspectionPanel;
        private contentInspectionPanel: ContentInspectionPanel;
        private pageInspectionPanel: PageInspectionPanel;
        private regionInspectionPanel: RegionInspectionPanel;
        private liveFormPanel: LiveFormPanel;

        constructor(config: InspectionPanelConfig) {
            super();

            this.noSelectionPanel = new NoSelectionInspectionPanel();
            this.imageInspectionPanel = new ImageInspectionPanel(config.liveFormPanel, config.siteTemplate);
            this.partInspectionPanel = new PartInspectionPanel(config.liveFormPanel, config.siteTemplate);
            this.layoutInspectionPanel = new LayoutInspectionPanel(config.liveFormPanel, config.siteTemplate);
            this.contentInspectionPanel = new ContentInspectionPanel();
            this.pageInspectionPanel = new PageInspectionPanel({
                contentType: config.contentType,
                siteTemplateKey: config.siteTemplate.getKey()});
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

        public inspectPage(page: Content, pageTemplate: PageTemplate,
                           pageDescriptor: PageDescriptor) {
            this.pageInspectionPanel.setPage(page, pageTemplate, pageDescriptor);
            this.showInspectionPanel(this.pageInspectionPanel);
        }

        public inspectRegion(region: Region) {
            this.regionInspectionPanel.setRegion(region);
            this.showInspectionPanel(this.regionInspectionPanel);
        }

        public inspectContent(content: Content) {
            this.contentInspectionPanel.setContent(content);
            this.showInspectionPanel(this.contentInspectionPanel);
        }

        public inspectComponent(component: PageComponent) {
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
                if (component) {
                    console.warn('Unsupported component in InspectionPanel', component);
                }
            }
        }

        setPage(page: Content, pageTemplate: PageTemplate, pageDescriptor: PageDescriptor) {
            this.pageInspectionPanel.setPage(page, pageTemplate, pageDescriptor);
        }

        getPageTemplate(): PageTemplateKey {
            return this.pageInspectionPanel.getPageTemplate();
        }

        getPageConfig(): RootDataSet {
            return this.pageInspectionPanel.getPageConfig();
        }
    }
}
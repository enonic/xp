module app.wizard.page.contextwindow.inspect {

    import RootDataSet = api.data.RootDataSet;
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

        contentInspectionPanel: ContentInspectionPanel;
        pageInspectionPanel: PageInspectionPanel;
        regionInspectionPanel: RegionInspectionPanel;
        imageInspectionPanel: ImageInspectionPanel;
        partInspectionPanel: PartInspectionPanel;
        layoutInspectionPanel: LayoutInspectionPanel;
    }

    export class InspectionPanel extends api.ui.DeckPanel {

        private noSelectionPanel: NoSelectionInspectionPanel;
        private imageInspectionPanel: ImageInspectionPanel;
        private partInspectionPanel: PartInspectionPanel;
        private layoutInspectionPanel: LayoutInspectionPanel;
        private contentInspectionPanel: ContentInspectionPanel;
        private pageInspectionPanel: PageInspectionPanel;
        private regionInspectionPanel: RegionInspectionPanel;

        constructor(config: InspectionPanelConfig) {
            super();

            this.noSelectionPanel = new NoSelectionInspectionPanel();
            this.imageInspectionPanel = config.imageInspectionPanel;
            this.partInspectionPanel = config.partInspectionPanel;
            this.layoutInspectionPanel = config.layoutInspectionPanel;
            this.contentInspectionPanel = config.contentInspectionPanel;
            this.pageInspectionPanel = config.pageInspectionPanel;
            this.regionInspectionPanel = config.regionInspectionPanel;

            this.addPanel(this.imageInspectionPanel);
            this.addPanel(this.partInspectionPanel);
            this.addPanel(this.layoutInspectionPanel);
            this.addPanel(this.contentInspectionPanel);
            this.addPanel(this.regionInspectionPanel);
            this.addPanel(this.pageInspectionPanel);
            this.addPanel(this.noSelectionPanel);

            this.clearSelection();
        }

        public showInspectionPanel(panel: api.ui.Panel) {
            this.showPanel(this.getPanelIndex(panel));
        }

        public clearSelection() {
            this.showInspectionPanel(this.noSelectionPanel);
        }

        onPageTemplateChanged(listener: {(event: PageTemplateChangedEvent): void;}) {
            this.pageInspectionPanel.onPageTemplateChanged(listener);
        }

        unPageTemplateChanged(listener: {(event: PageTemplateChangedEvent): void;}) {
            this.pageInspectionPanel.unPageTemplateChanged(listener);
        }
    }
}
module app.wizard.page.contextwindow.inspect {

    import Content = api.content.Content;
    import ContentTypeName = api.schema.content.ContentTypeName;
    import PageTemplateKey = api.content.page.PageTemplateKey;
    import PageTemplate = api.content.page.PageTemplate;
    import PageDescriptor = api.content.page.PageDescriptor;
    import Region = api.content.page.region.Region;
    import ImageComponent = api.content.page.region.ImageComponent
    import PartComponent = api.content.page.region.PartComponent;
    import LayoutComponent = api.content.page.region.LayoutComponent;

    export interface InspectionsPanelConfig {
        contentInspectionPanel: ContentInspectionPanel;
        pageInspectionPanel: page.PageInspectionPanel;
        regionInspectionPanel: region.RegionInspectionPanel;
        imageInspectionPanel: region.ImageInspectionPanel;
        partInspectionPanel: region.PartInspectionPanel;
        layoutInspectionPanel: region.LayoutInspectionPanel;
        fragmentInspectionPanel: region.FragmentInspectionPanel;
        saveAction: api.ui.Action;
    }

    export class InspectionsPanel extends api.ui.panel.Panel {

        private deck: api.ui.panel.DeckPanel;
        private buttons: api.dom.DivEl;

        private noSelectionPanel: NoSelectionInspectionPanel;
        private imageInspectionPanel: region.ImageInspectionPanel;
        private partInspectionPanel: region.PartInspectionPanel;
        private layoutInspectionPanel: region.LayoutInspectionPanel;
        private contentInspectionPanel: ContentInspectionPanel;
        private pageInspectionPanel: page.PageInspectionPanel;
        private regionInspectionPanel: region.RegionInspectionPanel;
        private fragmentInspectionPanel: region.FragmentInspectionPanel;

        private saveRequestListeners: {() : void}[] = [];

        constructor(config: InspectionsPanelConfig) {
            super('inspections-panel');

            this.deck = new api.ui.panel.DeckPanel();

            this.noSelectionPanel = new NoSelectionInspectionPanel();
            this.imageInspectionPanel = config.imageInspectionPanel;
            this.partInspectionPanel = config.partInspectionPanel;
            this.layoutInspectionPanel = config.layoutInspectionPanel;
            this.contentInspectionPanel = config.contentInspectionPanel;
            this.pageInspectionPanel = config.pageInspectionPanel;
            this.regionInspectionPanel = config.regionInspectionPanel;
            this.fragmentInspectionPanel = config.fragmentInspectionPanel;

            this.deck.addPanel(this.imageInspectionPanel);
            this.deck.addPanel(this.partInspectionPanel);
            this.deck.addPanel(this.layoutInspectionPanel);
            this.deck.addPanel(this.contentInspectionPanel);
            this.deck.addPanel(this.regionInspectionPanel);
            this.deck.addPanel(this.pageInspectionPanel);
            this.deck.addPanel(this.fragmentInspectionPanel);
            this.deck.addPanel(this.noSelectionPanel);

            this.deck.showPanel(this.pageInspectionPanel);

            this.buttons = new api.dom.DivEl('button-bar');
            var saveButton = new api.ui.button.ActionButton(config.saveAction);
            this.buttons.appendChild(saveButton);

            this.appendChildren(this.deck, this.buttons);

        }

        public showInspectionPanel(panel: api.ui.panel.Panel) {
            this.deck.showPanel(panel);
            var showButtons = !(api.ObjectHelper.iFrameSafeInstanceOf(panel, region.RegionInspectionPanel) ||
                                api.ObjectHelper.iFrameSafeInstanceOf(panel, NoSelectionInspectionPanel));
            this.buttons.setVisible(showButtons);
        }

        public clearInspection() {
            this.showInspectionPanel(this.pageInspectionPanel);
        }

        public isInspecting(): boolean {
            return this.deck.getPanelShown() != this.noSelectionPanel;
        }

    }
}
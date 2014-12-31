module app.wizard.page.contextwindow.inspect {

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

    export interface InspectionsPanelConfig {

        contentInspectionPanel: ContentInspectionPanel;
        pageInspectionPanel: PageInspectionPanel;
        regionInspectionPanel: RegionInspectionPanel;
        imageInspectionPanel: ImageInspectionPanel;
        partInspectionPanel: PartInspectionPanel;
        layoutInspectionPanel: LayoutInspectionPanel;
    }

    export class InspectionsPanel extends api.ui.panel.Panel {

        private deck: api.ui.panel.DeckPanel;
        private buttons: api.dom.DivEl;

        private noSelectionPanel: NoSelectionInspectionPanel;
        private imageInspectionPanel: ImageInspectionPanel;
        private partInspectionPanel: PartInspectionPanel;
        private layoutInspectionPanel: LayoutInspectionPanel;
        private contentInspectionPanel: ContentInspectionPanel;
        private pageInspectionPanel: PageInspectionPanel;
        private regionInspectionPanel: RegionInspectionPanel;

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

            this.deck.addPanel(this.imageInspectionPanel);
            this.deck.addPanel(this.partInspectionPanel);
            this.deck.addPanel(this.layoutInspectionPanel);
            this.deck.addPanel(this.contentInspectionPanel);
            this.deck.addPanel(this.regionInspectionPanel);
            this.deck.addPanel(this.pageInspectionPanel);
            this.deck.addPanel(this.noSelectionPanel);

            this.deck.showPanel(this.pageInspectionPanel);
            this.appendChild(this.deck);

            this.buttons = new api.dom.DivEl('button-bar');
            var saveButton = new api.ui.button.Button('Save');
            saveButton.onClicked((event: MouseEvent) => {
                this.notifySaveRequested();
            });
            this.buttons.appendChild(saveButton);
            this.appendChild(this.buttons);

        }

        public showInspectionPanel(panel: api.ui.panel.Panel) {
            this.deck.showPanel(panel);
            var showButtons = !(api.ObjectHelper.iFrameSafeInstanceOf(panel, RegionInspectionPanel) ||
                                api.ObjectHelper.iFrameSafeInstanceOf(panel, NoSelectionInspectionPanel));
            this.buttons.setVisible(showButtons);
        }

        public clearSelection() {
            this.showInspectionPanel(this.noSelectionPanel);
        }

        onSaveRequested(listener: () => void) {
            this.saveRequestListeners.push(listener);
        }

        unSaveRequested(listener: () => void) {
            this.saveRequestListeners = this.saveRequestListeners.filter((curr) => {
                return listener !== curr;
            });
        }

        private notifySaveRequested() {
            this.saveRequestListeners.forEach((listener) => {
                listener();
            })
        }
    }
}
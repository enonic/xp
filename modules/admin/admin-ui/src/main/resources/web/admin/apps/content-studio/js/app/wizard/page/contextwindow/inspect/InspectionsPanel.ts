import '../../../../../api.ts';
import {ContentInspectionPanel} from './ContentInspectionPanel';
import {FragmentInspectionPanel} from './region/FragmentInspectionPanel';
import {TextInspectionPanel} from './region/TextInspectionPanel';
import {LayoutInspectionPanel} from './region/LayoutInspectionPanel';
import {PartInspectionPanel} from './region/PartInspectionPanel';
import {ImageInspectionPanel} from './region/ImageInspectionPanel';
import {RegionInspectionPanel} from './region/RegionInspectionPanel';
import {PageInspectionPanel} from './page/PageInspectionPanel';
import {NoSelectionInspectionPanel} from './NoSelectionInspectionPanel';

export interface InspectionsPanelConfig {
    contentInspectionPanel: ContentInspectionPanel;
    pageInspectionPanel: PageInspectionPanel;
    regionInspectionPanel: RegionInspectionPanel;
    imageInspectionPanel: ImageInspectionPanel;
    partInspectionPanel: PartInspectionPanel;
    layoutInspectionPanel: LayoutInspectionPanel;
    fragmentInspectionPanel: FragmentInspectionPanel;
    textInspectionPanel: TextInspectionPanel;
    saveAction: api.ui.Action;
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
    private fragmentInspectionPanel: FragmentInspectionPanel;
    private textInspectionPanel: TextInspectionPanel;

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
        this.textInspectionPanel = config.textInspectionPanel;

        this.deck.addPanel(this.imageInspectionPanel);
        this.deck.addPanel(this.partInspectionPanel);
        this.deck.addPanel(this.layoutInspectionPanel);
        this.deck.addPanel(this.contentInspectionPanel);
        this.deck.addPanel(this.regionInspectionPanel);
        this.deck.addPanel(this.pageInspectionPanel);
        this.deck.addPanel(this.fragmentInspectionPanel);
        this.deck.addPanel(this.textInspectionPanel);
        this.deck.addPanel(this.noSelectionPanel);

        this.deck.showPanel(this.pageInspectionPanel);

        this.buttons = new api.dom.DivEl('button-bar');
        let saveButton = new api.ui.button.ActionButton(config.saveAction);
        this.buttons.appendChild(saveButton);

        this.appendChildren(<api.dom.Element>this.deck, this.buttons);
    }

    public showInspectionPanel(panel: api.ui.panel.Panel) {
        this.deck.showPanel(panel);
        let showButtons = !(api.ObjectHelper.iFrameSafeInstanceOf(panel, RegionInspectionPanel) ||
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

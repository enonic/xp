module api.ui {
    export class DockedWindow extends api.dom.DivEl {

        private deck:api.ui.NavigatedDeckPanel;
        private navigator:api.ui.tab.TabBar;
        private items:any[] = [];


        constructor() {
            super("docked-window");

            this.navigator = new api.ui.tab.TabBar();
            this.deck = new api.ui.NavigatedDeckPanel(this.navigator);

            this.appendChild(this.navigator);
            this.appendChild(this.deck);
        }

        addItem<T extends api.ui.Panel>(label:string, panel:T, hidden?:boolean):number {
            var item = new api.ui.tab.TabBarItem(label);
            this.addItemArray(item);

            (this.items.length == 1)
                ? this.deck.addNavigablePanelToFront(item, panel)
                : this.deck.addNavigablePanelToBack(item, panel);

            return this.deck.getPanelIndex(panel);
        }

        selectPanel<T extends api.ui.Panel>(panel:T) {
            this.deck.selectPanelFromIndex(this.deck.getPanelIndex(panel));
        }

        getNavigator():api.ui.tab.TabBar {
            return this.navigator;
        }

        getDeck():api.ui.DeckPanel {
            return this.deck;
        }

        private addItemArray(item:any) {
            this.items.push(item);
        }

    }
}
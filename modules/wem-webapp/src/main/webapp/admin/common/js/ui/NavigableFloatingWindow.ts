module api_ui {
    export class NavigableFloatingWindow extends FloatingWindow {

        private deck:api_ui.NavigatedDeckPanel;
        private navigator:api_ui_tab.TabBar;
        private items:any[] = [];


        constructor(options:FloatingWindowOptions = {}) {
            super(jQuery.extend({draggable: true, draggableOptions: { handle: ".tab-menu"} }, options));

            this.navigator = new api_ui_tab.TabBar();
            this.deck = new api_ui.NavigatedDeckPanel(this.navigator);
            this.deck.addClass("deck-panel");

            this.appendChild(this.navigator);
            this.appendChild(this.deck);
        }

        addItem<T extends api_ui.Panel>(label:string, panel:T, hidden?:boolean):number {


            var item = new api_ui_tab.TabBarItem(label);
            this.addItemArray(item);

            (this.items.length == 1)
                ? this.deck.addNavigablePanelToFront(item, panel)
                : this.deck.addNavigablePanelToBack(item, panel);

            return this.deck.getPanelIndex(panel);
        }

        selectPanel<T extends api_ui.Panel>(panel:T) {
            this.deck.selectPanelFromIndex(this.deck.getPanelIndex(panel));
        }

        getNavigator():api_ui_tab.TabBar {
            return this.navigator;
        }

        getDeck():api_ui.DeckPanel {
            return this.deck;
        }

        private addItemArray(item:any) {
            this.items.push(item);
        }

    }
}
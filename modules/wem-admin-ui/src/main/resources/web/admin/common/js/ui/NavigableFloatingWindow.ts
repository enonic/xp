module api.ui {
    export class NavigableFloatingWindow extends FloatingWindow {

        private deck: api.ui.NavigatedDeckPanel;
        private navigator: api.ui.tab.TabBar;
        private items: any[] = [];


        constructor(options: FloatingWindowOptions = {}) {
            super(wemjq.extend({draggable: true, draggableOptions: { handle: ".tab-menu"} }, options));

            this.navigator = new api.ui.tab.TabBar();
            this.deck = new api.ui.NavigatedDeckPanel(this.navigator);

            this.appendChild(this.navigator);
            this.appendChild(this.deck);
        }

        addItem<T extends api.ui.Panel>(label: string, panel: T, hidden?: boolean): number {


            var item = new api.ui.tab.TabBarItem(label);
            this.addItemArray(item);

            this.deck.addNavigablePanel(item, panel, this.items.length == 1);

            return this.deck.getPanelIndex(panel);
        }

        selectPanel<T extends api.ui.Panel>(panel: T) {
            this.deck.selectPanelByIndex(this.deck.getPanelIndex(panel));
        }

        getNavigator(): api.ui.tab.TabBar {
            return this.navigator;
        }

        getDeck(): api.ui.DeckPanel {
            return this.deck;
        }

        private addItemArray(item: any) {
            this.items.push(item);
        }

    }
}
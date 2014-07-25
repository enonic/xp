module api.ui {
    export class DockedWindow extends api.dom.DivEl {

        private deck: api.ui.panel.NavigatedDeckPanel;
        private navigator: api.ui.tab.TabBar;
        private items: any[] = [];


        constructor() {
            super("docked-window");

            this.navigator = new api.ui.tab.TabBar();
            this.deck = new api.ui.panel.NavigatedDeckPanel(this.navigator);

            this.appendChild(this.navigator);
            this.appendChild(this.deck);
        }

        addItem<T extends api.ui.panel.Panel>(label: string, panel: T, hidden?: boolean): number {
            var item = new api.ui.tab.TabBarItem(label);
            this.addItemArray(item);

            this.deck.addNavigablePanel(item, panel, this.items.length == 1);

            return this.deck.getPanelIndex(panel);
        }

        selectPanel<T extends api.ui.panel.Panel>(panel: T) {
            this.deck.selectPanelByIndex(this.deck.getPanelIndex(panel));
        }

        getNavigator(): api.ui.tab.TabBar {
            return this.navigator;
        }

        getDeck(): api.ui.panel.DeckPanel {
            return this.deck;
        }

        private addItemArray(item: any) {
            this.items.push(item);
        }

    }
}
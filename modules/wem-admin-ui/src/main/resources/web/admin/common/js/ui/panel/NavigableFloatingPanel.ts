module api.ui.panel {

    import TabBar = api.ui.tab.TabBar;

    export class NavigableFloatingPanel extends FloatingPanel {

        private deck: NavigatedDeckPanel;
        private navigator: TabBar;
        private items: any[] = [];


        constructor(options: FloatingPanelOptions = {}) {
            super(wemjq.extend({draggable: true, draggableOptions: { handle: ".tab-menu"} }, options));

            this.navigator = new TabBar();
            this.deck = new NavigatedDeckPanel(this.navigator);

            this.appendChild(this.navigator);
            this.appendChild(this.deck);
        }

        addItem<T extends Panel>(label: string, panel: T, hidden?: boolean): number {


            var item = new api.ui.tab.TabBarItem(label);
            this.addItemArray(item);

            this.deck.addNavigablePanel(item, panel, this.items.length == 1);

            return this.deck.getPanelIndex(panel);
        }

        selectPanel<T extends api.ui.panel.Panel>(panel: T) {
            this.deck.selectPanelByIndex(this.deck.getPanelIndex(panel));
        }

        getNavigator(): TabBar {
            return this.navigator;
        }

        getDeck(): DeckPanel {
            return this.deck;
        }

        private addItemArray(item: any) {
            this.items.push(item);
        }

    }
}
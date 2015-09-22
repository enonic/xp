module api.ui.panel {

    import TabBar = api.ui.tab.TabBar;

    export class DockedPanel extends Panel {

        private deck: NavigatedDeckPanel;
        private navigator: TabBar;
        private items: any[] = [];


        constructor() {
            super("docked-panel");

            this.navigator = new TabBar();
            this.deck = new NavigatedDeckPanel(this.navigator);

            this.appendChild(this.navigator);
            this.appendChild(this.deck);
            this.setDoOffset(false);
        }

        addItem<T extends Panel>(label: string, addLabelTitleAttribute: boolean, panel: T, select?: boolean): number {
            var item = new api.ui.tab.TabBarItemBuilder().setLabel(label).setAddLabelTitleAttribute(addLabelTitleAttribute).build();
            this.addItemArray(item);

            this.deck.addNavigablePanel(item, panel, select || this.items.length == 1);

            return this.deck.getPanelIndex(panel);
        }

        selectPanel<T extends Panel>(panel: T) {
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
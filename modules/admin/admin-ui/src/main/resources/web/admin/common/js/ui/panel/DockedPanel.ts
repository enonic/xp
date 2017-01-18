module api.ui.panel {

    import TabBar = api.ui.tab.TabBar;

    export class DockedPanel extends Panel {

        private deck: NavigatedDeckPanel;
        private navigator: TabBar;
        private items: api.ui.tab.TabBarItem[] = [];

        constructor() {
            super('docked-panel');

            this.navigator = new TabBar();
            this.deck = new NavigatedDeckPanel(this.navigator);

            this.setDoOffset(false);
            this.deck.setDoOffset(false);
        }

        doRender(): Q.Promise<boolean> {
            return super.doRender().then((rendered) => {

                this.appendChildren<api.dom.Element>(this.navigator, this.deck);

                return rendered;
            });
        }

        addItem<T extends Panel>(label: string, addLabelTitleAttribute: boolean, panel: T, select?: boolean): number {
            let item = new api.ui.tab.TabBarItemBuilder().setLabel(label).setAddLabelTitleAttribute(addLabelTitleAttribute).build();
            this.items.push(item);

            this.deck.addNavigablePanel(item, panel, select || this.items.length === 1);

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

    }
}

module api.app.view {

    import ViewItem = api.app.view.ViewItem;
    import NavigatedDeckPanel = api.ui.panel.NavigatedDeckPanel;
    import TabMenu = api.ui.tab.TabMenu;

    export class ItemStatisticsPanel<M> extends api.ui.panel.Panel {

        private browseItem: ViewItem<M>;

        private header: ItemStatisticsHeader<M>;

        private tabMenu: api.ui.tab.TabMenu;

        private deckPanel: api.ui.panel.NavigatedDeckPanel;

        constructor() {
            super();
            this.addClass("item-statistics-panel");

            this.tabMenu = new TabMenu();
            this.appendChild(this.tabMenu);

            this.header = new ItemStatisticsHeader<M>();
            this.appendChild(this.header);

            this.deckPanel = new NavigatedDeckPanel(this.tabMenu);
            this.deckPanel.setDoOffset(false);
            this.appendChild(this.deckPanel);
        }

        getHeader(): ItemStatisticsHeader<M> {
            return this.header;
        }

        getTabMenu(): TabMenu {
            return this.tabMenu;
        }

        getDeckPanel(): NavigatedDeckPanel {
            return this.deckPanel;
        }

        setItem(item: ViewItem<M>) {
            this.browseItem = item;
            this.header.setItem(item);
        }

        getItem(): ViewItem<M> {
            return this.browseItem;
        }

        addNavigablePanel(tab: api.ui.tab.TabMenuItem, panel: api.ui.panel.Panel, select?: boolean) {
            this.tabMenu.show();
            this.deckPanel.addNavigablePanel(tab, panel, select);
        }

        showPanel(index: number) {
            this.tabMenu.selectNavigationItem(index);
        }
    }
}

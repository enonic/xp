module api.app.view {

    export class ItemStatisticsPanel<M> extends api.ui.Panel {

        private browseItem:ViewItem<M>;

        private header:ItemStatisticsHeader<M>;

        private tabMenu:api.ui.tab.TabMenu;

        private deckPanel:api.ui.NavigatedDeckPanel;

        constructor() {
            super();
            this.addClass("item-statistics-panel");

            this.tabMenu = new api.ui.tab.TabMenu();
            this.tabMenu.hide();
            this.appendChild(this.tabMenu);

            this.header = new ItemStatisticsHeader<M>();
            this.appendChild(this.header);

            this.deckPanel = new api.ui.NavigatedDeckPanel(this.tabMenu);
            this.deckPanel.setDoOffset(false);
            this.appendChild(this.deckPanel);
        }

        getBrowseItem(): ViewItem<M> {
            return this.browseItem;
        }

        getHeader(): ItemStatisticsHeader<M> {
            return this.header;
        }

        getTabMenu(): api.ui.tab.TabMenu {
            return this.tabMenu;
        }

        getDeckPanel(): api.ui.NavigatedDeckPanel {
            return this.deckPanel;
        }

        setItem(item:api.app.view.ViewItem<M>) {
            this.browseItem = item;
            this.header.setItem(item);
        }

        addNavigablePanel(tab:api.ui.tab.TabMenuItem, panel: api.ui.Panel) {
            this.tabMenu.show();
            this.deckPanel.addNavigablePanelToBack(tab, panel);
        }

        showPanel(index:number) {
            this.tabMenu.selectNavigationItem(index);
        }
    }
}

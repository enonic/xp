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
            this.header = new ItemStatisticsHeader<M>(this.tabMenu);
            this.appendChild(this.header);

            this.deckPanel = new api.ui.NavigatedDeckPanel(this.tabMenu);
            this.appendChild(this.deckPanel);
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
            this.deckPanel.selectPanelFromIndex(index);
        }
    }
}

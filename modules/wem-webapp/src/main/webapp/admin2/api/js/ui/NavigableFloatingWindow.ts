module api_ui {
    export class NavigableFloatingWindow extends FloatingWindow {

        private deck:api_ui.NavigatedDeckPanel;
        private navigator:api_ui_tab.TabMenu;
        private items:any[];


        constructor() {
            super({draggable:true, draggableOptions: { handle: ".tab-menu"} });

            this.navigator = new api_ui_tab.TabMenu();
            this.deck = new api_ui.NavigatedDeckPanel(this.navigator);

            this.appendChild(this.navigator);
            this.appendChild(this.deck);

            api_ui_tab.TabMenuItemSelectEvent.on((event) => {
                var tabIndex = event.getTab().getIndex();
                this.navigator.selectNavigationItem(tabIndex);
                this.navigator.hideMenu();
                this.deck.showPanel(tabIndex);
            });
        }

        addItem(label:string, panel:api_ui.Panel):number {
            this.addItemArray(label);
            var item = new api_ui_tab.TabMenuItem(label);
            console.log(this.navigator);
            this.navigator.addNavigationItem(item);
            var panelIndex = this.deck.addPanel(panel);

            if (this.items.length == 1) {
                this.navigator.selectNavigationItem(0);
                this.deck.showPanel(0);
            }
            return panelIndex;
        }

        private addItemArray(item:any) {
            if (!this.items) {
                this.items = [];
            }
            this.items.push(item);

        }

    }
}
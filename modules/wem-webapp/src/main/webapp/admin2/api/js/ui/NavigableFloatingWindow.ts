module api_ui {
    export class NavigableFloatingWindow extends FloatingWindow {

        private deck:api_ui.NavigatedDeckPanel;
        private navigator:api_ui_tab.TabMenu;
        private items:any[];


        constructor() {
            super({draggable:true});

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

        addItem(label:string) {
            this.addItemArray(label);
            var item = new api_ui_tab.TabMenuItem(label);
            this.navigator.addNavigationItem(item);
            var panel = new api_ui.Panel();
            panel.getEl().setInnerHtml(label);
            this.deck.addPanel(panel);

            if (this.items.length == 1) {
                this.navigator.selectNavigationItem(0);
                this.deck.showPanel(0);
            }
        }

        private addItemArray(item:any) {
            if (!this.items) {
                this.items = [];
            }
            this.items.push(item);

        }

    }
}
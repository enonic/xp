module api_ui {
    export class NavigableFloatingWindow extends FloatingWindow {

        private deck:api_ui.NavigatedDeckPanel;
        private navigator:api_ui_tab.TabMenu;
        private items:any[] = [];


        constructor(options:FloatingWindowOptions = {}) {
            super(jQuery.extend({draggable: true, draggableOptions: { handle: ".tab-menu"} }, options));

            this.navigator = new api_ui_tab.TabMenu();
            this.deck = new api_ui.NavigatedDeckPanel(this.navigator);

            this.appendChild(this.navigator);
            this.appendChild(this.deck);
        }

        addItem<T extends api_ui.Panel>(label:string, panel:T, hidden?:boolean):number {


            var item = new api_ui_tab.TabMenuItem(label);
            this.addItemArray(item);

            item.addListener({onSelected: (tab:api_ui_tab.TabMenuItem) => {
                this.navigator.hideMenu()
            }});

            (this.items.length == 1)
                ? this.deck.addNavigablePanelToFront(item, panel)
                : this.deck.addNavigablePanelToBack(item, panel);

            return this.deck.getPanelIndex(panel);
        }

        selectPanel(index:number) {
            this.deck.selectPanelFromIndex(index);
        }

        private addItemArray(item:any) {
            this.items.push(item);
        }

    }
}
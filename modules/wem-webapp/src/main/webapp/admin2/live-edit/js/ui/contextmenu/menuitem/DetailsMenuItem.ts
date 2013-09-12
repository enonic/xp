module LiveEdit.ui.contextmenu.menuitem {

    export class DetailsMenuItem extends LiveEdit.ui.contextmenu.menuitem.BaseMenuItem {

        private menu = null;

        constructor(menu) {
            super();

            this.menu = menu;
            this.init();
        }

        init():void {
            var button = this.createButton({
                text: 'Show Details',
                name: 'details',
                handler: (event) => {
                    event.stopPropagation();
                }
            });

            this.appendTo(this.menu.getEl());
            this.menu.menuItems.push(this);
        }
    }
}
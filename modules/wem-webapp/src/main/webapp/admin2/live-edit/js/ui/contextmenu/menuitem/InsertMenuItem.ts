module LiveEdit.ui.contextmenu.menuitem {

    // Uses
    var $ = $liveEdit;

    export class InsertMenuItem extends LiveEdit.ui.contextmenu.menuitem.BaseMenuItem {

        private menu = null;

        constructor(menu) {
            super();

            this.menu = menu;
            this.init();
        }

        init():void {
            this.createMenuItem({
                text: 'Insert',
                name: 'insert',
                handler: (event) => {
                    event.stopPropagation();
                }
            });

            this.appendTo(this.menu.getEl());
            this.menu.menuItems.push(this);
        }
    }
}
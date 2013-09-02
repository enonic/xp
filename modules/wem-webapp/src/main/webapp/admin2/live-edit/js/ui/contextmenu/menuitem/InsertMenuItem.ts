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
            var $button = this.createButton({
                text: 'Insert',
                id: 'live-edit-button-insert',
                handler: (event) => {
                    event.stopPropagation();
                }
            });

            this.appendTo(this.menu.getEl());
            this.menu.buttons.push(this);
        }
    }
}
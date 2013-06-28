module LiveEdit.ui.contextmenu.menuitem {
    var $ = $liveEdit;

    export class Insert extends LiveEdit.ui.contextmenu.menuitem.Base {

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

            this.appendTo(this.menu.getRootEl());
            this.menu.buttons.push(this);
        }
    }
}
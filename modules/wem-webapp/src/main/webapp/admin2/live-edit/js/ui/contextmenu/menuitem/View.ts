module LiveEdit.ui.contextmenu.menuitem {
    var $ = $liveEdit;

    export class View extends LiveEdit.ui.contextmenu.menuitem.Base {

        private menu = null;

        constructor(menu) {
            super();

            this.menu = menu;
            this.init();
        }

        init():void {
            var $button = this.createButton({
                text: 'View',
                id: 'live-edit-button-view',
                cls: 'live-edit-component-menu-button',
                handler: (event) => event.stopPropagation()
            });

            this.appendTo(this.menu.getRootEl());
            this.menu.buttons.push(this);
        }
    }
}
module LiveEdit.ui.contextmenu.menuitem {

    // Uses
    var $ = $liveEdit;

    export class ResetMenuItem extends LiveEdit.ui.contextmenu.menuitem.BaseMenuItem {

        private menu = null;

        constructor(menu) {
            super();

            this.menu = menu;
            this.init();
        }

        init():void {
            var $button = this.createButton({
                text: 'Reset to Default',
                id: 'live-edit-button-reset',
                handler: (event) => event.stopPropagation()
            });

            this.appendTo(this.menu.getEl());
            this.menu.buttons.push(this);
        }
    }
}
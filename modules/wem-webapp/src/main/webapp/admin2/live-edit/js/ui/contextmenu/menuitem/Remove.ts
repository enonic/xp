module LiveEdit.ui.contextmenu.menuitem {
    var $ = $liveEdit;

    export class Remove extends LiveEdit.ui.contextmenu.menuitem.Base {

        private menu = null;

        constructor(menu) {
            super();

            this.menu = menu;
            this.init();
        }

        init():void {
            var $button = this.createButton({
                text: 'Remove',
                id: 'live-edit-button-remove',
                handler: (event) => {
                    event.stopPropagation();
                    // For demo purposes
                    this.menu.selectedComponent.remove();
                    $(window).trigger('removeComponent.liveEdit');
                }
            });

            this.appendTo(this.menu.getRootEl());
            this.menu.buttons.push(this);
        }
    }
}
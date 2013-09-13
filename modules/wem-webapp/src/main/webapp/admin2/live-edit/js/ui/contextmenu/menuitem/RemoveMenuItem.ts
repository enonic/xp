module LiveEdit.ui.contextmenu.menuitem {

    // Uses
    var $ = $liveEdit;

    export class RemoveMenuItem extends LiveEdit.ui.contextmenu.menuitem.BaseMenuItem {

        private menu = null;

        constructor(menu) {
            super();

            this.menu = menu;
            this.init();
        }

        init():void {
            this.createMenuItem({
                text: 'Remove',
                name: 'remove',
                handler: (event) => {
                    event.stopPropagation();
                    // For demo purposes

                    this.removeComponent();
                }
            });

            this.appendTo(this.menu.getEl());
            this.menu.menuItems.push(this);
        }

        private removeComponent() {
            this.menu.selectedComponent.getElement().remove();

            $(window).trigger('componentRemoved.liveEdit');
        }
    }
}
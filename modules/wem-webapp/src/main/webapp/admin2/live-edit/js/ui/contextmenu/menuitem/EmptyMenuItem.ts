module LiveEdit.ui.contextmenu.menuitem {

    // Uses
    var $ = $liveEdit;

    export class EmptyMenuItem extends LiveEdit.ui.contextmenu.menuitem.BaseMenuItem {

        private menu = null;

        constructor(menu) {
            super();

            this.menu = menu;
            this.init();
        }

        init():void {
            var button = this.createButton({
                text: 'Empty',
                name: 'clear',
                handler: (event) => {
                    event.stopPropagation();

                    this.emptyRegion();
                }
            });

            this.appendTo(this.menu.getEl());
            this.menu.menuItems.push(this);
        }

        private emptyRegion() {
            var region:JQuery = this.menu.selectedComponent.getElement();

            $(window).trigger('deselectComponent.liveEdit');

            $('[data-live-edit-type]', region).remove();

            $(window).trigger('componentRemoved.liveEdit');

        }
    }
}
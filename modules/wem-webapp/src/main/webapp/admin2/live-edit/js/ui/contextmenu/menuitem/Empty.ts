module LiveEdit.ui.contextmenu.menuitem {
    var $ = $liveEdit;

    export class Empty extends LiveEdit.ui.contextmenu.menuitem.Base {

        private menu = null;

        constructor(menu) {
            super();

            this.menu = menu;
            this.init();
        }

        init():void {
            var $button = this.createButton({
                text: 'Empty',
                id: 'live-edit-button-clear',
                handler: (event) => {
                    event.stopPropagation();

                    this.emptyRegion();
                }
            });

            this.appendTo(this.menu.getRootEl());
            this.menu.buttons.push(this);
        }

        private emptyRegion() {
            var region = this.menu.selectedComponent;

            $('[data-live-edit-type]', region).remove();

            $(window).trigger('deselectComponent.liveEdit');
            $(window).trigger('componentRemoved.liveEdit');

        }
    }
}
module LiveEdit.ui.contextmenu.menuitem {
    var $ = $liveedit;

    export class Edit extends LiveEdit.ui.contextmenu.menuitem.Base {

        private menu = null;

        constructor(menu) {
            super();

            this.menu = menu;
            this.init();
        }

        init():void {
            var $button = this.createButton({
                id: 'live-edit-button-edit',
                text: 'Edit',
                cls: 'live-edit-component-menu-button',
                handler: (event) => {
                    event.stopPropagation();

                    var $paragraph = this.menu.selectedComponent;
                    if ($paragraph && $paragraph.length > 0) {
                        $(window).trigger('paragraphEdit.liveEdit.component', [$paragraph]);
                    }
                }
            });

            this.appendTo(this.menu.getRootEl());
            this.menu.buttons.push(this);
        }
    }
}
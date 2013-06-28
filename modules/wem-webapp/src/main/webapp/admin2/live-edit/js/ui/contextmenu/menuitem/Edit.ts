module LiveEdit.ui.contextmenu.menuitem {
    var $ = $liveEdit;

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
                handler: (event) => {
                    event.stopPropagation();

                    var $paragraph = this.menu.selectedComponent;
                    if ($paragraph && $paragraph.length > 0) {
                        $(window).trigger('editParagraphComponent.liveEdit', [$paragraph]);
                    }
                }
            });

            this.appendTo(this.menu.getRootEl());
            this.menu.buttons.push(this);
        }
    }
}
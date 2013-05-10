module LiveEdit.ui {
    var $ = $liveedit;

    export class EditButton extends LiveEdit.ui.BaseButton {

        private menu = null;

        constructor(menu) {
            super();

            this.menu = menu;
            this.init();
        }

        init() {
            var $button = this.createButton({
                id: 'live-edit-button-edit',
                text: 'Edit',
                cls: 'live-edit-component-menu-button',
                handler: (event) => {
                    event.stopPropagation();

                    var $paragraph = this.menu.selectedComponent;
                    if ($paragraph && $paragraph.length > 0) {
                        $(window).trigger('component.onParagraphEdit', [$paragraph]);
                    }
                }
            });

            this.appendTo(this.menu.getEl());
            this.menu.buttons.push(this);
        }
    }
}
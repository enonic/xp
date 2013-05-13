module LiveEdit.ui {
    var $ = $liveedit;

    export class InsertButton extends LiveEdit.ui.BaseButton {

        private menu = null;

        constructor(menu) {
            super();

            this.menu = menu;
            this.init();
        }

        init() {
            var $button = this.createButton({
                text: 'Insert',
                id: 'live-edit-button-insert',
                cls: 'live-edit-component-menu-button',
                handler: (event) => {
                    event.stopPropagation();
                }
            });

            this.appendTo(this.menu.getRootEl());
            this.menu.buttons.push(this);
        }
    }
}
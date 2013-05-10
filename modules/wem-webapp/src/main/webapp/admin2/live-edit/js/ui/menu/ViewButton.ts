module LiveEdit.ui {
    var $ = $liveedit;

    export class ViewButton extends LiveEdit.ui.BaseButton {

        private menu = null;

        constructor(menu) {
            super();

            this.menu = menu;
            this.init();
        }

        init() {
            var $button = this.createButton({
                text: 'View',
                id: 'live-edit-button-view',
                cls: 'live-edit-component-menu-button',
                handler: (event) => {
                    event.stopPropagation();
                }
            });

            this.appendTo(this.menu.getEl());
            this.menu.buttons.push(this);
        }
    }
}
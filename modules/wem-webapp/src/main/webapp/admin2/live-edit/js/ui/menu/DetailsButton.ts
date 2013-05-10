module LiveEdit.ui {

    export class DetailsButton extends LiveEdit.ui.BaseButton {

        private menu = null;

        constructor(menu) {
            super();

            this.menu = menu;
            this.init();
        }

        init() {
            var $button = this.createButton({
                text: 'Show Details',
                id: 'live-edit-button-details',
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
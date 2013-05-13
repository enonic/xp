module LiveEdit.ui {
    var $ = $liveedit;

    export class ResetButton extends LiveEdit.ui.BaseButton {

        private menu = null;

        constructor(menu) {
            super();

            this.menu = menu;
            this.init();
        }

        init():void {
            var $button = this.createButton({
                text: 'Reset to Default',
                id: 'live-edit-button-reset',
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
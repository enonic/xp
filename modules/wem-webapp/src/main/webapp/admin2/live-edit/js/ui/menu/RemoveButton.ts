module LiveEdit.ui {
    var $ = $liveedit;

    export class RemoveButton extends LiveEdit.ui.BaseButton {

        private menu = null;

        constructor(menu) {
            super();

            this.menu = menu;
            this.init();
        }

        init():void {
            var $button = this.createButton({
                text: 'Remove',
                id: 'live-edit-button-remove',
                cls: 'live-edit-component-menu-button',
                handler: (event) => {
                    event.stopPropagation();
                    // For demo purposes
                    this.menu.selectedComponent.remove();
                    $(window).trigger('component.onRemove');
                }
            });

            this.appendTo(this.menu.getRootEl());
            this.menu.buttons.push(this);
        }
    }
}
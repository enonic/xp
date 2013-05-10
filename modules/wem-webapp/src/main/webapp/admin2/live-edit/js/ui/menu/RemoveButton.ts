module LiveEdit.ui {
    var $ = $liveedit;

    export class RemoveButton extends LiveEdit.ui.BaseButton {

        private menu = null;

        constructor(menu) {
            super();

            this.menu = menu;
            this.init();
        }

        init() {
            var me = this;
            var $button = me.createButton({
                text: 'Remove',
                id: 'live-edit-button-remove',
                cls: 'live-edit-component-menu-button',
                handler: function (event) {
                    event.stopPropagation();
                    // For demo purposes
                    me.menu.selectedComponent.remove();
                    $(window).trigger('component.onRemove');
                }
            });

            me.appendTo(me.menu.getEl());
            me.menu.buttons.push(me);
        }
    }
}
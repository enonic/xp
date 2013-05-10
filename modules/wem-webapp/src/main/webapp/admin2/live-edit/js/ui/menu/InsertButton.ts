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
            var me = this;

            var $button = me.createButton({
                text: 'Insert',
                id: 'live-edit-button-insert',
                cls: 'live-edit-component-menu-button',
                handler: function (event) {
                    event.stopPropagation();
                }
            });

            me.appendTo(me.menu.getEl());
            me.menu.buttons.push(me);
        }
    }
}
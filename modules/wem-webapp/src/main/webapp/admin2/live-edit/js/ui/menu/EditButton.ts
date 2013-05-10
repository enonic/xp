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
            var me = this;

            var $button = me.createButton({
                id: 'live-edit-button-edit',
                text: 'Edit',
                cls: 'live-edit-component-menu-button',
                handler: function (event) {
                    event.stopPropagation();

                    var $paragraph = me.menu.selectedComponent;
                    if ($paragraph && $paragraph.length > 0) {
                        $(window).trigger('component.onParagraphEdit', [$paragraph]);
                    }
                }
            });

            me.appendTo(me.menu.getEl());
            me.menu.buttons.push(me);
        }
    }
}
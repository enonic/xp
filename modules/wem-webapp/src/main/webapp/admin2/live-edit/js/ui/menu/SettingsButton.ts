module LiveEdit.ui {
    var $ = $liveedit;

    export class SettingsButton extends LiveEdit.ui.BaseButton {

        private menu = null;

        constructor(menu) {
            super();

            this.menu = menu;
            this.init();
        }

        init() {
            var me = this;

            var $button = me.createButton({
                text: 'Settings',
                id: 'live-edit-button-settings',
                cls: 'live-edit-component-menu-button',
                handler: function (event) {
                    event.stopPropagation();

                    // Temporary workaround until we get a firm messaging system
                    var parentWindow = window['parent'];
                    if (parentWindow && parentWindow['Admin'].MessageBus) {
                        parentWindow['Admin'].MessageBus.showLiveEditTestSettingsWindow({});
                    }
                }
            });

            me.appendTo(me.menu.getEl());
            me.menu.buttons.push(me);
        }
    }
}
module LiveEdit.ui {
    var $ = $liveedit;

    export class SettingsButton extends LiveEdit.ui.BaseButton {

        private menu = null;

        constructor(menu) {
            super();

            this.menu = menu;
            this.init();
        }

        init():void {
            var $button = this.createButton({
                text: 'Settings',
                id: 'live-edit-button-settings',
                cls: 'live-edit-component-menu-button',
                handler: (event) => {
                    event.stopPropagation();

                    // Temporary workaround until we get a firm messaging system
                    var parentWindow = window['parent'];
                    if (parentWindow && parentWindow['Admin'].MessageBus) {
                        parentWindow['Admin'].MessageBus.showLiveEditTestSettingsWindow({});
                    }
                }
            });

            this.appendTo(this.menu.getRootEl());
            this.menu.buttons.push(this);
        }
    }
}
module LiveEdit.ui.contextmenu.menuitem {

    // Uses
    var $ = $liveEdit;

    export class OpenContentMenuItem extends LiveEdit.ui.contextmenu.menuitem.BaseMenuItem {

        private menu = null;

        constructor(menu) {
            super();

            this.menu = menu;
            this.init();
        }

        init():void {
            var button = this.createButton({
                text: 'Open in new tab',
                name: 'opencontent',
                handler: (event) => {
                    event.stopPropagation();

                    // Temporary workaround until we get a firm messaging system
                    var parentWindow = window['parent'];
                    if (parentWindow && parentWindow['Admin'].MessageBus) {
                        // @TODO: [RYA] Should content in format (ContentModel.js) present here. Way to receive it: get by ID
                        parentWindow['Admin'].MessageBus.liveEditOpenContent();
                    }
                }
            });

            this.appendTo(this.menu.getEl());
            this.menu.menuItems.push(this);
        }
    }
}
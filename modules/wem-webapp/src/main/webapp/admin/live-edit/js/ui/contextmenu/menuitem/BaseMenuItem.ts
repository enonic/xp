interface ContextMenuItemConfig {
    name:string;
    text:string;
    cls?:string;
    iconCls?:string;
    handler(event:Event):void;
}

module LiveEdit.ui.contextmenu.menuitem {

    // Uses
    var $ = $liveEdit;

    export class BaseMenuItem extends LiveEdit.ui.Base {

        menu:ContextMenu;

        constructor(config:ContextMenuItemConfig, menu:LiveEdit.ui.contextmenu.ContextMenu) {
            super();

            this.menu = menu;

            var name:string = config.name;
            var text:string = config.text;
            var cls:string = config.cls || '';
            var iconCls:string = config.iconCls || '';
            var html:string = '<div data-live-edit-ctx-menu-item-name="' + name + '" class="live-edit-menu-item ' + cls + '">';

            if (iconCls !== '') {
                html += '<span class="live-menu-item-icon ' + iconCls + '"></span>';
            }
            html += '<span class="live-edit-menu-item-text">' + text + '</span></div>';

            var menuItem:JQuery = this.createHtmlFromString(html);

            if (config.handler) {
                menuItem.on('click', (event) => config.handler.call(this, event));
            }
        }

    }
}
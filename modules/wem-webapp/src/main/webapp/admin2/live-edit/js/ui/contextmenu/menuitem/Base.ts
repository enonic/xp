interface ButtonConfig {
    id?:string;
    text:string;
    cls?:string;
    iconCls?:string;
    handler(event:Event):void;
}

module LiveEdit.ui.contextmenu.menuitem {
    var $ = $liveEdit;

    export class Base extends LiveEdit.ui.Base {
        constructor() {
            super();
        }

        createButton(config:ButtonConfig):JQuery {
            var id:string = config.id || '';
            var text:string = config.text;
            var cls:string = config.cls || '';
            var iconCls:string = config.iconCls || '';
            var html:string = '<div data-live-edit-ui-cmp-id="' + id + '" class="live-edit-button ' + cls + '">';
            if (iconCls !== '') {
                html += '<span class="live-edit-button-icon ' + iconCls + '"></span>';
            }
            html += '<span class="live-edit-button-text">' + text + '</span></div>';
            var $button = this.createElementsFromString(html);

            if (config.handler) {
                $button.on('click', (event) => config.handler.call(this, event));
            }
            return $button;
        }
    }
}
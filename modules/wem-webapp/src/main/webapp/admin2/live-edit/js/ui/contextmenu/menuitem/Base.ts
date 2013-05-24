interface ButtonConfig {
    id?:string;
    text:string;
    cls?:string;
    iconCls?:string;
    handler(event:Event):void;
}

module LiveEdit.ui.contextmenu.menuitem {
    var $ = $liveedit;

    export class Base extends LiveEdit.ui.Base {
        constructor() {
            super();
        }


        createButton(config:ButtonConfig):JQuery {
            var id = config.id || '';
            var text = config.text;
            var cls = config.cls || '';
            var iconCls = config.iconCls || '';
            var html = '<div data-live-edit-ui-cmp-id="' + id + '" class="live-edit-button ' + cls + '">';
            if (iconCls !== '') {
                html += '<span class="live-edit-button-icon ' + iconCls + '"></span>';
            }
            html += '<span class="live-edit-button-text">' + text + '</span></div>';
            var $button = this.createElement(html);

            if (config.handler) {
                $button.on('click', (event) => config.handler.call(this, event));
            }
            return $button;
        }
    }
}
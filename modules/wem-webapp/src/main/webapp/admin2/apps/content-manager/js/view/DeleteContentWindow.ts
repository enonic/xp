module admin.ui {

    export class DeleteContentWindow {

        private container;
        private data;
        private title:String = "Delete content(s)";
        private deleteHandler = new admin.app.handler.DeleteContentHandler();

        private headerTemplate = '<h2>{title}</h2><tpl if="subtitle != undefined"><p>{subtitle}</p></tpl>';
        private listTemplate = '<div class="delete-container">' +
                           '    <tpl for=".">' +
                           '        <div class="delete-item">' +
                           '            <img class="icon" src="{data.iconUrl}"/>' +
                           '            <h4>{data.displayName}</h4>' +
                           '            <p>{data.type}</p>' +
                           '        </div>' +
                           '    </tpl>' +
                           '</div>';


        constructor() {
            var deleteCallback = (obj, success, result) => {
                this.container.hide();
                //TODO: Fire event
            };

            var ct = this.container = new Ext.container.Container();
            ct.border = false;
            ct.floating = true;
            ct.shadow = false;
            ct.width = 500;
            ct.modal = true;
            ct.autoHeight = true;
            ct.maxHeight = 600;
            ct.cls = 'admin-window';
            ct.padding = 20;

            var header = new Ext.Component();
            header.region = 'north';
            header.tpl = this.headerTemplate;
            header.data = { title: this.title };
            header.margin = '0 0 20 0';

            ct.add(header);

            var content = new Ext.Component();
            content.itemId = 'modalDialog';
            content.cls = 'dialog-info';
            content.border = false;
            content.height = 150;
            content.styleHtmlContent = true;
            content.tpl = this.listTemplate;

            ct.add(content);
        }

        setModel(model) { // setModel(s) ?
            this.data = model;
            if (model) {
                var info = this.container.down('#modalDialog');
                if (info) {
                    info.update(model);
                }

            }
        }

        doShow() {
            this.container.show();
        }
    }
}
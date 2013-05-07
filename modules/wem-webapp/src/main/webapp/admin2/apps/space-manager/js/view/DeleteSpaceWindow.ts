module admin.ui {

    export class DeleteSpaceWindow {

        private container;
        private data;
        private title:String = "Delete space(s)";
        private deleteHandler = new admin.app.handler.DeleteSpacesHandler();
        private content;

        private template = '<div class="delete-container">' +
                           '<tpl for=".">' +
                           '<div class="delete-item">' +
                           '<img class="icon" src="{data.iconUrl}"/>' +
                           '<h4>{data.displayName}</h4>' +
                           '<p>{data.type}</p>' +
                           '</div>' +
                           '</tpl>' +
                           '</div>';

        constructor() {
            this.initComponent();

            APP.event.onDeletePrompt((event) => {
                this.setModel(event.getModel());
                this.doShow();
            });
        }

        private initComponent() {
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
            header.tpl = '<h2>{title}</h2><tpl if="subtitle != undefined"><p>{subtitle}</p></tpl>';
            header.data = { title: this.title };
            header.margin = '0 0 20 0';

            ct.add(header);

            var content = this.content = new Ext.Component();
            content.region = 'center';
            content.cls = 'dialog-info';
            content.border = false;
            content.height = 150;
            content.styleHtmlContent = true;
            content.tpl = this.template;

            ct.add(content);

            var buttonRow = new Ext.container.Container();
            buttonRow.layout = { type: 'hbox', pack: 'end' };

            var deleteButton = new Ext.button.Button();
            deleteButton.text = 'Delete';
            deleteButton.margin = '0 0 0 10';
            deleteButton.setHandler((btn, evt) => {
                this.deleteHandler.doDelete(this.data, deleteCallback);
            });

            buttonRow.add(deleteButton);

            var cancelButton = new Ext.button.Button();
            cancelButton.text = 'Cancel';
            cancelButton.margin = '0 0 0 10';
            cancelButton.setHandler(() => {
                ct.hide();
            });

            buttonRow.add(cancelButton);

            ct.add(buttonRow);
        }


        setModel(model) {
            this.data = model;
            if (model) {
                if (this.content) {
                    this.content.update(model);
                }

            }
        }

        doShow() {
            this.container.show();
        }
    }
}
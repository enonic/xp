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

            APP.event.DeletePromptEvent.on((event) => {
                this.setModel(event.getModel());
                this.doShow();
            });
        }

        private initComponent() {
            var deleteCallback = (obj, success, result) => {
                this.container.hide();
                components.gridPanel.refresh();
                //TODO: Fire event
            };

            var ct = this.container = new Ext.container.Container({
                border: false,
                floating: true,
                shadow: false,
                width: 500,
                modal: true,
                autoHeight: true,
                maxHeight: 600,
                cls: 'admin-window',
                padding: 20
            });

            var header = new Ext.Component({
                region: 'north',
                tpl: '<h2>{title}</h2><tpl if="subtitle != undefined"><p>{subtitle}</p></tpl>',
                data: { title: this.title },
                margin: '0 0 20 0'
            });

            var content = this.content = new Ext.Component({
                region: 'center',
                cls: 'dialog-info',
                border: false,
                heigh: 150,
                styleHtmlContent: true,
                tpl: this.template
            });

            ct.add(header, content);

            var buttonRow = new Ext.container.Container({
                layout: { type: 'hbox', pack: 'end' },
                margin: '20 0 0 0'
            });

            var deleteButton = new Ext.button.Button({
                text: 'Delete',
                margin: '0 0 0 10',
                handler: (btn, evt) => {
                    this.deleteHandler.doDelete(this.data, deleteCallback);
                }
            });

            var cancelButton = new Ext.button.Button({
                text: 'Cancel',
                margin: '0 0 0 10',
                handler: () => {
                    ct.hide();
                }
            });

            buttonRow.add(deleteButton, cancelButton);

            ct.add(buttonRow);
        }


        setModel(model:APP.model.SpaceModel[]) {
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
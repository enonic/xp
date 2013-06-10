module admin.ui {

    export class DeleteContentWindow {

        private container;
        private data;
        private title:String = "Delete content(s)";
        private deleteHandler = new admin.app.handler.DeleteContentHandler();
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
            var deleteCallback = (obj, success, result) => {
                this.container.hide();
                //TODO: Fire event
            };

            var ct = this.container = <Ext_container_Container> new Ext.container.Container({
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


            var header = <Ext_Component> new Ext.Component({
                region: 'north',
                tpl: '<h2>{title}</h2><tpl if="subtitle != undefined"><p>{subtitle}</p></tpl>',
                data: { title: this.title },
                margin: '0 0 20 0'
            });
            ct.add(header);

            var content = this.content = <Ext_Component> new Ext.Component({
                region: 'center',
                cls: 'dialog-info',
                border: false,
                height: 150,
                styleHtmlContent: true,
                tpl: this.template
            });
            ct.add(content);

            var buttonRow = <Ext_container_Container> new Ext.container.Container({
                layout: {
                    type: 'hbox',
                    pack: 'end'
                }
            });

            var deleteButton = <Ext_button_Button> new Ext.button.Button({
                text: 'Delete',
                margin: '0 0 0 10',
                handler: (btn, evt) => {
                    this.deleteHandler.doDelete(this.data, deleteCallback);
                }
            });
            buttonRow.add(deleteButton);

            var cancelButton = <Ext_button_Button> new Ext.button.Button({
                text: 'Cancel',
                margin: '0 0 0 10',
                handler: (btn, evt) => {
                    ct.hide();
                }
            });
            buttonRow.add(cancelButton);

            ct.add(buttonRow);
        }

    ;


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
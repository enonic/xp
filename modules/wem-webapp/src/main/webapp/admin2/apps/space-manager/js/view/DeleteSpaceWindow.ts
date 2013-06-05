module app_ui {

    export class DeleteSpaceWindow {

        private container;
        private spaceModelArray:app_model.SpaceModel[];
        private title:String = "Delete space(s)";
        private deleteHandler:app_handler.DeleteSpacesHandler = new app_handler.DeleteSpacesHandler();
        private content;

        private template:string = '<div class="delete-container">' +
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

            app_event.DeletePromptEvent.on((event) => {
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

            this.container = new Ext.container.Container({
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

            this.content = new Ext.Component({
                region: 'center',
                cls: 'dialog-info',
                border: false,
                heigh: 150,
                styleHtmlContent: true,
                tpl: this.template
            });

            this.container.add(header, this.content);

            var buttonRow = new Ext.container.Container({
                layout: { type: 'hbox', pack: 'end' },
                margin: '20 0 0 0'
            });

            var deleteButton = new Ext.button.Button({
                text: 'Delete',
                margin: '0 0 0 10',
                handler: (btn, evt) => {
                    this.deleteHandler.doDelete(this.spaceModelArray, deleteCallback);
                }
            });

            var cancelButton = new Ext.button.Button({
                text: 'Cancel',
                margin: '0 0 0 10',
                handler: () => {
                    this.container.hide();
                }
            });

            buttonRow.add(deleteButton, cancelButton);

            this.container.add(buttonRow);
        }


        setModel(models:app_model.SpaceModel[]) {
            this.spaceModelArray = models;
            if (models) {
                if (this.content) {
                    this.content.update(models);
                }

            }
        }

        doShow() {
            this.container.show();
        }
    }
}
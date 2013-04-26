module admin.ui {

    export class DeleteSpaceWindow {

        private container;
        private data;
        private title:String = "Delete space(s)";
        private deleteHandler = new admin.app.handler.DeleteSpacesHandler();

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
            var me = this;
            var deleteCallback = (obj, success, result) => {
                this.container.hide();
                //TODO: Fire event
            };

            var c = new Ext.container.Container();
            c.border = false;
            c.floating = true;
            c.shadow = false;
            c.width = 500;
            c.modal = true;
            c.autoHeight = true;
            c.maxHeight = 600;
            c.cls = 'admin-window';
            c.padding = 20;

            var header = new Ext.Component();
            header.region = 'north';
            header.tpl = '<h2>{title}</h2><tpl if="subtitle != undefined"><p>{subtitle}</p></tpl>';
            header.data = { title: this.title };
            header.margin = '0 0 20 0';

            c.add(header);

            var content = new Ext.Component();
            content.itemId = 'modalDialog';
            content.cls = 'dialog-info';
            content.border = false;
            content.height = 150;
            content.styleHtmlContent = true;
            content.tpl = this.template;

            c.add(content);


            this.container = c;

            /*this.container = Ext.create('Ext.container.Container', {
             border: false,
             floating: true,
             shadow: false,
             width: 500,
             modal: true,
             autoHeight: true,
             maxHeight: 600,
             cls: 'admin-window',
             closeAction: 'hide',
             padding: 20,
             items: <any[]>[
             {
             region: 'north',
             xtype: 'component',
             tpl: '<h2>{title}</h2><tpl if="subtitle != undefined"><p>{subtitle}</p></tpl>',
             data: {
             title: me.title
             },
             margin: '0 0 20 0'
             },
             {
             region: 'center',
             layout: {
             type: 'vbox',
             align: 'stretch'
             },
             border: false,
             items: {
             itemId: 'modalDialog',
             cls: 'dialog-info',
             xtype: 'component',
             border: false,
             height: 150,
             styleHtmlContent: true,
             tpl: me.template
             }
             },
             {
             region: 'south',
             margin: '20 0 0 0',
             border: false,
             layout: {
             type: 'hbox',
             pack: 'end'
             },
             defaults: {
             xtype: 'button',
             margin: '0 0 0 10'
             },
             items: <any[]>[
             {
             text: 'Delete',
             handler: (btn, evt) => {
             btn.disable();
             this.deleteHandler.doDelete(this.data, deleteCallback);
             }
             },
             {
             text: 'Cancel',
             handler: function (btn, evt) {
             me.container.hide();
             }
             }
             ]
             }
             ]
             }

             );*/
        }

    ;


        setModel(model) {
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
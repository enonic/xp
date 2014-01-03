module app.browse.grid {

    export class TemplateTreeStore {

        private extDataStore:Ext_data_TreeStore;

        constructor() {

            this.extDataStore = <any> new Ext.data.TreeStore({

                model: 'Admin.model.schemaManager.SchemaModel',

                remoteSort: false,
                folderSort: false,
                autoLoad: false,
                clearOnLoad: true,
                autoSync: false,
                defaultRootId: undefined,
                nodeParam: 'parentKey',

                proxy: {
                    type: 'rest',
                    //TODO: add real url
                    url: api.util.getUri('/'),
                    reader: {
                        type: 'json',
                        root: '',
                        totalProperty: undefined
                    }
                }

            });
        }

        getExtDataStore():Ext_data_TreeStore {
            return this.extDataStore;
        }

    }
}

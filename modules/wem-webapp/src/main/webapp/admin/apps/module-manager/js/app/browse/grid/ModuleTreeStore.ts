module app_browse_grid {

    export class ModuleTreeStore {

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
                    url: api_util.getUri('/'),
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

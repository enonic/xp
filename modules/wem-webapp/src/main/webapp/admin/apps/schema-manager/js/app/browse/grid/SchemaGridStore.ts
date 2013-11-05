module app_browse_grid {

    export class SchemaGridStore {

        private extDataStore:Ext_data_Store;

        constructor() {

            this.extDataStore = <any> new Ext.data.Store({
                model: 'Admin.model.schemaManager.SchemaModel',

                pageSize: 50,
                remoteSort: true,
                sorters: [
                    {
                        property: 'modifiedTime',
                        direction: 'DESC'
                    }
                ],
                autoLoad: false,
                autoSync: false,

                proxy: {
                    type: 'rest',
                    url: api_util.getUri('admin/rest/content/list'),
                    reader: {
                        type: 'json',
                        root: '',
                        totalProperty: undefined
                    }
                }

            });
        }

        getExtDataStore():Ext_data_Store {
            return this.extDataStore;
        }

    }
}

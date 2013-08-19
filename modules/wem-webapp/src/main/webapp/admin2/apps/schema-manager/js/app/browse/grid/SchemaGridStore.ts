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

                proxy: {
                    type: 'direct',
                    directFn: api_remote_schema.RemoteSchemaService.schema_list,
                    simpleSortMode: true,
                    reader: {
                        type: 'json',
                        root: 'schemas',
                        totalProperty: 'total'
                    }
                }
            });
        }

        getExtDataStore():Ext_data_Store {
            return this.extDataStore;
        }

    }
}
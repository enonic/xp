module app_browse_grid {

    export class SchemaTreeStore {

        private extDataStore:Ext_data_TreeStore;

        constructor() {

            this.extDataStore = <any> new Ext.data.TreeStore({

                model: 'Admin.model.schemaManager.SchemaModel',

                folderSort: true,

                proxy: {
                    type: 'direct',
                    directFn: api_remote_schema.RemoteSchemaService.schema_tree,
                    simpleSortMode: true,
                    reader: {
                        type: 'json',
                        root: 'schemas',
                        totalProperty: 'total'
                    }
                }

            });
        }

        getExtDataStore():Ext_data_TreeStore {
            return this.extDataStore;
        }

    }
}
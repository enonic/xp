module app_browse_grid {

    export class ModuleGridStore {

        private extDataStore:Ext_data_Store;

        constructor() {

            this.extDataStore = <any> new Ext.data.Store({
                model: 'Admin.model.moduleManager.ModuleModel',

                autoLoad: true,

                proxy: {
                    type: 'ajax',
                    url: api_util.getAdminUri('rest/module/list'),
                    reader: {
                        type: 'json',
                        root: 'result.modules',
                        totalProperty: 'result.total'
                    }
                }



            });
        }

        getExtDataStore():Ext_data_Store {
            return this.extDataStore;
        }

    }
}

module app_browse_grid {

    export class ModuleGridStore {

        private extDataStore:Ext_data_Store;

        constructor() {

            this.extDataStore = <any> new Ext.data.Store({
                model: 'Admin.model.moduleManager.ModuleModel',

                autoLoad: true,

                proxy: {
                    type: 'ajax',
                    //TODO: add real url
                    url: api_util.getAdminUri('apps/module-manager/js/json/list.json'),
                    reader: {
                        type: 'json',
                        root: 'modules'
                    }
                }



            });
        }

        getExtDataStore():Ext_data_Store {
            return this.extDataStore;
        }

    }
}

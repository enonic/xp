module app_browse_grid {

    export class TemplateGridStore {

        private extDataStore:Ext_data_Store;

        constructor() {

            this.extDataStore = <any> new Ext.data.Store({
                model: 'Admin.model.templateManager.TemplateModel',

                autoLoad: false,

                proxy: {
                    type: 'ajax',
                    url: api_util.getAdminUri('apps/template-manager/js/json/list.json'),
                    reader: {
                        type: 'json',
                        root: 'templates'
                    }
                }

            });
        }

        getExtDataStore():Ext_data_Store {
            return this.extDataStore;
        }

    }
}

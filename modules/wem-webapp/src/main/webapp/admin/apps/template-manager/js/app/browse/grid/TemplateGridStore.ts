module app.browse.grid {

    export class TemplateGridStore {

        private extDataStore:Ext_data_Store;

        constructor() {

            this.extDataStore = <any> new Ext.data.Store({
                model: 'Admin.model.templateManager.TemplateModel',

                autoLoad: true,

                proxy: {
                    type: 'ajax',
                    url: api.util.getAdminUri('rest/content/site/template/list'),
                    reader: {
                        type: 'json',
                        root: 'siteTemplates',
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

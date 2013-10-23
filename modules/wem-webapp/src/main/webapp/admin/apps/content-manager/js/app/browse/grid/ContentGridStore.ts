module app_browse_grid {

    export class ContentGridStore {

        private extDataStore:Ext_data_Store;

        constructor() {

            this.extDataStore = <any> new Ext.data.Store({

                model: 'Admin.model.contentManager.ContentSummaryModel',

                autoSync: false,

                proxy: {
                    type: 'rest',
                    url: api_util.getUri('admin/rest/content/list'),
                    reader: {
                        type: 'json',
                        root: 'contents',
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
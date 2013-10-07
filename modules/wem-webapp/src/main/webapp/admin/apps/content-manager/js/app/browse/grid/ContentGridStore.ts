module app_browse_grid {

    export class ContentGridStore {

        private extDataStore:Ext_data_Store;

        constructor() {

            this.extDataStore = <any> new Ext.data.Store({

                model: 'Admin.model.contentManager.ContentModel',

                autoSync: false,

                proxy: {
                    type: 'rest',
                    url: '/admin/rest/content/list',
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
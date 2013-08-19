module app_browse_grid {

    export class ContentGridStore {

        private extDataStore:Ext_data_Store;

        constructor() {

            this.extDataStore = <any> new Ext.data.Store({

                model: 'Admin.model.contentManager.ContentModel',

                proxy: {
                    type: 'direct',
                    directFn: api_remote_content.RemoteContentService.content_find,
                    simpleSortMode: true,
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
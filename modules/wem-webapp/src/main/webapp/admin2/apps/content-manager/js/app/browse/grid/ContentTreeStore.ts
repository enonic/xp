module app_browse_grid {

    export class ContentTreeStore {

        private extDataStore:Ext_data_TreeStore;

        constructor() {

            this.extDataStore = <any> new Ext.data.TreeStore({

                model: 'Admin.model.contentManager.ContentModel',

                folderSort: true,
                autoLoad: false,

                proxy: {
                    type: 'direct',
                    directFn: api_remote_content.RemoteContentService.content_tree,
                    simpleSortMode: true,
                    reader: {
                        type: 'json',
                        root: 'contents',
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
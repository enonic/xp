module app_browse_grid {

    export class ContentTreeStore {

        private extDataStore:Ext_data_TreeStore;

        constructor() {

            this.extDataStore = <any> new Ext.data.TreeStore({

                model: 'Admin.model.contentManager.ContentSummaryModel',

                remoteSort: false,
                folderSort: false,
                autoLoad: false,
                clearOnLoad: true,
                autoSync: false,
                defaultRootId: undefined,
                nodeParam: 'parentId',

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

        getExtDataStore():Ext_data_TreeStore {
            return this.extDataStore;
        }

    }
}
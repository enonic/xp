module app_browse_grid {

    export class ContentTreeStore {

        private extDataStore:Ext_data_TreeStore;

        constructor() {

            this.extDataStore = <any> new Ext.data.TreeStore({

                model: 'Admin.model.contentManager.ContentModel',

                remoteSort: false,
                folderSort: false,
                autoLoad: false,
                clearOnLoad: false,
                autoSync: false,
                defaultRootId: undefined,
                nodeParam: 'parentId',

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

        getExtDataStore():Ext_data_TreeStore {
            return this.extDataStore;
        }

    }
}
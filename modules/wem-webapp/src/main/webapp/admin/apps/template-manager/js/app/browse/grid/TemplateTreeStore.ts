module app.browse.grid {

    export class TemplateTreeStore {

        private extDataStore:Ext_data_TreeStore;

        constructor() {

            this.extDataStore = <any> new Ext.data.TreeStore({

                model: 'Admin.model.templateManager.TemplateModel',

                remoteSort: false,
                folderSort: false,
                autoLoad: false,
                clearOnLoad: true,
                autoSync: false,
                defaultRootId: undefined,
                nodeParam: 'parentId',

                proxy: {
                    type: 'rest',
                    url: api.util.getAdminUri('rest/content/site/template/tree'),
                    reader: {
                        type: 'json',
                        root: 'templates',
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

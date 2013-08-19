module app_browse_grid {

    export class SpaceGridStore {

        private extDataStore:Ext_data_Store;

        constructor() {

            this.extDataStore = <any> new Ext.data.Store({
                pageSize: 100,
                autoLoad: true,
                model: 'Admin.model.SpaceModel',
                proxy: {
                    type: 'direct',
                    directFn: api_remote_space.RemoteSpaceService.space_list,
                    simpleSortMode: true,
                    reader: {
                        type: 'json',
                        root: 'spaces',
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
module app_browse_grid {

    export class SpaceTreeStore {

        private extDataStore:Ext_data_TreeStore;

        constructor() {
            this.extDataStore = <any> new Ext.data.TreeStore();
        }

        getExtDataStore():Ext_data_TreeStore {
            return this.extDataStore;
        }

    }
}
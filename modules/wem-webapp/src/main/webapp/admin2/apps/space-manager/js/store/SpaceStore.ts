module APP.store {
    export class SpaceStore {
        ext;

        constructor() {
            var store = this.ext = new Ext.data.Store({
                    pageSize: 100,
                    autoLoad: true,
                    model: 'Admin.model.SpaceModel',
                    proxy: {
                        type: 'direct',
                        directFn: Admin.lib.RemoteService.space_list,
                        simpleSortMode: true,
                        reader: {
                            type: 'json',
                            root: 'spaces',
                            totalProperty: 'total'
                        }
                    }
                }
            );
        }
    }
}

/*
 Ext.define('Admin.store.SpaceStore', {
 extend: 'Ext.data.Store',

 model: 'Admin.model.SpaceModel',

 pageSize: 100,
 autoLoad: true,

 proxy: {
 type: 'direct',
 directFn: Admin.lib.RemoteService.space_list,
 simpleSortMode: true,
 reader: {
 type: 'json',
 root: 'spaces',
 totalProperty: 'total'
 }
 }
 });*/

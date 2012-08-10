function getSelectedKeys(treeGrid, property) {
    var selection = [];
    Ext.Array.each(treeGrid.getSelection(), function (selected, index, all) {
        selection.push(selected.get(property));
    });
    return selection.join(',');
}

function testTreeGrid(t, treeGrid) {

    treeGrid.select('1');
    t.is(getSelectedKeys(treeGrid, 'type'), 1, 'Item with type == 1 must have been selected');
    treeGrid.select('2');
    t.is(getSelectedKeys(treeGrid, 'type'), 2, 'Item with type == 2 must have been selected');
    treeGrid.select('3', true);
    t.is(getSelectedKeys(treeGrid, 'type'), '2,3', 'Items with type == 2 and 3 must have been selected');
    treeGrid.select('1', true);
    treeGrid.deselect('3');
    t.is(getSelectedKeys(treeGrid, 'type'), '2,1', 'Items with type == 2 and 1 must have been selected');
    treeGrid.deselect(-1);
    t.is(treeGrid.getSelection().length, 0, 'List must have had nothing selected');

}


StartTest(function (t) {
    t.requireOk(
        [
            'Admin.view.TreeGridPanel'
        ],
        function () {

            var gridStore = Ext.create('Ext.data.Store', {
                fields: ['type', 'name', 'lastModified'],
                data: [
                    {"type": "1", "name": "One", "lastModified": "01-01-2012 01:00:00"},
                    {"type": "1-1", "name": "One-One", "lastModified": "02-02-2012 02:00:00"},
                    {"type": "2", "name": "Two", "lastModified": "03-03-2012 03:00:00"},
                    {"type": "2-1", "name": "Two-One", "lastModified": "04-04-2012 04:00:00"},
                    {"type": "2-2", "name": "Two-Two", "lastModified": "05-05-2012 05:00:00"},
                    {"type": "3", "name": "Three", "lastModified": "06-06-2012 06:00:00"},
                    {"type": "3-1", "name": "Three-One", "lastModified": "07-07-2012 07:00:00"},
                    {"type": "3-2", "name": "Three-Two", "lastModified": "08-08-2012 08:00:00"},
                    {"type": "3-3", "name": "Three-Three", "lastModified": "09-09-2012 09:00:00"}
                ]
            });

            var treeStore = Ext.create('Ext.data.TreeStore', {
                fields: ['type', 'name', 'lastModified'],
                rootVisible: false,
                proxy: {
                    type: 'ajax',
                    reader: {
                        type: 'json'
                    }
                },
                root: {
                    name: "root",
                    expanded: true,
                    children: [
                        {
                            "type": "1",
                            "name": "One",
                            "lastModified": "01-01-2012 01:00:00",
                            "children": [
                                {
                                    "type": "1-1",
                                    "name": "One-One",
                                    "lastModified": "02-02-2012 02:00:00",
                                    "leaf": true
                                }
                            ]
                        },
                        {
                            "type": "2",
                            "name": "Two",
                            "lastModified": "03-03-2012 03:00:00",
                            "children": [
                                {
                                    "type": "2-1",
                                    "name": "Two-One",
                                    "lastModified": "04-04-2012 04:00:00",
                                    "leaf": true
                                },
                                {
                                    "type": "2-2",
                                    "name": "Two-Two",
                                    "lastModified": "05-05-2012 05:00:00",
                                    "leaf": true
                                }
                            ]
                        },
                        {
                            "type": "3",
                            "name": "Three",
                            "lastModified": "06-06-2012 06:00:00",
                            "children": [
                                {
                                    "type": "3-1",
                                    "name": "Three-One",
                                    "lastModified": "07-07-2012 07:00:00",
                                    "leaf": true
                                },
                                {
                                    "type": "3-2",
                                    "name": "Three-Two",
                                    "lastModified": "08-08-2012 08:00:00",
                                    "leaf": true
                                },
                                {
                                    "type": "3-3",
                                    "name": "Three-Three",
                                    "lastModified": "09-09-2012 09:00:00",
                                    "leaf": true
                                }
                            ]
                        }
                    ]
                }
            });

            var treeGrid = Ext.create('widget.treeGridPanel', {
                renderTo: Ext.getBody(),
                height: 500,
                store: gridStore,
                treeStore: treeStore,
                keyField: 'type',
                gridConf: {
                    selModel: {
                        mode: 'MULTI'
                    }
                },
                treeConf: {
                    selModel: {
                        mode: 'MULTI'
                    }
                },
                columns: [
                    {
                        text: 'Display Name',
                        dataIndex: 'name',
                        sortable: true,
                        flex: 1
                    },
                    {
                        text: 'Type',
                        dataIndex: 'type',
                        sortable: true
                    },
                    {
                        xtype: 'datecolumn',
                        text: 'Last Modified',
                        dataIndex: 'lastModified',
                        sortable: true
                    }
                ]
            });

            t.diag("Testing tree");
            t.is(treeGrid.getActiveList().itemId, 'tree', 'Tree list must have been active after start');
            testTreeGrid(t, treeGrid);

            t.diag("Testing grid");
            treeGrid.setActiveList('grid');
            t.is(treeGrid.getActiveList().itemId, 'grid', 'Grid list must have been activated');
            testTreeGrid(t, treeGrid);

        }
    );
});
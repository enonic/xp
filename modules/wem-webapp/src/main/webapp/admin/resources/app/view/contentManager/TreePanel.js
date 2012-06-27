Ext.define('Admin.view.contentManager.TreePanel', {
    extend: 'Ext.tree.Panel',
    alias: 'widget.contentTree',

    cls: 'admin-tree-panel',

    collapsible: true,
    useArrows: true,
    rootVisible: false,

    viewConfig: {
        stripeRows: true
    },
    store: 'Admin.store.contentManager.ContentTreeStore',
    multiSelect: true,
    singleExpand: false,

    columns: [
        {
            text: 'Display Name',
            xtype: 'treecolumn', //this is so we know which column will show the tree
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
            text: 'Owner',
            dataIndex: 'owner',
            sortable: true
        },
        {
            text: 'Last Modified',
            dataIndex: 'lastModified',
            sortable: true
        }
    ]


});
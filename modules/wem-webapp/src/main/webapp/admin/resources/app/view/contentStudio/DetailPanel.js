Ext.define('Admin.view.contentStudio.DetailPanel', {
    extend: 'Admin.view.DetailPanel',
    alias: 'widget.contentTypeDetailPanel',

    header: false,

    overflowX: 'hidden',
    overflowY: 'auto',
    showToolbar: false,

    initComponent: function () {

        this.activeItem = this.resolveActiveItem(this.data);

        var noneSelectedCmp = this.createNoSelection();

        this.singleSelection.tabs = [
            {
                displayName: 'Traffic',
                tab: 'traffic'
            },
            {
                displayName: 'Graph',
                tab: 'graph'
            },
            {
                displayName: 'Meta',
                tab: 'meta'
            }
        ];

        this.singleSelection.tabData = {
            traffic: {
                html: '<h1>Traffic</h1>'
            },
            meta: {
                html: '<h1>Meta</h1>'
            },
            graph: {
                html: '<h1>Graph</h1>'
            }
        };

        this.singleTemplate.info = this.getCommonInfoTemplate();

        var previewCt = this.createSingleSelection(this.data);

        this.items = [
            noneSelectedCmp, previewCt
        ];

        this.callParent(arguments);
    },

    getCommonInfoTemplate: function () {
        return ['<div class="container">',
            '<table>',
            '<thead>',
            '<tr>',
            '<th colspan="2">General</th>',
            '</tr>',
            '</thead>',
            '<tbody>',
            '<tr>',
            '<td class="label">Created:</td>',
            '<td>{data.createdTime}</td>',
            '</tr>',
            '<tr>',
            '<td class="label">Modified:</td>',
            '<td>{data.modifiedTime}</td>',
            '</tr>',
            '</tbody>',
            '</table>',
            '</div>' +

            '<div class="container">',
            '<table>',
            '<thead>',
            '<tr>',
            '<th colspan="2">Statistics</th>',
            '</tr>',
            '</thead>',
            '<tbody>',
            '<tr>',
            '<td class="label">Usage Count:</td>',
            '<td>{data.usageCount}</td>',
            '</tr>',
            '</tbody>',
            '</table>',
            '</div>'];
    }

});

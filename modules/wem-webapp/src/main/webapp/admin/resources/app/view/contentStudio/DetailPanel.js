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
                title: "Configuration",
                itemId: 'configurationTab',
                items: [
                    {
                        xtype: 'textarea',
                        cls: 'config-container',
                        grow: true,
                        readOnly: true,
                        anchor: '100%',
                        itemId: 'configurationArea'
                        //value: data ? data.configXml : undefined
                    }
                ]
            }
        ];

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

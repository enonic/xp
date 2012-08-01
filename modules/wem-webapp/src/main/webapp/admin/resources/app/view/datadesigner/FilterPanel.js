Ext.define('Admin.view.datadesigner.FilterPanel', {
    extend: 'Admin.view.FilterPanel',
    alias: 'widget.datadesignerFilter',

    includeSearch: true,
    includeFacets: [
        {
            title: 'Type',
            xtype: 'checkboxgroup',
            name: 'type',
            items: [
                {
                    boxLabel: 'Field',
                    inputValue: 'field'
                },
                {
                    boxLabel: 'Fieldset',
                    inputValue: 'fieldset'
                },
                {
                    boxLabel: 'Content type',
                    inputValue: 'contenttype'
                }
            ]
        }
    ]

});

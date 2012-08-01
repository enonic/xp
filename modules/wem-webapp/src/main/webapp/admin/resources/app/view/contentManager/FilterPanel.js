Ext.define('Admin.view.contentManager.FilterPanel', {
    extend: 'Admin.view.FilterPanel',
    alias: 'widget.contentFilter',

    includeSearch: true,
    includeFacets: [
        {
            title: 'Sites',
            xtype: 'checkboxgroup',
            name: 'site',
            items: [
                {
                    boxLabel: 'Travel',
                    inputValue: 'travel'
                },
                {
                    boxLabel: 'Blueman',
                    inputValue: 'Blueman'
                },
                {
                    boxLabel: 'Cityscape',
                    inputValue: 'cityscape'
                }
            ]
        },
        {
            title: 'Types',
            xtype: 'checkboxgroup',
            name: 'type',
            items: [
                {
                    boxLabel: 'News',
                    inputValue: 'news'
                },
                {
                    boxLabel: 'Article',
                    inputValue: 'article'
                }
            ]
        },
        {
            title: 'Last Modified',
            xtype: 'checkboxgroup',
            name: 'lastModified',
            items: [
                {
                    boxLabel: '< 1h',
                    inputValue: 'hour'
                },
                {
                    boxLabel: '< 1d',
                    inputValue: 'day'
                },
                {
                    boxLabel: '< 1w',
                    inputValue: 'week'
                },
                {
                    boxLabel: '> [from date]',
                    inputValue: 'from'
                },
                {
                    boxLabel: '< [to date]',
                    inputValue: 'to'
                }
            ]
        }
    ]

});

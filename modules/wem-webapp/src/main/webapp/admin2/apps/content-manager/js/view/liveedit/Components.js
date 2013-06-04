Ext.define('Admin.view.contentManager.liveedit.Components', {
    extend: 'Ext.container.Container',
    alias: 'widget.liveEditComponentsPanel',

    layout: {
        type: 'vbox',
        align : 'stretch'
    },

    searchBar: undefined,
    listView: undefined,

    initComponent: function () {
        this.searchBar = this.createSearchBar();
        this.listView = this.createListView();
        this.items = [
            this.searchBar,
            this.listView
        ];
        this.callParent(arguments);
    },

    /**
     * @returns {Ext.Component}
     */
    createSearchBar: function () {
        return new Ext.Component({
            height: 50,
            cls: 'live-edit-component-search-bar',
            html: 'Search Bar goes here'
        });
    },

    /**
     * @returns {Ext.view.View}
     */
    createListView: function () {

        // fixme: formalize model, store 'n stuff

        Ext.define('LiveEdit.Component', {
            extend: 'Ext.data.Model',
            fields: [
                { name: 'key', type: 'string' },
                { name: 'type', type: 'string' },
                { name: 'name', type: 'string' },
                { name: 'subtitle', type: 'string' }
            ]
        });

        Ext.create('Ext.data.Store', {
            id: 'liveEditComponentsStore',
            model: 'LiveEdit.Component',
            proxy: {
                type: 'ajax',
                url: '../../admin2/live-edit/data/mock-components-2.json',
                reader: {
                    type: 'json',
                    root: 'components'
                }
            },
            autoLoad: true
        });

        var imageTpl = new Ext.XTemplate(
            '<tpl for=".">',
            '   <div class="live-edit-component">',
            '      <div class="live-edit-component-row">',
            '           <div class="live-edit-component-icon {[this.resolveIconCls(values.type)]}"></div>',
            '           <div>',
            '               <h3>{name}</h3>',
            '               <small>{subtitle}</small>',
            '           </div>',
            '       </div>',
            '   </div>',
            '</tpl>',
            {
                resolveIconCls: function (componentType) {
                    var iconCls;
                    switch (componentType) {
                    case 'page':
                        iconCls = 'icon-file';
                        break;
                    case 'region':
                        iconCls = 'icon-th-large';
                        break;
                    case 'layout':
                        iconCls = 'icon-columns';
                        break;
                    case 'part':
                        iconCls = 'icon-puzzle-piece';
                        break;
                    case 'paragraph':
                        iconCls = 'icon-edit';
                        break;
                    default:
                        iconCls = '';
                    }
                    return iconCls;
                }
            }
        );

        return new Ext.view.View({
            autoScroll: true,
            flex: 1,
            store: Ext.data.StoreManager.lookup('liveEditComponentsStore'),
            tpl: imageTpl,
            itemSelector: 'div.live-edit-component',
            emptyText: 'No components available'
        });
    }

});
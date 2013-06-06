Ext.define('Admin.view.contentManager.contextwindow.Components', {
    extend: 'Ext.container.Container',
    alias: 'widget.contextWindowComponentsPanel',

    layout: {
        type: 'vbox',
        align : 'stretch'
    },

    searchBar: undefined,
    searchInput: undefined,
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
        this.searchInput = this.createSearchInput();
        return new Ext.container.Container({
            height: 73,
            cls: 'live-edit-component-search-bar',
            items: [
                new Ext.Component({
                    html: '<p>Drag\'n drop Parts, Layouts and more..</p>'
                }),
                this.searchInput
            ]
        });
    },

    /**
     * @returns {Ext.Component}
     */
    createSearchInput: function () {
        return new Ext.Component({
            autoEl: 'input',
            cls: 'live-edit-component-search-input',
            listeners: {
                render: function () {
                    this.getEl().on('keyup', function () {

                        var store = Ext.data.StoreManager.lookup('ctxWindowComponentStore');
                        var valueLowerCased = this.getValue().toLowerCase();

                        store.clearFilter();
                        store.filterBy(function (item) {
                            return item.get('name').toLowerCase().indexOf(valueLowerCased) > -1;
                        });


                    });
                }
            }
        });
    },

    /**
     * @returns {Ext.view.View}
     */
    createListView: function () {

        // fixme: formalize model, store 'n stuff

        Ext.define('ContextWindow.Component', {
            extend: 'Ext.data.Model',
            fields: [
                { name: 'key', type: 'string' },
                { name: 'type', type: 'string' },
                { name: 'name', type: 'string' },
                { name: 'subtitle', type: 'string' }
            ]
        });

        Ext.create('Ext.data.Store', {
            id: 'ctxWindowComponentStore',
            model: 'ContextWindow.Component',
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
            flex: 1,
            store: Ext.data.StoreManager.lookup('ctxWindowComponentStore'),
            tpl: imageTpl,
            cls: 'live-edit-component-list',
            itemSelector: 'div.live-edit-component',
            emptyText: 'No components available',
            listeners: {
                render: function (view) {
                    view.getEl().on('mouseover', function() {
                        view.setOverflowXY('hidden', 'auto');
                    });
                    view.getEl().on('mouseout', function() {
                        view.setOverflowXY('hidden', 'hidden');
                    });
                }
            }
        });
    }

});
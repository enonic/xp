/**
 * fixme: Extract model and store
 */
Ext.define('Admin.view.contentManager.contextwindow.list.ComponentList', {
    extend: 'Ext.container.Container',
    alias: 'widget.contextWindowComponentList',
    uses: 'Admin.view.contentManager.contextwindow.Helper',

    title: 'Inspect',

    layout: {
        type: 'fit'
    },

    contextWindow: undefined,

    searchBar: undefined,

    searchInput: undefined,

    listView: undefined,

    store: undefined,

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
     * @returns {Ext.container.Container}
     */
    createSearchBar: function () {
        this.searchInput = this.createSearchInput();
        return new Ext.container.Container({
            cls: 'admin-component-search-bar',
            items: [
                new Ext.Component({
                    html: '<p>Choose Part</p>'
                }),
                this.searchInput
            ]
        });
    },

    /**
     * @returns {Ext.Component}
     */
    createSearchInput: function () {
        var me = this;
        return new Ext.Component({
            autoEl: {
                tag: 'input',
                placeholder: 'Search'
            },
            cls: 'admin-component-search-input',
            listeners: {
                render: function () {
                    this.getEl().on('keyup', function (event, el) {
                        me.doFilterStore(el.value);
                    });
                }
            }
        });
    },

    /**
     * @returns {Ext.view.View}
     */
    createListView: function () {
        var me = this;

        // fixme: formalize model, store 'n stuff

        Ext.define('Admin.ContextWindow.ComponentModel', {
            extend: 'Ext.data.Model',
            fields: [
                { name: 'key', type: 'string' },
                { name: 'type', type: 'string' },
                { name: 'name', type: 'string' },
                { name: 'subtitle', type: 'string' },
                { name: 'icon', type: 'string' }
            ]
        });

        this.store = Ext.create('Ext.data.Store', {
            id: 'contextWindowComponentStore',
            model: 'Admin.ContextWindow.ComponentModel',
            proxy: {
                type: 'ajax',
                url: '../admin2/apps/content-manager/js/data/context-window/mock-components.json',
                reader: {
                    type: 'json',
                    root: 'components'
                }
            },
            autoLoad: true
        });

        var templates = new Ext.XTemplate(
            '<tpl for=".">',
            '   <div class="admin-cw-item" data-live-edit-key="{key}" data-live-edit-type="{type}" data-live-edit-name="{name}">',
            '      <div class="admin-cw-item-row">',
            '           <div class="admin-cw-item-icon" style="background: url({icon}) no-repeat"></div>',
            '           <div class="admin-cw-item-info">',
            '               <h3>{name}</h3>',
            '               <sub title="{subtitle}">{[this.substringSubtitle(values.subtitle)]}</sub>',
            '           </div>',
            '       </div>',
            '   </div>',
            '</tpl>',
            {
                substringSubtitle: function (subtitle) {
                    var maxLength = 33,
                        result = subtitle;
                    if (subtitle.length > maxLength) {
                        result = subtitle.substring(0, maxLength) + ' ...'
                    }
                    return result;
                }
            }
        );

        return new Ext.view.View({
            flex: 1,
            store: Ext.data.StoreManager.lookup('contextWindowComponentStore'),
            tpl: templates,
            cls: 'admin-cw-items admin-component-items',
            itemSelector: 'div.admin-cw-item',
            emptyText: 'No components available',
            listeners: {
                itemclick: function (view, record, item) {

                    var contextWindow = me.contextWindow,
                        liveEditWindow = contextWindow.getLiveEditContentWindowObject();

                    liveEditWindow.LiveEdit.component.Inserter.replaceEmptyComponent(record.get('key'));

                }
            }
        });
    },

    doFilterStore: function (value) {
        var store = Ext.getStore('contextWindowComponentStore'),
            valueLowerCased = value.toLowerCase();

        store.clearFilter();
        store.filterBy(function (item) {
            return item.get('name').toLowerCase().indexOf(valueLowerCased) > -1;
        });
    }

});
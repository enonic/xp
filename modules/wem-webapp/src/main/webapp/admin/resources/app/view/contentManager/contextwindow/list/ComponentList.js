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

    componentType: undefined, // Live edit component type

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
        var me = this;
        me.searchInput = this.createSearchInput();
        return new Ext.container.Container({
            cls: 'admin-cw-search-bar',
            items: [
                new Ext.Component({
                    html: '<p>Choose ' + me.componentType.getName() + '</p>'
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
            cls: 'admin-cw-search-input',
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
                { name: 'typeName', type: 'string' },
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
                url: '../admin2/apps/content-manager/js/data/context-window/mock-components.jsp?componentType=' + me.componentType.getType(),
                reader: {
                    type: 'json',
                    root: 'components'
                }
            },
            autoLoad: true
        });

        var templates = new Ext.XTemplate(
            '<tpl for=".">',
            '   <div class="admin-cw-item" data-live-edit-key="{key}" data-live-edit-type="{typeName}" data-live-edit-name="{name}" title="Click to insert">',
            '      <div class="admin-cw-item-row">',
            '           <div class="admin-cw-item-icon" style="background: url({icon}) no-repeat"></div>',
            '           <div class="admin-cw-item-info">',
            '               <h3 title="Click to insert">{name}</h3>',
            '               <sub title="{subtitle} (Click to insert)">{[this.substringSubtitle(values.subtitle)]}</sub>',
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

                    var liveEditWindow = me.contextWindow.getLiveEditContentWindowObject();

                    liveEditWindow.LiveEdit.component.helper.EmptyComponent.loadComponent(record.get('key'));
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
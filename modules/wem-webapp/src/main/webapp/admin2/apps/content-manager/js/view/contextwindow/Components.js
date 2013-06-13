/**
 * fixme: Extract model and store
 */
Ext.define('Admin.view.contentManager.contextwindow.Components', {
    extend: 'Ext.container.Container',
    alias: 'widget.contextWindowComponentsPanel',
    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    COMPONENTS_STORE_URL: '../../admin2/live-edit/data/mock-components-2.json',

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
            height: 70,
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
        var me = this;
        return new Ext.Component({
            autoEl: {
                tag: 'input',
                placeholder: 'Search'
            },
            cls: 'live-edit-component-search-input',
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
            id: 'contextWindowComponentStore',
            model: 'ContextWindow.Component',
            proxy: {
                type: 'ajax',
                url: me.COMPONENTS_STORE_URL,
                reader: {
                    type: 'json',
                    root: 'components'
                }
            },
            autoLoad: true
        });

        var templates = new Ext.XTemplate(
            '<tpl for=".">',
            '   <div class="live-edit-component" data-live-edit-component-key="{key}" data-live-edit-component-type="{type}" data-live-edit-component-name="{name}">',
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
            store: Ext.data.StoreManager.lookup('contextWindowComponentStore'),
            tpl: templates,
            cls: 'live-edit-component-list',
            itemSelector: 'div.live-edit-component',
            emptyText: 'No components available',
            listeners: {
                render: function () {
                    me.registerListenersFromLiveEditPage();
                    me.initJQueryLiveDraggable();
                }
            }
        });
    },

    doFilterStore: function (value) {
        var store = Ext.data.StoreManager.lookup('contextWindowComponentStore'),
            valueLowerCased = value.toLowerCase();

        store.clearFilter();
        store.filterBy(function (item) {
            return item.get('name').toLowerCase().indexOf(valueLowerCased) > -1;
        });
    },

    initJQueryLiveDraggable: function () {
        var me = this,
            cursorAt = {left: -10, top: -15};

        $('.live-edit-component').liveDraggable({
            zIndex: 4000000,
            cursorAt: cursorAt,
            helper: me.createDragHelper,
            start: function (event, ui) {
                me.getContextWindow().hide();
                var jQuery = me.getJQueryFromLiveEditPage();
                var clone = jQuery(ui.helper.clone());

                clone.css('position', 'absolute');
                clone.css('z-index', '5100000');

                jQuery('body').append(clone);

                jQuery(clone).draggable({
                    connectToSortable: '[data-live-edit-type=region]',
                    cursorAt: cursorAt
                });

                jQuery(clone).simulate('mousedown');
            }
        });
    },

    registerListenersFromLiveEditPage: function () {
        var me = this,
            iFrame = me.getContextWindow().getLiveEditIframe(),
            jQuery = me.getJQueryFromLiveEditPage();

        jQuery(iFrame.contentWindow).on('sortStop.liveEdit.component', function () {
            $('.live-edit-component').simulate('mouseup');
            me.getContextWindow().doShow();
        });
    },

    createDragHelper: function (jQueryEvent) {
        var currentTarget = $(jQueryEvent.currentTarget),
            key = currentTarget.data('live-edit-component-key'),
            type = currentTarget.data('live-edit-component-type'),
            name = currentTarget.data('live-edit-component-name');

        return $('<div id="live-edit-drag-helper" class="live-edit-component" style="width: 150px; height: 16px;" data-live-edit-component-key="' + key + '" data-live-edit-component-name="' + type + '" data-live-edit-component-type="' + type + '"><div id="live-edit-drag-helper-status-icon"></div><span id="live-edit-drag-helper-text">' + name + '</span></div>');
    },

    getJQueryFromLiveEditPage: function () {
        return  this.getContextWindow().getLiveEditIframe().contentWindow.$liveEdit;
    },

    getContextWindow: function () {
        return this.up('contextWindow');
    }


});
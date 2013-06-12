Ext.define('Admin.view.contentManager.contextwindow.DeviceSelector', {
    extend: 'Ext.container.Container',
    alias: 'widget.contextWindowDeviceSelector',

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    searchBar: undefined,
    searchInput: undefined,
    listView: undefined,

    initComponent: function () {
        /*
        this.searchBar = this.createSearchBar();
        this.listView = this.createListView();
        this.items = [
            this.searchBar,
            this.listView
        ];
        */
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


        /*
         data-live-edit-component-key="10001" data-live-edit-component-name="fisk" data-live-edit-component-type="part"
         * */
        var imageTpl = new Ext.XTemplate(
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
            store: Ext.data.StoreManager.lookup('ctxWindowComponentStore'),
            tpl: imageTpl,
            cls: 'live-edit-component-list',
            itemSelector: 'div.live-edit-component',
            emptyText: 'No components available',
            listeners: {
                render: function () {
                    me.registerLiveEditListeners();
                    me.initJQueryLiveDraggable();
                }
            }
        });
    },

    doFilterStore: function (value) {
        var store = Ext.data.StoreManager.lookup('ctxWindowComponentStore'),
            valueLowerCased = value.toLowerCase();

        store.clearFilter();
        store.filterBy(function (item) {
            return item.get('name').toLowerCase().indexOf(valueLowerCased) > -1;
        });
    },

    initJQueryLiveDraggable: function () {
        var me = this,
            iFrame = this.getContextWindow().getLiveEditIframe(),
            cursorAt = { left: 5, top: 5 };

        $('.live-edit-component').liveDraggable({
            helper: function (event) {
                var currentTarget = $(event.currentTarget),
                    key = currentTarget.data('live-edit-component-key'),
                    type = currentTarget.data('live-edit-component-type'),
                    name = currentTarget.data('live-edit-component-name');
                return $('<div class="live-edit-component" style="width:163px; background: #fefefe; border: 1px solid #000; color: #000" data-live-edit-component-key="' + key + '" data-live-edit-component-name="' + type + '" data-live-edit-component-type="' + type + '">' + name + '</div>');
            },
            zIndex: 4000000,
            cursorAt: cursorAt,
            start: function (event, ui) {
                me.getContextWindow().hide();
                var $liveedit = iFrame.contentWindow.$liveedit;
                var clone = $liveedit(ui.helper.clone());

                clone.css('position', 'absolute');
                clone.css('z-index', '5100000');

                $liveedit('body').append(clone);

                $liveedit(clone).draggable({
                    connectToSortable: '[data-live-edit-type=region]',
                    cursorAt: cursorAt
                });

                $liveedit(clone).simulate('mousedown');
            }
        });
    },

    registerLiveEditListeners: function () {
        var me = this;
        var iFrame = me.getContextWindow().getLiveEditIframe();

        var $liveedit = iFrame.contentWindow.$liveedit;
        $liveedit(iFrame.contentWindow).on('sortStop.liveEdit.component', function () {
            $('.live-edit-component').simulate('mouseup');
            me.getContextWindow().doShow();
        });
    },

    getContextWindow: function () {
        return this.up('contextWindow');
    }


});
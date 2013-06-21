/**
 * fixme: Extract model and store
 */
Ext.define('Admin.view.contentManager.contextwindow.panel.Components', {
    extend: 'Ext.container.Container',
    alias: 'widget.contextWindowComponentsPanel',
    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    URL_TO_COMPONENTS: '../../admin2/apps/content-manager/js/data/context-window/mock-components.json',

    searchBarCt: undefined,
    searchInputCmp: undefined,
    listView: undefined,

    initComponent: function () {
        this.searchBarCt = this.createSearchBarCt();
        this.listView = this.createListView();
        this.items = [
            this.searchBarCt,
            this.listView
        ];
        this.callParent(arguments);
    },

    /**
     * @returns {Ext.container.Container}
     */
    createSearchBarCt: function () {
        this.searchInputCmp = this.createSearchInputCmp();
        return new Ext.container.Container({
            height: 80,
            cls: 'live-edit-component-search-bar',
            items: [
                new Ext.Component({
                    html: '<p>Drag\'n drop Parts, Layouts and more..</p>'
                }),
                this.searchInputCmp
            ]
        });
    },

    /**
     * @returns {Ext.Component}
     */
    createSearchInputCmp: function () {
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

        Ext.define('Admin.ContextWindow.ComponentModel', {
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
            model: 'Admin.ContextWindow.ComponentModel',
            proxy: {
                type: 'ajax',
                url: me.URL_TO_COMPONENTS,
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
            '           <div class="live-edit-component-info">',
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


    /***************************************************************************************************
     * fixme: Refactor, use a mixin or something similar for the drag drop implementation
     ***************************************************************************************************/

    windowRegion: null,
    cursorAt: {left: -10, top: -15},

    initJQueryLiveDraggable: function () {
        var me = this;

        // set up draggable
        $('.live-edit-component').liveDraggable({
            zIndex: 400000,
            cursorAt: me.cursorAt,
            //appendTo: 'body',
            helper: me.createDragHelper,
            start: function (event, ui) {
                me.onJQueryDraggableStart(event, ui);
            },
            drag: function (event, ui) {
                me.onJQueryDraggableDrag(event, ui);
            }
        });

        // set up droppable
        /*
         $(me.getContextWindow().getLiveEditIFrameDomEl()).droppable({
         over: function (event, ui) {
         me.onJQueryDroppableOver(event, ui);
         }
         });
         */
    },

    onJQueryDraggableStart: function (event, ui) {
        var me = this,
            panelHelper = Admin.view.contentManager.contextwindow.panel.Helper;

        // cache the window view region on drag start for performance
        me.windowRegion = panelHelper.getContextWindowViewRegion(panelHelper.getContextWindowFromChildCmp(me));

        panelHelper.getContextWindowFromChildCmp(me).hide();

        var jQuery = panelHelper.getJQueryFromLiveEditPage();
        var clone = jQuery(ui.helper.clone());

        clone.css('position', 'absolute');
        clone.css('z-index', '5100000');

        jQuery('body').append(clone);

        jQuery(clone).draggable({
            connectToSortable: '[data-live-edit-type=region]',
            cursorAt: me.cursorAt
        });

        jQuery(clone).simulate('mousedown');
    },

    onJQueryDraggableDrag: function (event, ui) {
        var me = this,
            panelHelper = Admin.view.contentManager.contextwindow.panel.Helper,
            mouseX = event.pageX,
            mouseY = event.pageY,
            mousePointerIsOutsideOfWindow = mouseY <= me.windowRegion.top || mouseY >= (me.windowRegion.bottom - 10) ||
                                            mouseX >= (me.windowRegion.right - 10) ||
                                            mouseX <= me.windowRegion.left;

        if (mousePointerIsOutsideOfWindow) {
            panelHelper.getContextWindowFromChildCmp(me).hide();
        }

    },

    onJQueryDroppableOver: function (event, ui) {
        var me = this,
            panelHelper = Admin.view.contentManager.contextwindow.panel.Helper,
            jQuery = panelHelper.getJQueryFromLiveEditPage(),
            clone = jQuery(ui.helper.clone());

        clone.css('position', 'absolute');
        clone.css('z-index', '5100000');

        jQuery('body').append(clone);

        ui.helper.hide(null);

        jQuery(clone).draggable({
            connectToSortable: '[data-live-edit-type=region]',
            cursorAt: me.cursorAt
        });

        jQuery(clone).simulate('mousedown');
    },

    createDragHelper: function (jQueryEvent) {
        var currentTarget = $(jQueryEvent.currentTarget),
            key = currentTarget.data('live-edit-component-key'),
            type = currentTarget.data('live-edit-component-type'),
            name = currentTarget.data('live-edit-component-name');

        return $('<div id="live-edit-drag-helper" class="live-edit-component" style="width: 150px; height: 16px;" data-live-edit-component-key="' +
                 key + '" data-live-edit-component-name="' + type + '" data-live-edit-component-type="' + type +
                 '"><div id="live-edit-drag-helper-status-icon"></div><span id="live-edit-drag-helper-text">' + name + '</span></div>');
    },

    registerListenersFromLiveEditPage: function () {
        var me = this,
            panelHelper = Admin.view.contentManager.contextwindow.panel.Helper,
        // Right now We need to use the jQuery object from the live edit page in order to listen for the events
            jQuery = panelHelper.getJQueryFromLiveEditPage(),
            liveEditWindow = panelHelper.getLiveEditWindow()

        jQuery(liveEditWindow).on('sortStop.liveEdit.component', function () {
            $('.live-edit-component').simulate('mouseup');
            panelHelper.getContextWindowFromChildCmp(me).doShow();
        });
    }

});
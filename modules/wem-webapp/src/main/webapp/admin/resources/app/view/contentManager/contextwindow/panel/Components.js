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

    URL_TO_COMPONENTS: '../admin2/apps/content-manager/js/data/context-window/mock-components.json',

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
            height: 75,
            cls: 'admin-components-search-bar',
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
            cls: 'admin-components-search-input',
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
            '   <div class="admin-components-item" data-context-window-draggable="true" data-live-edit-key="{key}" data-live-edit-type="{type}" data-live-edit-name="{name}">',
            '      <div class="admin-components-item-row">',
            '           <div class="admin-components-item-icon {[this.resolveIconCls(values.type)]}"></div>',
            '           <div class="admin-components-item-info">',
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
            cls: 'admin-components-list',
            itemSelector: 'div.live-edit-components-item',
            emptyText: 'No components available',
            listeners: {
                render: function () {
                    me.registerListenersFromLiveEditPage();
                    me.initComponentDraggables();
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
    },


    /***************************************************************************************************
     * fixme: Refactor, use a mixin or something similar for the drag drop implementation
     ***************************************************************************************************/

    /*
    windowRegion: null,
    frameRegion: null,
    */

    cursorAt: {left: -10, top: -15},

    initComponentDraggables: function () {
        var me = this,
            components = $('[data-context-window-draggable="true"]');

        components.liveDraggable({
            zIndex: 400000,
            cursorAt: me.cursorAt,
            appendTo: 'body',
            cursor: 'move',
            revert: 'invalid',
            distance: 10,
            addClasses: false,
            helper: me.createDragHelper,
            start: function (event, ui) {
                me.onStartDragComponent(event, ui);
            }
            /*
            ,
            drag: function (event, ui) {
                me.onDragComponent(event, ui);
            }
            */
        });

        $(me.getContextWindow().iFrameMask).droppable({
            tolerance: 'pointer',
            addClasses: false,
            over: function (event, ui) {
                me.onDragOverIFrame(event, ui);
            }
        });
    },

    onStartDragComponent: function (event, ui) {
        this.getContextWindow().showHideIFrameMask(true);
        this.getContextWindow().iFrameMask.className += ' live-edit-droppable-active';
        /*
        var me = this,
            panelHelper = Admin.view.contentManager.contextwindow.panel.Helper,
            contextWindow = me.getContextWindow();
        */

        // cache the regions on drag start for performance
        // this.windowRegion = panelHelper.getContextWindowViewRegion(contextWindow);
    },

    /*
    onDragComponent: function (event, ui) {
        var me = this;
        var panelHelper = Admin.view.contentManager.contextwindow.panel.Helper,
            mouseX = event.pageX,
            mouseY = event.pageY,
            mousePointerIsOutsideOfWindowRegion = mouseY <= me.windowRegion.top || mouseY >= (me.windowRegion.bottom - 10) ||
                                            mouseX >= (me.windowRegion.right - 10) ||
                                            mouseX <= me.windowRegion.left;

        if (mousePointerIsOutsideOfWindowRegion) {
            //
        }
    },
    */

    onDragOverIFrame: function (event, ui) {
        var me = this,
            contextWindow = me.getContextWindow(),
            jQuery = contextWindow.getLiveEditJQuery();

        contextWindow.showHideIFrameMask(false);

        var clone = jQuery(ui.draggable.clone());

        clone.css({
            'position': 'absolute',
            'z-index': '5100000',
            'top': '-1000px'
        });

        jQuery('body').append(clone);

        ui.helper.hide(null);

        contextWindow.getLiveEditContentWindowObject().LiveEdit.DragDropSort.createDraggable(clone);

        clone.simulate('mousedown');

        contextWindow.hide();
    },

    createDragHelper: function (jQueryEvent) {
        var draggable = $(jQueryEvent.currentTarget),
            text = draggable.data('live-edit-name');

        // fixme: can this be shared with live edit Live Edit/DragDropSort.ts ?
        var html = '<div id="live-edit-drag-helper" style="width: 150px; height: 28px; position: absolute;"><div id="live-edit-drag-helper-inner">' +
            '               <div id="live-edit-drag-helper-status-icon" class="live-edit-drag-helper-no"></div>' +
            '               <span id="live-edit-drag-helper-text" style="width: 134px;">' + text + '</span>' +
            '           </div></div>';

        return $(html);
    },

    registerListenersFromLiveEditPage: function () {
        var me = this,
            // Right now We need to use the jQuery object from the live edit page in order to listen for the events
            contextWindow = me.getContextWindow(),
            liveEditWindow = contextWindow.getLiveEditContentWindowObject(),
            liveEditJQuery = contextWindow.getLiveEditJQuery();

        liveEditJQuery(liveEditWindow).on('sortableStop.liveEdit draggableStop.liveEdit', function (event) {
            $('[data-context-window-draggable="true"]').simulate('mouseup');
            contextWindow.doShow();
            contextWindow.iFrameMask.className = contextWindow.iFrameMask.className.replace(/live-edit-droppable-active/g, '');
        });
    },

    getContextWindow: function () {
        return this.up('contextWindow');
    }

});
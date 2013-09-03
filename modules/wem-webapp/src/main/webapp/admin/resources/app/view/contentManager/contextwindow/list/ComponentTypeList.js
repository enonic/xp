/**
 * fixme: Extract model and store
 */
Ext.define('Admin.view.contentManager.contextwindow.list.ComponentTypeList', {
    extend: 'Ext.container.Container',
    alias: 'widget.contextWindowComponentTypeList',
    uses: 'Admin.view.contentManager.contextwindow.Helper',

    title: 'Insert',

    layout: {
        type: 'fit'
    },

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
            cls: 'admin-component-types-search-bar',
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
            cls: 'admin-component-types-search-input',
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

        this.store = Ext.create('Ext.data.Store', {
            id: 'contextWindowComponentStore',
            model: 'Admin.ContextWindow.ComponentModel',
            proxy: {
                type: 'ajax',
                url: '../admin2/apps/content-manager/js/data/context-window/mock-component-types.json',
                reader: {
                    type: 'json',
                    root: 'components'
                }
            },
            autoLoad: true
        });

        var templates = new Ext.XTemplate(
            '<tpl for=".">',
            '   <div class="admin-cw-item" data-context-window-draggable="true" data-live-edit-key="{key}" data-live-edit-type="{type}" data-live-edit-name="{name}">',
            '      <div class="admin-cw-item-row">',
            '           <div class="admin-cw-item-icon {[this.resolveIconCls(values.type)]}"></div>',
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
                },
                resolveIconCls: function (componentType) {
                    return Admin.view.contentManager.contextwindow.Helper.resolveComponentTypeIconCls(componentType);
                }
            }
        );

        return new Ext.view.View({
            flex: 1,
            store: Ext.data.StoreManager.lookup('contextWindowComponentStore'),
            tpl: templates,
            cls: 'admin-cw-items admin-component-types-items',
            itemSelector: 'div.admin-cw-item',
            emptyText: 'No component types available',
            listeners: {
                render: function () {
                    me.bindLiveEditEventListeners();
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
            },
            stop: function () {
                me.getContextWindow().showHideIFrameMask(false);
            }
        });

        $(me.getContextWindow().getLiveEditIFrameDom()).droppable({
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
    },

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

        contextWindow.getLiveEditContentWindowObject().LiveEdit.DragDropSort.createJQueryUiDraggable(clone);

        clone.simulate('mousedown');

        contextWindow.hide();
    },

    createDragHelper: function (jQueryEvent) {
        var draggable = $(jQueryEvent.currentTarget),
            text = draggable.data('live-edit-name');

        // fixme: can this be shared with live edit Live Edit/DragDropSort.ts ?
        var html = '<div id="live-edit-drag-helper" style="width: 150px; height: 28px; position: absolute;">' +
                   '    <div id="live-edit-drag-helper-inner">' +
                   '        <div id="live-edit-drag-helper-status-icon" class="live-edit-drag-helper-no"></div>' +
                   '        <span id="live-edit-drag-helper-text" style="width: 134px;">' + text + '</span>' +
                   '    </div>' +
                   '</div>';

        return $(html);
    },

    bindLiveEditEventListeners: function () {
        var me = this,
            // Right now We need to use the jQuery object from the live edit page in order to listen for the events
            contextWindow = me.getContextWindow(),
            liveEditWindow = contextWindow.getLiveEditContentWindowObject(),
            liveEditJQuery = contextWindow.getLiveEditJQuery();

        liveEditJQuery(liveEditWindow).on('sortableStop.liveEdit draggableStop.liveEdit', function (jQueryEvent) {
            $('[data-context-window-draggable="true"]').simulate('mouseup');
            contextWindow.doShow();
            contextWindow.iFrameMask.className = contextWindow.iFrameMask.className.replace(/live-edit-droppable-active/g, '');
        });
    },

    getContextWindow: function () {
        return this.up('contextWindow');
    }

});
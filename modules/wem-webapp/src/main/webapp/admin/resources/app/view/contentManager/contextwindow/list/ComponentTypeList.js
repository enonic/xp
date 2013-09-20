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
            cls: 'admin-cw-search-bar',
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

        Ext.define('Admin.ContextWindow.ComponentTypeModel', {
            extend: 'Ext.data.Model',
            fields: [
                { name: 'key', type: 'string' },
                { name: 'type', type: 'string' },
                { name: 'typeName', type: 'string' },
                { name: 'name', type: 'string' },
                { name: 'subtitle', type: 'string' }
            ]
        });

        this.store = Ext.create('Ext.data.Store', {
            id: 'contextWindowComponentTypeStore',
            model: 'Admin.ContextWindow.ComponentTypeModel',
            proxy: {
                type: 'ajax',

                url: '../admin2/apps/content-manager/js/data/context-window/mock-component-types.json',
                reader: {
                    type: 'json',
                    root: 'components'
                }
            },
            autoLoad: true,
            listeners: {
                load: function () {
                    me.initComponentDraggables();
                }
            }
        });

        var templates = new Ext.XTemplate(
            '<tpl for=".">',
            '   <div class="admin-cw-item" data-context-window-draggable="true" data-live-edit-key="{key}" data-live-edit-type="{typeName}" data-live-edit-name="{name}">',
            '      <div class="admin-cw-item-row">',
            '           <div class="admin-cw-item-icon {[this.resolveIconCls(values.typeName)]}"></div>',
            '           <div class="admin-cw-item-info">',
            '               <h3 title="Drag to insert">{name}</h3>',
            '               <sub title="{subtitle} (Drag to insert))">{[this.substringSubtitle(values.subtitle)]}</sub>',
            '           </div>',
            '       </div>',
            '   </div>',
            '</tpl>',
            {
                substringSubtitle: function (subtitle) {
                    var maxLength = 33,
                        result = subtitle;
                    if (subtitle.length > maxLength) {
                        result = subtitle.substring(0, maxLength) + ' ...';
                    }
                    return result;
                },
                resolveIconCls: function (componentTypeName) {
                    return Admin.view.contentManager.contextwindow.Helper.resolveComponentTypeIconCls(componentTypeName);
                }
            }
        );

        return new Ext.view.View({
            flex: 1,
            store: Ext.data.StoreManager.lookup('contextWindowComponentTypeStore'),
            tpl: templates,
            cls: 'admin-cw-items admin-component-types-items',
            itemSelector: 'div.admin-cw-item',
            emptyText: 'No component types available',
            listeners: {
                render: function () {
                    me.bindLiveEditEventListeners();
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
                me.onStartDrag(event, ui);
            },
            stop: function () {
                me.contextWindow.showHideIFrameMask(false);
            }
        });

        $(me.contextWindow.getLiveEditIFrameDom()).droppable({
            tolerance: 'pointer',
            addClasses: false,
            over: function (event, ui) {
                me.onDragOverIFrame(event, ui);
            }
        });
    },

    onStartDrag: function (event, ui) {
        this.contextWindow.showHideIFrameMask(true);
        this.contextWindow.iFrameMask.className += ' live-edit-droppable-active';
    },

    onDragOverIFrame: function (event, ui) {
        var me = this,
            contextWindow = me.contextWindow,
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

        contextWindow.getLiveEditContentWindowObject().LiveEdit.component.DragDropSort.createJQueryUiDraggable(clone);

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
            contextWindow = me.contextWindow,
            liveEditWindow = contextWindow.getLiveEditContentWindowObject(),
            liveEditJQuery = contextWindow.getLiveEditJQuery();

        liveEditJQuery(liveEditWindow).on('sortableStop.liveEdit draggableStop.liveEdit', function (jQueryEvent) {
            $('[data-context-window-draggable="true"]').simulate('mouseup');
            contextWindow.doShow();
            contextWindow.iFrameMask.className = contextWindow.iFrameMask.className.replace(/live-edit-droppable-active/g, '');
        });
    }

});
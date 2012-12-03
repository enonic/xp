Ext.define('Admin.view.TopBarMenu', {
    extend: 'Ext.menu.Menu',
    alias: 'widget.topBarMenu',

    requires: ['Admin.view.TopBarMenuItem'],

    bodyCls: 'admin-topbar-menu',

    showSeparator: false,
    styleHtmlContent: true,

    items: [
        {
            xtype: 'container',
            itemId: 'nonClosableItems',
            defaultType: 'topBarMenuItem'
        },
        {
            xtype: 'component',
            cls: 'title',
            itemId: 'editTitle',
            hidden: true,
            html: '<span>Editing</span>'
        },
        {
            xtype: 'container',
            itemId: 'editItems',
            defaultType: 'topBarMenuItem'
        },
        {
            xtype: 'component',
            cls: 'title',
            itemId: 'viewTitle',
            hidden: true,
            html: '<span>Viewing</span>'
        },
        {
            xtype: 'container',
            itemId: 'viewItems',
            defaultType: 'topBarMenuItem'
        },
        {
            xtype: 'component',
            cls: 'info',
            itemId: 'emptyTitle',
            hidden: false,
            html: 'List is empty'
        },
        {
            xtype: 'component',
            styleHtmlContent: true,
            cls: 'tools',
            html: '<a href="#" class="close-all">Close All</a><a href="#" class="close">Close</a>'
        }
    ],

    tabPanel: undefined,

    initComponent: function () {
        this.callParent(arguments);
        this.addEvents('close', 'closeAll');
        this.on('afterrender', this.bindCloseListeners);
        this.on('resize', this.updatePosition);
    },

    onClick: function (e) {
        var me = this,
            item;

        if (me.disabled) {
            e.stopEvent();
            return;
        }

        item = (e.type === 'click') ? me.getItemFromEvent(e) : me.activeItem;
        if (item && item.isMenuItem && item.onClick(e) !== false) {
            if (me.fireEvent('click', me, item, e) !== false && this.tabPanel) {
                this.tabPanel.setActiveTab(item.card.id || item.card.itemId || item.card);
            }
            this.hide();
        }
    },

    getItemFromEvent: function (e) {
        var item = this;
        do {
            item = item.getChildByElement(e.getTarget());
        } while (item && Ext.isDefined(item.getChildByElement) && item.getXType() !== 'topBarMenuItem');
        return item;
    },


    bindCloseListeners: function () {
        var me = this;
        var close = this.el.down('a.close');
        var closeAll = this.el.down('a.close-all');
        close.on('click', function (event, target, opts) {
            var items = me.getCheckedItems();
            if (me.fireEvent('close', items) !== false) {
                // me.removeItems(items);
                //tabPanel will also delete all tabs by calling tabBar.remove
                Ext.Array.each(items, function (item) {
                    if (item.closable) {
                        me.tabPanel.remove(item.card);
                    }
                });
            }
            me.hide();
        });
        closeAll.on('click', function (event, target, opts) {
            var items = me.getAllItems();
            if (me.fireEvent('closeAll', items) !== false) {
                // me.removeAllItems(false);
                //tabPanel will also delete all tabs by calling tabBar.remove
                Ext.Array.each(items, function (item) {
                    if (item.closable) {
                        me.tabPanel.remove(item.card);
                    }
                });
            }
            me.hide();
        });
    },

    getCheckedItems: function () {
        var items = this.getAllItems(),
            i,
            result = [];
        for (i = 0; i < items.length; i++) {
            if (items[i].isChecked()) {
                result.push(items[i]);
            }
        }
        return result;
    },

    getAllItems: function () {
        return this.query('topBarMenuItem');
    },

    addItems: function (items) {
        if (Ext.isEmpty(items)) {
            return;
        } else if (Ext.isObject(items)) {
            items = [].concat(items);
        }
        var editItems = [];
        var viewItems = [];
        var nonClosableItems = [];
        Ext.Array.each(items, function (item) {
            if (item.closable === false) {
                nonClosableItems.push(item);
            } else if (item.editing) {
                editItems.push(item);
            } else {
                viewItems.push(item);
            }
        });
        var added = [];
        if (nonClosableItems.length > 0) {
            added = added.concat(this.down("#nonClosableItems").add(nonClosableItems));
        }
        if (editItems.length > 0) {
            added = added.concat(this.down('#editItems').add(editItems));
        }
        if (viewItems.length > 0) {
            added = added.concat(this.down('#viewItems').add(viewItems));
        }
        this.updateTitles();
        return added;
    },

    removeAllItems: function (includeNonClosable) {
        var editItems = this.down('#editItems');
        var viewItems = this.down('#viewItems');
        var removed = [];
        Ext.Array.each(editItems.items.items, function (item) {
            if (item && item.closable !== false) {
                removed.push(editItems.remove(item));
            }
        });
        Ext.Array.each(viewItems.items.items, function (item) {
            if (item && item.closable !== false) {
                removed.push(viewItems.remove(item));
            }
        });
        if (includeNonClosable) {
            var nonClosableItems = this.down('#nonClosableItems');
            Ext.Array.each(nonClosableItems.items.items, function (item) {
                if (item && item.closable !== false) {
                    removed.push(nonClosableItems.remove(item));
                }
            });
        }
        this.updateTitles();
        return removed;
    },

    removeItems: function (items) {
        if (Ext.isEmpty(items)) {
            return;
        } else if (Ext.isObject(items)) {
            items = [].concat(items);
        }

        var editItems = this.down('#editItems');
        var viewItems = this.down('#viewItems');
        var nonClosableItems = this.down('#nonClosableItems');
        var removed = [];

        Ext.Array.each(items, function (item) {
            if (item && item.closable !== false) {
                removed.push(editItems.remove(item));
                removed.push(viewItems.remove(item));
                removed.push(nonClosableItems.remove(item));
            }
        });

        this.updateTitles();
    },

    updateTitles: function () {
        var editCount = this.down('#editItems').items.getCount();
        var viewCount = this.down('#viewItems').items.getCount();
        var nonClosableCount = this.down('#nonClosableItems').items.getCount();
        this.down('#editTitle')[editCount > 0 ? 'show' : 'hide']();
        this.down('#viewTitle')[viewCount > 0 ? 'show' : 'hide']();
        this.down('#emptyTitle')[(viewCount || editCount || nonClosableCount) > 0 ? 'hide' : 'show']();
    },

    // Need in case of resize while center positioned
    updatePosition: function (menu, width, height, oldWidth, oldHeight, opts) {
        this.el.move('r', ((oldWidth - width) / 2), false);
    }

});

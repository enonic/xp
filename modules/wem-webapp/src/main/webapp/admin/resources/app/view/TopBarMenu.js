Ext.define('Admin.view.TopBarMenu', {
    extend: 'Ext.menu.Menu',
    alias: 'widget.topBarMenu',

    requires: ['Admin.view.TopBarMenuItem'],

    cls: 'admin-topbar-menu',

    showSeparator: false,
    styleHtmlContent: true,
    layout: {
        type: 'vbox',
        align: 'stretchmax'
    },

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
        }
    ],

    tabPanel: undefined,

    initComponent: function () {
        this.callParent(arguments);
        this.on('closeMenuItem', this.onCloseMenuItem);
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
                this.tabPanel.setActiveTab(item.card);
            }
            this.hide();
        }
    },

    onBoxReady: function () {
        var tip = Ext.DomHelper.append(this.el, {
            tag: 'div',
            cls: 'balloon-tip'
        }, true);
        this.callParent(arguments);
    },

    onCloseMenuItem: function (item) {
        if (this.tabPanel) {
            this.tabPanel.remove(item.card);
        }
        // hide menu if all closable items have been closed
        if (this.getAllItems(false).length === 0) {
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

    getAllItems: function (includeNonClosable) {
        var items = [];
        if (includeNonClosable === false) {
            items = items.concat(this.down('#editItems').query('topBarMenuItem'));
            items = items.concat(this.down('#viewItems').query('topBarMenuItem'))
        } else {
            items = items.concat(this.query('topBarMenuItem'));
        }
        return items;
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
    },

});

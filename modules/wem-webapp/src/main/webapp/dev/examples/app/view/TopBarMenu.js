Ext.define('Admin.view.TopBarMenu', {
    extend: 'Ext.menu.Menu',
    alias: 'widget.topBarMenu',

    requires: ['Admin.view.TopBarMenuItem'],

    bodyCls: 'admin-topbar-menu',

    showSeparator: false,
    styleHtmlContent: true,

    items: [
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
            hidden: true,
            html: 'List is empty'
        },
        {
            xtype: 'component',
            styleHtmlContent: true,
            cls: 'tools',
            html: '<a href="#" class="close-all">Close All</a><a href="#" class="close">Close</a>'
        }
    ],

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
            me.fireEvent('click', me, item, e);
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
                me.removeItems(items);
            }
        });
        closeAll.on('click', function (event, target, opts) {
            var items = me.getAllItems();
            if (me.fireEvent('closeAll', items) !== false) {
                me.removeItems(items);
            }
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
        Ext.Array.each(items, function (item) {
            if (item.editing) {
                editItems.push(item);
            } else {
                viewItems.push(item);
            }
        });
        if (editItems.length > 0) {
            this.down('#editItems').add(editItems);
        }
        if (viewItems.length > 0) {
            this.down('#viewItems').add(viewItems);
        }
        this.updateTitles();
    },

    removeItems: function (items) {
        if (Ext.isEmpty(items)) {
            return;
        } else if (Ext.isObject(items)) {
            items = [].concat(items);
        }
        var editItems = this.down('#editItems');
        var viewItems = this.down('#viewItems');
        Ext.Array.each(items, function (item) {
            editItems.remove(item);
            viewItems.remove(item);
        });
        this.updateTitles();
    },

    updateTitles: function () {
        var editCount = this.down('#editItems').items.getCount();
        var viewCount = this.down('#viewItems').items.getCount();
        this.down('#editTitle')[editCount > 0 ? 'show' : 'hide']();
        this.down('#viewTitle')[viewCount > 0 ? 'show' : 'hide']();
        this.down('#emptyTitle')[(viewCount > 0 || editCount) > 0 ? 'hide' : 'show']();
        this.ownerButton.setText('' + (editCount + viewCount));
    },

    // Need in case of resize while center positioned
    updatePosition: function (menu, width, height, oldWidth, oldHeight, opts) {
        this.el.move('r', ((oldWidth - width) / 2), false);
    }

});

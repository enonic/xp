Ext.define('Admin.view.MegaKeyNav', {
    extend: 'Ext.util.KeyNav',

    requires: ['Ext.FocusManager'],

    constructor: function (menu) {
        var me = this;

        me.menu = menu;
        me.callParent([menu.el, {
            down: me.down,
            enter: me.enter,
            esc: me.escape,
            left: me.left,
            right: me.right,
            space: me.enter,
            tab: me.tab,
            up: me.up
        }]);
    },

    down: function (e) {
        var me = this,
            fi = me.menu.focusedItem;

        if (fi && e.getKey() === Ext.EventObject.DOWN && me.isWhitelisted(fi)) {
            return true;
        }
        var ni = me.menu.getItemBelow(fi);
        if (me.menu.canActivateItem(ni)) {
            me.menu.setActiveItem(ni);
        }
    },

    enter: function (e) {
        var menu = this.menu,
            focused = menu.focusedItem;

        if (menu.activeItem) {
            menu.onClick(e);
        } else if (focused && focused.isFormField) {
            // prevent stopEvent being called
            return true;
        }
    },

    escape: function (e) {
        Ext.menu.Manager.hideAll();
    },

    isWhitelisted: function (item) {
        return Ext.FocusManager.isWhitelisted(item);
    },

    left: function (e) {
        var menu = this.menu,
            fi = menu.focusedItem;

        if (fi && this.isWhitelisted(fi)) {
            return true;
        }

        var ni = menu.getItemLeft(fi);
        if (menu.canActivateItem(ni)) {
            menu.setActiveItem(ni);
        }
    },

    right: function (e) {
        var menu = this.menu,
            fi = menu.focusedItem;

        if (fi && this.isWhitelisted(fi)) {
            return true;
        }

        var ni = menu.getItemRight(fi);
        if (menu.canActivateItem(ni)) {
            menu.setActiveItem(ni);
        }
    },

    tab: function (e) {
        var me = this;

        if (e.shiftKey) {
            me.up(e);
        } else {
            me.down(e);
        }
    },

    up: function (e) {
        var me = this,
            fi = me.menu.focusedItem;

        if (fi && e.getKey() === Ext.EventObject.UP && me.isWhitelisted(fi)) {
            return true;
        }
        var ni = me.menu.getItemAbove(fi);
        if (me.menu.canActivateItem(ni)) {
            me.menu.setActiveItem(ni);
        }
    }
});
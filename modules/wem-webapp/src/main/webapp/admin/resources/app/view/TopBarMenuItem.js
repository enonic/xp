Ext.define('Admin.view.TopBarMenuItem', {
    extend: 'Ext.container.Container',
    alias: 'widget.topBarMenuItem',

    cls: 'admin-topbar-menu-item',
    activeCls: 'active',

    isMenuItem: true,
    canActivate: true,
    autoHeight: true,
    layout: {
        type: 'hbox',
        align: 'middle'

    },

    initComponent: function () {
        this.items = [];
        if (this.closable !== false) {
            this.items.push({
                xtype: 'checkbox',
                cls: 'checkbox',
                checked: this.checked
            });
        }
        if (this.iconCls || this.iconSrc) {
            this.items.push({
                xtype: 'image',
                margin: '0 0 0 12px',
                cls: this.iconCls,
                src: this.iconSrc
            });
        }
        if (this.text1 || this.text2) {
            this.items.push({
                xtype: 'component',
                margin: '0 0 0 12px',
                styleHtmlContent: true,
                tpl: '<strong>{text1}</strong><tpl if="text2"><br/><em>{text2}</em></tpl>',
                data: {
                    text1: this.text1,
                    text2: this.text2
                }
            });
        }
        this.callParent(arguments);
        this.addEvents('activate', 'deactivate', 'click');
    },

    activate: function () {
        var me = this;

        if (!me.activated && me.canActivate && me.rendered && !me.isDisabled() && me.isVisible()) {
            me.el.addCls(me.activeCls);
            me.focus();
            me.activated = true;
            me.fireEvent('activate', me);
        }
    },

    deactivate: function () {
        var me = this;

        if (me.activated) {
            me.el.removeCls(me.activeCls);
            me.blur();
            me.activated = false;
            me.fireEvent('deactivate', me);
        }
    },

    onClick: function (e) {
        var me = this;

        if (!me.href) {
            e.stopEvent();
        }

        if (me.disabled) {
            return;
        }

        Ext.callback(me.handler, me.scope || me, [me, e]);
        me.fireEvent('click', me, e);

        if (!me.hideOnClick) {
            me.focus();
        }
        // return false if the checkbox was clicked to prevent item click event
        return Ext.isEmpty(Ext.fly(e.getTarget()).findParent('.checkbox'));
    },

    isChecked: function () {
        var cb = this.down('checkbox');
        return cb ? cb.getValue() : false;
    }
});
Ext.define('Admin.view.TopBarMenuItem', {
    extend: 'Ext.container.Container',
    alias: 'widget.topBarMenuItem',

    cls: 'admin-topbar-menu-item',
    activeCls: 'active',

    isMenuItem: true,
    canActivate: true,

    layout: {
        type: 'hbox',
        align: 'middle'

    },
    bubbleEvents: [
        'closeMenuItem'
    ],

    initComponent: function () {
        var me = this;
        this.items = [];
        if (this.iconCls || this.iconSrc) {
            this.items.push({
                xtype: 'image',
                width: 32,
                height: 32,
                margin: '0 12px 0 0',
                cls: this.iconCls,
                src: this.iconSrc
            });
        }
        if (this.text1 || this.text2) {
            this.items.push({
                xtype: 'component',
                flex: 1,
                styleHtmlContent: true,
                tpl: '<strong>{text1}</strong><tpl if="text2"><br/><em>{text2}</em></tpl>',
                data: {
                    text1: this.text1,
                    text2: this.text2
                }
            });
        }
        if (this.closable !== false) {
            this.items.push({
                xtype: 'component',
                autoEl: 'a',
                cls: 'close-button',
                margins: '0 0 0 12px',
                listeners: {
                    afterrender: function (cmp) {
                        cmp.el.on('click', function () {
                            me.deactivate();
                            me.fireEvent('closeMenuItem', me);
                        });
                    }
                }
            });
        }
        this.callParent(arguments);
        this.addEvents('activate', 'deactivate', 'click', 'closeMenuItem');
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
        return Ext.isEmpty(Ext.fly(e.getTarget()).findParent('.close-button'));
    }
});
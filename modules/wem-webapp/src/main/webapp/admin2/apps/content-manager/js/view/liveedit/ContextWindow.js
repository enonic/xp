Ext.define('Admin.view.contentManager.liveedit.ContextWindow', {
    extend: 'Ext.container.Container',
    alias: 'widget.liveEditContextWindow',
    modal: false,
    width: 320,
    height: 518,
    border: false,
    floating: true,
    shadow: false,
    draggable: true,
    constrain: true,
    cls: 'admin-context-window',

    initComponent: function () {
        this.items = [
            this.createTitleBar()
        ];

        this.enableDrag();
        this.callParent(arguments);
    },

    createTitleBar: function () {
        var me = this;
        return new Ext.container.Container({
            cls: 'admin-context-window-title-bar',
            layout: {
                type: 'hbox',
                align: 'stretch'
            },
            items: [
                me.createMenuButton(),
                me.createTitleText(),
                me.createToggle()
            ]
        });
    },

    createMenuButton: function () {
        return new Ext.Component({
            cls: 'admin-context-window-menu icon-reorder',
            width: 30
        });
    },

    createTitleText: function () {
        return Ext.Component({
            cls: 'admin-context-window-title-text',
            html: 'Title',
            flex: 3
        });
    },

    createToggle: function () {
        return new Ext.Component({
            cls: 'admin-context-window-toggle icon-chevron-down',
            width: 30
        });
    },

    enableDrag: function () {
        var me = this;
        this.draggable = {
            delegate: '.admin-context-window-title-text'
        };
        this.constrain = true;
    },

    doShow: function () {
        this.show();
    },

    doHide: function () {
        this.hide();
    },

    close: function () {
        this.destroy();
    }

});
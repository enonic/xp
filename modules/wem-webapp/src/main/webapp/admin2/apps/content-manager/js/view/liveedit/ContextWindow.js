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

    titleBar: undefined,
    menuButton: undefined,
    titleText: undefined,
    toggleButton: undefined,

    initComponent: function () {
        this.titleBar = this.createTitleBar();
        this.items = [
            this.titleBar
        ];
        this.enableDrag();
        this.callParent(arguments);
    },

    createTitleBar: function () {
        this.menuButton = this.createMenuButton();
        this.titleText = this.createTitleText();
        this.toggleButton = this.createToggleButton();

        return new Ext.container.Container({
            cls: 'admin-context-window-title-bar',
            layout: {
                type: 'hbox',
                align: 'stretch'
            },
            items: [
                this.menuButton,
                this.titleText,
                this.toggleButton
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
        return new Ext.Component({
            cls: 'admin-context-window-title-text',
            html: 'Title',
            flex: 3
        });
    },

    createToggleButton: function () {
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

    doClose: function () {
        this.destroy();
    }

});
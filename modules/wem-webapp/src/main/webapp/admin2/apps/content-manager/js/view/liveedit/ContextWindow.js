Ext.define('Admin.view.contentManager.liveedit.ContextWindow', {
    extend: 'Ext.container.Container',
    alias: 'widget.liveEditContextWindow',
    modal: false,
    cls: 'admin-context-window',
    x: 10,
    y: 40,
    width: 320,
    shadow: false,
    border: false,
    floating: true,
    draggable: true,
    constrain: true,

    titleBar: undefined,
    menuButton: undefined,
    titleText: undefined,
    toggleButton: undefined,
    windowBody: undefined,
    draggingShim: undefined,
    isBodyVisible: true,

    defaultTitleBarHeight: 32,
    defaultBodyHeight: 516,

    initComponent: function () {
        this.titleBar = this.createTitleBar();
        this.draggingShim = this.createDraggingShim();
        this.windowBody = this.createWindowBody();
        this.items = [
            this.titleBar,
            this.windowBody
        ];
        this.enableDrag();
        this.callParent(arguments);
    },

    /**
     * @returns {Ext.container.Container}
     */
    createTitleBar: function () {
        this.menuButton = this.createMenuButton();
        this.titleText = this.createTitleText();
        this.toggleButton = this.createToggleButton();

        return new Ext.container.Container({
            cls: 'admin-context-window-title-bar',
            height: this.defaultTitleBarHeight,
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

    /**
     * @returns {Ext.Component}
     */
    createMenuButton: function () {
        return new Ext.Component({
            cls: 'admin-context-window-menu icon-reorder',
            width: 30
        });
    },

    /**
     * @returns {Ext.Component}
     */
    createTitleText: function () {
        return new Ext.Component({
            cls: 'admin-context-window-title-text',
            html: 'Title',
            flex: 3
        });
    },

    /**
     * @returns {Ext.Component}
     */
    createToggleButton: function () {
        var me = this;
        return new Ext.Component({
            cls: 'admin-context-window-toggle icon-chevron-down',
            width: 30,
            listeners: {
                render: function (component) {
                    component.getEl().on('click', function () {
                        if (me.isBodyVisible) {
                            component.getEl().addCls('icon-chevron-up').removeCls('icon-chevron-down');
                        } else {
                            component.getEl().addCls('icon-chevron-down').removeCls('icon-chevron-up');
                        }
                        me.toggleWindowBody();
                    });
                }
            }
        });
    },

    /**
     * @returns {Ext.container.Container}
     */
    createWindowBody: function () {
        return new Ext.container.Container({
            height: this.defaultBodyHeight, // fixme: set this dynamically
            autoScroll: false,
            cls: 'admin-context-window-body',
            listeners: {
                render: function (container) {
                    container.getEl().on('mouseover', function () {
                        container.setAutoScroll(true);
                    });
                    container.getEl().on('mouseout', function () {
                        container.setAutoScroll(false);
                    });
                }
            },
            items: [
                {
                    xtype: 'container',
                    html: '<p>test</p><p>test</p><p>test</p><p>test</p><p>test</p><p>test</p><p>test</p><p>test</p><p>test</p><p>test</p><p>test</p><p>test</p><p>test</p><p>test</p><p>test</p><p>test</p><p>test</p><p>test</p><p>test</p><p>test</p><p>test</p>'
                }
            ]
        });
    },

    /**
     * An opaque html element that is only displayed when during window drag
     * This is needed so the mouse drag event is not forwarded to the live edit frame on fast dragging.
     *
     * @returns {HTMLElement}
     */
    createDraggingShim: function () {
        var div = document.createElement('div');
        div.setAttribute('class', 'context-window-dragging-shim');
        div.setAttribute('style', 'display:none');
        document.body.appendChild(div);
        return div;
    },

    toggleWindowBody: function () {
        if (this.isBodyVisible) {
            this.windowBody.hide();
            this.setHeight(this.defaultTitleBarHeight);
            this.isBodyVisible = false;
        } else {
            this.windowBody.show();
            this.setHeight(this.defaultBodyHeight - this.defaultTitleBarHeight);
            this.isBodyVisible = true;
        }
    },

    enableDrag: function () {
        var me = this;
        this.draggable = {
            delegate: '.admin-context-window-title-text',
            listeners: {
                dragstart: function () {
                    me.getEl().toggleCls('is-dragging');
                    me.draggingShim.style.display = 'block'
                },
                dragend: function () {
                    me.getEl().toggleCls('is-dragging');
                    me.draggingShim.style.display = 'none'
                }
            }
        };
        this.constrain = true;
        this.constrainTo = Ext.get('live-edit-frame')
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
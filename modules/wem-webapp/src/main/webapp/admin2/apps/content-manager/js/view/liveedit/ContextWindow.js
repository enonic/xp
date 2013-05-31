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
    x: 10,
    y: 40,

    titleBar: undefined,
    menuButton: undefined,
    titleText: undefined,
    toggleButton: undefined,
    draggingCanvas: undefined,

    initComponent: function () {
        this.titleBar = this.createTitleBar();
        this.draggingCanvas = this.createDraggingShim();
        this.items = [
            this.titleBar
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
        return new Ext.Component({
            cls: 'admin-context-window-toggle icon-chevron-down',
            width: 30
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

    enableDrag: function () {
        var me = this;
        this.draggable = {
            delegate: '.admin-context-window-title-text',
            listeners: {
                dragstart: function () {
                    me.draggingCanvas.style.display = 'block'
                },
                dragend: function () {
                    me.draggingCanvas.style.display = 'none'
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
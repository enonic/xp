/**
 * TODO: Controller? :)
 */
Ext.define('Admin.view.contentManager.contextwindow.ContextWindow', {
    extend: 'Ext.container.Container',
    alias: 'widget.contextWindow',
    modal: false,
    cls: 'admin-context-window',
    x: 10,
    y: 40,
    width: 320,
    height: 548,
    shadow: false,
    border: false,
    floating: true,
    draggable: true,
    constrain: true,
    layout: {
        type: 'vbox',
        align : 'stretch'
    },
    listeners: {
        resize: function () {
            this.handleResize();
        }
    },

    DEFAULT_SELECTED_PANEL_INDEX: 0,
    TITLE_BAR_HEIGHT: 32,

    titleBar: undefined,
    menuButton: undefined,
    titleText: undefined,
    toggleButton: undefined,
    windowBody: undefined,
    draggingShim: undefined,
    collapsed: false,
    currentHeight: undefined,

    panels: [
        {
            name: 'Components',
            item: function () {
                return new Admin.view.contentManager.contextwindow.Components({hidden:true});
            }
        },
        {
            name: 'Device Selector',
            item: function () {
                return new Admin.view.contentManager.contextwindow.DeviceSelector({hidden:true});
            }
        },
        {
            name: 'Images',
            item: function () {
                return new Admin.view.contentManager.contextwindow.Images({hidden:true});
            }
        },
    ],


    initComponent: function () {
        this.titleBar = this.createTitleBar();
        this.draggingShim = this.createDraggingShim();
        this.windowBody = this.createWindowBody();
        this.items = [
            this.titleBar,
            this.windowBody
        ];
        this.enableDrag();
        this.currentHeight = this.height;

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
            height: this.TITLE_BAR_HEIGHT,
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
     * @returns {Ext.button.Button}
     */
    createMenuButton: function () {
        return new Ext.button.Button({
            text: '',
            arrowCls: '-none',
            cls: 'admin-context-window-menu-button icon-reorder',
            menu: this.createMenu()
        });
    },

    /**
     * @returns {Ext.menu.Menu}
     */
    createMenu: function () {
        return new Ext.menu.Menu({
            plain: true,
            cls: 'context-window-menu',
            items: this.createMenuItems()
        });
    },

    /**
     * @returns {Array}
     */
    createMenuItems: function () {
        var me = this, menuItems = [], panels = this.panels, key, panel;
        for (key in this.panels) {
            if (this.panels.hasOwnProperty(key)) {
                panel = panels[key];
                menuItems.push({
                    plain: true,
                    cls: 'context-window-menu-item',
                    itemId: key,
                    text: panel.name,
                    handler: function (item) {
                        me.setBodyPanel(item.itemId);
                    }
                });
            }
        }
        return menuItems;
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
            cls: 'admin-context-window-toggle-button icon-chevron-down',
            width: 30,
            listeners: {
                render: function (component) {
                    component.getEl().on('click', function () {
                        if (me.collapsed) {
                            component.getEl().addCls('icon-chevron-down').removeCls('icon-chevron-up');
                        } else {
                            component.getEl().addCls('icon-chevron-up').removeCls('icon-chevron-down');
                        }
                        me.toggleExpandCollapse();
                    });
                }
            }
        });
    },

    /**
     * @returns {Ext.container.Container}
     */
    createWindowBody: function () {
        var me = this;
        return new Ext.container.Container({
            flex: 1,
            height: this.height - this.TITLE_BAR_HEIGHT,
            cls: 'admin-context-window-body',
            listeners: {
                render: function () {
                    me.addBodyPanels();
                    me.setBodyPanel(me.DEFAULT_SELECTED_PANEL_INDEX);
                }
            }
        });
    },

    setTitleText: function (text) {
        this.titleText.getEl().setHTML(text);
    },

    toggleExpandCollapse: function () {
        if (this.collapsed) {
            this.animate({
                duration: 200,
                to: {
                    height: this.currentHeight
                }
            });
            this.collapsed = false;
        } else {
            this.animate({
                duration: 200,
                to: {
                    height: this.TITLE_BAR_HEIGHT
                }
            });
            this.currentHeight = this.height;
            this.collapsed = true;
        }
        this.handleResize();
    },

    setBodyPanel: function (index) {
        var addedPanels = this.windowBody.items.items, panel;
        for (var i = 0; i < addedPanels.length; i++) {
            panel = addedPanels[i];
            if (i == index) {
                panel.show();
                this.setTitleText(this.panels[i].name); // ai, move this

            } else {
                panel.hide();
            }
        }
    },

    addBodyPanels: function () {
        var key, panel;
        for (key in this.panels) {
            if (this.panels.hasOwnProperty(key)) {
                panel = this.panels[key];
                this.windowBody.add(panel.item());
            }
        }
    },

    /**
     * An opaque html element that is only displayed when during window drag
     * This is needed so the mouse drag event is not forwarded to the live edit frame on fast dragging.
     *
     * @returns {HTMLElement}
     */
    createDraggingShim: function () {
        var div = document.createElement('div');
        div.setAttribute('class', 'admin-context-window-dragging-shim');
        div.setAttribute('style', 'display: none');
        document.body.appendChild(div);
        return div;
    },

    enableDrag: function () {
        var me = this;
        this.draggable = {
            delegate: '.admin-context-window-title-text',
            listeners: {
                dragstart: function () {
                    me.getEl().toggleCls('is-dragging');
                    me.draggingShim.style.display = 'block';
                },
                dragend: function () {
                    me.getEl().toggleCls('is-dragging');
                    me.draggingShim.style.display = 'none';
                }
            }
        };
        this.constrain = true;
        this.constrainTo = Ext.get('live-edit-frame');
    },

    handleResize: function () {
        var addedPanels = this.windowBody.items.items,
            newBodyHeight = this.height - this.TITLE_BAR_HEIGHT;
        for (var i = 0; i < addedPanels.length; i++) {
            addedPanels[i].setHeight(newBodyHeight);
        }
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
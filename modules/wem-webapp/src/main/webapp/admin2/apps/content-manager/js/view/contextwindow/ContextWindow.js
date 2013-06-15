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
    width: 290,
    height: 508,
    shadow: false,
    border: false,
    floating: true,
    layout: {
        type: 'vbox',
        align : 'stretch'
    },
    listeners: {
        resize: function () {
            this.onWindowResize();
        }
    },

    DEFAULT_SELECTED_PANEL_INDEX: 0,
    TITLE_BAR_HEIGHT: 32,

    collapsed: false,
    currentHeight: undefined,

    titleBarCt: undefined,
    menuButton: undefined,
    titleTextCmp: undefined,
    toggleButton: undefined,
    windowBodyCt: undefined,
    draggingShim: undefined,

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
        }
    ],


    initComponent: function () {
        this.titleBarCt = this.createTitleBarCt();
        this.windowBodyCt = this.createWindowBodyCt();
        this.items = [
            this.titleBarCt,
            this.windowBodyCt
        ];
        this.draggingShim = this.createDraggingShim();
        this.enableWindowDrag();
        this.enableWindowResize();
        this.currentHeight = this.height;

        this.callParent(arguments);
    },

    /**
     * @returns {Ext.container.Container}
     */
    createTitleBarCt: function () {
        this.menuButton = this.createMenuButton();
        this.titleTextCmp = this.createTitleTextCmp();
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
                this.titleTextCmp,
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
            border: false,
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
                        me.showPanel(item.itemId);
                    }
                });
            }
        }
        return menuItems;
    },

    /**
     * @returns {Ext.Component}
     */
    createTitleTextCmp: function () {
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
                            // fixme: is there a simpler way to toggle classes?
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
    createWindowBodyCt: function () {
        var me = this;
        return new Ext.container.Container({
            flex: 1,
            height: this.height - this.TITLE_BAR_HEIGHT,
            cls: 'admin-context-window-body',
            listeners: {
                render: function () {
                    me.addPanels();
                    me.showPanel(me.DEFAULT_SELECTED_PANEL_INDEX);
                }
            }
        });
    },

    setTitleText: function (text) {
        this.titleTextCmp.getEl().setHTML(text);
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
    },

    showPanel: function (index) {
        var addedPanels = this.windowBodyCt.items.items, panel;
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

    addPanels: function () {
        var key, panel;
        for (key in this.panels) {
            if (this.panels.hasOwnProperty(key)) {
                panel = this.panels[key];
                this.windowBodyCt.add(panel.item());
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

    showHideDraggingShim: function (show) {
        this.draggingShim.style.display = show ? 'block' : 'none';
    },

    enableWindowDrag: function () {
        var me = this;
        this.draggable = {
            delegate: '.admin-context-window-title-text',
            listeners: {
                dragstart: function () {
                    me.getEl().toggleCls('is-dragging');
                    me.showHideDraggingShim(true);
                },
                dragend: function () {
                    me.getEl().toggleCls('is-dragging');
                    me.showHideDraggingShim(false);
                }
            }
        };
        this.constrain = true;
        this.constrainTo = Ext.get('live-edit-iframe-container');
    },

    enableWindowResize: function () {
        var me = this;
        this.resizable = {
            dynamic: true,
            transparent: true,
            listeners: {
                beforeresize: function () {
                    me.showHideDraggingShim(true)
                },
                resize: function () {
                    me.showHideDraggingShim(false)
                }
            }
        };
    },

    onWindowResize: function () {
        // fixme: optimize
        var addedPanels = this.windowBodyCt.items.items,
            newBodyHeight = this.height - this.TITLE_BAR_HEIGHT;

        for (var i = 0; i < addedPanels.length; i++) {
            addedPanels[i].setHeight(newBodyHeight);
        }
    },

    /**
     * @returns {Html_dom_Element}
     */
    getLiveEditIFrame: function () {
        return Ext.DomQuery.selectNode('#live-edit-iframe');
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
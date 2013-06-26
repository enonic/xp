/**
 * TODO: Controller? :)
 */
Ext.define('Admin.view.contentManager.contextwindow.ContextWindow', {
    extend: 'Ext.container.Container',
    alias: 'widget.contextWindow',

    requires: [
        'Admin.view.contentManager.contextwindow.panel.Components',
        'Admin.view.contentManager.contextwindow.panel.DeviceSelector',
        'Admin.view.contentManager.contextwindow.panel.Images'
    ],

    modal: false,
    cls: 'admin-context-window',
    x: 10,
    y: 10,
    width: 300,
    height: 480,
    shadow: false,
    border: false,
    floating: true,
    layout: {
        type: 'vbox',
        align : 'stretch'
    },
    listeners: {
        resize: function () {
            this.resizePanelHeights();
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
    iFrameMask: undefined,

    liveEditIFrameDom: undefined, // Passed as constructor config

    panels: [
        {
            name: 'Insert',
            item: function () {
                return new Admin.view.contentManager.contextwindow.panel.Components({hidden:true});
            }
        },
        {
            name: 'Device Selector',
            item: function () {
                return new Admin.view.contentManager.contextwindow.panel.DeviceSelector({hidden:true});
            }
        },
        {
            name: 'Images',
            item: function () {
                return new Admin.view.contentManager.contextwindow.panel.Images({hidden:true});
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
        this.iFrameMask = this.createIFrameMask();
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
        var me = this;
        return new Ext.menu.Menu({
            border: false,
            plain: true,
            cls: 'context-window-menu',
            items: this.createMenuItems(),
            listeners: {
                beforeshow: function (menu) {
                    menu.setWidth(me.getWidth());
                }
            }
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
                    width:'100%',
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
            cls: 'admin-context-window-expand-collapse-button icon-chevron-down',
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
        // Should this function add menu items too?
        var key,
            panel;
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
    createIFrameMask: function () {
        var div = document.createElement('div');
        div.setAttribute('class', 'admin-context-window-iframe-mask');
        div.setAttribute('style', 'display: none');
        document.body.appendChild(div);
        return div;
    },

    showHideIFrameMask: function (show) {
        var iFrameEl = Ext.get(this.getLiveEditIFrameDom().id),
            y = iFrameEl.getY(),
            x = iFrameEl.getX(),
            w = iFrameEl.getComputedWidth(),
            h = iFrameEl.getComputedHeight();

        this.iFrameMask.style.display = show ? 'block' : 'none';
        this.iFrameMask.style.top = y + 'px';
        this.iFrameMask.style.left = x + 'px';
        this.iFrameMask.style.width = w + 'px';
        this.iFrameMask.style.height = h + 'px';
    },

    enableWindowDrag: function () {
        var me = this;
        this.draggable = {
            delegate: '.admin-context-window-title-text',
            listeners: {
                dragstart: function () {
                    me.getEl().toggleCls('is-dragging');
                    me.showHideIFrameMask(true);
                },
                dragend: function () {
                    me.getEl().toggleCls('is-dragging');
                    me.showHideIFrameMask(false);
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
            transparent: false,
            listeners: {
                beforeresize: function () {
                    me.showHideIFrameMask(true)
                },
                resize: function () {
                    me.showHideIFrameMask(false)
                }
            }
        };
    },

    resizePanelHeights: function () {
        var addedBodyPanels = this.windowBodyCt.items.items,
            newBodyHeight = this.height - this.TITLE_BAR_HEIGHT;

        for (var i = 0; i < addedBodyPanels.length; i++) {
            addedBodyPanels[i].setHeight(newBodyHeight);
        }
    },

    /**
     * @returns {HTMLElement}
     */
    getLiveEditIFrameDom: function () {
        return this.liveEditIFrameDom;
    },

    /**
     * @returns {window|Window|Window}
     */
    getLiveEditContentWindowObject: function () {
        return this.getLiveEditIFrameDom().contentWindow;
    },

    /**
     * @returns JQuery
     */
    getLiveEditJQuery: function () {
        return this.getLiveEditContentWindowObject().$liveEdit;
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
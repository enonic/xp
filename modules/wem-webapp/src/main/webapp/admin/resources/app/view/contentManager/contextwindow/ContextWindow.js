/**
 * TODO: Controller? :)
 */
Ext.define('Admin.view.contentManager.contextwindow.ContextWindow', {
    extend: 'Ext.container.Container',
    alias: 'widget.contextWindow',

    requires: [
        'Admin.view.contentManager.contextwindow.Inspector',
        'Admin.view.contentManager.contextwindow.custompanel.Components',
        'Admin.view.contentManager.contextwindow.custompanel.Emulator'
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
            this.resizeCustomPanelHeights();
        }
    },

    DEFAULT_SELECTED_PANEL_INDEX: 0,
    TITLE_BAR_HEIGHT: 32,

    selectedPanelIndex: undefined,
    collapsed: false,
    currentHeight: undefined,

    titleBarCt: undefined,
    menuButton: undefined,
    titleTextCmp: undefined,
    toggleButton: undefined,
    customPanelsContainer: undefined,
    inspectorCmp: undefined,
    iFrameMask: undefined,

    liveEditIFrameDom: undefined, // Passed as constructor config

    customPanels: [
        {
            name: 'Insert',
            item: function () {
                return new Admin.view.contentManager.contextwindow.custompanel.Components({hidden:true});
            }
        },
        {
            name: 'Emulator',
            item: function () {
                return new Admin.view.contentManager.contextwindow.custompanel.Emulator({hidden:true});
            }
        },
    ],

    initComponent: function () {
        this.titleBarCt = this.createTitleBarCt();
        this.customPanelsContainer = this.createCustomPanelContainer();
        this.inspectorCmp = new Admin.view.contentManager.contextwindow.Inspector({hidden:true});
        this.items = [
            this.titleBarCt,
            this.customPanelsContainer,
            this.inspectorCmp
        ];
        this.iFrameMask = this.createIFrameMask();

        this.enableWindowDrag();

        this.enableWindowResize();

        this.registerListenersFromLiveEditPage();

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
            shadow: false,
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
        var me = this, menuItems = [], panels = this.customPanels,
            panel,
            i;
        for (i in this.customPanels) {
            if (this.customPanels.hasOwnProperty(i)) {
                panel = panels[i];
                menuItems.push({
                    plain: true,
                    cls: 'context-window-menu-item',
                    itemId: i, // fixme: use another property for storing the index value
                    width:'100%',
                    text: panel.name,
                    handler: function (item) {
                        me.showCustomPanel(item.itemId);
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
                        me.toggleExpandCollapseWindow();
                    });
                }
            }
        });
    },

    /**
     * @returns {Ext.container.Container}
     */
    createCustomPanelContainer: function () {
        var me = this;
        return new Ext.container.Container({
            flex: 1,
            height: this.height - this.TITLE_BAR_HEIGHT,
            cls: 'admin-context-window-custom-panels',
            listeners: {
                render: function () {
                    me.addCustomPanels();
                    me.showCustomPanel(me.DEFAULT_SELECTED_PANEL_INDEX);
                }
            }
        });
    },

    setTitleText: function (text) {
        this.titleTextCmp.getEl().setHTML(text);
    },

    toggleExpandCollapseWindow: function () {
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

    showCustomPanel: function (index) {
        var addedPanels = this.customPanelsContainer.items.items,
            panel;

        for (var i = 0; i < addedPanels.length; i++) {
            panel = addedPanels[i];
            if (i == index) {
                panel.show();

                this.setTitleText(this.customPanels[i].name); // ai, move this
                this.setSelectedPanelIndex(i);
            } else {
                panel.hide();
            }
        }
    },

    showHideInspector: function (show) {
        if (show) {
            this.setTitleText('Inspector');
            this.customPanelsContainer.hide();
            this.inspectorCmp.show();
        } else {
            this.customPanelsContainer.show();
            this.inspectorCmp.hide();
        }
    },

    addCustomPanels: function () {
        // Should this function add menu items too?
        var key,
            panel;
        for (key in this.customPanels) {
            if (this.customPanels.hasOwnProperty(key)) {
                panel = this.customPanels[key];
                this.customPanelsContainer.add(panel.item());
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

    resizeCustomPanelHeights: function () {
        var addedCustomPanels = this.customPanelsContainer.items.items,
            newHeight = this.height - this.TITLE_BAR_HEIGHT;

        for (var i = 0; i < addedCustomPanels.length; i++) {
            addedCustomPanels[i].setHeight(newHeight);
        }
    },

    registerListenersFromLiveEditPage: function () {
        var me = this,
        // Right now We need to use the jQuery object from the live edit page in order to listen for the events
            liveEditWindow = me.getLiveEditContentWindowObject(),
            liveEditJQuery = me.getLiveEditJQuery();

        liveEditJQuery(liveEditWindow).on('selectComponent.liveEdit', function (event) {
            me.showHideInspector(true);
        });
        liveEditJQuery(liveEditWindow).on('deselectComponent.liveEdit', function (event) {
            if (me.selectedPanelIndex != undefined) {
                me.showHideInspector(false);
                me.showCustomPanel(me.selectedPanelIndex);
            }
        });
    },

    setSelectedPanelIndex: function (index) {
        this.selectedPanelIndex = index;
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
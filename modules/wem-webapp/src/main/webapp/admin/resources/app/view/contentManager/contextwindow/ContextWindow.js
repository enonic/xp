/**
 * TODO: Controller? :)
 */
Ext.define('Admin.view.contentManager.contextwindow.ContextWindow', {
    extend: 'Ext.container.Container',
    alias: 'widget.contextWindow',

    requires: [
        'Admin.view.contentManager.contextwindow.Inspector',
        'Admin.view.contentManager.contextwindow.screen.ComponentTypeList',
        'Admin.view.contentManager.contextwindow.screen.Emulator'
    ],

    cls: 'admin-context-window',
    x: 10,
    y: 10,
    width: 318,
    height: 480,

    modal: false,
    floating: true,
    border: false,
    shadow: false,
    layout: {
        type: 'vbox',
        align : 'stretch'
    },

    listeners: {
        resize: function () {
            this.resizeScreenHeights();
        }
    },

    DEFAULT_SCREEN_INDEX: 0,
    TITLE_BAR_HEIGHT: 32,

    selectedScreenIndex: undefined,
    collapsed: false,
    currentHeight: undefined,

    titleBarCt: undefined,
    menuButton: undefined,
    titleTextCmp: undefined,
    toggleButton: undefined,
    screenContainer: undefined,
    inspectorCmp: undefined,
    iFrameMask: undefined,

    liveEditIFrameDom: undefined, // Passed as constructor config

    screens: [
        {
            item: function () {
                return new Admin.view.contentManager.contextwindow.screen.ComponentTypeList({hidden:true});
            }
        },
        {
            item: function () {
                return new Admin.view.contentManager.contextwindow.screen.Emulator({hidden:true});
            }
        },
    ],

    initComponent: function () {
        this.titleBarCt = this.createTitleBarCt();
        this.screenContainer = this.createScreenContainer();
        this.inspectorCmp = new Admin.view.contentManager.contextwindow.Inspector({hidden:true});
        this.items = [
            this.titleBarCt,
            this.screenContainer,
            this.inspectorCmp
        ];
        this.iFrameMask = this.createIFrameMask();

        this.enableWindowDrag();

        this.enableWindowResize();

        this.bindLiveEditEventListeners();

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
        var me = this;
        return new Ext.button.Button({
            text: '',
            arrowCls: '-none',
            cls: 'admin-context-window-menu-button icon-reorder',
            menu: new Ext.menu.Menu({
                border: false,
                plain: true,
                shadow: false,
                cls: 'context-window-menu',
                listeners: {
                    beforeshow: function (menu) {
                        menu.setWidth(me.getWidth());
                    }
                }
            })
        });
    },

    addMenuItem: function (text, index) {
        var me = this;
        var menuItemSpec = {
            plain: true,
            cls: 'context-window-menu-item',
            itemId: index,
            width:'100%',
            text: text,
            handler: function (item) {
                me.showScreen(item.itemId);
            }
        };

        this.menuButton.menu.add(menuItemSpec);
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
    createScreenContainer: function () {
        var me = this;
        return new Ext.container.Container({
            flex: 1,
            height: this.height - this.TITLE_BAR_HEIGHT,
            cls: 'admin-context-window-screens',
            listeners: {
                render: function () {
                    me.addScreens();
                    me.showScreen(me.DEFAULT_SCREEN_INDEX);
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

    showScreen: function (index) {
        var addedScreens = this.screenContainer.items.items,
            screen;

        for (var i = 0; i < addedScreens.length; i++) {
            screen = addedScreens[i];
            if (i == index) {
                screen.show();

                this.setTitleText(screen.screenTitle); // ai, move this
                this.setSelectedScreenIndex(i);
            } else {
                screen.hide();
            }
        }
    },

    showHideInspector: function (show) {
        if (show) {
            this.setTitleText('Inspector');
            this.screenContainer.hide();
            this.inspectorCmp.show();
        } else {
            this.setTitleText(this.screenContainer.items.items[this.selectedScreenIndex].screenTitle);
            this.screenContainer.show();
            this.inspectorCmp.hide();
        }
    },

    addScreens: function () {
        // Should this function add menu items too?
        var key,
            screen;
        for (key in this.screens) {
            if (this.screens.hasOwnProperty(key)) {
                screen = this.screens[key].item();
                this.screenContainer.add(screen);
                this.addMenuItem(screen.screenTitle, key);
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

    resizeScreenHeights: function () {
        var addedScreens = this.screenContainer.items.items,
            newHeight = this.height - this.TITLE_BAR_HEIGHT;

        for (var i = 0; i < addedScreens.length; i++) {
            addedScreens[i].setHeight(newHeight);
        }
    },

    bindLiveEditEventListeners: function () {
        var me = this,
        // Right now We need to use the jQuery object from the live edit page in order to listen for the events
            liveEditWindow = me.getLiveEditContentWindowObject(),
            liveEditJQuery = me.getLiveEditJQuery();

        liveEditJQuery(liveEditWindow).on('selectComponent.liveEdit', function (event) {
            me.showHideInspector(true);
        });
        liveEditJQuery(liveEditWindow).on('deselectComponent.liveEdit', function (event) {
            if (me.selectedScreenIndex != undefined) {
                me.showHideInspector(false);
            }
        });
        liveEditJQuery(liveEditWindow).on('componentRemoved.liveEdit', function (event) {
            if (me.selectedScreenIndex != undefined) {
                me.showHideInspector(false);
            }
        });
    },

    setSelectedScreenIndex: function (index) {
        this.selectedScreenIndex = index;
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
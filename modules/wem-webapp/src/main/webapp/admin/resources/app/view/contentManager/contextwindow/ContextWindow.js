/**
 * TODO: Controller? :)
 */
Ext.define('Admin.view.contentManager.contextwindow.ContextWindow', {
    extend: 'Ext.container.Container',
    alias: 'widget.contextWindow',

    requires: [
        'Admin.view.contentManager.contextwindow.TitleBar',
        'Admin.view.contentManager.contextwindow.list.ComponentTypeList',
        'Admin.view.contentManager.contextwindow.emulator.Emulator',
        'Admin.view.contentManager.contextwindow.inspector.Inspector'
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

    TITLE_BAR_HEIGHT: 32,

    titleBar: undefined,

    listContainer: undefined,

    emulatorContainer: undefined,

    inspectorContainer: undefined,

    containers: {
        'LIST': 0,
        'EMULATOR': 1,
        'INSPECTOR': 2
    },

    iFrameMask: undefined,

    liveEditIFrameDom: undefined, // Passed as constructor config

    initComponent: function () {
        var me = this;
        this.titleBar = new Admin.view.contentManager.contextwindow.TitleBar({
            height: this.TITLE_BAR_HEIGHT,
            contextWindow: this,
            currentWindowHeight: this.height
        });

        // fixme: move to TitleBar
        this.titleBar.addMenuItem('Insert', function () {
            me.displayContainer(me.containers.LIST);
        });
        this.titleBar.addMenuItem('Emulator', function () {
            me.displayContainer(me.containers.EMULATOR)
        });
        this.titleBar.addMenuItem('Inspector', function () {
            me.displayContainer(me.containers.INSPECTOR)
        });

        this.listContainer = this.createListContainer();
        this.emulatorContainer = new Admin.view.contentManager.contextwindow.emulator.Emulator({hidden: true, contextWindow: this});
        this.inspectorContainer = new Admin.view.contentManager.contextwindow.inspector.Inspector({hidden: true, contextWindow: this});

        this.items = [
            this.titleBar,
            this.listContainer,
            this.emulatorContainer,
            this.inspectorContainer
        ];

        this.iFrameMask = this.createIFrameMask();

        this.enableWindowDrag();
        this.enableWindowResize();
        this.bindLiveEditEventListeners();

        this.addListener('resize', function () {
            me.resizeContainerHeights();
        });

        this.addListener('afterrender', function () {
            me.loadList('Admin.view.contentManager.contextwindow.list.ComponentTypeList');
        });

        this.callParent(arguments);
    },

    /**
     * @returns {Ext.container.Container}
     */
    createListContainer: function () {
        return new Ext.container.Container({
            flex: 1,
            height: this.height - this.TITLE_BAR_HEIGHT,
            cls: 'admin-context-window-list-container'
        });
    },

    loadList: function (classPath) {
        var list = Ext.create(classPath, {contextWindow: this});

        this.listContainer.removeAll();
        this.listContainer.add(list);
        this.listContainer.doLayout();
        this.titleBar.setTitleText(list.title)
    },

    loadInspector: function (classPath) {
        /**/
    },

    displayContainer: function (containerIndex) {
        var me = this,
            containers = this.items.items,
            container;
        for(var i = 0; i < containers.length; i++) {
            // Skip title bar
            if (i == 0) {
                continue;
            }
            container = containers[i];

            if (i == containerIndex + 1) {
                container.show();
                container.doLayout();
                me.titleBar.setTitleText(container.title);

            } else {
                container.hide();
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

    resizeContainerHeights: function () {
        /*
        var containers = this.items.items,
            newHeight = this.height - this.TITLE_BAR_HEIGHT;

        for(var i = 0; i < containers.length; i++) {
            // Skip title bar
            if (i == 0) {
                continue;
            }
            containers[i].setHeight(newHeight);

            // fixme: dirty fix for list container
            if (containers[i].items && containers[i].items.items) {
                containers[i].items.items[0].setHeight(newHeight);
            }
        }
        */
    },

    bindLiveEditEventListeners: function () {
        var me = this,
            liveEditWindow = me.getLiveEditContentWindowObject(),
            liveEditJQuery = me.getLiveEditJQuery();

        liveEditJQuery(liveEditWindow).on('selectComponent.liveEdit', function (jQueryEvent, component) {
            me.onSelectComponent(component);
        });

        liveEditJQuery(liveEditWindow).on('deselectComponent.liveEdit', function (jQueryEvent) {
            me.onDeSelectComponent();
        });

        liveEditJQuery(liveEditWindow).on('componentRemoved.liveEdit', function (jQueryEvent) {
            me.displayContainer(me.containers.LIST);
        });
    },

    onSelectComponent: function (component) {
        var me = this,
            componentType = component.getComponentType().getType();

        if (component.isEmpty()) {
            me.loadList('Admin.view.contentManager.contextwindow.list.ComponentList');

        } else {
            me.displayContainer(me.containers.INSPECTOR);
        }
    },

    onDeSelectComponent: function () {
        var me = this;
        this.displayContainer(me.containers.LIST);
        this.loadList('Admin.view.contentManager.contextwindow.list.ComponentTypeList');
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
Ext.define('Admin.view.contentManager.contextwindow.inspector.Inspector', {
    extend: 'Ext.container.Container',
    alias: 'widget.contextWindowInspector',
    uses: 'Admin.view.contentManager.contextwindow.Helper',

    title: 'Inspector',

    tpl: new Ext.XTemplate(
        '<div class="admin-inspector-info-container">',
        '   <div class="admin-inspector-icon {componentTypeIconCls}"><!-- --></div>',
        '   <div class="admin-inspector-info">',
        '       <h3>{componentTypeName}</h3>',
        '       <div>{componentName}</div>',
        '       <div>Width: {width}px</div>',
        '       <div>Height: {height}px</div>',
        '   </div>',
        '</div>'
    ),

    listeners: {
        render: function () {
            this.registerListenersFromLiveEditPage();
        }
    },

    initComponent: function () {
        this.callParent(arguments);
    },

    registerListenersFromLiveEditPage: function () {
        var me = this,
        // Right now We need to use the jQuery object from the live edit page in order to listen for the events
            contextWindow = me.getContextWindow(),
            liveEditWindow = contextWindow.getLiveEditContentWindowObject(),
            liveEditJQuery = contextWindow.getLiveEditJQuery();

        liveEditJQuery(liveEditWindow).on('selectComponent.liveEdit', function (jQueryEvent, component) {
            me.displayComponentInfo(component);
        });

        liveEditJQuery(liveEditWindow).on('deselectComponent.liveEdit', function () {
            me.clearComponentInfo();
        });
    },

    displayComponentInfo: function (component) {
        var me = this,
            componentType = component.getComponentType(),
            dimensions = component.getElementDimensions(),
            data = {
                componentTypeName: componentType.getName(),
                componentName: component.getName(),
                componentTypeIconCls: componentType.getIconCls(),
                width: dimensions.width,
                height: dimensions.height
            };
        me.tpl.overwrite(me.getEl(), data);
    },

    clearComponentInfo: function () {
        var me = this;
        me.tpl.overwrite(me.getEl(), null);
    },

    getContextWindow: function () {
        return this.up('contextWindow');
    }

});
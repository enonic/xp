Ext.define('Admin.view.contentManager.contextwindow.panel.Inspector', {
    extend: 'Ext.Component',
    alias: 'widget.contextWindowInspector',
    uses: 'Admin.view.contentManager.contextwindow.Helper',

    tpl: new Ext.XTemplate(
        '<div class="admin-inspector-info-container">',
        '   <div class="admin-inspector-icon {componentTypeIconCls}"><!-- --></div>',
        '   <div class="admin-inspector-info">',
        '       <h3>{componentType}</h3>',
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
            componentDom = component[0], // Component is a jQuert object
            type = componentDom.getAttribute('data-live-edit-type'),
            name = componentDom.getAttribute('data-live-edit-name'),
            width = Ext.fly(componentDom).getWidth(),
            height = Ext.fly(componentDom).getHeight(),
            iconCls = Admin.view.contentManager.contextwindow.Helper.resolveComponentTypeIconCls(type),
            data = {
                componentType: type,
                componentName: name,
                componentTypeIconCls: iconCls,
                width: width,
                height: height
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
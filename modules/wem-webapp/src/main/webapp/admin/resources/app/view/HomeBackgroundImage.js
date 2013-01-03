Ext.define('Admin.view.HomeBackgroundImage', {
    extend: 'Ext.Component',
    alias: 'widget.homeBackgroundImage',
    floating: true,
    style: 'background-image:url("webapp/admin/resources/images/mont_blanc.jpg")',
    renderTo: Ext.getBody(),

    initComponent: function () {
        var me = this;

        console.log('home background image init');

        this.callParent(arguments);
    }

});

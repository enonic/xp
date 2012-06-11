/**
* @class Admin.plugin.PageSizePlugin
*/
Ext.define('Admin.plugin.PageSizePlugin', {
    extend      : 'Ext.form.field.ComboBox',
    alias       : 'plugin.pageSize',
    editable    : false,
    beforeText  : 'Show',
    afterText   : 'rows per page',
    mode        : 'local',
    displayField: 'text',
    valueField  : 'value',
    allowBlank  : false,
    triggerAction: 'all',
    width       : 50,
    maskRe      : /[0-9]/,
    /**
    * initialize the paging combo after the pagebar is randered
    */
    init: function(paging) {
        paging.on('afterrender', this.onInitView, this);
    },
    /**
    * create a local store for availabe range of pages
    */
    store: new Ext.data.SimpleStore({
        fields: ['text', 'value'],
        data: [['10', 10], ['50', 50], ['100', 100]]
    }),
    /**
    * assing the select and specialkey events for the combobox
    * after the pagebar is rendered.
    */
    onInitView: function(paging) {
        var me = this;
        var defaultValue = paging.store.pageSize ? paging.store.pageSize : 10;
        paging.items.insert(11, this);
        this.on('select', this.onPageSizeChanged, paging);
        this.on('specialkey', function(combo, e) {
            if(13 === e.getKey()) {
                this.onPageSizeChanged.call(paging, this);
            }
        });
        this.store.on("load", function() {
            me.setValue(defaultValue);
        });
        this.store.load();
    },
    /**
    * refresh the page when the value is changed
    */
    onPageSizeChanged: function(combo) {
        this.store.pageSize = parseInt(combo.getRawValue());
        this.doRefresh();
    }
});

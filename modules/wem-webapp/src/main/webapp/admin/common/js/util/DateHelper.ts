Ext.define('Admin.lib.DateHelper', {

    singleton: true,

    addHours: function (date, offset) {
        date.setHours(date.getHours() + offset);
    }
});
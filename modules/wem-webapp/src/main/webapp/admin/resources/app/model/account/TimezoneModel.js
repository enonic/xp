Ext.define('Admin.model.account.TimezoneModel', {
    extend: 'Ext.data.Model',

    fields: [
        'id',
        'humanizedId',
        'shortName',
        'name',
        'offset',
        {
            name: 'humanizedIdAndOffset',
            convert: function(value, record) {
                return record.get('humanizedId') + ' (' + record.get('offset') + ')';
            }
        }
    ]
});

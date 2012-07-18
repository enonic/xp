Class('AdminTestUtil', {

    isa: Siesta.Test.ExtJS,

    methods: {
        createUserModel: function () {
            var Ext = this.global.Ext;

            return Ext.create('Admin.model.account.AccountModel', {
                'key': '04D3731B72BFF823BCF0C00604ED8FA85E7B7D69',
                'name': 'jsi',
                'email': 'jsi@enonic.com',
                'qualifiedName': 'enonic\\jsi',
                'displayName': 'J\u00F6rgen Sivesind',
                'userStore': 'enonic',
                'lastModified': '2009-11-05 10:50:04',
                'hasPhoto': true,
                'type': 'user',
                'builtIn': false,
                'isEditable': true
            });
        }
    }
});
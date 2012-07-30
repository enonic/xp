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
        },

        createGroupModel: function () {
            var Ext = this.global.Ext;

            return Ext.create('Admin.model.account.GroupModel', {
                'key': 'SJH976ASD789795AS69AS69AS85GF756',
                'name': 'winners',
                'userStore': 'default',
                'type': 'role'
            });
        },

        createLanguageModel: function () {
            var Ext = this.global.Ext;

            return Ext.create('Admin.model.account.LanguageModel', {
                'key': 'SDF79SF79SF798SFA6DH9GJF07G90HJ7FG9',
                'languageCode': 'EN',
                'description': 'English',
                'lastModified': '2000-01-01 12:00:00'
            });
        },

        createLocaleModel: function () {
            var Ext = this.global.Ext;

            return Ext.create('Admin.model.account.LocaleModel', {
                'id': '54GJH3G64G6734LG6LK3456HL43G7L34G73L4K',
                'displayName': 'Barbados'
            });
        },

        createTimezoneModel: function () {
            var Ext = this.global.Ext;

            return Ext.create('Admin.model.account.TimezoneModel', {
                'id': '123',
                'humanizedId': 'GMT',
                'shortName': 'EEST',
                'name': 'Eastern',
                'offset': '+3'
            });
        },

        createContentManagerModel: function () {
            var Ext = this.global.Ext;

            return Ext.create('Admin.model.contentManager.ContentModel', {
                'key': '44JH23K56LKJ4252JL5H23L5JKH23L5JK23GL',
                'name': 'Form',
                'type': 'form',
                'owner': 'admin',
                'url': 'http://localhost:8080',
                'lastModified': '2000-01-01'
            });
        },

        createContentTypeModel: function () {
            var Ext = this.global.Ext;

            return Ext.create('Admin.model.datadesigner.ContentTypeModel', {
                'key': '89FDS79F87SD9F7SD89F798A7FA0',
                'type': 'article',
                'extends': 'base',
                'created': '2000-01-01',
                'lastModified': '2011-01-01',
                'name': 'News',
                'displayName': 'Hot News',
                'module': 'whatever',
                'configXml': '<config></config>',
                'usageCount': 120,
                'icon': 'blank_doc.jpg'
            });
        },

        createUserstoreConfigModel: function () {
            var Ext = this.global.Ext;

            return Ext.create('Admin.model.userstore.UserstoreConfigModel', {
                'key': 1,
                'name': 'default',
                'defaultStore': true,
                'connectorName': 'local',
                'configXML': '<config></config>',
                'lastModified': '2012-01-01'
            });
        },

        createUserstoreConnectorModel: function () {
            var Ext = this.global.Ext;

            return Ext.create('Admin.model.userstore.UserstoreConnectorModel', {
                'name': 'AD',
                'pluginType': 'dll',
                'canCreateUser': true,
                'canUpdateUser': true,
                'canUpdateUserPassword': true,
                'canDeleteUser': true,
                'canCreateGroup': true,
                'canUpdateGroup': true,
                'canReadGroup': true,
                'canDeleteGroup': true,
                'groupsLocal': true
            });
        },

        createUserFieldModel: function () {
            var Ext = this.global.Ext;

            return Ext.create('Admin.model.account.UserFieldModel', {
                'type': 'text',
                'readOnly': true,
                'required': true,
                'remote': true,
                'iso': true
            });
        }
    }
});

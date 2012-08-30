Ext.define('Admin.lib.UriHelper', {

    singleton: true,

    // UriHelper generates getters from the uris object
    // in the following format: get + <Module> + <Entity> + Uri()
    // if Entity is a string it is returned, if it is a function - called with arguments of getter,
    // if an object - the 'selector' function will be called with arguments of getter

    uris: {
        Account: {
            Search: 'admin/rest/account',
            Country: 'admin/rest/misc/country',
            Timezone: 'admin/rest/misc/timezone',
            Locale: 'admin/rest/misc/locale',
            Delete: function (account) {
                return Ext.String.format('admin/rest/account/{0}/delete', account.key);
            },
            Info: function (account) {
                return 'admin/rest/' + account.info_uri;
            },
            Graph: function (account) {
                return 'admin/rest/' + account.graph_uri;
            },
            Icon: function (account) {
                return 'admin/rest/' + account.image_uri;
            },
            VerifyEmail: 'admin/rest/account/user/verify-unique-email',
            ChangePassword: function (account) {
                return Ext.String.format("admin/rest/account/user/{0}/change-password", account.key);
            }
        },
        Userstore: {
            Search: 'admin/rest/userstore/search'
        },
        ContentManager: {
            Search: 'admin/resources/data/contentManagerStub.json',
            SearchTree: 'admin/resources/data/contentManagerTreeStub.json'
        }
    },

    constructor: function () {
        var module, moduleName, entity, entityName;
        for (moduleName in this.uris) {
            if (this.uris.hasOwnProperty(moduleName)) {
                module = this.uris[moduleName];
                for (entityName in module) {
                    if (module.hasOwnProperty(entityName)) {
                        entity = module[entityName];
                        this.addMethod(this, 'get' + moduleName + entityName + "Uri", this.getUri(module, entity));
                    }
                }
            }
        }
    },

    getUri: function (module, entity) {
        var me = this;
        return function () {
            var uri;
            if (Ext.isString(entity)) {
                uri = entity;
            } else if (Ext.isFunction(entity)) {
                uri = entity.apply(this, arguments);
            } else if (Ext.isObject(entity) && Ext.isFunction(entity.selector)) {
                uri = entity.selector.apply(this, arguments);
            }
            return me.getAbsoluteUri(uri);
        };
    },

    getAbsoluteUri: function (uri) {
        return window.CONFIG.baseUrl + '/' + uri;
    },

    // addMethod - By John Resig (MIT Licensed)
    addMethod: function (object, name, fn) {
        var old = object[name];
        if (old) {
            object[name] = function () {
                if (fn.length === arguments.length) {
                    return fn.apply(this, arguments);
                } else if (typeof old === 'function') {
                    return old.apply(this, arguments);
                }
            };
        } else {
            object[name] = fn;
        }
    }

});

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
                return Ext.String.format('/admin/rest/account/{0}/delete', account.key);
            },
            Info: function (account) {
                return Ext.String.format('/admin/rest/account/{0}/{1}', account.type, account.key);
            },
            Graph: function (account) {
                return Ext.String.format('/admin/rest/account/graph/{0}', account.key);
            },
            Icon: {
                user: 'admin/rest/account/user/{0}/photo?size={1}',
                anonymous: 'admin/resources/images/icons/128x128/ghost.png',
                admin: 'admin/resources/images/icons/128x128/superhero.png',
                group: 'admin/resources/images/icons/128x128/group.png',
                role: 'admin/resources/images/icons/128x128/masks.png',
                selector: function (account, size) {
                    //TODO: take into account size param
                    var url;
                    switch (account.type) {
                    case 'user':
                        if (account.builtIn && account.name === 'admin') {
                            url = this.uris.Account.Icon.admin;
                        } else if (account.builtIn && account.name === 'anonymous') {
                            url = this.uris.Account.Icon.anonymous;
                        } else {
                            url = Ext.String.format(this.uris.Account.Icon.user, account.key, size || 100);
                        }
                        break;
                    case 'group':
                        url = this.uris.Account.Icon.group;
                        break;
                    case 'role':
                        url = this.uris.Account.Icon.role;
                        break;
                    }
                    return url;
                }
            }
        },
        Userstore: {
            Search: 'admin/rest/userstore/search'
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
        var currentLocation = window.location;
        if (Ext.isEmpty(uri)) {
            return "";
        }
        var currentPath = currentLocation.pathname.substring(0, currentLocation.pathname.lastIndexOf("/"));
        if (uri.charAt(0) !== "/") {
            uri = "/" + uri;
        }
        var targetLocation = this.getLocationFromUri(uri);
        var targetPath = targetLocation.pathname;

        var targetUri = currentLocation.protocol + "//" + currentLocation.host;

        var currentSegments = currentPath.split("/");
        var targetSegments = targetPath.split("/");

        var i, j;
        // add current segments one by one until we see match with target segments
        for (i = 1; i < currentSegments.length; i++) {
            var currentSegment = currentSegments[i];
            var contains = false;
            for (j = 1; j < targetSegments.length; j++) {
                if (targetSegments[j] === currentSegment) {
                    contains = true;
                    break;
                }
            }
            if (!contains) {
                targetUri += "/" + currentSegment;
            } else {
                break;
            }
        }

        targetUri += targetPath;

        return targetUri;
    },

    getLocationFromUri: function (uri) {
        var a = document.createElement("a");
        a.href = uri;
        return {
            href: a.href,
            host: a.host,
            hostname: a.hostname,
            port: a.port,
            pathname: a.pathname,
            protocol: a.protocol,
            hash: a.hash,
            search: a.search
        };
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

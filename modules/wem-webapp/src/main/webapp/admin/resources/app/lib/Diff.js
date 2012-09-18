Ext.define('Admin.lib.Diff', {

    singleton: true,

    defaultUserStore: 'default',

    userFieldsets: {
        'profile': ['firstName', 'middleName', 'lastName', 'organization', 'homePage', 'fax', 'mobile', 'phone'],
        'user': ['username', 'email', 'password', 'repeatPassword', 'country', 'locale', 'timezone', 'globalPosition'],
        'places': ['address'],
        'memberships': ['membership']
    },
    groupFieldsets: {
        general: ['public', 'description'],
        members: ['members']
    },
    userstoreFieldsets: {
        general: ['name', 'remote', 'connectorName'],
        config: ['configXML'],
        admin: [ 'administrators' ]
    },

    fieldLabels: {
        membership: 'Member of'
    },

    hideFields: [ 'repeatPassword' ],
    hideProperties: [ 'index', 'originalIndex' ],

    usingUserstoreConfig: false,


    /*      public      */

    compare: function (type, newOne, oldOne, changedOnly) {
        if (type !== 'user' && type !== 'group' && type !== 'userstore') {
            throw new Error('Unknown type for comparison: ' + type);
        }
        switch (type) {
        case 'user':
            return this.compareUsers(newOne, oldOne, changedOnly);
        case 'group':
            return this.compareGroups(newOne, oldOne, changedOnly);
        case 'userstore':
            return this.compareUserstores(newOne, oldOne, changedOnly);
        default:
            return {};
        }
    },

    compareUsers: function (newOne, oldOne, changedOnly) {

        if (newOne === null) {
            return;
        }

        var comparison = {
            "expanded": true,
            "children": [
                {
                    label: "1. Profile",
                    fieldsetType: "profile",
                    expanded: true,
                    leaf: false
                },
                {
                    label: "2. User",
                    fieldsetType: "user",
                    expanded: true,
                    leaf: false
                },
                {
                    label: "3. Places",
                    fieldsetType: "places",
                    expanded: true,
                    leaf: false
                },
                {
                    label: "4. Memberships",
                    fieldsetType: "memberships",
                    expanded: true,
                    leaf: false
                }
            ]
        };
        var props = [];

        var userstores = Ext.data.StoreManager.lookup('Admin.store.account.UserstoreConfigStore');
        if (userstores) {
            // compare based on userstore config, assuming both users are from the same store if present
            var userstoreName = (newOne.userStore && (!oldOne || oldOne.userStore === newOne.userStore)) ? newOne.userStore
                : this.defaultUserStore;
            var userstore = userstores.findRecord('name', userstoreName);
            if (userstore && userstore.raw.userFields) {
                this.usingUserstoreConfig = true;
                props = props.concat(userstore.raw.userFields);
                // add static properties
                var staticProps = this.getUserStaticProperties();
                if (staticProps) {
                    props = props.concat(staticProps);
                }
            }
        }

        if (props.length === 0) {
            // fall back to comparing objects properties
            props = props.concat(this.collectUniqueProperties(newOne, oldOne, true));
        }

        if (props.length > 0) {
            comparison.children = this.compareProperties('user', comparison.children, props, newOne, oldOne, changedOnly);
        }

        // filter empty fieldsets
        comparison.children = this.filterEmptyNodes(comparison.children);

        return comparison;
    },

    compareUserstores: function (newOne, oldOne, changedOnly) {
        if (newOne === null) {
            return;
        }

        var comparison = {
            "expanded": true,
            "children": [
                {
                    label: "1. General",
                    fieldsetType: "general",
                    expanded: true,
                    leaf: false
                },
                {
                    label: "2. Config",
                    fieldsetType: "config",
                    expanded: true,
                    leaf: false
                },
                {
                    label: "3. Administrators",
                    fieldsetType: "admin",
                    expanded: true,
                    leaf: false
                }
            ]
        };
        var props = [];

        //TODO need to decide it to use static proprs or fall back to object props comparison
        // add static properties
        var staticProps = this.getUserstoreStaticProperties();
        if (staticProps) {
            props = props.concat(staticProps);
        }

        if (props.length === 0) {
            // fall back to comparing objects properties
            props = props.concat(this.collectUniqueProperties(newOne, oldOne, true));
        }

        if (props.length > 0) {
            comparison.children = this.compareProperties('userstore', comparison.children, props, newOne, oldOne, changedOnly);
        }

        // filter empty fieldsets
        comparison.children = this.filterEmptyNodes(comparison.children);

        return comparison;
    },

    compareGroups: function (newOne, oldOne, changedOnly) {
        if (newOne === null) {
            return;
        }

        var comparison = {
            "expanded": true,
            "children": [
                {
                    label: "1. General",
                    fieldsetType: "general",
                    expanded: true,
                    leaf: false
                },
                {
                    label: "2. Members",
                    fieldsetType: "members",
                    expanded: true,
                    leaf: false
                }
            ]
        };
        var props = [];

        var userstores = Ext.data.StoreManager.lookup('Admin.store.account.UserstoreConfigStore');
        if (userstores) {
            // compare based on userstore config, assuming both users are from the same store if present
            var userstoreName = (newOne.userStore && (!oldOne || oldOne.userStore === newOne.userStore)) ? newOne.userStore
                : this.defaultUserStore;
            var userstore = userstores.findRecord('name', userstoreName);
            if (userstore && userstore.raw.userFields) {
                this.usingUserstoreConfig = true;
                props = props.concat(userstore.raw.userFields);
                // add static properties
                var staticProps = this.getGroupStaticProperties();
                if (staticProps) {
                    props = props.concat(staticProps);
                }
            }
        }

        if (props.length === 0) {
            // fall back to comparing objects properties
            props = props.concat(this.collectUniqueProperties(newOne, oldOne, true));
        }

        if (props.length > 0) {
            comparison.children = this.compareProperties('group', comparison.children, props, newOne, oldOne, changedOnly);
        }

        // filter empty fieldsets
        comparison.children = this.filterEmptyNodes(comparison.children);

        return comparison;
    },


    /*      private     */

    collectUniqueProperties: function (newOne, oldOne, asMap) {
        var duplicateProps = [].concat(this.collectProperties(oldOne, asMap), this.collectProperties(newOne, asMap));
        return this.filterProperties(duplicateProps, asMap);
    },

    collectProperties: function (obj, asMap) {
        var list = [];
        var prop;
        if (Ext.isObject(obj)) {
            for (prop in obj) {
                if (obj.hasOwnProperty(prop) && !this.isPropertyHidden(prop)) {
                    list.push(asMap ? {
                        iso: false,
                        readOnly: true,
                        remote: false,
                        required: false,
                        type: prop
                    } : prop);
                }
            }
        }
        return list;
    },

    filterProperties: function (duplicateProps, asMap) {
        var props = [];
        if (asMap) {
            var contains = false;
            Ext.Array.forEach(duplicateProps, function (duplicateProp) {
                contains = false;
                Ext.Array.forEach(props, function (prop) {
                    if (prop.type === duplicateProp.type) {
                        contains = true;
                        return false;
                    }
                });
                if (!contains) {
                    props.push(duplicateProp);
                }
            });
        } else {
            props = Ext.Array.unique(duplicateProps);
        }
        return props;
    },

    compareProperties: function (accountType, resultList, propertiesList, newOne, oldOne, changedOnly) {

        Ext.Array.each(propertiesList, function (prop, index, props) {

            if (!this.isFieldHidden(prop.type)) {
                var newVal = this.getValue(newOne, prop.type);
                var oldVal = this.getValue(oldOne, prop.type);
                var changeType = this.getChangeType(oldVal, newVal);

                if ((!changedOnly || changeType !== 'none') && (!Ext.isEmpty(newVal) || !Ext.isEmpty(oldVal))) {
                    var item = this.createItem(prop.type, oldVal, newVal, changeType);
                    var fieldset = this.getFieldset(accountType, resultList, prop.type);
                    if (fieldset) {
                        if (!Ext.isDefined(fieldset.children)) {
                            fieldset.children = [];
                        }
                        item = this.handleSpecialCase(prop.type, item);
                        if (item) {
                            fieldset.children = fieldset.children.concat(item);
                        }
                    } else {
                        Ext.Array.insert(resultList, 0, [item]);
                    }
                }
            }

        }, this);
        return resultList;
    },


    createItem: function (name, oldVal, newVal, changeType) {
        var item;
        if (Ext.isArray(oldVal) || Ext.isArray(newVal)) {
            item = {
                label: this.getLabel(name),
                fieldType: name,
                newValue: newVal ? newVal.length + " item(s)" : undefined,
                previousValue: oldVal ? oldVal.length + " item(s)" : undefined,
                changeType: changeType,
                expanded: true,
                leaf: false,
                children: this.createArray(name, oldVal, newVal)
            };

        } else if (Ext.isObject(oldVal) || Ext.isObject(newVal)) {
            item = {
                label: this.getLabel(name),
                fieldType: name,
                newValue: Ext.isObject(newVal) ? '[' + newVal.index + ']' : newVal,
                previousValue: Ext.isObject(oldVal) ? '[' + oldVal.index + ']' : oldVal,
                changeType: changeType,
                expanded: true,
                leaf: false,
                children: this.createObject(oldVal, newVal)
            };
        } else {
            item = {
                label: this.getLabel(name),
                fieldType: name,
                newValue: Ext.htmlEncode(newVal),
                previousValue: Ext.htmlEncode(oldVal),
                changeType: changeType,
                leaf: true
            };
        }
        return item;
    },

    createArray: function (name, oldOne, newOne) {
        var list = [];
        var newVal, oldVal, changeType, i, j, k;

        var processedItems = [];

        // remember objects indexes in array for getCorrespondingItem to track em
        if (oldOne) {
            for (i = 0; i < oldOne.length; i++) {
                oldOne[i].index = i;
            }
        }

        if (newOne) {
            for (j = 0; j < newOne.length; j += 1) {
                newVal = newOne[j];
                newVal.index = j;
                oldVal = this.getCorrespondingItem(oldOne, newVal);
                changeType = this.getChangeType(oldVal, newVal);
                if (oldVal) {
                    processedItems.push(oldVal);
                }
                list.push(this.createItem(name, oldVal, newVal, changeType));
            }
        }
        // need to iterate over old one as well,
        // because there can be deleted items,
        // which hasn't been processed yet
        if (oldOne) {
            for (k = 0; k < oldOne.length; k += 1) {
                oldVal = oldOne[k];
                if (!Ext.Array.contains(processedItems, oldVal)) {
                    newVal = this.getCorrespondingItem(newOne, oldVal);
                    changeType = this.getChangeType(oldVal, newVal);
                    list.push(this.createItem(name, oldVal, newVal, changeType));
                }
                delete oldVal.index;
            }
        }

        // delete indexes
        if (newOne) {
            for (i = 0; i < newOne.length; i++) {
                delete newOne[i].index;
            }
        }

        return list;
    },

    createObject: function (oldOne, newOne) {
        var list = [];
        var p, prop, newVal, oldVal, changeType;
        var props = this.collectUniqueProperties(newOne, oldOne);
        for (p = 0; p < props.length; p += 1) {
            prop = props[p];
            if (!this.isPropertyHidden(prop)) {
                newVal = this.getValue(newOne, prop);
                oldVal = this.getValue(oldOne, prop);
                changeType = this.getChangeType(oldVal, newVal);
                list.push(this.createItem(prop, oldVal, newVal, changeType));
            }
        }
        return list;
    },


    filterEmptyNodes: function (array) {
        return Ext.Array.filter(array, function (prop, index, all) {
            return prop.leaf || (!prop.leaf && prop.children && prop.children.length > 0);
        });
    },

    handleSpecialCase: function (name, item) {
        var i;
        switch (name) {
        case 'address':
            // special case for addresses which need to be added directly to fieldset
            if (!item.children || item.children.length === 0) {
                return undefined;
            } else {
                return item.children;
            }
        case 'members':
        case 'administrators':
        case 'membership':
            // special case for members which need to be shown as summary
            var added = 0, removed = 0, modified = 0;
            for (i = 0; i < item.children.length; i++) {
                switch (item.children[i].changeType) {
                case 'added':
                    added += 1;
                    break;
                case 'removed':
                    removed += 1;
                    break;
                case 'modified':
                    modified += 1;
                    break;
                }
            }
            var value = item.children.length + ' Account(s)';
            if (added > 0 || removed > 0 || modified > 0) {
                value += ' (' + (added > 0 ? added + ' added' + (removed > 0 || modified > 0 ? ',' : '') : '')
                             + (removed > 0 ? removed + ' removed' + (modified > 0 ? ',' : '') : '')
                             + (modified > 0 ? modified + ' modified' : '') + ')';
            }
            value += ' &ndash; <a href="#" class="admin-summary-show-details-link">Details</a>';
            item.newValue = value;
            item.leaf = true;
            delete item.children;
            break;
        }
        return item;
    },

    getUserStaticProperties: function () {
        var props = [];
        if (Admin && Admin.view && Admin.view.account &&
            Admin.view.account.EditUserFormPanel &&
            Admin.view.account.EditUserFormPanel.staticFields) {
            props = props.concat(Admin.view.account.EditUserFormPanel.staticFields);
        }
        props.push({
            type: 'membership',
            required: true,
            remote: false,
            readonly: false,
            iso: true
        });
        return props;
    },

    getUserstoreStaticProperties: function () {
        return [
            {
                type: 'name',
                required: true,
                remote: false,
                readonly: false,
                iso: true
            },
            {
                "type": "remote",
                "readOnly": false,
                "required": false,
                "remote": false,
                "iso": true
            },
            {
                "type": "connectorName",
                "readOnly": false,
                "required": false,
                "remote": false,
                "iso": true
            },
            {
                "type": "configXML",
                "readOnly": false,
                "required": false,
                "remote": false,
                "iso": true
            },
            {
                "type": "administrators",
                "readOnly": false,
                "required": false,
                "remote": false,
                "iso": true
            }
        ];
    },

    getGroupStaticProperties: function () {
        return [
            {
                type: 'displayName',
                required: true,
                remote: false,
                readonly: false,
                iso: true
            },
            {
                "type": "public",
                "readOnly": false,
                "required": false,
                "remote": false,
                "iso": true
            },
            {
                "type": "description",
                "readOnly": false,
                "required": false,
                "remote": false,
                "iso": true
            },
            {
                "type": "members",
                "readOnly": false,
                "required": false,
                "remote": false,
                "iso": true
            }
        ];
    },

    isPropertyHidden: function (name) {
        var i;
        for (i = 0; i < this.hideProperties.length; i++) {
            if (this.hideProperties[i] === name) {
                return true;
            }
        }
        return false;
    },

    isFieldHidden: function (name) {
        var i;
        for (i = 0; i < this.hideFields.length; i++) {
            if (this.hideFields[i] === name) {
                return true;
            }
        }
        return false;
    },


    getCorrespondingItem: function (array, oldOne) {
        if (!Ext.isArray(array) || !Ext.isDefined(oldOne)) {
            return undefined;
        }
        var i;
        var key = oldOne.key;
        var originalIndex = parseInt(oldOne.originalIndex, 10);
        var newOne;
        for (i = 0; i < array.length; i++) {
            newOne = array[i];
            if ((Ext.isDefined(key) && newOne && newOne.key === key) ||
                (Ext.isDefined(originalIndex) && newOne.originalIndex === originalIndex) ||
                this.itemsEqual(newOne, oldOne)) {
                return newOne;
            }
        }
        return undefined;
    },

    getFieldset: function (accountType, list, name) {
        var fs, f;
        var fsName = this.getFieldsetName(accountType, name);
        if (fsName) {
            for (f = 0; f < list.length; f += 1) {
                fs = list[f];
                if (fs.fieldsetType === fsName) {
                    return fs;
                }
            }
        }
        return undefined;
    },

    getFieldsetName: function (accountType, name) {
        var fsName, fs, f;
        var accounts;
        if (this[accountType + 'Fieldsets']) {
            accounts = this[accountType + 'Fieldsets'];
        } else {
            accounts = this.userFieldsets;
        }
        for (fsName in accounts) {
            if (accounts.hasOwnProperty(fsName)) {
                fs = accounts[fsName];
                for (f = 0; f < fs.length; f += 1) {
                    if (fs[f] === name) {
                        return fsName;
                    }
                }
            }
        }
        return undefined;
    },

    getValue: function (obj, name) {
        var result;
        if (obj) {
            if (this.usingUserstoreConfig) {
                switch (name) {
                case 'address':
                    name = 'addresses';
                    break;
                case 'membership':
                    name = 'groups';
                    break;
                }
            }
            var info = obj.profile;
            result = info && info[name] ? info[name] : obj[name];
        }
        return result;
    },

    getLabel: function (name) {
        //TODO: need to be changed for i18n
        var label = this.fieldLabels[name];
        if (!label) {
            label = (Admin && Admin.view && Admin.view.account && Admin.view.account.EditUserFormPanel &&
                     Admin.view.account.EditUserFormPanel.fieldLabels && Admin.view.account.EditUserFormPanel.fieldLabels[name]) ?
                Admin.view.account.EditUserFormPanel.fieldLabels[name] : name;
        }
        return label;
    },

    getChangeType: function (oldVal, newVal) {
        var change = "none";
        if (!Ext.isEmpty(newVal) && Ext.isEmpty(oldVal)) {
            change = 'added';
        } else if (Ext.isEmpty(newVal) && !Ext.isEmpty(oldVal)) {
            change = 'removed';
        } else if (!this.itemsEqual(newVal, oldVal)) {
            change = 'modified';
        }
        return change;
    },


    itemsEqual: function (newVal, oldVal) {
        var result = true;
        if (Ext.isArray(newVal) && Ext.isArray(oldVal)) {
            result = this.arraysEqual(newVal, oldVal);
        } else if (Ext.isObject(newVal) && Ext.isObject(oldVal)) {
            result = this.objectsEqual(newVal, oldVal);
        } else {
            result = Ext.encode(newVal) === Ext.encode(oldVal);
        }
        return result;
    },

    arraysEqual: function (a, b) {
        var i;
        if (a.length !== b.length) {
            return false;
        }
        for (i = 0; i < a.length; i++) {
            if (!this.itemsEqual(a[i], b[i])) {
                return false;
            }
        }
        return true;
    },

    objectsEqual: function (a, b) {
        var p;
        for (p in a) {
            if (a.hasOwnProperty(p) && !this.isPropertyHidden(p) && !this.itemsEqual(a[p], b[p])) {
                return false;
            }
        }
        for (p in b) {
            if (b.hasOwnProperty(p) && !this.isPropertyHidden(p) && typeof (a[p]) === 'undefined') {
                return false;
            }
        }
        return true;
    }

});

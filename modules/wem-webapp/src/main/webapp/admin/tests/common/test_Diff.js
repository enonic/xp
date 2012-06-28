var empty = {};

var common = {
    "propertyConstant": "constant property value",
    "propertyChanged": "constant property value",

    "arrayConstant": [
        {
            "key": "key1",
            "value": "constant"
        },
        {
            "key": "key2",
            "value": "array"
        },
        {
            "key": "key3",
            "value": "value"
        }
    ],
    "arrayAdded": [
        {
            "key": "key1",
            "value": "constant"
        },
        {
            "key": "key2",
            "value": "array"
        },
        {
            "key": "key3",
            "value": "value"
        }
    ],
    "arrayChanged": [
        {
            "key": "key1",
            "value": "constant"
        },
        {
            "key": "key2",
            "value": "array"
        },
        {
            "key": "key3",
            "value": "value"
        }
    ],
    "arrayRemoved": [
        {
            "key": "key1",
            "value": "constant"
        },
        {
            "key": "key2",
            "value": "array"
        },
        {
            "key": "key3",
            "value": "value"
        }
    ],
    // reordering using a 'key'
    "arrayReordered": [
        {
            "key": "key1",
            "value": "constant"
        },
        {
            "key": "key2",
            "value": "array"
        },
        {
            "key": "key3",
            "value": "value"
        }
    ],
    "arrayReorderedAdded": [
        {
            "key": "key1",
            "value": "constant"
        },
        {
            "key": "key2",
            "value": "array"
        },
        {
            "key": "key3",
            "value": "value"
        }
    ],
    "arrayReorderedChanged": [
        {
            "key": "key1",
            "value": "constant"
        },
        {
            "key": "key2",
            "value": "array"
        },
        {
            "key": "key3",
            "value": "value"
        }
    ],
    "arrayReorderedRemoved": [
        {
            "key": "key1",
            "value": "constant"
        },
        {
            "key": "key2",
            "value": "array"
        },
        {
            "key": "key3",
            "value": "value"
        }
    ],
    // reordering using an 'originalIndex'
    "arrayReordered2": [
        {
            "value": "constant",
            "originalIndex": 0
        },
        {
            "value": "array",
            "originalIndex": 1
        },
        {
            "value": "value",
            "originalIndex": 2
        }
    ],
    "arrayReorderedAdded2": [
        {
            "value": "constant",
            "originalIndex": 0
        },
        {
            "value": "array",
            "originalIndex": 1
        },
        {
            "value": "value",
            "originalIndex": 2
        }
    ],
    "arrayReorderedChanged2": [
        {
            "value": "constant",
            "originalIndex": 0
        },
        {
            "value": "array",
            "originalIndex": 1
        },
        {
            "value": "value",
            "originalIndex": 2
        }
    ],
    "arrayReorderedRemoved2": [
        {
            "value": "constant",
            "originalIndex": 0
        },
        {
            "value": "array",
            "originalIndex": 1
        },
        {
            "value": "value",
            "originalIndex": 2
        }
    ],
    // reordering of strings or objects, can't figure the order if item was changed, but still can detect added and removed
    "arrayReordered3": [
        "constant",
        "array",
        "value"
    ],
    "arrayReorderedAdded3": [
        "constant",
        "array",
        "value"
    ],
    "arrayReorderedChanged3": [
        "constant",
        "array",
        "value"
    ],
    "arrayReorderedRemoved3": [
        "constant",
        "array",
        "value"
    ],

    "objectConstant": {"constant": true, "object": "foo", "value": 1},
    "objectAdded": {"constant": true, "object": "foo", "value": 1},
    "objectChanged": {"constant": true, "object": "foo", "value": 1},
    "objectRemoved": {"constant": true, "object": "foo", "value": 1}
};
var commonChanged = {
    "propertyConstant": "constant property value",
    "propertyChanged": "changed property value",

    "arrayConstant": [
        {
            "key": "key1",
            "value": "constant"
        },
        {
            "key": "key2",
            "value": "array"
        },
        {
            "key": "key3",
            "value": "value"
        }
    ],
    "arrayAdded": [
        {
            "key": "key1",
            "value": "constant"
        },
        {
            "key": "key2",
            "value": "array"
        },
        {
            "key": "key3",
            "value": "value"
        },
        {
            "key": "key4",
            "value": "added"
        }
    ],
    "arrayChanged": [
        {
            "key": "key1",
            "value": "changed"
        },
        {
            "key": "key2",
            "value": "array"
        },
        {
            "key": "key3",
            "value": "value"
        }
    ],
    "arrayRemoved": [
        {
            "key": "key1",
            "value": "constant"
        },
        {
            "key": "key2",
            "value": "array"
        }
    ],
    "arrayReordered": [
        {
            "key": "key2",
            "value": "array"
        },
        {
            "key": "key3",
            "value": "value"
        },
        {
            "key": "key1",
            "value": "constant"
        }
    ],
    "arrayReorderedAdded": [
        {
            "key": "key2",
            "value": "array"
        },
        {
            "key": "key4",
            "value": "added"
        },
        {
            "key": "key3",
            "value": "value"
        },
        {
            "key": "key1",
            "value": "constant"
        }
    ],
    "arrayReorderedChanged": [
        {
            "key": "key2",
            "value": "array"
        },
        {
            "key": "key3",
            "value": "value"
        },
        {
            "key": "key1",
            "value": "changed"
        }
    ],
    "arrayReorderedRemoved": [
        {
            "key": "key2",
            "value": "array"
        },
        {
            "key": "key1",
            "value": "constant"
        }
    ],
    "arrayReordered2": [
        {
            "value": "array",
            "originalIndex": 1
        },
        {
            "value": "value",
            "originalIndex": 2
        },
        {
            "value": "constant",
            "originalIndex": 0
        }
    ],
    "arrayReorderedAdded2": [
        {
            "value": "array",
            "originalIndex": 1
        },
        {
            "value": "added"
        },
        {
            "value": "value",
            "originalIndex": 2
        },
        {
            "value": "constant",
            "originalIndex": 0
        }
    ],
    "arrayReorderedChanged2": [
        {
            "value": "array",
            "originalIndex": 1
        },
        {
            "value": "value",
            "originalIndex": 2
        },
        {
            "value": "changed",
            "originalIndex": 0
        }
    ],
    "arrayReorderedRemoved2": [
        {
            "value": "array",
            "originalIndex": 1
        },
        {
            "value": "constant",
            "originalIndex": 0
        }
    ],
    "arrayReordered3": [
        "array",
        "value",
        "constant"
    ],
    "arrayReorderedAdded3": [
        "array",
        "added",
        "value",
        "constant"
    ],
    "arrayReorderedChanged3": [
        "array",
        "value",
        "changed"
    ],
    "arrayReorderedRemoved3": [
        "array",
        "constant"
    ],

    "objectConstant": {"constant": true, "object": "foo", "value": 1},
    "objectAdded": {"constant": true, "object": "foo", "value": 1, "added": "bar"},
    "objectChanged": {"constant": false, "object": "foo", "value": 1},
    "objectRemoved": {"constant": true, "object": "foo"}
};

var user = {
    "displayName": "Display name",
    "username": "User name",
    "phone": "+1234567890",
    "address": [
        "Address 1",
        "Address 2"
    ],
    "membership": [
        "Group 1",
        "Group 2",
        "Group 3"
    ]
};
var userChanged = {
    "displayName": "New display name",
    "username": "User name",
    "phone": "+0987654321",
    "address": [
        "Address 2",
        "Address 3",
        "Address 1"
    ],
    "membership": [
        "Group 3",
        "Group 2"
    ]
};

var group = {
    "public": false,
    "description": "Group description",
    "members": [
        {
            "key": "key1",
            "name": "Member1"
        },
        {
            "key": "key2",
            "name": "Member2"
        },
        {
            "key": "key3",
            "name": "Member3"
        }
    ]
};
var groupChanged = {
    "public": false,
    "description": "Group description",
    "members": [
        {
            "key": "key2",
            "name": "Member2"
        },
        {
            "key": "key1",
            "name": "Member1"
        }
    ]
};

var userstore = {
    "name": "Userstore name",
    "remote": false,
    "connectorName": "Connectore name",
    "configXML": "<config></config>",
    "administrators": [
        {
            "name": "name1"
        },
        {
            "name": "name2"
        }
    ]
};
var userstoreChanged = {
    "name": "Userstore name",
    "remote": false,
    "connectorName": "Connectore name",
    "configXML": "<config><item name='item1'>value1</item></config>",
    "administrators": [
        {
            "name": "name2"
        },
        {
            "name": "name3"
        }
    ]
};


function getProps(propName, result) {
    var p, prop, tempProps, props = [];
    for (p in result) {
        if (result.hasOwnProperty(p)) {
            prop = result[p];
            if (Ext.isObject(prop) || Ext.isArray(prop)) {
                if (Ext.isObject(prop) && prop.fieldType === propName) {
                    props.push(prop);
                }
                tempProps = getProps(propName, prop);
                if (tempProps.length > 0) {
                    props = props.concat(tempProps);
                }
            }
        }
    }
    return props;
}

function getPropsChangeType(propName, result) {
    var props = getProps(propName, result);
    return Ext.pluck(props, 'changeType').join(',');
}

function getPropsCount(result) {
    var p, prop, count = 0;
    for (p in result) {
        if (result.hasOwnProperty(p)) {
            prop = result[p];
            if (Ext.isObject(prop) || Ext.isArray(prop)) {
                if (Ext.isObject(prop) && Ext.isDefined(prop.fieldType)) {
                    count += 1;
                }
                count += getPropsCount(prop);
            }
        }
    }
    return count;
}

function getFieldset(fieldsetName, result) {
    var i, fs;
    if (result && result.children) {
        for (i = 0; i < result.children.length; i++) {
            fs = result.children[i];
            if (fieldsetName === fs.fieldsetType) {
                return fs;
            }
        }
    }
    return undefined;
}

function getFieldsetChildrenCount(fieldsetName, result) {
    var fs = getFieldset(fieldsetName, result);
    return fs && fs.children ? fs.children.length : undefined;
}

function getFieldsetsCount(result) {
    var i, count = 0;
    if (result && result.children) {
        for (i = 0; i < result.children.length; i++) {
            if (Ext.isDefined(result.children[i].fieldsetType)) {
                count++;
            }
        }
    }
    return count;
}


function testObjects(t, diff, changedOnly) {
    // General object comparison tests
    var result;
    t.diag('GENERAL OBJECTS ' + (changedOnly === true ? '(changed only mode)' : '(all data mode)'));
    // can use any of the compare methods, without userstore they will fallback to objects comparison

    t.diag('Adding...');
    result = diff.compareUsers(common, empty, changedOnly);

    t.is(getPropsChangeType('propertyConstant', result), 'added', 'property must have been marked as added');
    t.is(getPropsChangeType('arrayConstant', result), 'added,added,added,added', 'array must have been marked as added with 3 children');
    t.is(getPropsChangeType('objectConstant', result), 'added', 'object must have been marked as added');

    t.diag('Modifying...');
    result = diff.compareUsers(commonChanged, common, changedOnly);

    if (changedOnly) {
        t.is(getProps('propertyConstant', result).length, 0, 'property constant must not have been in the result');
        t.is(getProps('arrayConstant', result).length, 0, 'array constant must not have been in the result');
        t.is(getProps('objectConstant', result).length, 0, 'object constant must not have been in the result');
    } else {
        t.is(getPropsChangeType('propertyConstant', result), 'none', 'property constant must have been marked as not modified');
        t.is(getPropsChangeType('arrayConstant', result), 'none,none,none,none', 'array constant must have been marked as not modified');
        t.is(getPropsChangeType('objectConstant', result), 'none', 'object constant must have been marked as not modified');
    }
    t.is(getPropsChangeType('propertyChanged', result), 'modified', 'property changed must have been marked as modified');

    t.is(getPropsChangeType('arrayAdded', result), 'modified,none,none,none,added',
        'array added must have been marked as modified with 3 not modified children and 1 added');
    t.is(getPropsChangeType('arrayChanged', result), 'modified,modified,none,none',
        'array changed must have been marked as modified with 2 not modified children and 1 modified');
    t.is(getPropsChangeType('arrayRemoved', result), 'modified,none,none,removed',
        'array removed must have been marked as modified with 2 not modified children and 1 removed');

    t.ok(getPropsChangeType('arrayReordered', result) === 'modified,none,none,none' &&
         getPropsChangeType('arrayReordered2', result) === 'modified,none,none,none' &&
         getPropsChangeType('arrayReordered3', result) === 'modified,none,none,none',
        'all arrays reordered must have been marked as modified with 3 not modified children');
    t.ok(getPropsChangeType('arrayReorderedAdded', result) === 'modified,none,added,none,none' &&
         getPropsChangeType('arrayReorderedAdded2', result) === 'modified,none,added,none,none' &&
         getPropsChangeType('arrayReorderedAdded3', result) === 'modified,none,added,none,none',
        'all arrays reordered added must have been marked as modified with 3 not modified children and 1 added');
    t.ok(getPropsChangeType('arrayReorderedChanged', result) === 'modified,none,none,modified' &&
         getPropsChangeType('arrayReorderedChanged2', result) === 'modified,none,none,modified' &&
         getPropsChangeType('arrayReorderedChanged3', result) === 'modified,none,none,added,removed',
        'all arrays reordered changed must have been marked as modified with 2 not modified children and 1 modified');
    t.ok(getPropsChangeType('arrayReorderedRemoved', result) === 'modified,none,none,removed' &&
         getPropsChangeType('arrayReorderedRemoved2', result) === 'modified,none,none,removed' &&
         getPropsChangeType('arrayReorderedRemoved3', result) === 'modified,none,none,removed',
        'all arrays reordered removed must have been marked as modified with 2 not modified children and 1 removed');

    t.is(getPropsChangeType('objectAdded', result), 'modified', 'object added must have been marked as modified with 1 added property');
    t.is(getPropsChangeType('objectChanged', result), 'modified',
        'object changed must have been marked as modified with 1 changed property');
    t.is(getPropsChangeType('objectRemoved', result), 'modified',
        'object removed must have been marked as modified with 1 removed property');

    t.diag('Removing...');
    result = diff.compareUsers(empty, commonChanged, changedOnly);

    t.is(getPropsChangeType('propertyConstant', result), 'removed', 'property must have been marked as removed');
    t.is(getPropsChangeType('arrayConstant', result), 'removed,removed,removed,removed',
        'array must have been marked as removed with 3 children');
    t.is(getPropsChangeType('objectConstant', result), 'removed', 'object must have been marked as removed');
}

function testUsers(t, diff, changedOnly) {
    // User specific tests
    var result;
    t.diag('USERS ' + (changedOnly === true ? '(changed only mode)' : '(all data mode)'));

    t.diag('Adding...');
    result = diff.compareUsers(user, empty, changedOnly);
    t.is(result.children[0].fieldType, 'displayName', 'displayName must have been the first property');
    t.is(getFieldsetChildrenCount('places', result), 2, 'places fieldset must have had 2 direct children');
    t.is(getPropsChangeType('address', result), 'added,added', 'places fieldset must have had 2 added children');
    t.is(getFieldsetChildrenCount('memberships', result), 1, 'memberships fieldset must have had 1 direct summary child');
    t.is(getFieldsetsCount(result), 4, 'All data changed, must have been 4 fieldsets');

    t.diag('Modifying...');
    result = diff.compareUsers(userChanged, user, changedOnly);
    t.is(result.children[0].fieldType, 'displayName', 'displayName must have been the first property');
    t.is(getFieldsetChildrenCount('places', result), 3, 'places fieldset must have had 3 direct children');
    t.is(getPropsChangeType('address', result), 'none,added,none', 'places fieldset must have had 1 added and 2 reordered children');
    t.is(getFieldsetChildrenCount('memberships', result), 1, 'memberships fieldset must have had 1 direct summary child');
    if (changedOnly) {
        t.is(getFieldsetsCount(result), 3, 'Showing changed data, must have been 3 fieldsets');
    } else {
        t.is(getFieldsetsCount(result), 4, 'Showing all data, must have been 4 fieldsets');
    }

    t.diag('Removing...');
    result = diff.compareUsers(empty, userChanged, changedOnly);
    t.is(result.children[0].fieldType, 'displayName', 'displayName must have been the first property');
    t.is(getFieldsetChildrenCount('places', result), 3, 'places fieldset must have had 3 direct children');
    t.is(getPropsChangeType('address', result), 'removed,removed,removed', 'places fieldset must have had 2 removed children');
    t.is(getFieldsetChildrenCount('memberships', result), 1, 'memberships fieldset must have had 1 direct summary child');
    t.is(getFieldsetsCount(result), 4, 'All data changed, must have been 4 fieldsets');
}

function testGroups(t, diff, changedOnly) {
    //Group specific tests
    var result;
    t.diag('GROUPS ' + (changedOnly === true ? '(changed only mode)' : '(all data mode)'));

    t.diag('Adding...');
    result = diff.compareGroups(group, empty, changedOnly);
    t.is(getFieldsetChildrenCount('members', result), 1, 'members fieldset must have had 1 direct summary child');
    t.is(getFieldsetsCount(result), 2, 'All data changed, must have been 2 fieldsets');

    t.diag('Modifying...');
    result = diff.compareGroups(groupChanged, group, changedOnly);
    t.is(getFieldsetChildrenCount('members', result), 1, 'members fieldset must have had 1 direct summary child');
    if (changedOnly) {
        t.is(getFieldsetsCount(result), 1, 'Showing changed data, must have been 1 fieldsets');
    } else {
        t.is(getFieldsetsCount(result), 2, 'Showing all data, must have been 2 fieldsets');
    }

    t.diag('Removing...');
    result = diff.compareGroups(empty, groupChanged, changedOnly);
    t.is(getFieldsetChildrenCount('members', result), 1, 'members fieldset must have had 1 direct summary child');
    t.is(getFieldsetsCount(result), 2, 'All data changed, must have been 2 fieldsets');
}

function testUserstores(t, diff, changedOnly) {
    //Userstore specific tests
    var result;
    t.diag('USERSTORES ' + (changedOnly === true ? '(changed only mode)' : '(all data mode)'));

    t.diag('Adding...');
    result = diff.compareUserstores(userstore, empty, changedOnly);
    t.is(getFieldsetChildrenCount('admin', result), 1, 'admin fieldset must have had 1 direct summary child');
    t.is(getFieldsetsCount(result), 3, 'All data changed, must have been 3 fieldsets');

    t.diag('Modifying...');
    result = diff.compareUserstores(userstoreChanged, userstore, changedOnly);
    t.is(getFieldsetChildrenCount('admin', result), 1, 'admin fieldset must have had 1 direct summary child');
    if (changedOnly) {
        t.is(getFieldsetsCount(result), 2, 'Showing changed data, must have been 2 fieldsets');
    } else {
        t.is(getFieldsetsCount(result), 3, 'Showing all data, must have been 3 fieldsets');
    }

    t.diag('Removing...');
    result = diff.compareUserstores(empty, userstoreChanged, changedOnly);
    t.is(getFieldsetChildrenCount('admin', result), 1, 'admin fieldset must have had 1 direct summary child');
    t.is(getFieldsetsCount(result), 3, 'All data changed, must have been 3 fieldsets');
}


StartTest(function (t) {
    t.requireOk(
        [
            'Admin.plugin.Diff',
            'Admin.store.account.UserstoreConfigStore',
            'Admin.view.account.EditUserFormPanel'
        ],
        function () {

            var diff = Admin.plugin.Diff;
            var us = Ext.create('Admin.store.account.UserstoreConfigStore', {
                // needed to be able to lookup it, added automatically by controller
                storeId: 'Admin.store.account.UserstoreConfigStore',
                listeners: {
                    load: function () {

                        testObjects(t, diff);
                        testObjects(t, diff, true);

                        testUsers(t, diff);
                        testUsers(t, diff, true);

                        testGroups(t, diff);
                        testGroups(t, diff, true);

                        testUserstores(t, diff);
                        testUserstores(t, diff, true);

                    }
                }
            });
        }
    );
});
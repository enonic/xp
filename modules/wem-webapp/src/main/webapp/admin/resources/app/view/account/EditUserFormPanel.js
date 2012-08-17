Ext.define('Admin.view.account.EditUserFormPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.editUserFormPanel',

    requires: [
        'Admin.view.account.AddressContainer',
        'Admin.view.account.AddressPanel',
        'Admin.view.account.UserFormField',
        'Admin.view.account.DoublePasswordField'
    ],

    cls: 'admin-form-panel',

    statics: {
        fieldLabels: {
            'username': 'User Name',
            'email': 'E-mail',
            'password': 'Password',
            'repeatPassword': 'Repeat Password',
            'timezone': 'Timezone',
            /* general fields */
            'prefix': 'Prefix',
            'firstName': 'First Name',
            'middleName': 'Middle Name',
            'lastName': 'Last Name',
            'suffix': 'Suffix',
            'initials': 'Initials',
            'nickName': 'Nick Name',
            'personalId': 'Personal ID',
            'memberId': 'Member ID',
            'organization': 'Organisation',
            'birthday': 'Birthday',
            'gender': 'Gender',
            'title': 'Title',
            'description': 'Descripion',
            'htmlEmail': 'E-mail',
            'homePage': 'Web Page',
            'timeZone': 'Time Zone',
            'locale': 'Locale',
            'country': 'Country',
            'globalPosition': 'Global Position',
            'phone': 'Phone Number',
            'mobile': 'Mobile Number',
            'fax': 'Fax Number',
            'address': 'Address'
        },
        staticFields: [
            {
                label: 'Username',
                type: 'username',
                required: true,
                remote: false,
                readOnly: false
            },
            {
                label: 'Password',
                type: 'password',
                required: true,
                remote: false,
                readOnly: false
            },
            {
                label: 'E-mail',
                type: 'email',
                required: true,
                remote: false,
                readOnly: false
            },
            {
                label: 'Display name',
                type: 'displayName',
                required: true,
                remote: false,
                readOnly: false
            },
            {
                "type": "country",
                "readOnly": false,
                "required": false,
                "remote": false,
                "iso": true
            },
            {
                "type": "locale",
                "readOnly": false,
                "required": false,
                "remote": false,
                "iso": true
            },
            {
                "type": "timezone",
                "readOnly": false,
                "required": false,
                "remote": false,
                "iso": true
            },
            {
                "type": "globalPosition",
                "readOnly": false,
                "required": false,
                "remote": false,
                "iso": true
            }
        ]
    },
    validationUrls: {
        username: 'data/account/userkey',
        email: 'data/account/verifyUniqueEmail'
    },

    autoScroll: false,
    autoHeight: true,
    border: false,
    currentUser: undefined,
    defaultUserStoreName: 'default',
    enableToolbar: true,

    store: 'Admin.store.account.UserstoreConfigStore',

    initComponent: function () {
        var me = this;
        me.store = Ext.data.StoreManager.lookup(me.store);
        if (!this.staticFields) {
            this.staticFields = [].concat(Admin.view.account.EditUserFormPanel.staticFields);
        }
        if (!this.excludedFields) {
            this.excludedFields = ["username", "email", "country", "globalPosition", "locale",
                "address", "photo", "password", "repeatPassword", "timezone"];
        }

        if (this.enableToolbar) {
            this.dockedItems = [
                {
                    dock: 'top',
                    xtype: 'toolbar',
                    border: false,
                    padding: 5,
                    items: [
                        {
                            text: 'Save',
                            iconCls: 'icon-save',
                            action: 'saveUser'
                        },
                        {
                            text: 'Cancel',
                            action: 'closeUserForm'
                        },
                        '->',
                        {
                            text: 'Delete',
                            iconCls: 'icon-delete-user',
                            action: 'deleteAccount'
                        },
                        {
                            text: 'Change Password',
                            iconCls: 'icon-change-password',
                            action: 'changePassword'
                        }
                    ]
                }
            ];
        }
        this.userFieldSet = {
            'username': this.createTextField,
            'email': this.createTextField
        };
        this.securityFieldSet = {
            'password': this.createPasswordField
        };
        this.nameFieldSet = {
            'prefix': this.createTextField,
            'firstName': this.createTextField,
            'middleName': this.createTextField,
            'lastName': this.createTextField,
            'suffix': this.createTextField,
            'initials': this.createTextField,
            'nickName': this.createTextField
        };
        this.detailsFieldSet = {
            'personalId': this.createTextField,
            'memberId': this.createTextField,
            'organization': this.createTextField,
            'birthday': this.createDateField,
            'gender': this.createComboBoxField,
            'title': this.createTextField,
            'description': this.createTextField,
            'htmlEmail': this.createCheckBoxField,
            'homePage': this.createTextField
        };
        this.locationFieldSet = {
            'timezone': this.createComboBoxField,
            'locale': this.createComboBoxField,
            'country': this.createComboBoxField,
            'globalPosition': this.createTextField
        };
        this.communicationFieldSet = {
            'phone': this.createAutoCompleteField,
            'mobile': this.createAutoCompleteField,
            'fax': this.createAutoCompleteField
        };
        this.addressFieldSet = {
            'address': function (field) {
                if (me.userFields && me.userFields.info && me.userFields.info.addresses) {
                    var addresses = me.userFields.info.addresses;
                    var tabs = [];
                    var index;
                    for (index in addresses) {
                        if (addresses.hasOwnProperty(index)) {
                            Ext.Array.include(tabs, me.generateAddressPanel(field, index !== 0, addresses[index], index === 0));
                        }
                    }
                    if (tabs.length === 0) {
                        Ext.Array.include(tabs, me.generateAddressPanel(field, false, null, true));
                    }
                    return {
                        sourceField: field,
                        xtype: 'addressContainer',
                        items: tabs
                    };
                } else {
                    var tabItem = me.generateAddressPanel(field, false, null, true);
                    return {
                        sourceField: field,
                        xtype: 'addressContainer',
                        items: [tabItem]
                    };
                }
            }
        };
        this.callParent(arguments);
        this.addEvents('fieldsloaded');
        this.removeAll();
        this.show();
    },

    renderUserForm: function (user) {
        var me = this;
        me.currentUser = user;
        var userStoreName = user ? user.userStore : me.defaultUserStoreName;
        var userStore = me.store.findRecord('name', userStoreName);
        me.removeAll();
        me.generateForm(userStore);
        me.doLayout();
    },

    getCallingCodesStore: function () {
        var codeStore = Ext.create('Ext.data.Store', {
            remoteSort: false,
            model: 'Admin.model.account.CallingCodeModel'
        });
        var countryStore = Ext.data.StoreManager.lookup('Admin.store.account.CountryStore');
        if (countryStore) {
            var i, codes;
            for (i = 0; i < countryStore.getCount(); i++) {
                codes = countryStore.getAt(i).callingCodes().getRange();
                if (codes && codes.length > 0) {
                    codeStore.add(codes);
                }
            }
            codeStore.sort('callingCode', 'ASC');
        }
        return codeStore;
    },

    createAutoCompleteField: function (field) {
        var store;
        switch (field.get('type')) {
        case 'phone':
        case 'mobile':
        case 'fax':
            store = this.getCallingCodesStore();
            break;
        }
        return {
            xtype: 'userFormField',
            type: 'autocomplete',
            fieldLabel: field.get('fieldlabel'),
            fieldStore: store,
            valueField: 'callingCode',
            displayField: 'callingCode',
            displayConfig: {
                getInnerTpl: function () {
                    return '<b>{callingCode}</b> ({englishName})';
                }
            }
        };
    },

    createComboBoxField: function (field) {
        var fieldStore;
        var valueField;
        var displayField;
        var displayConfig;
        var listeners = null;

        if (field.type === 'timezone') {
            fieldStore = Ext.data.StoreManager.lookup('Admin.store.account.TimezoneStore');
            valueField = 'id';
            displayField = 'humanizedIdAndOffset';
            displayConfig = {
                getInnerTpl: function () {
                    return '{humanizedId} ({offset})';
                }
            };
            listeners = {
                beforequery: {
                    fn: function (query) {
                        var queryText = query.query;
                        queryText = queryText.split('(')[0];
                        var pattern = new RegExp(queryText, 'gi');
                        fieldStore.clearFilter();
                        fieldStore.filter('humanizedIdAndOffset', pattern);
                        query.combo.expand();

                        var picker = query.combo.getPicker();
                        var firstItem = picker.getNode(0);
                        if (firstItem) {
                            picker.highlightItem(firstItem);
                        }
                        return false;
                    }
                }
            };
        } else if (field.get('type') === 'locale') {
            fieldStore = Ext.data.StoreManager.lookup('Admin.store.account.LocaleStore');
            valueField = 'id';
            displayField = 'displayName';
        } else if (field.get('type') === 'country') {
            fieldStore = Ext.create('Admin.store.account.CountryStore');
            valueField = 'code';
            displayField = 'englishName';
        } else if (field.get('type') === 'region') {
            fieldStore = Ext.create('Ext.data.Store', {
                model: 'Admin.model.account.RegionModel'
            });
            valueField = 'code';
            displayField = 'englishName';
        } else if (field.get('type') === 'locale') {
            fieldStore = 'Admin.store.account.LanguageStore';
            valueField = 'languageCode';
            displayField = 'description';
        } else if (field.get('type') === 'gender') {
            fieldStore = new Ext.data.Store({
                fields: ['label', 'value'],
                data: [
                    {label: 'Male', value: 'MALE'},
                    {label: 'Female', value: 'FEMALE'}
                ]
            });
            valueField = 'value';
            displayField = 'label';
        }

        return {
            xtype: 'userFormField',
            type: 'combo',
            queryMode: 'local',
            minChars: 1,
            emptyText: 'Please select',
            fieldStore: fieldStore,
            valueField: valueField,
            displayField: displayField,
            displayConfig: displayConfig,
            listeners: listeners
        };
    },

    createTextField: function (field) {
        var validationResultType = 'none';
        var delayValidation = false;
        var validationData = {};
        if (field.get('type') === 'username' || field.get('type') === 'email') {
            validationResultType = 'detail';
            delayValidation = true;
            validationData.userStore = this.currentUser ? this.currentUser.userStore : this.defaultUserStoreName;
            validationData.userKey = this.userFields ? this.userFields.key : undefined;
        }
        return {
            xtype: 'userFormField',
            validationResultType: validationResultType,
            delayValidation: delayValidation,
            validationData: validationData,
            type: 'text'
        };
    },

    createCheckBoxField: function (field) {
        return {
            xtype: 'userFormField',
            type: 'boolean'
        };
    },

    createPhotoField: function (field) {
        return {
            xtype: 'userFormField',
            type: 'file'
        };
    },

    createPasswordField: function (field) {
        return {
            xtype: 'doublePasswordField',
            itemId: 'password',
            passwordLabel: 'Password<span style="color: red;">*</span>',
            repeatLabel: 'Repeat password<span style="color: red;">*</span>',
            labelWidth: 120
        };
    },

    createDateField: function (field) {
        return {
            xtype: 'userFormField',
            type: 'date'
        };
    },

    generateForm: function (storeConfig, staticFields, excludedFields) {
        if (staticFields) {
            this.staticFields = staticFields;
        }
        if (excludedFields) {
            this.excludedFields = excludedFields;
        }
        if (storeConfig && storeConfig.userFields) {
            var fields = Ext.Array.filter(Ext.Array.toArray(storeConfig.userFields().getRange()), function (field) {
                var index;
                for (index in this.staticFields) {
                    if (this.staticFields.hasOwnProperty(index)) {
                        if (this.staticFields[index].type === field.get('type')) {
                            return false;
                        }
                    }
                }
                return true;
            }, this);
            Ext.Array.forEach(this.staticFields, function (staticField) {
                var fieldModel = Ext.create('Admin.model.account.UserFieldModel', staticField);
                fields.push(fieldModel);
            });
            this.add(this.generateFieldSet('User', this.userFieldSet, fields));
            if (!this.userFields) {
                this.add(this.generateFieldSet('Security', this.securityFieldSet, fields));
            }
            this.add(this.generateFieldSet('Name', this.nameFieldSet, fields));
            this.add(this.generateFieldSet('Personal Information', this.detailsFieldSet, fields));
            this.add(this.generateFieldSet('Settings', this.locationFieldSet, fields));
            this.add(this.generateFieldSet('Communication', this.communicationFieldSet, fields));
            this.add(this.generateFieldSet('Address', this.addressFieldSet, fields));
        }
        this.fireEvent('fieldsloaded', this, this);
    },

    generateFieldSet: function (title, fieldSet, storeConfig) {
        var self = Admin.view.account.EditUserFormPanel;
        var me = this;
        var fieldSetItem = {
            defaults: {
                bodyPadding: 10
            },
            xtype: 'fieldset',
            layout: {
                type: 'table',
                columns: 1,
                tableAttrs: {
                    style: {
                        width: '100%'
                    }
                }
            },
            measureWidth: true,
            title: title
        };
        var fieldItems = [];
        Ext.Array.each(storeConfig, function (item) {
            var canBeAdded = true;
            if (this.includedFields) {
                canBeAdded = Ext.Array.contains(this.includedFields, item.get('type'));
            } else {
                canBeAdded = !Ext.Array.contains(this.excludedFields, item.get('type'));
            }
            if (fieldSet[item.get('type')] && canBeAdded) {
                var fieldValue;
                if (me.userFields) {
                    fieldValue = me.userFields[item.get('type')];
                    if ((fieldValue === undefined) && (me.userFields.info !== undefined)) {
                        fieldValue = me.userFields.info[item.get('type')];
                    }
                }
                var baseConfig = {
                    fieldLabel: self.fieldLabels[item.get('type')] || item.get('type'),
                    fieldname: item.get('type'),
                    required: item.get('required') || false,
                    remote: item.get('remote') || false,
                    readonly: item.get('readOnly') || false || (item.get('type') === 'username' && me.userFields),
                    // what is that field?
                    vtype: item.get('vtype'),
                    fieldValue: fieldValue,
                    validationUrl: me.validationUrls[item.get('type')],
                    currentUser: me.currentUser
                };
                var createFunc = fieldSet[item.get('type')];
                var newField = createFunc.call(me, item);
                newField = Ext.apply(newField, baseConfig);
                Ext.Array.include(fieldItems, newField);
            }
        }, this);
        if (title === 'Address') {
            return fieldItems;
        } else if (fieldItems.length > 0) {
            fieldSetItem.items = fieldItems;
            return fieldSetItem;
        } else {
            return [];
        }
    },

    generateAddressPanel: function (field, closable, values, remote) {
        var addressPanel = {
            xtype: 'addressPanel',
            values: values,
            closable: closable || false,
            readonly: field.get('readOnly'),
            iso: field.get('iso'),
            remote: remote
        };
        return addressPanel;
    },

    setItemValue: function (itemId, value) {
        var field = this.down('#' + itemId);
        if (field) {
            field.setValue(value);
        }
    },

    getAddresses: function (formValues) {
        var addressFields = ['country', 'region', 'street',
            'postalCode', 'postalAddress', 'label', 'isoCountry', 'isoRegion', 'originalIndex'];
        var address;
        var addresses = [];
        if (Ext.isArray(formValues.label)) {
            // multiple address panels in form
            var numAdr = formValues.label.length;
            var a;
            var updateAddressFn = function (fieldId) {
                if (formValues[fieldId] && formValues[fieldId][a]) {
                    address[fieldId] = formValues[fieldId][a];
                }
            };
            for (a = 0; a < numAdr; a++) {
                address = {};
                Ext.Array.forEach(addressFields, updateAddressFn);
                addresses.push(address);
            }
        } else {
            // single address panel in form
            address = {};
            Ext.Array.forEach(addressFields, function (fieldId) {
                if (formValues[fieldId]) {
                    address[fieldId] = formValues[fieldId];
                }
            });
            addresses.push(address);
        }
        return addresses;
    },

    getData: function () {
        var formValues = this.getForm().getFieldValues();
        var userData;
        var isPlacesForm = formValues.label;
        if (isPlacesForm) {
            userData = {
                info: {addresses: this.getAddresses(formValues) }
            };
        } else {
            userData = {
                info: formValues
            };
            if (formValues.username) {
                userData.username = formValues.username;
            }
            if (formValues.email) {
                userData.email = formValues.email;
            }
        }
        return userData;
    }

});


Ext.define('Admin.view.account.UserFormField', {
    extend: 'Ext.form.FieldContainer',
    alias: 'widget.userFormField',

    requires: ['Admin.view.account.PasswordMeter'],

    layout: {
        type: 'hbox'
    },

    validationUrls: {},
    validationData: {},

    validationResultType: 'none',

    width: 1000,

    actionName: undefined,

    delayValidation: false,

    delayValidationTime: 1000,

    validationTask: undefined,

    fieldWidth: {
        'name': 200,
        'email': 200,
        'initials': 150,
        'birthday': 300,
        'gender': 200,
        'country': 400,
        'timezone': 400,
        'globalPosition': 200,
        'locale': 300,
        'fax': 300,
        'mobile': 300,
        'phone': 300,
        'password': 250,
        'repeatPassword': 250
    },


    initComponent: function () {
        var me = this;
        me.items = [];
        me.fieldConfigBuilders = {
            'date': this.createDateConfig,
            'file': this.createFileConfig,
            'combo': this.createComboConfig,
            'autocomplete': this.createAutoCompleteConfig,
            'password': this.createPasswordConfig,
            'text': this.createTextConfig,
            'boolean': this.createCheckBoxConfig
        };

        var fieldConfig = me.createBasicFieldConfig();
        Ext.Array.include(me.items, fieldConfig);

        if (me.required && (me.fieldLabel !== undefined)) {
            me.fieldLabel += "<span style=\"color:red;\" ext:qtip=\"This field is required\">*</span>";
        }
        if (me.delayValidation) {
            var spinningWheel = me.createWaitingLabel();
            Ext.Array.include(me.items, spinningWheel);
        }

        var validationLabel = me.createValidationLabel();
        if (validationLabel) {
            Ext.Array.include(me.items, validationLabel);
        }

        me.callParent(arguments);
        me.childField = me.down('#' + me.fieldname);
        me.addEvents('validitychange');
    },

    createValidationLabel: function () {
        var me = this;
        var validationLabel = {
            itemId: 'validationLabel',
            height: 16,
            margin: '0 0 0 15',
            cls: 'admin-validation-label',
            hideMode: 'visibility'
        };
        if (me.validationResultType === 'short') {
            var shortLabel = {
                width: 16,
                tpl: Templates.account.shortValidationResult
            };
            return Ext.apply(validationLabel, shortLabel);
        }
        if (me.validationResultType === 'detail') {
            var detailLabel = {
                tpl: '<div class="{[ values.type==="info" ? "validationInfo" : "validationError" ]}">{text}</div> ',
                data: {text: ''},
                width: 200
            };
            return Ext.apply(validationLabel, detailLabel);
        }
    },

    createWaitingLabel: function () {
        return {
            itemId: 'spinningWheel',
            xtype: 'component',
            hideMode: 'visibility',
            hidden: true,
            cls: 'validationLoader',
            margin: '0 0 0 15',
            height: 16,
            width: 16
        };
    },

    createBasicFieldConfig: function () {
        var me = this;
        me.validationTask = new Ext.util.DelayedTask(function () {
            me.validate();
        });

        var fieldConfig = {
            enableKeyEvents: true,
            disabled: me.readonly,
            allowBlank: !me.required,
            vtype: me.vtype,
            name: me.fieldname,
            itemId: me.fieldname,
            action: me.actionName,
            value: me.fieldValue,
            width: 600,
            padding: '0 0 0 20',
            validateOnChange: !me.delayValidation,
            validateOnBlur: !me.delayValidation,
            listeners: {
                'validitychange': me.validityChanged,
                'change': me.delayValidation ?
                    me.callValidationTask : function () {
                },
                'keyup': function (field, event) {
                    if (me.delayValidation && (!event.isSpecialKey() ||
                                               (event.getKey() === event.BACKSPACE) ||
                                               (event.getKey() === event.DELETE))) {
                        var spinningWheelTask = new Ext.util.DelayedTask(function () {
                            var spinningWheel = me.down('#spinningWheel');
                            var validationLabel = me.down('#validationLabel');
                            if (spinningWheel) {
                                spinningWheel.hide();
                            }
                            if (validationLabel) {
                                validationLabel.show();
                            }
                        });
                        var spinningWheel = me.down('#spinningWheel');
                        var validationLabel = me.down('#validationLabel');
                        if (spinningWheel) {
                            spinningWheel.show();
                        }
                        if (validationLabel) {
                            validationLabel.hide();
                        }
                        spinningWheelTask.delay(me.delayValidationTime);
                    }
                }
            }
        };
        if (me.fieldWidth[me.fieldname]) {
            fieldConfig.width = me.fieldWidth[me.fieldname];
        }
        var builderFunction = me.type ? me.fieldConfigBuilders[me.type] : me.fieldConfigBuilders.text;
        fieldConfig = builderFunction(fieldConfig, me);
        if (me.remote) {
            fieldConfig.cls = 'admin-remote-field';
        }
        return fieldConfig;
    },

    callValidationTask: function () {
        var userField = this.up('userFormField');
        var validationTask = userField.validationTask;
        if (validationTask) {
            validationTask.delay(userField.delayValidationTime);
        }
    },

    createCheckBoxConfig: function (fieldConfig) {
        var checkBoxConfig = {xtype: 'checkbox',
            checked: fieldConfig.value};
        return Ext.apply(fieldConfig, checkBoxConfig);
    },

    createDateConfig: function (fieldConfig) {
        fieldConfig.value = Ext.Date.parse(fieldConfig.value, 'Y-m-d');
        var dateConfig = {
            xtype: 'datefield',
            format: 'Y-m-d'
        };
        return Ext.apply(fieldConfig, dateConfig);
    },

    createComboConfig: function (fieldConfig, me) {
        var comboConfig;
        if (me.fieldStore) {
            comboConfig = {
                xtype: 'combobox',
                store: me.fieldStore,
                valueField: me.valueField,
                displayField: me.displayField,
                listeners: me.listeners,
                queryMode: me.queryMode,
                minChars: me.minChars,
                emptyText: me.emptyText,
                listConfig: me.displayConfig
            };
        } else {
            comboConfig = {xtype: 'textfield'};
        }
        return Ext.apply(fieldConfig, comboConfig);
    },

    createAutoCompleteConfig: function (fieldConfig, me) {
        var autoCompleteConfig = {
            xtype: 'combobox',
            enableKeyEvents: true,
            store: me.fieldStore,
            triggeredAction: 'all',
            typeAhead: true,
            queryMode: 'local',
            minChars: 0,
            forceSelection: false,
            hideTrigger: true,
            valueField: me.valueField,
            displayField: me.displayField,
            listConfig: me.displayConfig,
            action: 'initValue'
        };
        return Ext.apply(fieldConfig, autoCompleteConfig);
    },

    createPasswordConfig: function (fieldConfig, me) {
        var passwordConfig;

        if (me.fieldname === 'password') {
            me.cls = 'admin-glowing-item';
            passwordConfig = {
                xtype: 'passwordMeter'
            };
        } else {
            passwordConfig = {
                xtype: 'textfield',
                inputType: 'password',
                validator: me.validatePassword
            };
        }
        return Ext.apply(fieldConfig, passwordConfig);
    },

    createFileConfig: function (fieldConfig) {
        var fileConfig = {xtype: 'filefield'};
        return Ext.apply(fieldConfig, fileConfig);
    },

    createTextConfig: function (fieldConfig, me) {
        var textConfig = {
            xtype: 'textfield',
            enableKeyEvents: true,
            bubbleEvents: ['keyup']
        };
        if (me.fieldname === 'name') {
            textConfig.validator = me.validateUserName;
            textConfig.validValue = true;
        } else if (me.fieldname === 'email') {
            textConfig.validator = me.validateUniqueEmail;
            textConfig.validValue = true;
            textConfig.currentEmail = fieldConfig.value;
            textConfig.prevValue = fieldConfig.value || '';
        }
        return Ext.apply(fieldConfig, textConfig);
    },

    validatePassword: function () {
        var validationStatus = this.up('userFormField').down('#validationLabel');
        var passwordField = this.up('fieldset').down('#password');
        var repeatPasswordField = this.up('fieldset').down('#repeatPassword');
        if (passwordField.getValue() === repeatPasswordField.getValue()) {
            validationStatus.update({type: 'info', text: ''});
            return true;
        } else {
            validationStatus.update({type: 'error', text: 'Passwords don\'t match'});
            return 'Passwords don\'t match';
        }
    },

    validateUserName: function (value) {
        var me = this;
        var parentField = me.up('userFormField');
        var validationStatus = parentField.down('#validationLabel');
        if (me.prevValue !== value && value !== '') {
            this.prevValue = value;
            if (value.search('\\W+') !== -1) {
                validationStatus.update({type: 'error', text: 'Invalid characters'});
                return "Invalid characters";
            }
            Ext.Ajax.request({
                url: parentField.validationUrl,
                method: 'GET',
                params: {
                    'userstore': parentField.validationData.userStore,
                    'name': value
                },
                success: function (response) {
                    var respObj = Ext.decode(response.responseText, true);
                    if (respObj.userkey !== null) {
                        me.validValue = false;
                        validationStatus.update({type: 'error', text: 'Not available'});
                    } else {
                        me.validValue = true;
                        validationStatus.update({type: 'info', text: 'Available'});
                    }
                    parentField.validate();
                }
            });
        }
        if (value === '') {
            validationStatus.update({type: 'info', text: ''});
            return true;
        }
        var msg = "User with this user name already exists";
        return me.validValue || msg;

    },

    emailChanged: function (field) {
        field.pendingServerValidation = true;
    },

    validateUniqueEmail: function (value) {
        var me = this;
        var parentField = me.up('userFormField');
        var validationStatus = parentField.down('#validationLabel');
        if ((me.prevValue !== value) && (value !== '')) {
            me.prevValue = value;
            if (!Ext.data.validations.email({}, value)) {
                // skip server unique-email validation, invalid email format will be triggered
                validationStatus.update({type: 'error', text: 'Invalid e-mail'});
                return 'Invalid e-mail';
            } else {
                validationStatus.update({type: 'info', text: 'Valid e-mail'});
            }

            Admin.lib.RemoteService.account_verifyUniqueEmail({ userStore: parentField.validationData.userStore, email: value },
                function (response) {
                    if (response.success) {
                        if (response.emailInUse) {
                            validationStatus.update({type: 'error', text: 'Not available'});
                            me.validValue = (response.key === parentField.validationData.userKey);
                        } else {
                            validationStatus.update({type: 'info', text: 'Available'});
                            me.validValue = true;
                        }
                        parentField.validate();
                    }
                });
        }
        if (value === '' || value === me.currentEmail) {
            validationStatus.update({type: 'info', text: ''});
            return true;
        }
        return me.validValue || "A user with this email already exists in the userstore";
    },

    validityChanged: function (field, isValid, opts) {
        var parentField = field.up('userFormField');
        var validationLabel = parentField.down('#validationLabel');
        if (parentField.validationResultType !== 'none') {
            field.clearInvalid();
        }
        if (parentField.validationResultType === 'short') {
            validationLabel.update({valid: isValid});
        }
        parentField.fireEvent('validitychange', parentField, isValid, opts);
    },

    validate: function () {
        this.items.each(function (item) {
            if (item.validate) {
                item.validate();
            }
        });
    },

    getValue: function () {
        return this.down('#' + this.fieldname).getValue();
    },

    isValid: function () {
        return this.down('#' + this.fieldname).isValid();
    },

    onDestroy: function () {
        // Cancel validation task if any
        if (this.validationTask) {
            this.validationTask.cancel();
        }
        // Cancel typeAheadTask if any. Either it can break tests
        if (this.childField && this.childField.typeAheadTask) {
            this.childField.typeAheadTask.cancel();
        }
        this.callParent();
    }



});

Ext.define('Admin.view.account.PasswordMeter', {
    extend: 'Ext.form.FieldContainer',
    alias: 'widget.passwordMeter',

    requires: ['Ext.ProgressBar'],

    layout: {
        type: 'table',
        columns: 2
    },

    passwordInputName: undefined,

    passwordStatuses: {
        0: {
            text: 'Too short',
            color: 'black'
        },
        1: {
            text: 'Weak',
            color: '#7D1D1F'
        },
        2: {
            text: 'Good',
            color: '#7D3D50'
        },
        3: {
            text: 'Strong',
            color: '#7D7750'
        },
        4: {
            text: 'Very Strong',
            color: '#3B8150'
        },
        5: {
            text: 'Extremely Strong',
            color: '#3B8150'
        }
    },

    updateStatus: function (field, e, opts) {
        var passwordLevel = this.calculatePasswordStrength(field.getValue());
        var passwordStatus = this.down('#passwordStatus');
        passwordStatus.setVisible(true);
        var passwordInput = this.down('#passwordInput');
        if (passwordLevel === 5) {
            passwordInput.addCls('admin-password-extra-strong');
        } else {
            passwordInput.removeCls('admin-password-extra-strong');
        }
        passwordStatus.update(this.passwordStatuses[passwordLevel]);
    },

    calculatePasswordStrength: function (pwd) {
        var featuresNumber = 0;

        // Calculating feature count
        // Has text and numbers
        if (pwd.match(/\d+/g) && pwd.match(/[A-Za-z]+/g)) {
            featuresNumber += 1;
        }
        // Has special chars
        if (pwd.match(/[\]\[!"#$%&'()*+,.\/:;<=>?@\^_`{|}~\-]+/g)) {
            featuresNumber += 1;
        }
        // Has at least two "text" and two "number" characters
        if (pwd.match(/\d\d+/g) && pwd.match(/[A-Za-z]+/g)) {
            featuresNumber += 1;
        }
        // Has both uppercase and lower case text
        if (pwd.match(/[A-Z]+/g) && pwd.match(/[a-z]+/g)) {
            featuresNumber += 1;
        }
        // Calculating level
        if ((pwd.length >= 12) && (featuresNumber >= 4)) {
            return 5;
        }
        if ((pwd.length >= 10) && (featuresNumber >= 3)) {
            return 4;
        }
        if ((pwd.length >= 10) && (featuresNumber >= 2)) {
            return 3;
        }
        if ((pwd.length >= 8) && (featuresNumber >= 1)) {
            return 2;
        }
        if ((pwd.length >= 6) && (pwd.match(/\d+/g) || pwd.match(/[A-Za-z]+/g))) {
            return 1;
        }

        return 0;
    },

    initComponent: function () {
        var me = this;
        var passwordInputName = me.passwordInputName;
        me.items = [
            {
                xtype: 'textfield',
                inputType: 'password',
                itemId: 'passwordInput',
                name: passwordInputName,
                enableKeyEvents: true,
                allowBlank: this.allowBlank,
                listeners: {
                    keyup: {
                        fn: me.updateStatus,
                        scope: me
                    },
                    'validitychange': me.validityChanged
                },
                width: me.width,
                validator: me.validator
            },
            {
                xtype: 'container',
                itemId: 'passwordStatus',
                cls: 'admin-password-meter-status',
                width: 100,
                hidden: true,
                tpl: undefined, // template is lost
                data: me.passwordStatuses[0]
            }
        ];
        // Additional width for status
        me.width += 100;
        me.callParent(arguments);
        me.addEvents('validitychange');
    },

    getField: function () {
        return this.down('textfield');
    },

    getValue: function () {
        return this.down('textfield').getValue();
    },

    setValue: function (value) {
        this.down('textfield').setValue(value);
    },

    validityChanged: function (field, isValid, opts) {
        var parentField = field.up('passwordMeter');
        parentField.fireEvent('validitychange', parentField, isValid, opts);
    },

    validate: function () {
        this.down('textfield').validate();
    }
});

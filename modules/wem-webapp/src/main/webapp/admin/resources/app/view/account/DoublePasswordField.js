Ext.define( 'Admin.view.account.DoublePasswordField', {
    extend: 'Ext.container.Container',
    alias: 'widget.doublePasswordField',

    // TODO: Why does this require ProgressBar
    requires: ['Ext.ProgressBar'],

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

    labelWidth: 100,
    inputWidth: 260,
    messageWidth: 140,
    repeatDelay: 500,

    passwordLabel: 'Password',
    passwordName: 'password',

    repeatLabel: 'Confirm password',
    repeatName: 'repeatPassword',

    /*    private     */
    isPasswordValid: false,
    isRepeatValid: false,
    updateRepeatDelayed: null,

    initComponent: function()
    {
        var me = this;

        this.updateRepeatDelayed = new Ext.util.DelayedTask( function()
        {
            var data;
            var repeat = me.getRepeatField();
            if ( me.testEqual( repeat.getValue(), me.getPasswordField().getValue() ) ) {
                data = {type: 'match', text: ''};
            } else {
                data = {type: '', text: ''};
            }
            me.getRepeatStatus().update( data );
        } );

        me.items = [
            {
                xtype: 'fieldcontainer',
                layout: {
                    type: 'table',
                    columns: 2
                },
                items: [
                    {
                        xtype: 'textfield',
                        inputType: 'password',
                        itemId: 'passwordInput',
                        fieldLabel: me.passwordLabel,
                        name: me.passwordName,
                        width: me.inputWidth,
                        labelWidth: me.labelWidth,
                        cls: 'admin-glowing-item',
                        enableKeyEvents: true,
                        allowBlank: false,
                        validator: me.validatePassword,
                        validateOnBlur: false,
                        listeners: {
                            keyup: {
                                fn: me.updatePasswordStatus,
                                scope: me
                            },
                            'validitychange': me.passwordValidityChanged
                        }
                    },
                    {
                        xtype: 'container',
                        itemId: 'passwordStatus',
                        cls: 'admin-password-meter-status',
                        width: me.messageWidth,
                        tpl: '<div class="passwordStatus" style="color: {color};">{text}</div>'
                    }
                ]
            },
            {
                xtype: 'fieldcontainer',
                layout: {
                    type: 'table',
                    columns: 2
                },
                items: [
                    {
                        xtype: 'textfield',
                        inputType: 'password',
                        itemId: 'repeatInput',
                        fieldLabel: me.repeatLabel,
                        name: me.repeatName,
                        width: me.inputWidth,
                        labelWidth: me.labelWidth,
                        enableKeyEvents: true,
                        allowBlank: false,
                        submitValue: false,
                        validator: me.validateRepeat,
                        validateOnBlur: false,
                        listeners: {
                            keyup: {
                                fn: me.updateRepeatStatus,
                                scope: me
                            },
                            'validitychange': me.repeatValidityChanged
                        }
                    },
                    {
                        xtype: 'container',
                        itemId: 'repeatStatus',
                        tpl: '<div class="repeatStatus {type}">{text}</div> ',
                        width: me.messageWidth,
                        cls: 'admin-validation-label',
                        margin: '0 0 0 5'
                    }
                ]
            }
        ];
        me.callParent( arguments );
        me.addEvents( 'validitychange' );
    },


    updatePasswordStatus: function( field, event, opts )
    {
        var data;
        if ( !Ext.isEmpty( field.getValue() ) ) {

            var passwordLevel = this.calculatePasswordStrength( field.getValue() );
            data = this.passwordStatuses[ passwordLevel ];
            var input = field.el.down( 'input' );
            if ( passwordLevel == 5 ) {
                input.addCls( 'admin-password-extra-strong' );
            } else {
                input.removeCls( 'admin-password-extra-strong' )
            }

        } else {
            data = { text: '' }
        }
        this.getPasswordStatus().update( data );
    },

    calculatePasswordStrength: function( pwd )
    {
        var featuresNumber = 0;

        // Calculating feature count
        // Has text and numbers
        if ( pwd.match( /\d+/g ) && pwd.match( /[A-Za-z]+/g ) ) {
            featuresNumber += 1;
        }
        // Has special chars
        if ( pwd.match( /[\]\[!"#$%&'()*+,.\/:;<=>?@\^_`{|}~-]+/g ) ) {
            featuresNumber += 1;
        }
        // Has at least two "text" and two "number" characters
        if ( pwd.match( /\d\d+/g ) && pwd.match( /[A-Za-z]+/g ) ) {
            featuresNumber += 1;
        }
        // Has both uppercase and lower case text
        if ( pwd.match( /[A-Z]+/g ) && pwd.match( /[a-z]+/g ) ) {
            featuresNumber += 1;
        }
        // Calculating level
        if ( (pwd.length >= 12) && (featuresNumber >= 4) ) {
            return 5;
        }
        if ( (pwd.length >= 10) && (featuresNumber >= 3) ) {
            return 4;
        }
        if ( (pwd.length >= 10) && (featuresNumber >= 2) ) {
            return 3;
        }
        if ( (pwd.length >= 8) && (featuresNumber >= 1) ) {
            return 2;
        }
        if ( (pwd.length >= 6) && (pwd.match( /\d+/g ) || pwd.match( /[A-Za-z]+/g )) ) {
            return 1;
        }

        return 0;
    },

    validatePassword: function( value )
    {
        var parent = this.up( 'doublePasswordField' );
        if ( this.isDirty() ) {
            parent.updatePasswordStatus( parent.getPasswordField() );
        }

        var repeat = parent.getRepeatField();
        if ( repeat.isDirty() ) {
            repeat.validate();
        }
        return true;
    },

    passwordValidityChanged: function( field, isValid, opts )
    {
        var parent = field.up( 'doublePasswordField' );
        var wasValid = parent.isPasswordValid && parent.isRepeatValid;
        var nowValid = isValid && parent.isRepeatValid;
        parent.isPasswordValid = isValid;

        // fire change if both fields became either valid or invalid
        if ( wasValid != nowValid ) {
            parent.fireEvent( 'validitychange', parent, nowValid );
        }
    },


    updateRepeatStatus: function( field, event, opts )
    {
        var isSpecialKey = event && event.isSpecialKey()
                                   && event.getKey() != event.BACKSPACE && event.getKey() != event.DELETE;
        if ( isSpecialKey ) {
            return;
        }
        var status = this.getRepeatStatus();
        if ( !Ext.isEmpty( field.getValue() ) ) {
            status.update( { type: 'loading', text: '' } );
            this.updateRepeatDelayed.delay( this.repeatDelay );
        } else {
            status.update( { type: '', text: '' } );
        }
    },

    testEqual: function( value1, value2 )
    {
        return value1 === value2;
    },

    validateRepeat: function( value )
    {
        var parent = this.up( 'doublePasswordField' );
        if ( this.isDirty() ) {
            parent.updateRepeatStatus( parent.getRepeatField() );
        }
        return parent.testEqual( value, parent.getPasswordField().getValue() );
    },

    repeatValidityChanged: function( field, isValid, opts )
    {
        var parent = field.up( 'doublePasswordField' );
        var wasValid = parent.isPasswordValid && parent.isRepeatValid;
        var nowValid = parent.isPasswordValid && isValid;
        parent.isRepeatValid = isValid;

        // fire change if both fields became either valid or invalid
        if ( wasValid != nowValid ) {
            parent.fireEvent( 'validitychange', parent, nowValid );
        }
    },


    getPasswordField: function()
    {
        return this.down( '#passwordInput' );
    },

    getPasswordStatus: function()
    {
        return this.down( '#passwordStatus' );
    },

    getRepeatField: function()
    {
        return this.down( '#repeatInput' );
    },

    getRepeatStatus: function()
    {
        return this.down( '#repeatStatus' );
    },

    getValue: function()
    {
        return this.getPasswordField().getValue();
    },

    setValue: function( value )
    {
        this.getPasswordField().setValue( value );
        this.getRepeatField().setValue( value );
    },

    validate: function()
    {
        return this.getPasswordField().validate()
                && this.getRepeatField().validate();
    },

    reset: function()
    {
        this.getPasswordField().reset();
        this.getPasswordStatus().update( { color: 'black', text: '' } );
        this.getRepeatField().reset();
        this.getRepeatStatus().update( { text: '' } );
    }

} );

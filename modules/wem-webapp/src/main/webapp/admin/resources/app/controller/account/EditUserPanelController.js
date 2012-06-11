Ext.define( 'Admin.controller.account.EditUserPanelController', {
    extend: 'Admin.controller.account.Controller',

    /*      Controller for handling Edit User Panel UI events       */

    stores: [],
    models: [],
    views: [
        'Admin.view.account.SelectUserStoreWindow'
    ],

    init: function()
    {

        this.control(
                {
                    'editUserFormPanel addressPanel #isoCountry': {
                        select: this.countryChangeHandler
                    },
                    'editUserFormPanel addressContainer button[action=addNewAddress]': {
                        click: this.addNewAddress
                    },
                    'editUserFormPanel textfield[name=label]': {
                        keyup: this.updateTabTitle
                    },
                    'editUserFormPanel button[action=closeUserForm]': {
                        click: this.closeUserForm
                    },
                    'editUserFormPanel userFormField[action=initValue]': {
                        added: this.initValue
                    },
                    'editUserFormPanel userFormField': {
                        validitychange: this.userFieldValidityChange
                    }
                }
        );

    },

    countryChangeHandler: function( country, selected, options )
    {
        var region = country.up( 'addressPanel' ).down( '#isoRegion' );
        if ( region ) {
            var regionStore = region.getStore();
            regionStore.removeAll();
            var regions = [];
            for ( var i = 0; i < selected.length; i++ ) {
                regions = regions.concat( selected[ i ].regions().getRange() );
            }
            region.clearValue();
            region.setDisabled( regions.length == 0 );
            if ( regions.length > 0 ) {
                regionStore.add( regions );
            }
        }
        return true;
    },

    updateTabTitle: function( field, event )
    {
        var addressPanel = field.up( 'addressPanel' );
        addressPanel.setTitle( field.getValue() );
    },

    closeUserForm: function( button, event )
    {
        this.getCmsTabPanel().getActiveTab().close();
    },

    initValue: function( field )
    {
        var formField = field.up( 'userFormField' );
        field.valueNotFoundText = formField.fieldValue;
        field.setValue( formField.fieldValue );
    },

    addNewAddress: function( button, event )
    {
        var wizardPanel = button.up( 'wizardPanel' );
        if ( wizardPanel ) {
            wizardPanel.fireEvent( 'dirtychange', wizardPanel, true );
        }

        var container = button.up( 'addressContainer' );
        var closable = container.down( 'addressColumn' ).items.getCount() != 0;
        var newTab = this.getEditUserFormPanel().generateAddressPanel( container.sourceField, closable );
        container.down( 'addressColumn' ).add( newTab );
    },

    userFieldValidityChange: function( field, isValid )
    {
        if ( field.fieldname === 'repeatPassword' ) {
            var repeatPassword = field.up( 'fieldset' ).down( '#repeatPassword' );
            var passwordMeter = field.up( 'fieldset' ).down( '#password' );
            if ( repeatPassword ) {
                repeatPassword.validate();
            }
            if ( passwordMeter ) {
                passwordMeter.validate();
            }

        }
    },


    /*      Getters     */

    getEditUserFormPanel: function()
    {
        return Ext.ComponentQuery.query( 'editUserFormPanel' )[0];
    }

} );

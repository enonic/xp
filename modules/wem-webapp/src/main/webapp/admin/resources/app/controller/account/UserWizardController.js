Ext.define( 'Admin.controller.account.UserWizardController', {
    extend:'Admin.controller.account.UserController',

    /*      Controller for handling User Wizard UI events       */

    requires: [
        'Admin.plugin.Diff'
    ],

    stores:[
        'Admin.store.account.UserstoreConfigStore',
        'Admin.store.account.CountryStore',
        'Admin.store.account.RegionStore',
        'Admin.store.account.TimezoneStore',
        'Admin.store.account.LocaleStore'
    ],
    models:[
        'Admin.model.account.UserstoreConfigModel',
        'Admin.model.account.CountryModel',
        'Admin.model.account.RegionModel',
        'Admin.model.account.TimezoneModel',
        'Admin.model.account.LocaleModel'
    ],
    views:[],

    EMPTY_DISPLAY_NAME_TEXT:'Display Name',

    init:function ()
    {
        var me = this;
        me.control( {
            'userWizardPanel *[action=saveUser]':{
                click:function ( el, e )
                {
                    me.saveUser( el.up( 'userWizardPanel' ), false );
                }
            },
            'userWizardPanel *[action=changePassword]':{
                click:me.changePassword
            },
            'userWizardPanel *[action=deleteUser]':{
                click:me.deleteUser
            },
            'userWizardPanel':{
                afterrender:me.bindDisplayNameEvents
            },
            'userWizardPanel wizardPanel':{
                beforestepchanged:me.validateStep,
                stepchanged:me.stepChanged,
                finished:function ( wizard, data )
                {
                    me.saveUser( wizard.up( 'userWizardPanel' ), true );
                },
                validitychange:this.validityChanged,
                dirtychange:this.dirtyChanged
            },
            // For unknown reason selector 'userWizardPanel editUserFormPanel' doesn't work
            'userWizardPanel editUserFormPanel':{
                fieldsloaded:{
                    fn:me.userStoreFieldsLoaded,
                    scope:me
                }
            },
            'userWizardPanel *[action=newGroup]':{
                click:me.createNewGroup
            },
            'userWizardPanel *[action=closeWizard]':{
                click:me.closeWizard
            }
        } );

        me.application.on( {
            userWizardNext:{
                fn:me.wizardNext,
                scope:me
            },
            userWizardPrev:{
                fn:me.wizardPrev,
                scope:me
            }
        } );
    },

    saveUser:function ( userWizard, closeWizard )
    {
        var me = this;
        var wizardPanel = userWizard.getWizardPanel();
        var data = userWizard.getData();
        data['displayName'] = this.getDisplayNameValue( userWizard );
        var step = wizardPanel.getLayout().getActiveItem();
        if ( Ext.isFunction( step.getData ) ) {
            Ext.merge( data, step.getData() );
        }

        var parentApp = parent.mainApp;
        var onUpdateUserSuccess = function ( key )
        {
            wizardPanel.addData( {
                'key':key
            } );
            if ( closeWizard ) {
                me.getUserWizardTab().close();
            }
            if ( parentApp ) {
                parentApp.fireEvent( 'notifier.show', "User was saved",
                        "Something just happened! Li Europan lingues es membres del sam familie. Lor separat existentie es un myth.",
                        false );
            }
        };
        this.saveUserToDB( data, onUpdateUserSuccess );
    },

    deleteUser:function ( el, e )
    {
        var userWizard = el.up( 'userWizardPanel' );
        if ( userWizard && userWizard.userFields ) {
            this.showDeleteAccountWindow( {data:userWizard.userFields} );
        }
    },

    changePassword:function ( el, e )
    {
        var userWizard = el.up( 'userWizardPanel' );
        if ( userWizard && userWizard.userFields ) {
            this.showChangePasswordWindow( {data:userWizard.userFields} );
        }
    },

    validateStep:function ( wizard, step )
    {
        var data = undefined;
        if ( step.getData ) {
            data = step.getData();
        }
        if ( data ) {
            wizard.addData( data );
        }
        return true;
    },

    stepChanged:function ( wizard, oldStep, newStep )
    {
        var userWizard = wizard.up( 'userWizardPanel' );

        userWizard.addStickyNavigation( userWizard );

        if ( newStep.getXType() === 'wizardStepProfilePanel' ) {
            // move to 1st step
            userWizard.setFileUploadDisabled( true );
        }

        if ( newStep.getXType() === 'summaryTreePanel' ) {
            var treePanel = newStep;
            // Can not re-use data object each time the rootnode is set
            // This somewhat confuses the store. Clone for now.
            treePanel.getStore().setRootNode( Admin.plugin.Diff.compareUsers( userWizard.getData(),
                    userWizard.userFields ) );
        }

        // oldStep can be null for first page
        if ( oldStep && oldStep.getXType() === 'userStoreListPanel' ) {
            // move from 1st step
            userWizard.setFileUploadDisabled( false );
        }

        // auto-suggest username
        if ( ( oldStep && oldStep.itemId === 'profilePanel' ) && newStep.itemId === 'userPanel' ) {
            var formPanel = wizard.down( 'editUserFormPanel' );
            var firstName = formPanel.down( '#firstName' );
            var firstNameValue = firstName ? Ext.String.trim( firstName.getValue() ) : '';
            var lastName = formPanel.down( '#lastName' );
            var lastNameValue = lastName ? Ext.String.trim( lastName.getValue() ) : '';
            var userStoreName = wizard.getData()['userStore'];
            var usernameField = wizard.down( '#username' );
            if ( firstNameValue || lastNameValue ) {
                this.autoSuggestUsername( firstNameValue, lastNameValue, userStoreName, usernameField );
            }
        }
    },

    validityChanged:function ( wizard, valid )
    {
        // Need to go this way up the hierarchy in case there are multiple wizards
        var tb = wizard.up( 'userWizardPanel' ).down( 'userWizardToolbar' );
        var pb = wizard.getProgressBar();
        var save = tb.down( '#save' );
        var finish = wizard.down( '#controls #finish' );
        var conditionsMet = valid && ( wizard.isWizardDirty || wizard.isNew );
        if ( save )
            save.setDisabled( !conditionsMet );
        if ( finish )
            finish.setVisible( conditionsMet );
        pb.setDisabled( wizard.isNew ? !wizard.isStepValid() : !conditionsMet );
    },

    dirtyChanged:function ( wizard, dirty )
    {
        var tb = wizard.up( 'userWizardPanel' ).down( 'userWizardToolbar' );
        var pb = wizard.getProgressBar();
        var save = tb.down( '#save' );
        var finish = wizard.down( '#controls #finish' );
        var conditionsMet = (dirty || wizard.isNew ) && wizard.isWizardValid;
        if ( save )
            save.setDisabled( !conditionsMet );
        if ( finish )
            finish.setVisible( conditionsMet );
        pb.setDisabled( wizard.isNew ? !wizard.isStepValid() : !conditionsMet );
    },

    wizardPrev:function ( btn, evt )
    {
        var wizard = this.getUserWizardPanel().getWizardPanel();
        wizard.prev( btn );
    },

    wizardNext:function ( btn, evt )
    {
        var wizard = this.getUserWizardPanel().getWizardPanel();
        if ( wizard.isStepValid() ) {
            wizard.next( btn );
        }
    },

    bindDisplayNameEvents:function ( wizard )
    {
        var wizardId = wizard.getId();
        var displayName = Ext.query( '#' + wizardId + ' input.cms-display-name' );
        if ( displayName ) {
            Ext.Element.get( displayName ).on( 'blur', this.displayNameBlur, this, {wizard:wizard} );
            Ext.Element.get( displayName ).on( 'focus', this.displayNameFocus, this );
            Ext.Element.get( displayName ).on( 'change', this.displayNameChanged, this, {wizard:wizard} );
        }
    },

    hasDefaultDisplayName:function ( displayNameInputField )
    {
        var text = Ext.String.trim( displayNameInputField.value );
        return (text === this.EMPTY_DISPLAY_NAME_TEXT);
    },

    setDefaultDisplayName:function ( wizard )
    {
        this.updateWizardHeader( wizard, {displayName:this.EMPTY_DISPLAY_NAME_TEXT, edited:false} );
    },

    displayNameFocus:function ( event, element )
    {
        if ( this.hasDefaultDisplayName( element ) ) {
            element.value = '';
        }
    },

    displayNameBlur:function ( event, element, opts )
    {
        var text = Ext.String.trim( element.value );
        if ( text === '' ) {
            var sourceFields = this.getDisplayNameSourceFields( opts.wizard );
            var autogeneratedDispName = this.autoGenerateDisplayName( sourceFields );
            if ( autogeneratedDispName === '' ) {
                this.setDefaultDisplayName( opts.wizard );
            } else {
                this.updateWizardHeader( opts.wizard, {displayName:autogeneratedDispName, edited:true} );
            }
        }
    },

    displayNameChanged:function ( event, element, opts )
    {
        if ( element.value === '' ) {
            opts.wizard.displayNameAutoGenerate = true;
        } else {
            opts.wizard.displayNameAutoGenerate = false;
        }
    },

    userStoreFieldsLoaded:function ( target )
    {
        var userWizard = target.up( 'userWizardPanel' );
        if ( !userWizard.isNewUser() ) {
            var fields = this.getDisplayNameSourceFields( userWizard );
            var displayNameValue = userWizard.getData().displayName;
            var generatedDisplayName = this.autoGenerateDisplayName( fields );
            if ( generatedDisplayName !== displayNameValue ) {
                userWizard.displayNameAutoGenerate = false;
            }
        }

        userWizard.getWizardPanel().focusFirstField();
        this.bindFormEvents( target );
    },

    getDisplayNameSourceFields:function ( wizard )
    {
        var fields = [];
        var firstStep = wizard.getSteps().get( 0 ).itemId;
        if ( firstStep == 'userPanel' ) {
            fields = wizard.query( '#username' );
        }
        else if ( firstStep == 'profilePanel' ) {
            fields = wizard.query( '#prefix , #firstName , #middleName , #lastName , #suffix' )
        }
        return fields;
    },

    bindFormEvents:function ( form )
    {
        var me = this;
        var fields = me.getDisplayNameSourceFields( form.up( 'userWizardPanel' ) );
        Ext.Array.each( fields, function ( item )
        {
            item.on( 'change', me.profileNameFieldChanged, me );
        } );
    },

    profileNameFieldChanged:function ( field )
    {
        var userWizard = field.up( 'userWizardPanel' );
        var fields = this.getDisplayNameSourceFields( userWizard );

        if ( !userWizard.displayNameAutoGenerate ) {
            return;
        }

        var displayNameValue = this.autoGenerateDisplayName( fields );

        if ( displayNameValue !== '' ) {
            this.updateWizardHeader( userWizard, {displayName:displayNameValue, edited:true} );
        } else {
            this.updateWizardHeader( userWizard, {displayName:this.EMPTY_DISPLAY_NAME_TEXT, edited:false} );
        }
    },

    autoGenerateDisplayName:function ( fields )
    {
        var displayNameValue = Ext.Array.pluck( fields, 'value' ).join( ' ' );
        return Ext.String.trim( displayNameValue.replace( /  /g, ' ' ) );
    },

    getDisplayNameValue:function ( userWizard )
    {
        var wizardPanelId = userWizard.getId();
        var displayNameField = Ext.query( '#' + wizardPanelId + ' input.cms-display-name' )[0];
        var displayName = displayNameField.value;
        return displayName === this.EMPTY_DISPLAY_NAME_TEXT ? '' : displayName;
    },

    usernameFieldChanged:function ( field )
    {
        var userWizard = field.up( 'userWizardPanel' );
        this.updateWizardHeader( userWizard, {qUserName:field.value} );
    },

    updateWizardHeader:function ( wizard, data )
    {
        wizard.updateHeader( data );
        this.bindDisplayNameEvents( wizard );
    },

    updateTabTitle:function ( field, event )
    {
        var addressPanel = field.up( 'addressPanel' );
        addressPanel.setTitle( field.getValue() );
    },

    autoSuggestUsername:function ( firstName, lastName, userStoreName, usernameField )
    {
        if ( usernameField.getValue() !== '' ) {
            return;
        }

        Ext.Ajax.request( {
            url:'data/account/suggestusername',
            method:'GET',
            params:{
                'firstname':firstName,
                'lastname':lastName,
                'userstore':userStoreName
            },
            success:function ( response )
            {
                var respObj = Ext.decode( response.responseText, true );
                if ( usernameField.getValue() === '' ) {
                    usernameField.setValue( respObj.username );
                }
            }
        } );
    },

    createNewGroup:function ( el, e )
    {
        this.showNewAccountWindow( 'group' );
    },

    closeWizard:function ( el, e )
    {
        var tab = this.getUserWizardTab();
        var userWizard = this.getUserWizardPanel();
        if ( userWizard.getWizardPanel().isWizardDirty ) {
            Ext.Msg.confirm( 'Close wizard', 'There are unsaved changes, do you want to close it anyway ?',
                    function ( answer )
                    {
                        if ( 'yes' == answer ) {
                            tab.close();
                        }
                    } );
        } else {
            tab.close();
        }
    },


    /*      Getters     */

    getUserWizardTab:function ()
    {
        return this.getCmsTabPanel().getActiveTab();
    },

    getUserWizardPanel:function ()
    {
        return this.getUserWizardTab().items.get( 0 );
    }

} );

Ext.define( 'Admin.controller.contentManager.ContentWizardController', {
    extend: 'Admin.controller.contentManager.ContentController',

    /*      Controller for handling Content Wizard UI events       */


    stores: [
    ],
    models: [
    ],
    views: [],


    init: function()
    {
        var me = this;
        me.control( {
            'contentWizardPanel *[action=closeWizard]': {
                click: me.closeWizard
            },
            'contentWizardToolbar *[action=duplicateContent]': {
                click: function( el, e )
                {
                    me.duplicateContent( this.getContentWizardPanel().data );
                }
            },
            'contentWizardToolbar *[action=deleteContent]': {
                click: function( el, e )
                {
                    this.deleteContent( this.getContentWizardPanel().data );
                }
            }
        } );

        me.application.on( {
        } );
    },

    closeWizard: function( el, e )
    {
        var tab = this.getContentWizardTab();
        var contentWizard = this.getContentWizardPanel();
        if ( contentWizard.getWizardPanel().isWizardDirty ) {
            Ext.Msg.confirm( 'Close wizard', 'There are unsaved changes, do you want to close it anyway ?',
                    function( answer )
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

    getContentWizardTab: function()
    {
        return this.getCmsTabPanel().getActiveTab();
    },

    getContentWizardPanel: function()
    {
        return this.getContentWizardTab();
    }

} );

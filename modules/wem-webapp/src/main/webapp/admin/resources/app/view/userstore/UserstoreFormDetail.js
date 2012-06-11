Ext.define( 'Admin.view.userstore.UserstoreFormDetail', {
    extend: 'Ext.form.Panel',
    alias: 'widget.userstoreFormDetail',

    layout: 'accordion',
    defaults: {
        bodyPadding: 10
    },

    items: [
        {
            title: 'Detail',
            layout: 'anchor',
            items: [
                {
                    xtype: 'fieldset',
                    title: 'Userstore',
                    defaults: {
                        xtype: 'displayfield',
                        anchor: '100%',
                        readOnly: true
                    },
                    items: [
                        {
                            fieldLabel: 'Users',
                            name: 'userCount'
                        },{
                            fieldLabel: 'Groups',
                            name: 'groupCount'
                        },
                        {
                            fieldLabel: 'Last modified',
                            name: 'lastModified'
                        }
                    ]
                },{
                    xtype: 'fieldset',
                    title: 'Connector',
                    defaults: {
                        xtype: 'displayfield',
                        anchor: '100%',
                        readOnly: true
                    },
                    items: [
                        {
                            fieldLabel: 'Name',
                            name: 'connectorName'
                        },{
                            fieldLabel: 'Plugin',
                            name: 'plugin'
                        },
                        {
                            fieldLabel: 'User Policy',
                            name: 'userPolicy'
                        },
                        {
                            fieldLabel: 'Group Policy',
                            name: 'groupPolicy'
                        }
                    ]
                }
            ]
        },
        {
            title: 'Synchronize',
            html: 'panel 2'
        }
    ],

    initComponent: function() {

        this.callParent( arguments );

        if ( this.userstore ) {
            this.setUserstore( this.userstore )
        }
    },

    setUserstore: function ( u ) {
        this.userstore = u;
        this.getForm().setValues( u.data );
    }

});
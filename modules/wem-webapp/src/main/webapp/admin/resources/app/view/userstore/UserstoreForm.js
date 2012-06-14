Ext.define( 'Admin.view.userstore.UserstoreForm', {
    extend: 'Ext.form.Panel',
    alias: 'widget.userstoreForm',

    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    border: false,

    items: [
            {

                xtype: 'panel',
                cls: 'admin-userstore-info',
                styleHtmlContent: true,
                itemId: 'headerPanel',
                tpl: new Ext.XTemplate( Templates.userstore.editFormHeader ),
                border: false,
                bodyPadding: '0 0 10 0',
                height: 90,
                bbar: [
                    {
                        text: 'Save',
                        iconCls: 'icon-save',
                        action: 'saveUserstore'
                    },
                    {
                        text: 'Cancel',
                        iconCls: 'icon-cancel',
                        action: 'cancelUserstore'
                    },
                        '->',
                    {
                        text: 'Delete',
                        iconCls: 'icon-delete',
                        action: 'deleteUserstore'
                    }
                ]
            },
            {
                xtype: 'fieldset',
                title: 'Userstore',
                layout: 'anchor',
                margins: '10 0 0',
                defaults: {
                    anchor: '100%',
                    enableKeyEvents: true
                },
                items: [
                    {
                        xtype: 'hiddenfield',
                        name: 'key'
                    },
                    {
                        xtype: 'textfield',
                        fieldLabel: 'Name',
                        name: 'name',
                        vtype: 'alphanum',
                        allowBlank: false
                    },
                    {
                        xtype: 'combo',
                        fieldLabel: 'Connector',
                        name: 'connectorName',
                        triggerAction: 'all',
                        queryMode: 'local',
                        typeAhead: true,
                        valueField: 'name',
                        displayField: 'name',
                        store : 'UserstoreConnectorStore',
                        allowBlank: false,
                        forceSelection: true
                    }
                    ,{
                        xtype: 'checkbox',
                        itemId: 'defaultCheckbox',
                        fieldLabel: 'Set as default',
                        name: 'defaultStore'
                    }
                ]
            },
            {
                xtype: 'fieldset',
                title: 'Form configuration',
                layout: 'fit',
                flex: 1,
                style: 'padding-bottom: 30',
                items: [
                    {
                        xtype: 'textarea',
                        fieldLabel: 'XML',
                        name: 'configXML'
                    }
                ]
            }
        ],


    initComponent: function() {

        Ext.apply(this.initialConfig, {
            method: "POST",
            url: '/admin/data/userstore/config'
        });

        if ( !this.userstore ) {
            this.userstore = { data: {
                name: 'Userstore',
                connectorName: "local"
            } };
        }

        this.callParent( arguments );

        this.setUserstore( this.userstore );
        this.updateUserstoreHeader( this.userstore );
    },

    setUserstore: function ( u ) {
        this.userstore = u;
        this.getForm().setValues( u.data );
        this.getForm().findField('defaultStore').setReadOnly( u.data.defaultStore );
    },

    updateUserstoreHeader: function ( u ) {
        this.child('#headerPanel').update( u.data );
    }

});

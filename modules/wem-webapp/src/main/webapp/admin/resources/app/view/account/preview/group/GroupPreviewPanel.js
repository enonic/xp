Ext.define( 'Admin.view.account.preview.group.GroupPreviewPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.groupPreviewPanel',

    requires: [
        'Admin.view.account.preview.group.GroupPreviewToolbar',
        'Admin.view.WizardPanel'
    ],

    autoWidth: true,
    autoScroll: true,

    cls: 'cms-user-preview-panel',
    width: undefined,

    showToolbar: true,

    initComponent: function()
    {
        if ( this.data && this.data.type == 'role' ) {
            this.data.staticDesc = this.getRoleDescription( this.data.name );
        }
        this.items = [
            {
                xtype: 'panel',
                layout: {
                    type: 'column',
                    columns: 3
                },
                defaults: {
                    border: 0
                },
                items: [
                    {
                        width: 100,
                        itemId: 'previewPhoto',
                        tpl: Templates.account.userPreviewPhoto,
                        data: this.data,
                        margin: 5
                    },
                    {
                        columnWidth: 1,
                        cls: 'center',
                        xtype: 'panel',
                        defaults: {
                            border: 0
                        },
                        items: [
                            {
                                height: 70,
                                itemId: 'previewHeader',
                                tpl: Templates.account.userPreviewHeader,
                                data: this.data
                            },
                            {
                                flex: 1,
                                cls: 'center',
                                xtype: 'tabpanel',
                                items: [
                                    {
                                        title: "Memberships",
                                        itemId: 'membershipsTab',
                                        tpl: Templates.account.groupPreviewMemberships,
                                        data: this.data
                                    }
                                ]
                            }
                        ]
                    },
                    {
                        width: 300,
                        margin: 5,
                        itemId: 'previewInfo',
                        cls: 'east',
                        tpl: Templates.account.groupPreviewCommonInfo,
                        data: this.data
                    }
                ]
            }
        ];

        if ( this.showToolbar ) {
            this.tbar = {
                xtype: 'groupPreviewToolbar',
                isEditable: this.data.isEditable,
                isRole: this.data.type == 'role'
            };
        }

        this.callParent( arguments );
    },


    setData: function( data )
    {
        if ( data ) {
            this.data = data;

            var previewHeader = this.down( '#previewHeader' );
            previewHeader.update( data );

            var previewPhoto = this.down( '#previewPhoto' );
            previewPhoto.update( data );

            var previewInfo = this.down( '#previewInfo' );
            previewInfo.update( data );

            var membershipsTab = this.down( '#membershipsTab' );
            membershipsTab.update( data );
        }
    },

    //TODO: Should be replaced, better move to some kind of service
    getRoleDescription: function( name )
    {
        if ( name === 'Contributors' ) {
            return 'Sed at commodo arcu. Integer mattis lorem pharetra ligula dignissim. ';
        }
        if ( name === 'Developers' ) {
            return 'Curabitur suscipit condimentum ultrices. Nam dolor sem, suscipit ac faucibus. ';
        }
        if ( name === 'Enterprise Administrators' ) {
            return 'Mauris pellentesque diam in ligula pulvinar luctus. Donec ac elit. ';
        }
        if ( name === 'Expert Contributors' ) {
            return 'Morbi vulputate purus non neque dignissim eu iaculis sapien auctor. ';
        }
        return 'Vivamus tellus turpis, varius vel hendrerit et, commodo vitae ipsum.';
    }

} );
Ext.define( 'Admin.view.BaseDialogWindow', {
    extend: 'Ext.window.Window',
    alias: 'widget.baseDialogWindow',

    border: false,
    padding: 1,

    draggable: false,
    closable: false,
    width: 500,
    modal: true,
    modelData: undefined,

    autoHeight: true,
    maxHeight: 350,
    autoScroll: true,

    cls: 'cms-dialog-window',
    closeAction: 'hide',
    bodyPadding: 10,
    bodyStyle: 'background: #fff;',

    dialogTitle: 'Base dialog',
    dialogSubTitle: '',
    dialogInfoTpl: Templates.common.userInfo,

    listeners: {
        show: function( cmp )
        {
            var header = this.down( '#dialogHeader' );
            if ( header )
            {
                header.doLayout();
            }
            var info = this.down( '#dialogInfo' );
            if ( info )
            {
                info.doLayout();
            }
            var form = cmp.down( 'form' );
            if ( form )
            {
                form.getForm().reset();
                form.doLayout();
                var firstField = form.down( 'field' );
                if ( firstField )
                {
                    firstField.focus();
                }
            }
        },
        resize: function( window )
        {
            // Support maxHeight which is not actually supported for Window with autoHeight set to true.
            if ( this.getHeight() > this.maxHeight )
            {
                this.setHeight( this.maxHeight );
            }

            this.center();
        }
    },

    initComponent: function()
    {
        var me = this;
        me.dockedItems = [];
        Ext.Array.insert(this.dockedItems, 0, [{
            xtype: 'toolbar',
            dock: 'right',
            autoHeight: true,
            items: [
                {
                    scale: 'medium',
                    iconAlign: 'top',
                    text: 'Close',
                    action: 'close',
                    iconCls: 'icon-close',
                    listeners: {
                        click: function( btn, evt )
                        {
                            btn.up( 'baseDialogWindow' ).close();
                        }
                    }
                }
            ]
        }]);


        var dialogHtml = '<h3>' + me.dialogTitle + '</h3>'
        dialogHtml += (Ext.isEmpty(me.dialogSubTitle)) ? '' : '<h4>' + me.dialogSubTitle + '</h4>';

        Ext.Array.insert( this.items, 0, [
            {
                itemId: 'dialogHeader',
                xtype: 'container',
                cls: 'dialog-header',
                styleHtmlContent: true,
                html: dialogHtml
            }

        ] );
        if (this.dialogInfoTpl) {
            Ext.Array.insert( this.items, 1, [{
                itemId: 'dialogInfo',
                cls: 'dialog-info',
                xtype: 'container',
                border: false,
                height: 80,
                styleHtmlContent: true,
                tpl: new Ext.XTemplate( me.dialogInfoTpl )
            }]);
        }

        this.callParent( arguments );
    },

    setDialogInfoTpl: function( tpl )
    {
        var dialogInfo = this.down( '#dialogInfo' )
        dialogInfo.tpl = new Ext.XTemplate( tpl );
    },

    doShow: function( model )
    {
        if ( model )
        {
            this.modelData = model.data;
            var info = this.down( '#dialogInfo' );
            if ( info )
            {
                info.update( this.modelData );
            }
        }
        this.show();
    }

} );


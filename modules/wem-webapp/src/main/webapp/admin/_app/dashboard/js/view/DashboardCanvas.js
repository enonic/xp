Ext.define( "App.view.DashboardCanvas", {
    extend: "Ext.panel.Panel",
    alias: 'widget.dashboardCanvas',

    layout: 'column',
    defaults: {
        columnWidth: 1 / 3,
        border: false
    },

    requires: [
        "Ext.layout.component.Body",
        "App.view.DropTarget",
        "App.view.DashletColumn"
    ],
    cls: "dashboard",
    bodyCls: "dashboard-body",
    defaultType: "dashletColumn",
    autoScroll: true,

    manageHeight: false,

    initComponent: function()
    {
        var me = this;

        // Implement a Container beforeLayout call from the layout to this Container
        this.layout = {
            type: 'column'
        };
        this.callParent();

        this.addEvents( {
            validatedrop: true,
            beforedragover: true,
            dragover: true,
            beforedrop: true,
            drop: true
        } );
    },

    beforeLayout: function()
    {
        var items = this.layout.getLayoutItems(),
                len = items.length,
                firstAndLast = ['dashboard-column-first', 'dashboard-column-last'],
                i, item, last;

        for ( i = 0; i < len; i++ ) {
            item = items[i];
            item.columnWidth = 1 / len;
            last = (i == len - 1);

            if ( !i ) { // if (first)
                if ( last ) {
                    item.addCls( firstAndLast );
                } else {
                    item.addCls( 'dashboard-column-first' );
                    item.removeCls( 'dashboard-column-last' );
                }
            } else if ( last ) {
                item.addCls( 'dashboard-column-last' );
                item.removeCls( 'dashboard-column-first' );
            } else {
                item.removeCls( firstAndLast );
            }
        }

        return this.callParent( arguments );
    },

    initEvents: function()
    {
        this.callParent();
        this.dd = Ext.create( "App.view.DropTarget", this, this.dropConfig )
    },

    beforeDestroy: function()
    {
        if ( this.dd ) {
            this.dd.unreg()
        }
        this.callParent();
    }

} );

Ext.define( 'Admin.view.account.AddressDropTarget', {
    extend: 'Ext.dd.DropTarget',

    requires: [
        'Ext.panel.Proxy'
    ],

    ddScrollConfig: {
        vthresh: 50,
        hthresh: -1,
        animate: true,
        increment: 200
    },

    constructor: function( el, config )
    {
        this.portal = el;
        Ext.dd.ScrollManager.register( el.body );
        Admin.view.account.AddressDropTarget.superclass.constructor.call( this, el.body, config );
        el.body.ddScrollConfig = this.ddScrollConfig
    },
    createEvent: function( source, event, data, colIndex, column, pos )
    {
        return {
            portal: this.portal,
            panel: data.panel,
            columnIndex: colIndex,
            column: column,
            position: pos,
            data: data,
            source: source,
            rawEvent: event,
            status: this.dropAllowed
        }
    },
    notifyOver: function( source, event, data )
    {
        var pos = event.getXY(),
                container = this.portal,
                proxy = source.panelProxy;
        if ( !this.grid ) {
            this.grid = this.getGrid()
        }
        var contWidth = container.body.dom.clientWidth;
        if ( !this.lastCW ) {
            this.lastCW = contWidth
        } else {
            if ( this.lastCW != contWidth ) {
                this.lastCW = contWidth;
                this.grid = this.getGrid()
            }
        }
        var o = 0,
                c = 0,
                n = this.grid.columnX,
                q = n.length,
                m = false;
        for ( q; o < q; o++ ) {
            c = n[o].x + n[o].w;
            if ( pos[0] < c ) {
                m = true;
                break
            }
        }
        if ( !m ) {
            o--
        }
        var i, g = 0,
                r = 0,
                l = false,
                firstItem = container.items.getAt( o ),
                contItems = container.items,
                j = false;
        q = contItems.length;
        for ( q; g < q; g++ ) {
            i = contItems.get( g );
            r = i.el.getHeight();
            if ( r === 0 ) {
                j = true
            } else {
                if ( (i.el.getY() + (r / 2)) > pos[1] ) {
                    l = true;
                    break
                }
            }
        }
        g = (l && i ? g : firstItem.getCount()) + (j ? -1 : 0);
        var newEvent = this.createEvent( source, event, data, o, firstItem, g );
        if ( container.fireEvent( "validatedrop", newEvent ) !== false &&
             container.fireEvent( "beforedragover", newEvent ) !== false ) {
            if ( !data.draggedRecord ) {
                proxy.getProxy().setWidth( "auto" );
                if ( i ) {
                    proxy.moveProxy( i.el.dom.parentNode, l ? i.el.dom : null )
                } else {
                    proxy.moveProxy( firstItem.el.dom, null )
                }
            }
            this.lastPos = {
                c: firstItem,
                col: o,
                p: j || (l && i) ? g : false
            };
            this.scrollPos = container.body.getScroll();
            container.fireEvent( "dragover", newEvent );
            return newEvent.status
        } else {
            return newEvent.status
        }
    },
    notifyOut: function()
    {
        delete this.grid
    },
    notifyDrop: function( source, event, data )
    {
        delete this.grid;
        if ( !this.lastPos ) {
            return
        }
        var j = this.portal,
                f = this.lastPos.col,
                k = this.lastPos.p,
                a = source.panel,
                b = this.createEvent( source, event, data, f, j, k !== false ? k : j.items.getCount() );
        if ( this.portal.fireEvent( "validatedrop", b ) !== false &&
             this.portal.fireEvent( "beforedrop", b ) !== false ) {
            if ( !data.draggedRecord ) {
                a.el.dom.style.display = "";
                if ( k !== false ) {
                    j.insert( k, a )
                } else {
                    j.add( a )
                }
                source.proxy.hide();
                source.panelProxy.hide();
            } else {
                var dashlet = Ext.create( data.draggedRecord.data.xtype, {
                    title: g.draggedRecord.data.title,
                    html: g.draggedRecord.data.body
                } );
                Ext.isNumber( k ) ? j.insert( k, dashlet ) : j.add( dashlet );
            }
            this.portal.fireEvent( "drop", b );
            var m = this.scrollPos.top;
            if ( m ) {
                var i = this.portal.body.dom;
                setTimeout( function()
                {
                    i.scrollTop = m
                }, 10 )
            }
        }
        delete this.lastPos;
        var containerItems = this.portal.getItems();
        Ext.Array.each( containerItems, function( item, index )
        {
            if ( index == 0 ) {
                item.setClosable( false );
                item.addCls( 'remote' );
            }
            else {
                item.setClosable( true );
                item.removeCls( 'remote' );
            }
        } );
        return true
    },
    getGrid: function()
    {
        var a = this.portal.body.getBox();
        a.columnX = [];
        this.portal.items.each( function( b )
        {
            a.columnX.push( {
                x: b.el.getX(),
                w: b.el.getWidth()
            } )
        } );
        return a
    },
    unreg: function()
    {
        Ext.dd.ScrollManager.unregister( this.portal.body );
        Admin.view.account.AddressDropTarget.superclass.unreg.call( this )
    }
} );
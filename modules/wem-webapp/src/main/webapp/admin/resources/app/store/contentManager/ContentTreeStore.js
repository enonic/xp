Ext.define( 'Admin.store.contentManager.ContentTreeStore', {
    extend: 'Ext.data.TreeStore',

    model: 'Admin.model.contentManager.ContentModel',

    folderSort: true,

    proxy: {
        type: 'ajax',
        url: 'resources/data/contentManagerTreeStub.json',
        reader: {
            type: 'json',
            totalProperty: 'total'
        }
    },

    listeners: {
        beforeappend: function( parentNode, node, opts )
        {
            var iconCls;
            if ( node && Ext.isEmpty( node.get( 'iconCls' ) ) ) {
                switch ( node.get( 'type' ) ) {
                    case 'site':
                        iconCls = 'icon-site-32';
                        break;
                    case 'contentType':
                        iconCls = 'icon-content-32';
                        break;
                    default:
                        iconCls = undefined;
                        break;
                }
                if ( iconCls ) {
                    node.set( 'iconCls', iconCls );
                }
            }
        }
    }

} );
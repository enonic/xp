Ext.define( 'Admin.util.liveedit.Util', {
    singleton: true,

    getIframe: function()
    {
        return Ext.DomQuery.selectNode( '#live-edit-page-frame' );
    },

    getIframeWindow: function()
    {
        return (this.getIframe().contentWindow || this.getIframe().window);
    },

    getIframeDocument: function()
    {
        return (this.getIframe().contentDocument || this.getIframe().document);
    }

});
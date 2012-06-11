// http://www.sencha.com/forum/showthread.php?151211-Reloading-TreeStore-adds-all-records-to-store-getRemovedRecords

Ext.override(Ext.data.TreeStore, {
    load : function(options) {
        options = options || {};
        options.params = options.params || {};

        var me = this, node = options.node || me.tree.getRootNode(), root;

        // If there is not a node it means the user hasnt defined a
        // rootnode yet. In this case lets just
        // create one for them.
        if (!node) {
            node = me.setRootNode({
                expanded : true
            });
        }

        // copied from 4.1.0.BETA to fix delete calls to the proxy for the remove element.
        if (me.clearOnLoad) {
            if (me.clearRemovedOnLoad) {
                // clear from the removed array any nodes that were
                // descendants of the node being reloaded so that they
                // do not get saved on next sync.
                me.clearRemoved(node);
            }
            // temporarily remove the onNodeRemove event listener so
            // that when removeAll is called, the removed nodes do not
            // get added to the removed array
            me.tree.un('remove', me.onNodeRemove, me);
            // remove all the nodes
            node.removeAll(false);
            // reattach the onNodeRemove listener
            me.tree.on('remove', me.onNodeRemove, me);
        }

        Ext.applyIf(options, {
            node : node
        });
        options.params[me.nodeParam] = node ? node.getId() : 'root';

        if (node) {
            node.set('loading', true);
        }

        return me.callParent([options]);
    }
});
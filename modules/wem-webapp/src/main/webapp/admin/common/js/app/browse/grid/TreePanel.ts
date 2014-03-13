module api.app.browse.grid {

    export class TreePanel {

        private extTreePanel: Ext_tree_Panel;

        private treeShowContextMenuListeners: {(event: TreeShowContextMenuEvent):void}[] = [];

        private treeItemDoubleClickedListeners: {(event: TreeItemDoubleClickedEvent):void}[] = [];

        private treeSelectionChangedListeners: {(event: TreeSelectionChangedEvent):void}[] = [];

        private expandedIds: string[] = [];

        constructor(treeStore: Ext_data_TreeStore, columns: any[], keyField: string, treeConfig?: Object) {


            var persistentGridSelectionPlugin = new Admin.plugin.PersistentGridSelectionPlugin({
                keyField: keyField
            });

            this.extTreePanel = <any> new Ext.tree.Panel(Ext.apply({
                xtype: 'treepanel',
                cls: 'admin-tree',
                hideHeaders: true,
                itemId: 'tree',
                useArrows: true,
                border: false,
                rootVisible: false,
                viewConfig: {
                    trackOver: true,
                    stripeRows: true
                },
                store: treeStore,
                columns: columns,
                plugins: [
                    persistentGridSelectionPlugin
                ]
            }, treeConfig));

            api.dom.Window.get().onResized((event: UIEvent) => {
                this.extTreePanel.doComponentLayout();
            });

            this.extTreePanel.addDocked({
                xtype: 'toolbar',
                itemId: 'selectionToolbar',
                cls: 'admin-white-toolbar',
                dock: 'top',
                store: treeStore,
                gridPanel: this.extTreePanel,
                resultCountHidden: true,
                countTopLevelOnly: true,
                plugins: ['gridToolbarPlugin']
            });


            this.extTreePanel.on("selectionchange", this.notifySelectionChanged, this, {buffer: 10});
            this.extTreePanel.on("itemdblclick", this.notifyItemDoubleClicked, this);
            this.extTreePanel.on("itemcontextmenu", this.handleItemContextMenuEvent, this);
            this.extTreePanel.on("itemexpand", this.handleItemExpand, this);
            this.extTreePanel.on("itemcollapse", this.handleItemCollapse, this);

            treeStore.on("load", this.handleStoreLoad, this);
        }

        onTreeSelectionChanged(listener: (event: TreeSelectionChangedEvent)=>void) {
            this.treeSelectionChangedListeners.push(listener);
        }

        unTreeSelectionChanged(listener: (event: TreeSelectionChangedEvent)=>void) {
            this.treeSelectionChangedListeners =
            this.treeSelectionChangedListeners.filter((currentListener: (event: TreeSelectionChangedEvent)=>void)=> {
                return currentListener != listener;
            });
        }

        onTreeItemDoubleClicked(listener: (event: TreeItemDoubleClickedEvent)=>void) {
            this.treeItemDoubleClickedListeners.push(listener);
        }

        unTreeItemDoubleClicked(listener: (event: TreeItemDoubleClickedEvent)=>void) {
            this.treeItemDoubleClickedListeners =
            this.treeItemDoubleClickedListeners.filter((currentListener: (event: TreeItemDoubleClickedEvent)=>void)=> {
                return currentListener != listener;
            });
        }

        onTreeShowContextMenu(listener: (event: TreeShowContextMenuEvent)=>void) {
            this.treeShowContextMenuListeners.push(listener);
        }

        unTreeShowContextMenu(listener: (event: TreeShowContextMenuEvent)=>void) {
            this.treeShowContextMenuListeners =
            this.treeShowContextMenuListeners.filter((currentListener: (event: TreeShowContextMenuEvent)=>void)=> {
                return currentListener != listener;
            })
        }

        private notifySelectionChanged(selectionModel: Ext_selection_Model, models: Ext_data_Model[]) {
            var selectionPlugin = <any>this.extTreePanel.getPlugin('persistentGridSelection');
            this.treeSelectionChangedListeners.forEach((listener: (event: TreeSelectionChangedEvent)=>void)=> {
                listener.call(this, new TreeSelectionChangedEvent(selectionPlugin.getSelectionCount(), selectionPlugin.getSelection()));
            });
        }

        private notifyItemDoubleClicked(view: Ext_view_View, record: Ext_data_Model) {

            this.treeItemDoubleClickedListeners.forEach((listener: (event: TreeItemDoubleClickedEvent)=>void)=> {
                listener.call(this, new TreeItemDoubleClickedEvent(record));
            });
        }

        private handleItemContextMenuEvent(view: Ext_view_View, record: Ext_data_Model, item: HTMLElement, index: number,
                                           event: Ext_EventObject) {
            event.stopEvent();
            this.treeShowContextMenuListeners.forEach((listener: (event: TreeShowContextMenuEvent)=>void)=> {
                listener.call(this, new TreeShowContextMenuEvent(event.getXY()[0], event.getXY()[1]));
            });
        }

        private handleItemExpand(node: Ext_data_NodeInterface, event: Ext_EventObject) {

            var id = this.getNodeId(node);

            for (var i = 0; i < this.expandedIds.length; i++) {
                if (this.expandedIds[i] == id) {
                    return;
                }
            }
            this.expandedIds.push(id);
        }

        private handleItemCollapse(node: Ext_data_NodeInterface, event: Ext_EventObject) {

            var id = this.getNodeId(node);

            for (var i = 0; i < this.expandedIds.length; i++) {
                if (this.expandedIds[i] == id) {
                    this.expandedIds.splice(i, 1);
                    return;
                }
            }
        }

        private handleStoreLoad(store: Ext_data_TreeStore, event: Ext_EventObject) {

            for (var i = 0; i < this.expandedIds.length; i++) {
                var node = store.getNodeById(this.expandedIds[i]);
                if (node && !node.isExpanded() && !node.hasChildNodes()) {
                    node.expand();
                    return;
                }
            }
        }

        private getNodeId(node: Ext_data_NodeInterface): string {
            var path = node.getPath();
            return path.substring(path.lastIndexOf("/") + 1);
        }

        getExt() {
            return this.extTreePanel;
        }
    }
}
module api.app.browse.grid {

    export interface TreeGridPanelParams {

        columns:any[];

        gridStore:Ext_data_Store;

        treeStore:Ext_data_TreeStore;

        gridConfig?:Object;

        treeConfig?:Object;

        contextMenu:api.ui.menu.ContextMenu;
    }

    export class TreeGridPanel extends api.ui.Panel {

        static GRID = "grid";
        static TREE = "tree";

        ext: Ext_panel_Panel;


        private gridStore: Ext_data_Store;
        private gridConfig: Object;
        private columns: any[];
        private treeStore: Ext_data_TreeStore;
        private treeConfig: Object;
        private keyField: string = 'name';
        private activeList: string = "grid";
        private itemId: string;

        private contextMenu: api.ui.menu.ContextMenu;

        private treeGridItemDoubleClickedListeners: {(event: TreeGridItemDoubleClickedEvent):void}[] = [];
        private treeGridSelectionChangedListeners: {(event: TreeGridSelectionChangedEvent):void}[] = [];
        private treeGridStoreLoadedListeners: {():void}[] = [];

        constructor(params: TreeGridPanelParams) {
            super("grid-container");
            this.setScrollY();

            this.gridStore = params.gridStore;
            this.treeStore = params.treeStore;
            this.columns = params.columns;
            this.gridConfig = params.gridConfig;
            this.treeConfig = params.treeConfig;
            this.contextMenu = params.contextMenu;

            this.onRendered((event) => {
                this.createExtGrid();
            });
        }

        private createExtGrid() {

            this.ext = <any> new Ext.panel.Panel({
                cls: 'tree-grid-panel',
                region: 'center',
                renderTo: this.getId(),
                layout: 'card',
                border: false,
                activeItem: this.activeList,
                itemId: this.itemId
            });

            var onLoadListener = (store, node, records, success, opts) => {
                // update the parent node deletable flag if no records were loaded
                if (!records || records.length == 0) {
                    node.set('deletable', true);
                }
                this.notifyTreeGridStoreLoaded();
            };

            var gridPanel = new GridPanel(this.gridStore, this.columns, this.keyField, this.gridConfig);
            this.ext.add(gridPanel.getExt());

            this.gridStore.on('load', onLoadListener);

            var treeColumns = Ext.clone(this.columns);
            treeColumns[0].xtype = 'treecolumn';

            var treePanel = new TreePanel(this.treeStore, treeColumns, this.keyField, this.treeConfig);
            this.ext.add(treePanel.getExt());

            this.treeStore.on('load', onLoadListener);

            treePanel.onTreeSelectionChanged((event: TreeSelectionChangedEvent) => {

                this.notifyTreeGridSelectionChanged(event.getSelectedModels(), event.getSelectionCount());
            });
            treePanel.onTreeItemDoubleClicked((event: TreeItemDoubleClickedEvent) => {

                this.notifyTreeGridItemDoubleClicked(event.getSelectedModel());
            });
            treePanel.onTreeShowContextMenu((event: TreeShowContextMenuEvent) => {

                this.contextMenu.showAt(event.getX(), event.getY());
            });

            gridPanel.onGridSelectionChanged((event: GridSelectionChangedEvent) => {

                this.notifyTreeGridSelectionChanged(event.getSelectedModels(), event.getSelectionCount());
            });
            gridPanel.onGridItemDoubleClicked((event: GridItemDoubleClickedEvent) => {

                this.notifyTreeGridItemDoubleClicked(event.getClickedModel());
            });
            gridPanel.onGridShowContextMenu((event: GridShowContextMenuEvent) => {

                this.contextMenu.showAt(event.getX(), event.getY());
            });

            return this;
        }

        onTreeGridSelectionChanged(listener: (event: TreeGridSelectionChangedEvent)=>void) {
            this.treeGridSelectionChangedListeners.push(listener);
        }

        onTreeGridItemDoubleClicked(listener: (event: TreeGridItemDoubleClickedEvent)=>void) {
            this.treeGridItemDoubleClickedListeners.push(listener);
        }

        unTreeGridSelectionChanged(listener: (event: TreeGridSelectionChangedEvent)=>void) {
            this.treeGridSelectionChangedListeners =
            this.treeGridSelectionChangedListeners.filter((currentListener: (event: TreeGridSelectionChangedEvent)=>void)=> {
                return currentListener != listener;
            });
        }

        unTreeGridItemDoubleClicked(listener: (event: TreeGridItemDoubleClickedEvent)=>void) {
            this.treeGridItemDoubleClickedListeners =
            this.treeGridItemDoubleClickedListeners.filter((currentListener: (event: TreeGridItemDoubleClickedEvent)=>void)=> {
                return currentListener != listener;
            });
        }

        onTreeGridStoreLoaded(listener: ()=>void) {
            this.treeGridStoreLoadedListeners.push(listener);
        }

        unTreeGridStoreLoaded(listener: ()=>void) {
            this.treeGridStoreLoadedListeners =
            this.treeGridStoreLoadedListeners.filter((currentListener: ()=>void)=> {
                return currentListener != listener;
            });
        }

        private notifyTreeGridStoreLoaded() {
            this.treeGridStoreLoadedListeners.forEach((listener: ()=>void) => {
                listener();
            })
        }

        private notifyTreeGridSelectionChanged(selectedModels: Ext_data_Model[], selectionCount: number) {
            this.treeGridSelectionChangedListeners.forEach((listener: (event: TreeGridSelectionChangedEvent)=>void) => {
                listener.call(this, new TreeGridSelectionChangedEvent(selectedModels, selectionCount));
            })
        }

        private notifyTreeGridItemDoubleClicked(clickedModel: Ext_data_Model) {
            this.treeGridItemDoubleClickedListeners.forEach((listener: (event: TreeGridItemDoubleClickedEvent)=>void)=> {
                listener.call(this, new TreeGridItemDoubleClickedEvent(clickedModel));
            });
        }

        getActiveList(): Ext_panel_Table {
            // returns either grid or tree that both extend table
            return <Ext_panel_Table> (<Ext_layout_container_Card> this.ext.getLayout()).getActiveItem();
        }

        /*
         * Switches the view
         * @param listId the view to show can be either of TreeGridPanel.GRID or TreeGridPanel.TREE
         */
        setActiveList(listId) {
            this.activeList = listId;
            if (this.ext) {
                (<Ext_layout_container_Card> this.ext.getLayout()).setActiveItem(listId);
            }
        }

        setKeyField(keyField: string) {
            this.keyField = keyField;
        }

        getKeyField() {
            return this.keyField;
        }

        setItemId(itemId: string) {
            this.itemId = itemId;
        }

        getItemId() {
            return this.itemId;
        }

        refresh() {
            var activeStore = this.getActiveList().getStore();
            if (this.activeList == TreeGridPanel.GRID) {
                activeStore.loadPage(activeStore.currentPage);
            } else {
                activeStore.load();
            }
        }

        loadData(data: Object[], append?: boolean) {
            var activeList = this.getActiveList();
            if (this.activeList == TreeGridPanel.GRID) {
                activeList.getStore().loadData(data, append);
            } else {
                var root = (<Ext_tree_Panel>activeList).getRootNode();
                if (!append) {
                    (<Ext_tree_Panel>activeList).getRootNode().removeAll();
                }
                for (var i = 0; i < data.length; i++) {
                    root.appendChild(root.createNode(data[i]));
                }
            }
            this.notifyTreeGridStoreLoaded();
        }

        removeAll() {
            this.deselectAll();
            var activeList = this.getActiveList();
            if (this.activeList == TreeGridPanel.GRID) {
                activeList.removeAll();
            } else {
                (<Ext_tree_Panel>activeList).getRootNode().removeAll();
            }
        }

        removeItem(keyFieldValue: any) {
            this.deselectItem(keyFieldValue);
            var activeList = this.getActiveList();
            if (this.activeList == TreeGridPanel.GRID) {
                var store = this.getActiveList().getStore();
                var model = store.findRecord(this.keyField, keyFieldValue);
                if (model) {
                    store.remove(model);
                }
            } else {
                var root = (<Ext_tree_Panel>activeList).getRootNode();
                var nodesToRemoveParents = [];
                root.cascadeBy((childNode) => {
                    if (childNode.get(this.keyField) == keyFieldValue) {
                        if (nodesToRemoveParents.indexOf(childNode.parentNode) < 0) {
                            nodesToRemoveParents.push(childNode.parentNode);
                        }
                        return false;
                    }
                    return true;
                }, this);
                for (var i = 0; i < nodesToRemoveParents.length; i++) {
                    this.treeStore.load({
                        node: nodesToRemoveParents[i]
                    });
                }
            }
        }

        deselectAll() {
            var activeList = this.getActiveList();
            var plugin = <PersistentGridSelectionPlugin> activeList.getPlugin('persistentGridSelection');
            if (plugin) {
                plugin.clearSelection();
            }
            activeList.getSelectionModel().deselectAll();
        }

        deselectItem(keyFieldValue: any) {
            var selModel = this.getActiveList().getSelectionModel(),
                selNodes = selModel.getSelection(),
                i;

            for (i = 0; i < selNodes.length; i++) {
                var selNode = selNodes[i];
                if (keyFieldValue == selNode.get(this.keyField)) {
                    selModel.deselect([selNode]);
                }
            }

        }

        getSelection() {
            var selection = [],
                activeList = this.getActiveList(),
                plugin = <PersistentGridSelectionPlugin> activeList.getPlugin('persistentGridSelection');

            if (plugin) {
                selection = plugin.getSelection();
            } else {
                selection = activeList.getSelectionModel().getSelection();
            }

            return selection;
        }

        setRemoteSearchParams(params) {
            var activeStore = this.getActiveList().getStore();
            (<any> activeStore.getProxy()).extraParams = params;
        }

        setResultCountVisible(visible) {
            var plugin = <GridToolbarPlugin> this.getActiveList().getDockedComponent('selectionToolbar').getPlugin('gridToolbarPlugin');
            plugin.setResultCountVisible(visible);
        }

        updateResultCount(count) {
            var plugin = <GridToolbarPlugin> this.getActiveList().getDockedComponent('selectionToolbar').getPlugin('gridToolbarPlugin');
            plugin.updateResultCount(count);
        }

    }


    interface GridToolbarPlugin extends Ext_AbstractPlugin {

        setResultCountVisible(visible: boolean): void;

        updateResultCount(count: number): void;
    }


    interface PersistentGridSelectionPlugin extends Ext_AbstractPlugin {

        getSelection():api.model.SpaceExtModel[];

        clearSelection():void;
    }

}

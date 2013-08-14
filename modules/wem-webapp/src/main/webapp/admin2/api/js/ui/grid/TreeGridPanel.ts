module api_ui_grid {

    export class TreeGridPanel {

        static GRID = "grid";
        static TREE = "tree";

        ext:Ext_panel_Panel;

        private gridStore:Ext_data_Store;
        private gridConfig:Object;
        private columns:any[];
        private treeStore:Ext_data_TreeStore;
        private treeConfig:Object;
        private keyField:string = 'name';
        private activeList:string = "grid";
        private itemId:string;
        private refreshNeeded:bool = false;

        //TODO: move to constructor after ext has been dropped
        create(region?:string, renderTo?:string) {

            this.ext = <any> new Ext.panel.Panel({
                cls: 'tree-grid-panel',
                region: region,
                renderTo: renderTo,
                layout: 'card',
                border: false,
                activeItem: this.activeList,
                itemId: this.itemId
            });

            this.ext.add(this.createGridPanel(this.gridStore, this.gridConfig));

            this.ext.add(this.createTreePanel(this.treeStore, this.treeConfig));

            return this;
        }

        constructor(columns:any[], gridStore:Ext_data_Store, treeStore:Ext_data_TreeStore, gridConfig?:Object, treeConfig?:Object) {

            this.gridStore = gridStore;
            this.treeStore = treeStore;
            this.columns = columns;
            this.gridConfig = gridConfig;
            this.treeConfig = treeConfig;

        }

        private createGridPanel(gridStore:Ext_data_Store, gridConfig?:Object) {

            var grid:Ext_grid_Panel = <any> new Ext.grid.Panel(Ext.apply({
                itemId: 'grid',
                cls: 'admin-grid',
                border: false,
                hideHeaders: true,
                columns: this.columns,
                viewConfig: {
                    trackOver: true,
                    stripeRows: true,
                    loadMask: {
                        store: gridStore
                    }
                },
                store: gridStore,
                plugins: [
                    new Admin.plugin.PersistentGridSelectionPlugin({
                        keyField: this.keyField
                    })
                ]
            }, gridConfig));

            grid.addDocked(new Ext.toolbar.Toolbar({
                itemId: 'selectionToolbar',
                cls: 'admin-white-toolbar',
                dock: 'top',
                store: gridStore,
                gridPanel: grid,
                resultCountHidden: true,
                plugins: ['gridToolbarPlugin']
            }));

            gridStore.on('datachanged', this.fireUpdateEvent, this);

            return grid;
        }

        private createTreePanel(treeStore:Ext_data_TreeStore, treeConfig?:Object) {

            var treeColumns = Ext.clone(this.columns);
            treeColumns[0].xtype = 'treecolumn';

            var tree:Ext_tree_Panel = <any> new Ext.tree.Panel(Ext.apply({
                xtype: 'treepanel',
                cls: 'admin-tree',
                hideHeaders: true,
                itemId: 'tree',
                useArrows: true,
                border: false,
                rootVisible: false,
                viewConfig: {
                    trackOver: true,
                    stripeRows: true,
                    loadMask: {
                        store: treeStore
                    }
                },
                store: treeStore,
                columns: treeColumns,
                plugins: [
                    new Admin.plugin.PersistentGridSelectionPlugin({
                        keyField: this.keyField
                    })
                ]
            }, treeConfig));

            tree.addDocked({
                xtype: 'toolbar',
                itemId: 'selectionToolbar',
                cls: 'admin-white-toolbar',
                dock: 'top',
                store: treeStore,
                gridPanel: tree,
                resultCountHidden: true,
                countTopLevelOnly: true,
                plugins: ['gridToolbarPlugin']
            });

            treeStore.on('datachanged', this.fireUpdateEvent, this);

            return tree;
        }

        private fireUpdateEvent(values) {
            this.ext.fireEvent('datachanged', values);
        }

        getActiveList():Ext_panel_Table {
            // returns either grid or tree that both extend table
            return <Ext_panel_Table> (<Ext_layout_container_Card> this.ext.getLayout()).getActiveItem();
        }

        /**
         * Switches the view
         * @param listId the view to show can be either of TreeGridPanel.GRID or TreeGridPanel.TREE
         */
            setActiveList(listId) {
            this.activeList = listId;
            if (this.ext) {
                (<Ext_layout_container_Card> this.ext.getLayout()).setActiveItem(listId);
            }
        }

        setKeyField(keyField:string) {
            this.keyField = keyField;
        }

        getKeyField() {
            return this.keyField;
        }

        setItemId(itemId:string) {
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
            this.refreshNeeded = false;
        }

        isRefreshNeeded():bool {
            return this.refreshNeeded;
        }

        setRefreshNeeded(refreshNeeded:bool) {
            this.refreshNeeded = refreshNeeded;
        }

        removeAll() {
            var activeList = this.getActiveList();
            if (this.activeList == TreeGridPanel.GRID) {
                activeList.removeAll();
            } else {
                (<Ext_tree_Panel>activeList).getRootNode().removeAll();
            }
        }

        deselect(key) {
            var activeList = this.getActiveList(),
                selModel = activeList.getSelectionModel();

            if (!key || key === -1) {
                selModel.deselectAll();
            } else {
                var selNodes = selModel.getSelection();
                var i;

                for (i = 0; i < selNodes.length; i++) {
                    var selNode = selNodes[i];
                    if (key == selNode.get(this.keyField)) {
                        selModel.deselect([selNode]);
                    }
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

        setResultCountVisible(visible:bool): void;
        updateResultCount(count:number): void;

    }


    interface PersistentGridSelectionPlugin extends Ext_AbstractPlugin {

        getSelection():api_model.SpaceExtModel[];
        clearSelection():void;

    }

}

module api_app_browse_grid {

    export interface TreeGridPanelParams {

        columns:any[];

        gridStore:Ext_data_Store;

        treeStore:Ext_data_TreeStore;

        gridConfig?:Object;

        treeConfig?:Object;

        contextMenu:api_ui_menu.ContextMenu;
    }

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
        private refreshNeeded:boolean = false;

        private contextMenu:api_ui_menu.ContextMenu;

        private listeners:TreeGridPanelListener[] = [];

        constructor(params:TreeGridPanelParams) {

            this.gridStore = params.gridStore;
            this.treeStore = params.treeStore;
            this.columns = params.columns;
            this.gridConfig = params.gridConfig;
            this.treeConfig = params.treeConfig;
            this.contextMenu = params.contextMenu;
        }

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

            var gridPanel = new GridPanel(this.gridStore, this.columns, this.keyField, this.gridConfig);
            this.ext.add(gridPanel.getExt());

            this.gridStore.on('datachanged', this.fireUpdateEvent, this);

            var treeColumns = Ext.clone(this.columns);
            treeColumns[0].xtype = 'treecolumn';

            var treePanel = new TreePanel(this.treeStore, treeColumns, this.keyField, this.treeConfig);
            this.ext.add(treePanel.getExt());

            this.treeStore.on('datachanged', this.fireUpdateEvent, this);

            treePanel.addListener(<TreePanelListener>{
                onSelectionChanged: (event:TreeSelectionChangedEvent) => {

                    console.log("TreeGridPanel onSelectionChanged from tree", event);

                    this.notifyTreeGridSelectionChanged({
                        selectionCount: event.selectionCount,
                        selectedModels: event.selectedModels
                    });
                },
                onSelect: (event:TreeSelectEvent) => {
                    this.notifyTreeGridSelect({
                        selectedModel: event.selectedModel
                    });
                },
                onDeselect: (event:TreeDeselectEvent) => {
                    this.notifyTreeGridDeselect({
                        deselectedModel: event.deselectedModel
                    });
                },
                onItemDoubleClicked: (event:TreeItemDoubleClickedEvent) => {

                    console.log("TreeGridPanel onItemDoubleClicked from tree", event);

                    this.notifyItemDoubleClicked({
                        clickedModel: event.clickedModel
                    });
                },
                onShowContextMenu: (event:TreeShowContextMenuEvent) => {

                    console.log("TreeGridPanel onShowContextMenu from tree", event);

                    this.contextMenu.showAt(event.x, event.y);
                }
            });

            gridPanel.addListener(<GridPanelListener>{
                onSelectionChanged: (event:GridSelectionChangedEvent) => {

                    console.log("TreeGridPanel onSelectionChanged from grid", event);

                    this.notifyTreeGridSelectionChanged({
                        selectionCount: event.selectionCount,
                        selectedModels: event.selectedModels
                    });
                },
                onSelect: (event:TreeSelectEvent) => {
                    this.notifyTreeGridSelect({
                        selectedModel: event.selectedModel
                    });
                },
                onDeselect: (event:TreeDeselectEvent) => {
                    this.notifyTreeGridDeselect({
                        deselectedModel: event.deselectedModel
                    });
                },
                onItemDoubleClicked: (event:GridItemDoubleClickedEvent) => {

                    console.log("TreeGridPanel onItemDoubleClicked from grid", event);

                    this.notifyItemDoubleClicked({
                        clickedModel: event.clickedModel
                    });
                },
                onShowContextMenu: (event:GridShowContextMenuEvent) => {

                    console.log("TreeGridPanel onShowContextMenu from grid", event);

                    this.contextMenu.showAt(event.x, event.y);
                }
            });

            return this;
        }

        addListener(listener:TreeGridPanelListener) {
            this.listeners.push(listener);
        }

        private notifyTreeGridSelectionChanged(event:TreeGridSelectionChangedEvent) {

            this.listeners.forEach((listener:TreeGridPanelListener)=> {
                if (listener.onSelectionChanged) {
                    listener.onSelectionChanged(event);
                }
            });
        }

        private notifyTreeGridSelect(event: TreeGridSelectEvent) {

            this.listeners.forEach((listener:TreeGridPanelListener) => {
                if (listener.onSelect) {
                    listener.onSelect(event);
                }
            });
        }

        private notifyTreeGridDeselect(event: TreeGridDeselectEvent) {

            this.listeners.forEach((listener:TreeGridPanelListener) => {
                if (listener.onDeselect) {
                    listener.onDeselect(event);
                }
            });
        }

        private notifyItemDoubleClicked(event:TreeGridItemDoubleClickedEvent) {

            this.listeners.forEach((listener:TreeGridPanelListener)=> {
                if (listener.onItemDoubleClicked) {
                    listener.onItemDoubleClicked(event);
                }
            });
        }


        private fireUpdateEvent(values) {
            this.ext.fireEvent('datachanged', values);
        }

        getActiveList():Ext_panel_Table {
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

        loadData(data:Object[], append?:boolean) {
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
        }

        isRefreshNeeded():boolean {
            return this.refreshNeeded;
        }

        setRefreshNeeded(refreshNeeded:boolean) {
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

        deselect(item:api_app_browse.BrowseItem) {
            var activeList = this.getActiveList(),
                selModel = activeList.getSelectionModel();

            if (!item) {
                selModel.deselectAll();
            } else {
                var key = item.getModel().get(this.keyField);
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

        setResultCountVisible(visible:boolean): void;

        updateResultCount(count:number): void;
    }


    interface PersistentGridSelectionPlugin extends Ext_AbstractPlugin {

        getSelection():api_model.SpaceExtModel[];

        clearSelection():void;
    }

}

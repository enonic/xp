module api_app_browse_grid {

    export class TreePanel {

        private extTreePanel:Ext_tree_Panel;

        private listeners:TreePanelListener[] = [];

        constructor(treeStore:Ext_data_TreeStore, columns:any[], keyField:string, treeConfig?:Object) {


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
            this.extTreePanel.on("select", this.notifySelect, this);
            this.extTreePanel.on("deselect", this.notifyDeselect, this);
            this.extTreePanel.on("itemdblclick", this.notifyItemDoubleClicked, this);
            this.extTreePanel.on("itemcontextmenu", this.handleItemContextMenuEvent, this);
        }

        addListener(listener:TreePanelListener) {
            this.listeners.push(listener);
        }

        private notifySelectionChanged(selectionModel:Ext_selection_Model, models:Ext_data_Model[]) {

            this.listeners.forEach((listener:TreePanelListener)=> {
                if (listener.onSelectionChanged != null) {
                    listener.onSelectionChanged({
                        selectionCount: selectionModel.getCount(),
                        selectedModels: models
                    });
                }
            });
        }

        private notifySelect(rowModel:Ext_selection_RowModel, model:Ext_data_Model) {

            this.listeners.forEach((listener:TreePanelListener) => {
                if (listener.onSelect) {
                    listener.onSelect({
                        selectedModel: model
                    });
                }
            });
        }

        private notifyDeselect(rowModel:Ext_selection_RowModel, model:Ext_data_Model) {

            this.listeners.forEach((listener:TreePanelListener) => {
                if (listener.onDeselect) {
                    listener.onDeselect({
                        deselectedModel: model
                    });
                }
            });
        }

        private notifyItemDoubleClicked(view:Ext_view_View, record:Ext_data_Model) {

            this.listeners.forEach((listener:TreePanelListener)=> {
                if (listener.onItemDoubleClicked != null) {
                    listener.onItemDoubleClicked({
                        clickedModel: record
                    });
                }
            });
        }

        private handleItemContextMenuEvent(view:Ext_view_View, record:Ext_data_Model, item:HTMLElement, index:number,
                                           event:Ext_EventObject) {

            event.stopEvent();

            this.listeners.forEach((listener:TreePanelListener)=> {
                if (listener.onShowContextMenu != null) {
                    listener.onShowContextMenu({
                        x: event.getXY()[0],
                        y: event.getXY()[1]
                    });
                }
            });
        }

        getExt() {
            return this.extTreePanel;
        }
    }
}
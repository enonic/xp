module api_app_browse_grid {

    export class GridPanel {

        private extGridPanel:Ext_grid_Panel;

        private listeners:GridPanelListener[] = [];

        constructor(gridStore:Ext_data_Store, columns:any[], keyField:string, gridConfig?:Object) {

            var persistentGridSelectionPlugin = new Admin.plugin.PersistentGridSelectionPlugin({
                keyField: keyField
            });

            this.extGridPanel = <any> new Ext.grid.Panel(Ext.apply({
                itemId: 'grid',
                cls: 'admin-grid',
                border: false,
                hideHeaders: true,
                columns: columns,
                viewConfig: {
                    trackOver: true,
                    stripeRows: true
                },
                store: gridStore,
                plugins: [
                    persistentGridSelectionPlugin
                ]
            }, gridConfig));

            var sortable = gridConfig && gridConfig['sortableColumns'] == false ? false : true;
            this.extGridPanel.addDocked(new Ext.toolbar.Toolbar({
                itemId: 'selectionToolbar',
                cls: 'admin-white-toolbar',
                dock: 'top',
                store: gridStore,
                gridPanel: this.extGridPanel,
                resultCountHidden: true,
                plugins: [new Admin.plugin.GridToolbarPlugin({disableSorting: !sortable})]
            }));

            this.extGridPanel.on("selectionchange", this.notifySelectionChanged, this, {buffer: 10});
            this.extGridPanel.on("select", this.notifySelect, this);
            this.extGridPanel.on("deselect", this.notifyDeselect, this);
            this.extGridPanel.on("itemdblclick", this.notifyItemDoubleClicked, this);
            this.extGridPanel.on("itemcontextmenu", this.handleItemContextMenuEvent, this);
        }

        addListener(listener:GridPanelListener) {
            this.listeners.push(listener);
        }

        private notifySelectionChanged(selectionModel:Ext_selection_Model, models:Ext_data_Model[]) {

            this.listeners.forEach((listener:GridPanelListener)=> {
                if (listener.onSelectionChanged != null) {
                    listener.onSelectionChanged({
                        selectionCount: selectionModel.getCount(),
                        selectedModels: models
                    });
                }
            });
        }

        private notifySelect(rowModel:Ext_selection_RowModel, model:Ext_data_Model) {

            this.listeners.forEach((listener:GridPanelListener) => {
                if (listener.onSelect) {
                    listener.onSelect({
                        selectedModel: model
                    });
                }
            });
        }

        private notifyDeselect(rowModel:Ext_selection_RowModel, model:Ext_data_Model) {

            this.listeners.forEach((listener:GridPanelListener) => {
                if (listener.onDeselect) {
                    listener.onDeselect({
                        deselectedModel: model
                    });
                }
            });
        }

        private notifyItemDoubleClicked(view:Ext_view_View, record:Ext_data_Model) {

            this.listeners.forEach((listener:GridPanelListener)=> {
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

            this.listeners.forEach((listener:GridPanelListener)=> {
                if (listener.onShowContextMenu != null) {
                    listener.onShowContextMenu({
                        x: event.getXY()[0],
                        y: event.getXY()[1]
                    });
                }
            });
        }

        getExt() {
            return this.extGridPanel;
        }
    }
}
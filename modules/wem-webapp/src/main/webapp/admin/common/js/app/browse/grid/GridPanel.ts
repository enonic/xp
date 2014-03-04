module api.app.browse.grid {

    export class GridPanel {

        private extGridPanel: Ext_grid_Panel;

        private gridShowContextMenuListeners: {(event: GridShowContextMenuEvent):void}[] = [];
        private gridItemDoubleClickedListeners: {(event: GridItemDoubleClickedEvent):void}[] = [];
        private gridSelectionChangedListeners: {(event: GridSelectionChangedEvent):void}[] = [];

        constructor(gridStore: Ext_data_Store, columns: any[], keyField: string, gridConfig?: Object) {

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
            this.extGridPanel.on("itemdblclick", this.notifyItemDoubleClicked, this);
            this.extGridPanel.on("itemcontextmenu", this.handleItemContextMenuEvent, this);
        }


        onGridShowContextMenu(listener: (event: GridShowContextMenuEvent)=>void) {
            this.gridShowContextMenuListeners.push(listener);
        }

        unGridShowContextMenu(listener: (event: GridShowContextMenuEvent)=>void) {
            this.gridShowContextMenuListeners =
            this.gridShowContextMenuListeners.filter((currentListener: (event: GridShowContextMenuEvent)=>void)=> {
                return listener != currentListener;
            });
        }

        onGridItemDoubleClicked(listener: (event: GridItemDoubleClickedEvent)=>void) {
            this.gridItemDoubleClickedListeners.push(listener);
        }

        unGridItemDoubleClicked(listener: (event: GridItemDoubleClickedEvent)=>void) {
            this.gridItemDoubleClickedListeners =
            this.gridItemDoubleClickedListeners.filter((currentListener: (event: GridItemDoubleClickedEvent)=>void)=> {
                return currentListener != listener;
            })
        }

        onGridSelectionChanged(listener: (event: GridSelectionChangedEvent)=>void) {
            this.gridSelectionChangedListeners.push(listener);
        }

        unGridSelectionChanged(listener: (event: GridSelectionChangedEvent)=>void) {
            this.gridSelectionChangedListeners =
            this.gridSelectionChangedListeners.filter((currentListener: (event: GridSelectionChangedEvent)=>void)=> {
                return currentListener != listener;
            });
        }


        private notifySelectionChanged(selectionModel: Ext_selection_Model, models: Ext_data_Model[]) {

            var selectionPlugin = <any>this.extGridPanel.getPlugin('persistentGridSelection');
            this.gridSelectionChangedListeners.forEach((listener: (event: GridSelectionChangedEvent)=>void)=> {
                listener.call(this, new GridSelectionChangedEvent(selectionPlugin.getSelectionCount(), selectionPlugin.getSelection()))
            });

        }

        private notifyItemDoubleClicked(view: Ext_view_View, record: Ext_data_Model) {

            this.gridItemDoubleClickedListeners.forEach((listener: (event: GridItemDoubleClickedEvent)=>void)=> {
                listener.call(this, new GridItemDoubleClickedEvent(record));
            });
        }

        private handleItemContextMenuEvent(view: Ext_view_View, record: Ext_data_Model, item: HTMLElement, index: number,
                                           event: Ext_EventObject) {

            event.stopEvent();

            this.gridShowContextMenuListeners.forEach((listener: (event: GridShowContextMenuEvent)=>void)=> {
                listener.call(this, new GridShowContextMenuEvent(event.getXY()[0], event.getXY()[1]));
            });
        }

        getExt() {
            return this.extGridPanel;
        }
    }
}
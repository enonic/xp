module api.ui.treegrid.actions {

    import Action = api.ui.Action;
    import TreeGrid = api.ui.treegrid.TreeGrid;

    export class TreeGridToolbarActions<M extends api.Equitable> implements TreeGridActions<M> {

        public SELECT_ALL: api.ui.Action;
        public CLEAR_SELECTION: ClearSelectionAction<M>;

        private actions: api.ui.Action[] = [];

        constructor(grid: TreeGrid<any>, selectedItemsCount?: number) {
            this.SELECT_ALL = new SelectAllAction(grid);
            this.CLEAR_SELECTION = new ClearSelectionAction(grid, selectedItemsCount);
            this.actions.push(this.SELECT_ALL, this.CLEAR_SELECTION);
        }

        getAllActions(): api.ui.Action[] {
            return this.actions;
        }

        updateActionsEnabledState(browseItems: api.app.browse.BrowseItem<M>[]): wemQ.Promise<api.app.browse.BrowseItem<M>[]> {
            var deferred = wemQ.defer<api.app.browse.BrowseItem<M>[]>();
            deferred.resolve(browseItems);
            return deferred.promise;
        }
    }
}

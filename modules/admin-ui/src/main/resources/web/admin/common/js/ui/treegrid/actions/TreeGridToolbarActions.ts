module api.ui.treegrid.actions {

    import Action = api.ui.Action;
    import Grid = api.ui.grid.Grid;

    export class TreeGridToolbarActions<M extends api.Equitable> implements TreeGridActions<M> {

        public SELECT_ALL: api.ui.Action;
        public CLEAR_SELECTION: api.ui.Action;

        private actions: api.ui.Action[] = [];

        constructor(grid: Grid<TreeNode<Object>>) {
            this.SELECT_ALL = new SelectAllAction(grid);
            this.CLEAR_SELECTION = new ClearSelectionAction(grid);
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

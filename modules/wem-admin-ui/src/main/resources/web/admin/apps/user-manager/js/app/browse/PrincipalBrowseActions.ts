module app.browse {
    import Action = api.ui.Action;
    import TreeGridActions = api.ui.treegrid.actions.TreeGridActions;
    export class PrincipalBrowseActions {

        public SHOW_NEW_PRINCIPAL_DIALOG_ACTION: Action;
        public EDIT_PRINCIPAL: Action;
        public DELETE_PRINCIPAL: Action;
        public DUPLICATE_PRINCIPAL: Action;
        public SYNCH_PRINCIPAL: Action;
        public TOGGLE_SEARCH_PANEL: Action;
        private static INSTANCE: PrincipalBrowseActions;
        private actions: api.ui.Action[] = [];

        static init(principalTreeGrid: app.browse.PrincipalTreeGrid): PrincipalBrowseActions {
            new PrincipalBrowseActions(principalTreeGrid);

            return PrincipalBrowseActions.INSTANCE;
        }

        constructor(grid: app.browse.PrincipalTreeGrid) {

            this.EDIT_PRINCIPAL = new app.browse.action.EditPrincipalAction(grid);

            this.actions.push(this.EDIT_PRINCIPAL);
            PrincipalBrowseActions.INSTANCE = this;
        }

        getAllActions(): api.ui.Action[] {
            return this.actions;
        }
    }
}

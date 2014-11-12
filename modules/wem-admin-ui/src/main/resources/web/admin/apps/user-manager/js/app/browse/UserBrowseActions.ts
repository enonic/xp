module app.browse {
    import Action = api.ui.Action;
    import TreeGridActions = api.ui.treegrid.actions.TreeGridActions;

    export class UserBrowseActions {

        public SHOW_NEW_DIALOG_ACTION: Action;
        public EDIT: Action;
        public DELETE: Action;
        public DUPLICATE: Action;
        public SYNCH: Action;

        private static INSTANCE: UserBrowseActions;
        private actions: api.ui.Action[] = [];

        static init(userTreeGrid: app.browse.UserItemsTreeGrid): UserBrowseActions {
            new UserBrowseActions(userTreeGrid);

            return UserBrowseActions.INSTANCE;
        }

        constructor(grid: app.browse.UserItemsTreeGrid) {
            this.SHOW_NEW_DIALOG_ACTION = new app.browse.action.ShowNewPrincipalDialogAction(grid);
            this.EDIT = new app.browse.action.EditPrincipalAction(grid);
            this.DELETE = new app.browse.action.DeleteUserItemAction(grid);
            this.DUPLICATE = new app.browse.action.DuplicatePrincipalAction(grid);
            this.SYNCH = new app.browse.action.SynchPrincipalAction(grid);

            this.actions.push(this.SHOW_NEW_DIALOG_ACTION, this.EDIT, this.DELETE, this.DUPLICATE,
                this.SYNCH);

            UserBrowseActions.INSTANCE = this;
        }

        getAllActions(): api.ui.Action[] {
            return this.actions;
        }
    }
}

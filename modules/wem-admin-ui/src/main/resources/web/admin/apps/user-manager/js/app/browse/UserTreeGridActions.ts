module app.browse {
    import Action = api.ui.Action;
    import TreeGridActions = api.ui.treegrid.actions.TreeGridActions;

    export class UserTreeGridActions implements TreeGridActions {

        public SHOW_NEW_DIALOG_ACTION: Action;
        public EDIT: Action;
        public DELETE: Action;
        public DUPLICATE: Action;
        public SYNCH: Action;

        private actions: api.ui.Action[] = [];

        constructor(grid: app.browse.UserItemsTreeGrid) {
            this.SHOW_NEW_DIALOG_ACTION = new app.browse.action.ShowNewPrincipalDialogAction(grid);
            this.EDIT = new app.browse.action.EditPrincipalAction(grid);
            this.DELETE = new app.browse.action.DeleteUserItemAction(grid);
            this.DUPLICATE = new app.browse.action.DuplicatePrincipalAction(grid);
            this.SYNCH = new app.browse.action.SynchPrincipalAction(grid);

            this.actions.push(this.SHOW_NEW_DIALOG_ACTION, this.EDIT, this.DELETE, this.DUPLICATE,
                this.SYNCH);
        }

        getAllActions(): api.ui.Action[] {
            return this.actions;
        }

        updateActionsEnabledState(selectedItems: UserTreeGridItem[]) {
            var userStoresSelected: number = 0;
            var principalsSelected: number = 0;
            selectedItems.forEach((item: UserTreeGridItem) => {
                var itemType = item.getType();
                if (itemType === UserTreeGridItemType.PRINCIPAL) {
                    principalsSelected++;
                } else if (itemType === UserTreeGridItemType.USER_STORE) {
                    userStoresSelected++;
                }
            });
            var anyPrincipal = principalsSelected > 0;
            var anyUserStore = userStoresSelected > 0;

            this.SHOW_NEW_DIALOG_ACTION.setEnabled(true);
            this.EDIT.setEnabled(anyPrincipal);
            this.DELETE.setEnabled(anyPrincipal);
            this.DUPLICATE.setEnabled((principalsSelected == 1) && (userStoresSelected == 0));
            this.SYNCH.setEnabled(anyUserStore);
        }
    }
}

module app.browse {
    import Action = api.ui.Action;
    import TreeGridActions = api.ui.treegrid.actions.TreeGridActions;

    export class UserTreeGridActions implements TreeGridActions {

        public NEW: Action;
        public EDIT: Action;
        public DELETE: Action;
        public DUPLICATE: Action;
        public SYNCH: Action;

        private actions: api.ui.Action[] = [];

        constructor(grid: app.browse.UserItemsTreeGrid) {
            this.NEW = new app.browse.action.NewPrincipalAction(grid);
            this.EDIT = new app.browse.action.EditPrincipalAction(grid);
            this.DELETE = new app.browse.action.DeletePrincipalAction(grid);
            this.DUPLICATE = new app.browse.action.DuplicatePrincipalAction(grid);
            this.SYNCH = new app.browse.action.SynchPrincipalAction(grid);

            this.actions.push(this.NEW, this.EDIT, this.DELETE, this.DUPLICATE, this.SYNCH);
        }

        getAllActions(): api.ui.Action[] {
            return this.actions;
        }

        updateActionsEnabledState(selectedItems: UserTreeGridItem[]) {
            var userStoresSelected:  number = 0,
                principalsSelected:  number = 0,
                directoriesSelected: number = 0;

            selectedItems.forEach((item: UserTreeGridItem) => {
                var itemType = item.getType();
                switch (itemType) {
                case UserTreeGridItemType.PRINCIPAL:
                    principalsSelected++;
                    break;
                case UserTreeGridItemType.ROLES:
                    directoriesSelected++;
                    break;
                case UserTreeGridItemType.GROUPS:
                    directoriesSelected++;
                    break;
                case UserTreeGridItemType.USERS:
                    directoriesSelected++;
                    break;
                case UserTreeGridItemType.USER_STORE:
                    userStoresSelected++;
                    break;
                }
            });

            var totalSelection = userStoresSelected + principalsSelected + directoriesSelected,
                anyPrincipal = principalsSelected > 0,
                anyUserStore = userStoresSelected > 0;

            this.NEW.setEnabled((directoriesSelected <= 1) && (totalSelection <= 1));
            this.EDIT.setEnabled(anyUserStore || anyPrincipal);
            this.DELETE.setEnabled(principalsSelected == 1 && totalSelection == 1);
            this.DUPLICATE.setEnabled((principalsSelected === 1) && (totalSelection === 1));
            this.SYNCH.setEnabled(anyUserStore);
        }
    }
}

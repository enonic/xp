module app.browse {
    import Action = api.ui.Action;
    import TreeGridActions = api.ui.treegrid.actions.TreeGridActions;
    import BrowseItem = api.app.browse.BrowseItem;

    export class UserTreeGridActions implements TreeGridActions<UserTreeGridItem> {

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

        updateActionsEnabledState(userItemBrowseItems: BrowseItem<UserTreeGridItem>[]): wemQ.Promise<BrowseItem<UserTreeGridItem>[]> {
            var userStoresSelected:  number = 0,
                principalsSelected:  number = 0,
                directoriesSelected: number = 0;

            userItemBrowseItems.forEach((browseItem: BrowseItem<UserTreeGridItem>) => {
                var item = <UserTreeGridItem>browseItem.getModel();
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

            var deferred = wemQ.defer<BrowseItem<UserTreeGridItem>[]>();
            deferred.resolve(userItemBrowseItems);
            return deferred.promise;
        }
    }
}

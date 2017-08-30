import '../../api.ts';
import {UserTreeGridItem, UserTreeGridItemType} from './UserTreeGridItem';
import {SyncPrincipalAction} from './action/SyncPrincipalAction';
import {DeletePrincipalAction} from './action/DeletePrincipalAction';
import {EditPrincipalAction} from './action/EditPrincipalAction';
import {NewPrincipalAction} from './action/NewPrincipalAction';
import {UserItemsTreeGrid} from './UserItemsTreeGrid';

import Action = api.ui.Action;
import TreeGridActions = api.ui.treegrid.actions.TreeGridActions;
import BrowseItem = api.app.browse.BrowseItem;
import PrincipalType = api.security.PrincipalType;
import UserStore = api.security.UserStore;
import GetPrincipalsByUserStoreRequest = api.security.GetPrincipalsByUserStoreRequest;
import User = api.security.User;

export class UserTreeGridActions implements TreeGridActions<UserTreeGridItem> {

    public NEW: Action;
    public EDIT: Action;
    public DELETE: Action;
    public SYNC: Action;

    private actions: api.ui.Action[] = [];

    constructor(grid: UserItemsTreeGrid) {
        this.NEW = new NewPrincipalAction(grid);
        this.EDIT = new EditPrincipalAction(grid);
        this.DELETE = new DeletePrincipalAction(grid);
        this.SYNC = new SyncPrincipalAction(grid);

        this.actions.push(this.NEW, this.EDIT, this.DELETE, this.SYNC);
    }

    getAllActions(): api.ui.Action[] {
        return this.actions;
    }

    updateActionsEnabledState(userItemBrowseItems: BrowseItem<UserTreeGridItem>[]): wemQ.Promise<BrowseItem<UserTreeGridItem>[]> {
        let userStoresSelected: number = 0;
        let principalsSelected: number = 0;
        let directoriesSelected: number = 0;
        let usersSelected: number = 0;

        userItemBrowseItems.forEach((browseItem: BrowseItem<UserTreeGridItem>) => {
            const item = <UserTreeGridItem>browseItem.getModel();
            const itemType = item.getType();
            switch (itemType) {
            case UserTreeGridItemType.PRINCIPAL:
                principalsSelected++;
                if (api.ObjectHelper.iFrameSafeInstanceOf(item.getPrincipal(), User)) {
                    usersSelected++;
                }
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

        const totalSelection = userStoresSelected + principalsSelected + directoriesSelected;
        const anyPrincipal = principalsSelected > 0;
        const anyUserStore = userStoresSelected > 0;
        const onlyUsersSelected = totalSelection >= 1 && totalSelection === usersSelected;
        const onePrincipalSelected = totalSelection === 1 && totalSelection === principalsSelected;

        this.NEW.setEnabled(true);
        this.EDIT.setEnabled(directoriesSelected < 1 && (anyUserStore || anyPrincipal));

        if (onlyUsersSelected || onePrincipalSelected) {
            this.DELETE.setEnabled(true);
        } else if (totalSelection === 1) {
            this.establishDeleteActionState((<BrowseItem<UserTreeGridItem>>userItemBrowseItems[0]).getModel());
        } else {
            this.DELETE.setEnabled(false);
        }

        this.SYNC.setEnabled(anyUserStore);

        let deferred = wemQ.defer<BrowseItem<UserTreeGridItem>[]>();
        deferred.resolve(userItemBrowseItems);
        return deferred.promise;
    }

    private establishDeleteActionState(userBrowseItem: UserTreeGridItem) {
        if (this.itemTypeAllowsDeletion(userBrowseItem.getType()) && userBrowseItem.getUserStore() &&
            userBrowseItem.getUserStore().getKey()) {
            UserStore.checkOnDeletable(userBrowseItem.getUserStore().getKey()).then((result: boolean) => {
                this.DELETE.setEnabled(result);
            });
        } else {
            this.DELETE.setEnabled(false);
        }
    }

    private itemTypeAllowsDeletion(userTreeGridItemType: UserTreeGridItemType): boolean {
        return (userTreeGridItemType !== UserTreeGridItemType.USERS && userTreeGridItemType !== UserTreeGridItemType.GROUPS);
    }
}

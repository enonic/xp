import "../../api.ts";

import Action = api.ui.Action;
import TreeGridActions = api.ui.treegrid.actions.TreeGridActions;
import BrowseItem = api.app.browse.BrowseItem;
import PrincipalType = api.security.PrincipalType;
import UserStore = api.security.UserStore;
import GetPrincipalsByUserStoreRequest = api.security.GetPrincipalsByUserStoreRequest;
import {UserTreeGridItem, UserTreeGridItemType} from "./UserTreeGridItem";
import {SynchPrincipalAction} from "./action/SynchPrincipalAction";
import {DeletePrincipalAction} from "./action/DeletePrincipalAction";
import {EditPrincipalAction} from "./action/EditPrincipalAction";
import {NewPrincipalAction} from "./action/NewPrincipalAction";
import {UserItemsTreeGrid} from "./UserItemsTreeGrid";

export class UserTreeGridActions implements TreeGridActions<UserTreeGridItem> {

    public NEW: Action;
    public EDIT: Action;
    public DELETE: Action;
    public SYNCH: Action;

    private actions: api.ui.Action[] = [];

    constructor(grid: UserItemsTreeGrid) {
        this.NEW = new NewPrincipalAction(grid);
        this.EDIT = new EditPrincipalAction(grid);
        this.DELETE = new DeletePrincipalAction(grid);
        this.SYNCH = new SynchPrincipalAction(grid);

        this.actions.push(this.NEW, this.EDIT, this.DELETE, this.SYNCH);
    }

    getAllActions(): api.ui.Action[] {
        return this.actions;
    }

    updateActionsEnabledState(userItemBrowseItems: BrowseItem<UserTreeGridItem>[]): wemQ.Promise<BrowseItem<UserTreeGridItem>[]> {
        var userStoresSelected: number = 0,
            principalsSelected: number = 0,
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
        this.EDIT.setEnabled(directoriesSelected < 1 && (anyUserStore || anyPrincipal));

        if (totalSelection == 1) {
            if (principalsSelected == 1) {
                this.DELETE.setEnabled(true);
            } else {
                this.establishDeleteActionState((<BrowseItem<UserTreeGridItem>>userItemBrowseItems[0]).getModel());
            }
        } else {
            this.DELETE.setEnabled(false);
        }

        this.SYNCH.setEnabled(anyUserStore);

        var deferred = wemQ.defer<BrowseItem<UserTreeGridItem>[]>();
        deferred.resolve(userItemBrowseItems);
        return deferred.promise;
    }

    private establishDeleteActionState(userBrowseItem: UserTreeGridItem) {
        if (this.itemTypeAllowsDeletion(userBrowseItem.getType()) && userBrowseItem.getUserStore() &&
            userBrowseItem.getUserStore().getKey()) {
            UserStore.checkOnDeletable(userBrowseItem.getUserStore().getKey()).then((result: boolean) => {
                this.DELETE.setEnabled(result);
            });
        }
        else {
            this.DELETE.setEnabled(false);
        }
    }

    private itemTypeAllowsDeletion(userTreeGridItemType: UserTreeGridItemType): boolean {
        return (userTreeGridItemType !== UserTreeGridItemType.USERS && userTreeGridItemType !== UserTreeGridItemType.GROUPS);
    }
}

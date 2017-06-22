import '../../../api.ts';
import {UserItemsTreeGrid} from '../UserItemsTreeGrid';
import {UserTreeGridItemType, UserTreeGridItem} from '../UserTreeGridItem';

import Action = api.ui.Action;
import i18n = api.util.i18n;

export class DeletePrincipalAction extends Action {

    constructor(grid: UserItemsTreeGrid) {
        super(i18n('action.delete'), 'mod+del');
        this.setEnabled(false);
        const confirmation = new api.ui.dialog.ConfirmationDialog()
            .setQuestion(i18n('dialog.delete.question'))
            .setNoCallback(null)
            .setYesCallback(() => {

                let principalItems = grid.getSelectedDataList().filter(
                    userItem => UserTreeGridItemType.PRINCIPAL === userItem.getType()).map((userItem: UserTreeGridItem) => {
                    return userItem.getPrincipal();
                });

                let userStoreItems = grid.getSelectedDataList().filter(
                    userItem => UserTreeGridItemType.USER_STORE === userItem.getType()).map((userItem: UserTreeGridItem) => {
                    return userItem.getUserStore();
                });

                let principalKeys = principalItems.filter((userItem) => {
                    return api.ObjectHelper.iFrameSafeInstanceOf(userItem, api.security.Principal);
                }).map((principal: api.security.Principal) => {
                    return principal.getKey();
                });

                let userStoreKeys = userStoreItems.filter((userItem) => {
                    return api.ObjectHelper.iFrameSafeInstanceOf(userItem, api.security.UserStore);
                }).map((userStore: api.security.UserStore) => {
                    return userStore.getKey();
                });

                if (principalKeys && principalKeys.length > 0) {
                    new api.security.DeletePrincipalRequest()
                        .setKeys(principalKeys)
                        .send()
                        .done((jsonResponse: api.rest.JsonResponse<any>) => {
                            let json = jsonResponse.getJson();

                            if (json.results && json.results.length > 0) {
                                let key = json.results[0].principalKey;

                                api.notify.showFeedback(i18n('notify.delete.principal', key));
                                api.security.UserItemDeletedEvent.create().setPrincipals(principalItems).build().fire();
                            }
                        });
                }

                if (userStoreKeys && userStoreKeys.length > 0) {
                    new api.security.DeleteUserStoreRequest()
                        .setKeys(userStoreKeys)
                        .send()
                        .done((jsonResponse: api.rest.JsonResponse<any>) => {
                            let json = jsonResponse.getJson();

                            if (json.results && json.results.length > 0) {
                                let key = json.results[0].userStoreKey;

                                api.notify.showFeedback(i18n('notify.delete.userstore', key));
                                api.security.UserItemDeletedEvent.create().setUserStores(userStoreItems).build().fire();
                            }
                        });
                }
            });

        this.onExecuted(() => {
            confirmation.open();
        });
    }
}

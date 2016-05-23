import "../../../api.ts";

import Action = api.ui.Action;
import {UserItemsTreeGrid} from "../UserItemsTreeGrid";
import {UserTreeGridItem} from "../UserTreeGridItem";
import {UserTreeGridItemType} from "../UserTreeGridItem";

export class SynchPrincipalAction extends Action {

    constructor(grid: UserItemsTreeGrid) {
        super("Synch");
        this.setEnabled(false);
        this.onExecuted(() => {
            var principals: UserTreeGridItem[] = grid.getSelectedDataList();

            var userStoreKeys = grid.getSelectedDataList().
                filter(userItem => UserTreeGridItemType.USER_STORE == userItem.getType()).
                map((userItem: UserTreeGridItem) => {
                    return userItem.getUserStore();
                }).filter((userStoreItem) => {
                    return api.ObjectHelper.iFrameSafeInstanceOf(userStoreItem, api.security.UserStore);
                }).map((userStore: api.security.UserStore) => {
                    return userStore.getKey();
                });

            if (userStoreKeys && userStoreKeys.length > 0) {
                new api.security.SynchUserStoreRequest()
                    .setKeys(userStoreKeys)
                    .send()
                    .done((jsonResponse: api.rest.JsonResponse<any>) => {
                        var json = jsonResponse.getJson();
                        if (json.results) {
                            if (json.results.length > 1) {
                                api.notify.showFeedback('UserStores synchronized!');
                            } else {
                                var key = json.results[0].userStoreKey;

                                api.notify.showFeedback('UserStore [' + key + '] synchronized!');

                            }
                        }
                    });
            }
        });
    }
}

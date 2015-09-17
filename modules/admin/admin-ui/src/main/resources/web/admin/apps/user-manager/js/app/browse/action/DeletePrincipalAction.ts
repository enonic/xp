module app.browse.action {

    import Action = api.ui.Action;

    export class DeletePrincipalAction extends Action {

        constructor(grid: UserItemsTreeGrid) {
            super("Delete", "mod+del");
            this.setEnabled(false);
            this.onExecuted(() => {
                api.ui.dialog.ConfirmationDialog.get()
                    .setQuestion("Are you sure you want to delete this user item?")
                    .setNoCallback(null)
                    .setYesCallback(() => {


                        var principalItems = grid.getSelectedDataList().
                            filter(userItem => UserTreeGridItemType.PRINCIPAL == userItem.getType()).
                            map((userItem: UserTreeGridItem) => {
                                return userItem.getPrincipal();
                        });

                        var userStoreItems = grid.getSelectedDataList().
                            filter(userItem => UserTreeGridItemType.USER_STORE == userItem.getType()).
                            map((userItem: UserTreeGridItem) => {
                                return userItem.getUserStore();
                        });

                        var principalKeys = principalItems.
                            filter((userItem) => {
                                return api.ObjectHelper.iFrameSafeInstanceOf(userItem, api.security.Principal);
                            }).
                            map((principal: api.security.Principal) => {
                                return principal.getKey();
                            });

                        var userStoreKeys = userStoreItems.
                            filter((userItem) => {
                                return api.ObjectHelper.iFrameSafeInstanceOf(userItem, api.security.UserStore);
                            }).
                            map((userStore: api.security.UserStore) => {
                                return userStore.getKey();
                            });


                        if (principalKeys && principalKeys.length > 0) {
                            new api.security.DeletePrincipalRequest()
                                .setKeys(principalKeys)
                                .send()
                                .done((jsonResponse: api.rest.JsonResponse<any>) => {
                                    var json = jsonResponse.getJson();

                                    if (json.results && json.results.length > 0) {
                                        var key = json.results[0].principalKey;

                                        api.notify.showFeedback('Principal [' + key + '] deleted!');
                                        api.security.UserItemDeletedEvent.create().setPrincipals(principalItems).build().fire();
                                    }
                                });
                        }

                        if (userStoreKeys && userStoreKeys.length > 0) {
                            new api.security.DeleteUserStoreRequest()
                                .setKeys(userStoreKeys)
                                .send()
                                .done((jsonResponse: api.rest.JsonResponse<any>) => {
                                    var json = jsonResponse.getJson();

                                    if (json.results && json.results.length > 0) {
                                        var key = json.results[0].userStoreKey;

                                        api.notify.showFeedback('UserStore [' + key + '] deleted!');
                                        api.security.UserItemDeletedEvent.create().setUserStores(userStoreItems).build().fire();
                                    }
                                });
                        }
                    }).open();
            });
        }
    }
}

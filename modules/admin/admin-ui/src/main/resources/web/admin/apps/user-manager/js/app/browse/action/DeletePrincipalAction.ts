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

                        var pathGuardItems = grid.getSelectedDataList().
                            filter(userItem => UserTreeGridItemType.PATH_GUARD == userItem.getType()).
                            map((userItem: UserTreeGridItem) => {
                                return userItem.getPathGuard();
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

                        var pathGuardKeys = pathGuardItems.map((pathGuard: api.security.PathGuard) => pathGuard.getKey());


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

                        if (pathGuardKeys && pathGuardKeys.length > 0) {
                            new api.security.DeletePathGuardRequest()
                                .setKeys(pathGuardKeys)
                                .send()
                                .done((jsonResponse: api.rest.JsonResponse<api.security.DeletePathGuardResultsJson>) => {
                                    var json = jsonResponse.getJson();

                                    if (json.results && json.results.length > 0) {
                                        var key = json.results[0].key;

                                        api.notify.showFeedback('Path guard [' + key + '] deleted!');
                                        api.security.UserItemDeletedEvent.create().setPathGuards(pathGuardItems).build().fire();
                                    }
                                });
                        }
                    }).open();
            });
        }
    }
}

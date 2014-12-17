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
                        var principals = grid.getSelectedDataList().map((userItem: UserTreeGridItem) => {
                            return userItem.getPrincipal().getKey();
                        });
                        new api.security.DeletePrincipalRequest()
                            .setKeys(principals)
                            .send()
                            .done((jsonResponse: api.rest.JsonResponse<any>) => {
                                var json = jsonResponse.getJson();

                                if (json.results && json.results.length > 0) {
                                    var key = json.results[0].principalKey;

                                    api.notify.showFeedback('Principal [' + key + '] deleted!');
                                    new app.browse.PrincipalDeletedEvent(principals).fire();
                                }
                            });
                    }).open();
            });
        }
    }
}

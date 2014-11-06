module app.browse.action {

    import Action = api.ui.Action;

    export class DeleteUserItemAction extends Action {

        constructor(grid: UserItemsTreeGrid) {
            super("Delete", "mod+del");
            this.setEnabled(false);
            this.onExecuted(() => {
                var principals: app.browse.UserTreeGridItem[] = grid.getSelectedDataList();
                new UserItemDeletePromptEvent(principals).fire();
            });
        }
    }
}

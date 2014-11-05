module app.browse.action {

    import Action = api.ui.Action;

    export class DeleteUserItemAction extends Action {

        constructor(grid: UserItemTreeGrid) {
            super("Delete", "mod+del");
            this.setEnabled(false);
            this.onExecuted(() => {
                var principals: api.security.UserTreeGridItem[] = grid.getSelectedDataList();
                new UserItemDeletePromptEvent(principals).fire();
            });
        }
    }
}

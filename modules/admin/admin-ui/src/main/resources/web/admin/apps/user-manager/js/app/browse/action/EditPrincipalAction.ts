module app.browse.action {

    import Action = api.ui.Action;

    export class EditPrincipalAction extends Action {

        constructor(grid: UserItemsTreeGrid) {
            super("Edit", "f4");
            this.setEnabled(false);
            this.onExecuted(() => {
                var principals: UserTreeGridItem[] = grid.getSelectedDataList();
                new EditPrincipalEvent(principals).fire();
            });
        }
    }
}

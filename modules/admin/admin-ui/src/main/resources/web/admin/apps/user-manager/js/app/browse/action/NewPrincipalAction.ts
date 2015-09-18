module app.browse.action {

    import Action = api.ui.Action;

    export class NewPrincipalAction extends Action {

        constructor(grid: UserItemsTreeGrid) {
            super("New", "mod+alt+n");
            this.setEnabled(false);
            this.onExecuted(() => {
                var principals: UserTreeGridItem[] = grid.getSelectedDataList();
                new NewPrincipalEvent(principals).fire();
            });
        }
    }
}

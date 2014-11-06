module app.browse.action {

    import Action = api.ui.Action;

    export class ShowNewPrincipalDialogAction extends Action {

        constructor(grid: UserItemsTreeGrid) {
            super("New", "mod+alt+n");
            this.setEnabled(true);
            this.onExecuted(() => {
                // var principals: api.security.Principal[] = grid.getSelectedDataList();
                // new ShowNewPrincipalDialogEvent(principals.length > 0 ? principals[0] : null).fire();
            });
        }
    }
}

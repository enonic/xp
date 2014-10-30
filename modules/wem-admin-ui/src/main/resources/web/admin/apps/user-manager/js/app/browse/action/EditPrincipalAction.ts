module app.browse.action {

    import Action = api.ui.Action;
    export class EditPrincipalAction extends Action {

        constructor(grid: PrincipalTreeGrid) {
            super("Edit", "f4");
            this.setEnabled(false);
            this.onExecuted(() => {
                var principals: api.security.Principal[] = grid.getSelectedDataList();
                new app.browse.EditPrincipalEvent(principals).fire();
            });
        }
    }
}

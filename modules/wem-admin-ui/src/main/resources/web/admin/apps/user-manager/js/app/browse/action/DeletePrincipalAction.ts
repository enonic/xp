module app.browse.action {

    import Action = api.ui.Action;

    export class DeletePrincipalAction extends Action {

        constructor(grid: PrincipalTreeGrid) {
            super("Delete", "mod+del");
            this.setEnabled(false);
            this.onExecuted(() => {
                var principals: api.security.Principal[] = grid.getSelectedDataList();
                new PrincipalDeletePromptEvent(principals).fire();
            });
        }
    }
}

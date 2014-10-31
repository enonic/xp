module app.browse.action {

    import Action = api.ui.Action;
    export class DuplicatePrincipalAction extends Action {

        constructor(grid: PrincipalTreeGrid) {
            super("Duplicate");
            this.setEnabled(false);
            this.onExecuted(() => {
                grid.getSelectedDataList().forEach((elem) => {
                    this.duplicate(elem);
                });
            });
        }

        private duplicate(source: api.security.Principal) {
            console.log('Duplicate principals action');
        }
    }
}
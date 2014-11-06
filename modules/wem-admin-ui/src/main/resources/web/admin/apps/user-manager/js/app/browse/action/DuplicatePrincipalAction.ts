module app.browse.action {

    import Action = api.ui.Action;
    export class DuplicatePrincipalAction extends Action {

        constructor(grid: UserItemsTreeGrid) {
            super("Duplicate");
            this.setEnabled(false);
            this.onExecuted(() => {
                grid.getSelectedDataList().forEach((elem) => {
                    this.duplicate(elem);
                });
            });
        }

        private duplicate(source: api.security.UserTreeGridItem) {
            console.log('Duplicate principals action');
        }
    }
}
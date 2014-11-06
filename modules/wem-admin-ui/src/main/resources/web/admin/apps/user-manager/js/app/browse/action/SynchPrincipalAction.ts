module app.browse.action {

    import Action = api.ui.Action;
    export class SynchPrincipalAction extends Action {

        constructor(grid: UserItemsTreeGrid) {
            super("Synch");
            this.setEnabled(false);
            this.onExecuted(() => {
                var principals: api.security.UserTreeGridItem[] = grid.getSelectedDataList();
                grid.getSelectedDataList().forEach((elem) => {
                    this.synch(elem);
                });
            });
        }

        private synch(principal: api.security.UserTreeGridItem) {
            console.log('Synch principals action');
        }
    }
}

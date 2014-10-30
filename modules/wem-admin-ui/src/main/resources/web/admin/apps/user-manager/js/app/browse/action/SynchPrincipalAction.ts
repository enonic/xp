module app.browse.action {

    import Action = api.ui.Action;
    export class SynchPrincipalAction extends Action {

        constructor(grid: PrincipalTreeGrid) {
            super("Synch");
            this.setEnabled(false);
            this.onExecuted(() => {
                var principals: api.security.Principal[] = grid.getSelectedDataList();
                grid.getSelectedDataList().forEach((elem) => {
                    this.synch(elem);
                });
            });
        }

        private synch(principal: api.security.Principal) {
            new api.security.SynchPrincipalRequest(principal.getKey()).
                sendAndParse().then((principal: api.security.Principal) => {
                    api.notify.showFeedback('Security object [' + principal.getDisplayName() + '] was synchronized!');
                    new PrincipalSynchronizedEvent(principal).fire();
                })
        }
    }
}

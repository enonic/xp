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
            new api.security.DuplicatePrincipalRequest(source.getKey()).
                sendAndParse().then((principal: api.security.Principal) => {

                    api.notify.showFeedback('Security object [' + principal.getDisplayName() + '] was duplicated!');
                    new PrincipalDuplicatedEvent(principal, source).fire();
                })
        }
    }
}
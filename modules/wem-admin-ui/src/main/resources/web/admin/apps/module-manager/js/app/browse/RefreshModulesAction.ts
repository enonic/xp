module app.browse {

    import ModuleSummary = api.module.ModuleSummary;

    export class RefreshModulesAction extends api.ui.Action {

        constructor() {
            super("Refresh");
            this.setEnabled(true);
            this.onExecuted(() => {
                new RefreshModulesEvent().fire();
            });
        }
    }
}

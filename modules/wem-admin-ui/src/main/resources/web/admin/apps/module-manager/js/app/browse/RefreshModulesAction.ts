module app.browse {

    import ModuleSummary = api.module.ModuleSummary;

    export class RefreshModulesAction extends api.ui.Action {

        constructor(moduleTreeGrid: ModuleTreeGrid) {
            super("Refresh");
            this.setEnabled(true);
            this.onExecuted(() => {
                moduleTreeGrid.reload();
            });
        }
    }
}

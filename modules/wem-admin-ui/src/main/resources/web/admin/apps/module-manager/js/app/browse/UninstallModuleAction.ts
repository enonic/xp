module app.browse {

    import ModuleSummary = api.module.ModuleSummary;

    export class UninstallModuleAction extends api.ui.Action {

        constructor(moduleTreeGrid: ModuleTreeGrid) {
            super("Uninstall");
            this.setEnabled(false);
            this.onExecuted(() => {
                var modules: ModuleSummary[] = moduleTreeGrid.getSelectedDataNodes();
                new UninstallModuleEvent(modules).fire();
            });
        }
    }
}

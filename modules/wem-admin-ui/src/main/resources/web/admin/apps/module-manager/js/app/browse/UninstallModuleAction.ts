module app.browse {

    import ModuleSummary = api.module.ModuleSummary;

    export class UninstallModuleAction extends api.ui.Action {

        constructor(ModuleTreeGrid: ModuleTreeGrid) {
            super("Uninstall");
            this.setEnabled(false);
            this.onExecuted(() => {
                var modules: ModuleSummary[] = ModuleTreeGrid.getSelectedDataNodes();
                console.log('uninstall', modules);
            });
        }
    }
}

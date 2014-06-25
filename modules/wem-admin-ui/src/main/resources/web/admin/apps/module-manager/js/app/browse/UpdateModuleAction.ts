module app.browse {

    import ModuleSummary = api.module.ModuleSummary;

    export class UpdateModuleAction extends api.ui.Action {

        constructor(ModuleTreeGrid: ModuleTreeGrid) {
            super("Update");
            this.setEnabled(false);
            this.onExecuted(() => {
                var modules: ModuleSummary[] = ModuleTreeGrid.getSelectedDataNodes();
                console.log('update', modules);
            });
        }
    }
}

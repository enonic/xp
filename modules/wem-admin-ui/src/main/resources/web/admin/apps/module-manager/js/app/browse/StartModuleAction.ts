module app.browse {

    import ModuleSummary = api.module.ModuleSummary;

    export class StartModuleAction extends api.ui.Action {

        constructor(ModuleTreeGrid: ModuleTreeGrid) {
            super("Start");
            this.setEnabled(false);
            this.onExecuted(() => {
                var modules: ModuleSummary[] = ModuleTreeGrid.getSelectedDataNodes();
                console.log('start', modules);
            });
        }
    }
}

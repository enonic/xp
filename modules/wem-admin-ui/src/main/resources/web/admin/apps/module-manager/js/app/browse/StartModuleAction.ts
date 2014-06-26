module app.browse {

    import ModuleSummary = api.module.ModuleSummary;

    export class StartModuleAction extends api.ui.Action {

        constructor(moduleTreeGrid: ModuleTreeGrid) {
            super("Start");
            this.setEnabled(false);
            this.onExecuted(() => {
                var modules: ModuleSummary[] = moduleTreeGrid.getSelectedDataNodes();
                new StartModuleEvent(modules).fire();
            });
        }
    }
}

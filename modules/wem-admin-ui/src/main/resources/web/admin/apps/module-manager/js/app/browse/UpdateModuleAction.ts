module app.browse {

    import ModuleSummary = api.module.ModuleSummary;

    export class UpdateModuleAction extends api.ui.Action {

        constructor(moduleTreeGrid: ModuleTreeGrid) {
            super("Update");
            this.setEnabled(false);
            this.onExecuted(() => {
                var modules: ModuleSummary[] = moduleTreeGrid.getSelectedDataNodes();
                new UpdateModuleEvent(modules).fire();
            });
        }
    }
}

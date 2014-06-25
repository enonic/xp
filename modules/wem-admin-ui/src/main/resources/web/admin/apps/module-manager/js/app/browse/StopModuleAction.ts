module app.browse {

    import ModuleSummary = api.module.ModuleSummary;

    export class StopModuleAction extends api.ui.Action {

        constructor(moduleTreeGrid: ModuleTreeGrid) {
            super("Stop");
            this.setEnabled(false);
            this.onExecuted(() => {
                var modules: ModuleSummary[] = moduleTreeGrid.getSelectedDataNodes();
                console.log('stop', modules);
                new StopModuleEvent(modules).fire();
            });
        }
    }
}

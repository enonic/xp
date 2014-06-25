module app.browse {

    import ModuleSummary = api.module.ModuleSummary;

    export class StopModuleAction extends api.ui.Action {

        constructor(ModuleTreeGrid: ModuleTreeGrid) {
            super("Stop");
            this.setEnabled(false);
            this.onExecuted(() => {
                var modules: ModuleSummary[] = ModuleTreeGrid.getSelectedDataNodes();
                console.log('stop', modules);
            });
        }
    }
}

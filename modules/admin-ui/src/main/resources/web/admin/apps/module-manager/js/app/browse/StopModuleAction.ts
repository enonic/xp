module app.browse {

    import Module = api.module.Module;

    export class StopModuleAction extends api.ui.Action {

        constructor(moduleTreeGrid: ModuleTreeGrid) {
            super("Stop");
            this.setEnabled(false);
            this.onExecuted(() => {
                var modules: Module[] = moduleTreeGrid.getSelectedDataList();
                new StopModuleEvent(modules).fire();
            });
        }
    }
}

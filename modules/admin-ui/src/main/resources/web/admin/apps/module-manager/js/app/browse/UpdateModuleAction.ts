module app.browse {

    import Module = api.module.Module;

    export class UpdateModuleAction extends api.ui.Action {

        constructor(moduleTreeGrid: ModuleTreeGrid) {
            super("Update");
            this.setEnabled(false);
            this.onExecuted(() => {
                var modules: Module[] = moduleTreeGrid.getSelectedDataList();
                new UpdateModuleEvent(modules).fire();
            });
        }
    }
}

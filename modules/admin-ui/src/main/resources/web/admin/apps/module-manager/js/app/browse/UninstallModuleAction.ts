module app.browse {

    import Module = api.module.Module;

    export class UninstallModuleAction extends api.ui.Action {

        constructor(moduleTreeGrid: ModuleTreeGrid) {
            super("Uninstall");
            this.setEnabled(false);
            this.onExecuted(() => {
                var modules: Module[] = moduleTreeGrid.getSelectedDataList();
                new UninstallModuleEvent(modules).fire();
            });
        }
    }
}

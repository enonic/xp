module app.browse {

    import ModuleSummary = api.module.ModuleSummary;

    export class InstallModuleAction extends api.ui.Action {

        constructor(ModuleTreeGrid: ModuleTreeGrid) {
            super("Install");
            this.setEnabled(true);
            this.onExecuted(() => {
                console.log('install');
            });
        }
    }
}

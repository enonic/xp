module app.browse {

    import ModuleSummary = api.module.ModuleSummary;

    export class InstallModuleAction extends api.ui.Action {

        constructor() {
            super("Install");
            this.setEnabled(true);
            this.onExecuted(() => {
                new InstallModuleEvent().fire();
            });
        }
    }
}

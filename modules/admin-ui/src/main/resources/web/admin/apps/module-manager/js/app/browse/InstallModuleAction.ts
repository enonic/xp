module app.browse {

    import Module = api.module.Module;

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

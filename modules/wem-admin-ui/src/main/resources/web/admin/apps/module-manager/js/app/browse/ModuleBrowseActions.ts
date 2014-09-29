module app.browse {

    export class ModuleBrowseActions {

        public INSTALL_MODULE: api.ui.Action;
        public UNINSTALL_MODULE: api.ui.Action;
        public START_MODULE: api.ui.Action;
        public STOP_MODULE: api.ui.Action;
        public UPDATE_MODULE: api.ui.Action;

        private allActions: api.ui.Action[] = [];

        private static INSTANCE: ModuleBrowseActions;

        static init(moduleTreeGrid: ModuleTreeGrid): ModuleBrowseActions {
            new ModuleBrowseActions(moduleTreeGrid);
            return ModuleBrowseActions.INSTANCE;
        }

        static get(): ModuleBrowseActions {
            return ModuleBrowseActions.INSTANCE;
        }

        constructor(moduleTreeGrid: ModuleTreeGrid) {

            this.INSTALL_MODULE = new InstallModuleAction();
            this.UNINSTALL_MODULE = new UninstallModuleAction(moduleTreeGrid);
            this.START_MODULE = new StartModuleAction(moduleTreeGrid);
            this.STOP_MODULE = new StopModuleAction(moduleTreeGrid);
            this.UPDATE_MODULE = new UpdateModuleAction(moduleTreeGrid);

            this.allActions.push(this.INSTALL_MODULE, this.UNINSTALL_MODULE, this.START_MODULE, this.STOP_MODULE, this.UPDATE_MODULE);

            ModuleBrowseActions.INSTANCE = this;
        }

        getAllActions(): api.ui.Action[] {
            return this.allActions;
        }

        updateActionsEnabledState(selectedModules: Module[]) {
            var modulesSelected = selectedModules.length;
            var anySelected = modulesSelected > 0;
            var anyStarted = false;
            var anyStopped = false;
            selectedModules.forEach((mod: Module) => {
                var state = mod.getState();
                if (state === 'started') {
                    anyStarted = true;
                } else if (state === 'stopped') {
                    anyStopped = true;
                }
            });

            this.INSTALL_MODULE.setEnabled(true);
            this.UNINSTALL_MODULE.setEnabled(anyStopped);
            this.START_MODULE.setEnabled(anyStopped);
            this.STOP_MODULE.setEnabled(anyStarted);
            this.UPDATE_MODULE.setEnabled(anyStarted);
        }

    }
}

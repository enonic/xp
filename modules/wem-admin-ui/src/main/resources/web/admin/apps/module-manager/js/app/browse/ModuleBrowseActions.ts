module app.browse {

    export class ModuleBrowseActions {

        public INSTALL_MODULE: api.ui.Action;
        public UNINSTALL_MODULE: api.ui.Action;
        public START_MODULE: api.ui.Action;
        public STOP_MODULE: api.ui.Action;
        public UPDATE_MODULE: api.ui.Action;
        public REFRESH_MODULES: api.ui.Action;

        private allActions: api.ui.Action[] = [];

        private static INSTANCE: ModuleBrowseActions;

        static init(ModuleTreeGrid: ModuleTreeGrid): ModuleBrowseActions {
            new ModuleBrowseActions(ModuleTreeGrid);
            return ModuleBrowseActions.INSTANCE;
        }

        static get(): ModuleBrowseActions {
            return ModuleBrowseActions.INSTANCE;
        }

        constructor(ModuleTreeGrid: ModuleTreeGrid) {

            this.INSTALL_MODULE = new InstallModuleAction(ModuleTreeGrid);
            this.UNINSTALL_MODULE = new UninstallModuleAction(ModuleTreeGrid);
            this.START_MODULE = new StartModuleAction(ModuleTreeGrid);
            this.STOP_MODULE = new StopModuleAction(ModuleTreeGrid);
            this.UPDATE_MODULE = new UpdateModuleAction(ModuleTreeGrid);
            this.REFRESH_MODULES = new RefreshModulesAction(ModuleTreeGrid);

            this.allActions.push(this.INSTALL_MODULE, this.UNINSTALL_MODULE, this.START_MODULE, this.STOP_MODULE, this.UPDATE_MODULE,
                this.REFRESH_MODULES);

            ModuleBrowseActions.INSTANCE = this;
        }

        getAllActions(): api.ui.Action[] {
            return this.allActions;
        }

        updateActionsEnabledState(modules: any[]) {
            var modulesSelected = modules.length;
            var anySelected = modulesSelected > 0;
            this.INSTALL_MODULE.setEnabled(true);
            this.UNINSTALL_MODULE.setEnabled(anySelected);
            this.START_MODULE.setEnabled(anySelected);
            this.STOP_MODULE.setEnabled(anySelected);
            this.UPDATE_MODULE.setEnabled(anySelected);
            this.REFRESH_MODULES.setEnabled(true);
        }

    }
}

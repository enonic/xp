module app.browse {

    export class BaseModuleBrowseAction extends api.ui.Action {

        constructor(label: string, shortcut?: string) {
            super(label, shortcut);
        }

    }

    export class DeleteModuleAction extends BaseModuleBrowseAction {

        constructor() {
            super("Delete");
            this.setEnabled(false);
            this.onExecuted(() => {
                var moduleModel: api.module.ModuleSummary = api.module.ModuleSummary.fromExtModel(components.gridPanel.getSelection()[0]);
                new DeleteModulePromptEvent(moduleModel).fire();
            });
        }
    }

    export class ModuleBrowseActions {

        public DELETE_MODULE: api.ui.Action;

        private allActions: api.ui.Action[] = [];

        private static INSTANCE: ModuleBrowseActions;

        static init(): ModuleBrowseActions {
            new ModuleBrowseActions();
            return ModuleBrowseActions.INSTANCE;
        }

        static get(): ModuleBrowseActions {
            return ModuleBrowseActions.INSTANCE;
        }

        constructor() {

            this.DELETE_MODULE = new DeleteModuleAction();

            this.allActions.push(this.DELETE_MODULE);

            ModuleBrowseActions.INSTANCE = this;
        }

        getAllActions(): api.ui.Action[] {
            return this.allActions;
        }

        updateActionsEnabledState(modules: any[]) {
            var modulesSelected = modules.length;
            this.DELETE_MODULE.setEnabled(modulesSelected > 0);
        }

    }
}
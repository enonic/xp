module app_browse {

    export class BaseModuleBrowseAction extends api_ui.Action {

        constructor(label:string, shortcut?:string) {
            super(label, shortcut);
        }

    }

    export class ImportModuleAction extends api_ui.Action {

        constructor() {
            super("Import");
            this.addExecutionListener(() => {
                //TODO: add callback
            });
        }
    }

    export class ExportModuleAction extends BaseModuleBrowseAction {

        constructor() {
            super("Export");
            this.setEnabled(false);
            this.addExecutionListener(() => {
                //TODO: add callback
            });
        }
    }

    export class DeleteModuleAction extends BaseModuleBrowseAction {

        constructor() {
            super("Delete");
            this.setEnabled(false);
            this.addExecutionListener(() => {
                //TODO: add callback
            });
        }
    }

    export class ModuleBrowseActions {

        public IMPORT_MODULE:api_ui.Action;
        public EXPORT_MODULE:api_ui.Action;
        public DELETE_MODULE:api_ui.Action;

        private allActions:api_ui.Action[] = [];

        private static INSTANCE:ModuleBrowseActions;

        static init():ModuleBrowseActions {
            new ModuleBrowseActions();
            return ModuleBrowseActions.INSTANCE;
        }

        static get():ModuleBrowseActions {
            return ModuleBrowseActions.INSTANCE;
        }

        constructor() {

            this.IMPORT_MODULE = new ImportModuleAction();
            this.DELETE_MODULE = new DeleteModuleAction();
            this.EXPORT_MODULE = new ExportModuleAction();

            this.allActions.push(this.IMPORT_MODULE, this.DELETE_MODULE, this.EXPORT_MODULE);

            ModuleBrowseActions.INSTANCE = this;
        }

        getAllActions():api_ui.Action[] {
            return this.allActions;
        }

        updateActionsEnabledState(modules:any[]) {

            if (modules.length <= 0) {
                this.IMPORT_MODULE.setEnabled(true);
                this.DELETE_MODULE.setEnabled(false);
                this.EXPORT_MODULE.setEnabled(false);
            } else {
                this.IMPORT_MODULE.setEnabled(true);
                this.DELETE_MODULE.setEnabled(true);
                this.EXPORT_MODULE.setEnabled(true);
            }
        }

    }
}
module app.browse {

    export class BaseModuleModelEvent extends api.event.Event {
        private model:api.module.ModuleSummary[];

        constructor(name:string, model:api.module.ModuleSummary[]) {
            this.model = model;
            super(name);
        }

        getModules():api.module.ModuleSummary[] {
            return this.model;
        }
    }


    export class ImportModuleEvent extends BaseModuleModelEvent {
        constructor() {
            super('importModule', null);
        }

        static on(handler:(event:ImportModuleEvent) => void) {
            api.event.onEvent('importModule', handler);
        }
    }

    export class ExportModuleEvent extends BaseModuleModelEvent {
        constructor() {
            super('exportModule', null);
        }

        static on(handler:(event:ExportModuleEvent) => void) {
            api.event.onEvent('exportModule', handler);
        }
    }

    export class DeleteModulePromptEvent extends BaseModuleModelEvent {

        constructor(moduleModel:api.module.ModuleSummary) {
            super('deleteModulePrompt', [moduleModel]);
        }

        getModule():api.module.ModuleSummary {
            return this.getModules()[0];
        }

        static on(handler:(event:DeleteModulePromptEvent) => void) {
            api.event.onEvent('deleteModulePrompt', handler);
        }
    }

}

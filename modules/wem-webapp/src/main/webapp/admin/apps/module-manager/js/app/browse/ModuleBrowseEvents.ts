module app_browse {

    export class BaseModuleModelEvent extends api_event.Event {
        private model:api_module.Module[];

        constructor(name:string, model:api_module.Module[]) {
            this.model = model;
            super(name);
        }

        getModules():api_module.Module[] {
            return this.model;
        }
    }


    export class ImportModuleEvent extends BaseModuleModelEvent {
        constructor() {
            super('importModule', null);
        }

        static on(handler:(event:ImportModuleEvent) => void) {
            api_event.onEvent('importModule', handler);
        }
    }


}
module api.module {

    export interface ModuleUpdatedEventJson {
        state: string;
        moduleKey: string;
    }

    export class ModuleUpdatedEvent extends api.event.Event {

        private moduleKey: api.module.ModuleKey;

        private state: string;

        constructor(moduleKey: api.module.ModuleKey, state: string) {
            super();
            this.moduleKey = moduleKey;
            this.state = state;
        }

        public getModuleKey(): api.module.ModuleKey {
            return this.moduleKey;
        }

        public getState(): string {
            return this.state;
        }

        static on(handler: (event: ModuleUpdatedEvent) => void) {
            api.event.Event.bind(api.util.getFullName(this), handler);
        }

        static un(handler?: (event: ModuleUpdatedEvent) => void) {
            api.event.Event.unbind(api.util.getFullName(this), handler);
        }

        static fromJson(json: ModuleUpdatedEventJson): ModuleUpdatedEvent {
            var moduleKey = api.module.ModuleKey.fromString(json.moduleKey);
            var state = json.state;
            return new ModuleUpdatedEvent(moduleKey, state);
        }
    }

}
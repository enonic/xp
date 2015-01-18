module api.module {

    export interface ModuleUpdatedEventJson {
        eventType: string;
        moduleKey: string;
    }

    export class ModuleUpdatedEvent extends api.event.Event {

        private moduleKey: api.module.ModuleKey;

        private eventType: string;

        constructor(moduleKey: api.module.ModuleKey, eventType: string) {
            super();
            this.moduleKey = moduleKey;
            this.eventType = eventType;
        }

        public getModuleKey(): api.module.ModuleKey {
            return this.moduleKey;
        }

        public getEventType(): string {
            return this.eventType;
        }

        static on(handler: (event: ModuleUpdatedEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: ModuleUpdatedEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }

        static fromJson(json: ModuleUpdatedEventJson): ModuleUpdatedEvent {
            var moduleKey = api.module.ModuleKey.fromString(json.moduleKey);
            var eventType = json.eventType;
            return new ModuleUpdatedEvent(moduleKey, eventType);
        }
    }

}
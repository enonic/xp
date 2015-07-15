module api.module {

    export enum ModuleUpdatedEventType {
        INSTALLED, UNINSTALLED, RESOLVED, STARTING, STARTED, UPDATED, STOPPING, STOPPED, UNRESOLVED
    }

    export interface ModuleUpdatedEventJson {
        eventType: string;
        applicationKey: string;
    }

    export class ModuleUpdatedEvent extends api.event.Event {

        private moduleKey: api.module.ModuleKey;

        private eventType: ModuleUpdatedEventType;

        constructor(moduleKey: api.module.ModuleKey, eventType: ModuleUpdatedEventType) {
            super();
            this.moduleKey = moduleKey;
            this.eventType = eventType;
        }

        public getModuleKey(): api.module.ModuleKey {
            return this.moduleKey;
        }

        public getEventType(): ModuleUpdatedEventType {
            return this.eventType;
        }

        isNeedToUpdateModule(): boolean {
            return ModuleUpdatedEventType.RESOLVED != this.eventType &&
                ModuleUpdatedEventType.STARTING != this.eventType &&
                ModuleUpdatedEventType.UNRESOLVED != this.eventType &&
                ModuleUpdatedEventType.STOPPING != this.eventType;
        }

        static on(handler: (event: ModuleUpdatedEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: ModuleUpdatedEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }

        static fromJson(json: ModuleUpdatedEventJson): ModuleUpdatedEvent {
            var moduleKey = api.module.ModuleKey.fromString(json.applicationKey);
            var eventType = ModuleUpdatedEventType[json.eventType];
            return new ModuleUpdatedEvent(moduleKey, eventType);
        }
    }

}
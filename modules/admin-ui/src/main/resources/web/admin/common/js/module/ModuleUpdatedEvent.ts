module api.module {

    export enum ModuleUpdatedEventType {
        INSTALLED, UNINSTALLED, RESOLVED, STARTING, STARTED, UPDATED, STOPPING, STOPPED, UNRESOLVED
    }

    export interface ModuleUpdatedEventJson {
        eventType: string;
        applicationKey: string;
    }

    export class ModuleUpdatedEvent extends api.event.Event {

        private applicationKey: api.module.ApplicationKey;

        private eventType: ModuleUpdatedEventType;

        constructor(applicationKey: api.module.ApplicationKey, eventType: ModuleUpdatedEventType) {
            super();
            this.applicationKey = applicationKey;
            this.eventType = eventType;
        }

        public getApplicationKey(): api.module.ApplicationKey {
            return this.applicationKey;
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
            var applicationKey = api.module.ApplicationKey.fromString(json.applicationKey);
            var eventType = ModuleUpdatedEventType[json.eventType];
            return new ModuleUpdatedEvent(applicationKey, eventType);
        }
    }

}
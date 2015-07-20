module api.module {

    export enum ApplicationUpdatedEventType {
        INSTALLED, UNINSTALLED, RESOLVED, STARTING, STARTED, UPDATED, STOPPING, STOPPED, UNRESOLVED
    }

    export interface ApplicationUpdatedEventJson {
        eventType: string;
        applicationKey: string;
    }

    export class ApplicationUpdatedEvent extends api.event.Event {

        private applicationKey: api.module.ApplicationKey;

        private eventType: ApplicationUpdatedEventType;

        constructor(applicationKey: api.module.ApplicationKey, eventType: ApplicationUpdatedEventType) {
            super();
            this.applicationKey = applicationKey;
            this.eventType = eventType;
        }

        public getApplicationKey(): api.module.ApplicationKey {
            return this.applicationKey;
        }

        public getEventType(): ApplicationUpdatedEventType {
            return this.eventType;
        }

        isNeedToUpdateModule(): boolean {
            return ApplicationUpdatedEventType.RESOLVED != this.eventType &&
                ApplicationUpdatedEventType.STARTING != this.eventType &&
                ApplicationUpdatedEventType.UNRESOLVED != this.eventType &&
                ApplicationUpdatedEventType.STOPPING != this.eventType;
        }

        static on(handler: (event: ApplicationUpdatedEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: ApplicationUpdatedEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }

        static fromJson(json: ApplicationUpdatedEventJson): ApplicationUpdatedEvent {
            var applicationKey = api.module.ApplicationKey.fromString(json.applicationKey);
            var eventType = ApplicationUpdatedEventType[json.eventType];
            return new ApplicationUpdatedEvent(applicationKey, eventType);
        }
    }

}
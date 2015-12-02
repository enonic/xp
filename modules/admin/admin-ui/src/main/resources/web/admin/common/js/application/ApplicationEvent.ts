module api.application {

    export enum ApplicationEventType {
        INSTALLED, UNINSTALLED, RESOLVED, STARTING, STARTED, UPDATED, STOPPING, STOPPED, UNRESOLVED
    }

    export interface ApplicationEventJson extends api.app.EventJson {
        data: ApplicationEventDataJson;
    }

    export interface ApplicationEventDataJson {
        eventType: string;
        applicationKey: string;
    }

    export class ApplicationEvent extends api.event.Event {

        private applicationKey: api.application.ApplicationKey;

        private eventType: ApplicationEventType;

        constructor(applicationKey: api.application.ApplicationKey, eventType: ApplicationEventType) {
            super();
            this.applicationKey = applicationKey;
            this.eventType = eventType;
        }

        public getApplicationKey(): api.application.ApplicationKey {
            return this.applicationKey;
        }

        public getEventType(): ApplicationEventType {
            return this.eventType;
        }

        isNeedToUpdateApplication(): boolean {
            return ApplicationEventType.RESOLVED != this.eventType &&
                   ApplicationEventType.STARTING != this.eventType &&
                   ApplicationEventType.UNRESOLVED != this.eventType &&
                   ApplicationEventType.STOPPING != this.eventType;
        }

        static on(handler: (event: ApplicationEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: ApplicationEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }

        static fromJson(applicationEventJson: ApplicationEventJson): ApplicationEvent {
            var applicationKey = api.application.ApplicationKey.fromString(applicationEventJson.data.applicationKey);
            var eventType = ApplicationEventType[applicationEventJson.data.eventType];
            return new ApplicationEvent(applicationKey, eventType);
        }
    }

}
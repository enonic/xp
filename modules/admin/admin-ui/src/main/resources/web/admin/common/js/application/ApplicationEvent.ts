module api.application {

    export enum ApplicationEventType {
        INSTALLED, UNINSTALLED, RESOLVED, STARTING, STARTED, UPDATED, STOPPING, STOPPED, UNRESOLVED, PROGRESS
    }

    export interface ApplicationEventJson extends api.event.EventJson {
        data: ApplicationEventDataJson;
    }

    export interface ApplicationEventDataJson {
        eventType: string;
        applicationKey: string;
        applicationUrl?: string;
        progress?: number;
    }

    export class ApplicationEvent extends api.event.Event {

        private applicationKey: api.application.ApplicationKey;

        private applicationUrl: string;

        private eventType: ApplicationEventType;

        private progress: number;

        constructor(applicationKey: api.application.ApplicationKey, eventType: ApplicationEventType, applicationUrl?: string,
                    progress?: number) {
            super();
            this.applicationKey = applicationKey;
            this.applicationUrl = applicationUrl;
            this.eventType = eventType;
            this.progress = progress;
        }

        public getApplicationKey(): api.application.ApplicationKey {
            return this.applicationKey;
        }

        public getEventType(): ApplicationEventType {
            return this.eventType;
        }

        public getApplicationUrl(): string {
            return this.applicationUrl;
        }

        public getProgress(): number {
            return this.progress;
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
            let applicationKey = api.application.ApplicationKey.fromString(applicationEventJson.data.applicationKey);
            let eventType = ApplicationEventType[applicationEventJson.data.eventType];
            let applicationUrl = applicationEventJson.data.applicationUrl;
            let progress = applicationEventJson.data.progress;
            return new ApplicationEvent(applicationKey, eventType, applicationUrl, progress);
        }
    }

}

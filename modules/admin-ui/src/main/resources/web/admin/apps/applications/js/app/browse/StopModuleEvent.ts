module app.browse {

    import Application = api.module.Application;
    import Event = api.event.Event;

    export class StopModuleEvent extends Event {
        private applications: Application[];

        constructor(applications: Application[]) {
            this.applications = applications;
            super();
        }

        getApplications(): Application[] {
            return this.applications;
        }

        static on(handler: (event: StopModuleEvent) => void) {
            Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: StopModuleEvent) => void) {
            Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }
}

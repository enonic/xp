module app.browse {

    import Application = api.application.Application;
    import Event = api.event.Event;

    export class StartApplicationEvent extends Event {
        private applications: Application[];

        constructor(applications: Application[]) {
            this.applications = applications;
            super();
        }

        getApplications(): Application[] {
            return this.applications;
        }

        static on(handler: (event: StartApplicationEvent) => void) {
            Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: StartApplicationEvent) => void) {
            Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }
}

import '../../api.ts';

import Application = api.application.Application;
import Event = api.event.Event;

export class UninstallApplicationEvent extends Event {
    private applications: Application[];

    constructor(applications: Application[]) {
        super();
        this.applications = applications;
    }

    getApplications(): Application[] {
        return this.applications;
    }

    static on(handler: (event: UninstallApplicationEvent) => void) {
        Event.bind(api.ClassHelper.getFullName(this), handler);
    }

    static un(handler?: (event: UninstallApplicationEvent) => void) {
        Event.unbind(api.ClassHelper.getFullName(this), handler);
    }
}

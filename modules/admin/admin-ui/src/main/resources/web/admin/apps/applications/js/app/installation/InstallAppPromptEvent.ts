import '../../api.ts';

import  Application = api.application.Application;

export class InstallAppPromptEvent extends api.event.Event {

    private installedApplications: Application[];

    constructor(installedApplications: Application[]) {
        super();
        this.installedApplications = installedApplications;
    }

    getInstalledApplications(): Application[] {
        return this.installedApplications;
    }

    static on(handler: (event: InstallAppPromptEvent) => void) {
        api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
    }

    static un(handler?: (event: InstallAppPromptEvent) => void) {
        api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
    }
}

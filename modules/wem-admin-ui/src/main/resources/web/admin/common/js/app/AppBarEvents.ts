module api.app {

    export class ShowAppLauncherEvent extends api.event.Event {

        constructor() {
            super('showAppLauncher');
        }

        static on(handler:(event:ShowAppLauncherEvent) => void) {
            api.event.onEvent('showAppLauncher', handler);
        }

    }

    export class ShowAppBrowsePanelEvent extends api.event.Event {

        constructor() {
            super('showAppBrowsePanel');
        }

        static on(handler:(event:ShowAppBrowsePanelEvent) => void) {
            api.event.onEvent('showAppBrowsePanel', handler);
        }

    }

}
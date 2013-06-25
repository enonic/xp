module api_app {

    export class ShowAppLauncherEvent extends api_event.Event {

        constructor() {
            super('showAppLauncher');
        }

        static on(handler:(event:ShowAppLauncherEvent) => void) {
            api_event.onEvent('showAppLauncher', handler);
        }

    }

    export class ShowAppBrowsePanelEvent extends api_event.Event {

        constructor() {
            super('showAppBrowsePanel');
        }

        static on(handler:(event:ShowAppBrowsePanelEvent) => void) {
            api_event.onEvent('showAppBrowsePanel', handler);
        }

    }

}
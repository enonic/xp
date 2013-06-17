module api_appbar {

    export class OpenAppLauncherEvent extends api_event.Event {

        constructor() {
            super('openAppLauncher');
        }

    }

    export class ShowAppBrowsePanelEvent extends api_event.Event {

        constructor() {
            super('showAppBrowsePanel');
        }

    }

}
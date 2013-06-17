module api_appbar {

    export class OpenStartMenuEvent extends api_event.Event {

        constructor() {
            super('openStartMenu');
        }

    }

    export class OpenHomePageEvent extends api_event.Event {

        constructor() {
            super('openHomePage');
        }

    }

    export class ToggleUserInfoEvent extends api_event.Event {

        static NAME:string = 'toggleUserInfo';

        constructor() {
            super(ToggleUserInfoEvent.NAME);
        }

    }

}
module app {

    export class ContentAppBar extends api_app.AppBar {

        constructor() {
            super("Content Manager", new ContentAppBarTabMenu());
        }

    }

}
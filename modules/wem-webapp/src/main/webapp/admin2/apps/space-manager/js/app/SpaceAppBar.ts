module app {

    export class SpaceAppBar extends api_app.AppBar {

        constructor() {
            super("Space Admin", new SpaceAppBarTabMenu());
        }

    }

}
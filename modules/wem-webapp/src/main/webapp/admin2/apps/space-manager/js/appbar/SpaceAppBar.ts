module app_ui {

    export class SpaceAppBar extends api_appbar.AppBar {

        constructor() {
            super("Space Admin", app.SpaceAppTabPanelController.get().getAppBarTabMenu())
        }

    }

}
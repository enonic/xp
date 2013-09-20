module app_launcher {

    export interface AppSelectorListener {

        onAppHighlighted(app:Application): void;

        onAppUnhighlighted(app:Application): void;

        onAppSelected(app:Application): void;
    }

}

module app.launcher {

    export interface AppSelectorListener {

        onAppHighlighted(app:api.app.Application): void;

        onAppUnhighlighted(app:api.app.Application): void;

        onAppSelected(app:api.app.Application): void;
    }

}

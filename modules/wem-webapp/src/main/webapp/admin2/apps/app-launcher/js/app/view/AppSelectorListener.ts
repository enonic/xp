module app_view {

    export interface AppSelectorListener {

        onAppHighlighted(app:app_model.Application): void;

        onAppUnhighlighted(app:app_model.Application): void;

        onAppSelected(app:app_model.Application): void;
    }

}

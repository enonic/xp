module api_app {

    export interface AppManagerListener {

        onConnectionLost();

        onConnectionRestored();

        onShowLauncher();
    }
}
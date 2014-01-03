module api.app {

    export interface AppManagerListener {

        onConnectionLost();

        onConnectionRestored();

        onShowLauncher();
    }
}
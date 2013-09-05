module api_app {

    export interface AppBridgeListener {

        onConnectionLost?();

        onConnectionRestored?();

        onShowLauncher?();
    }
}
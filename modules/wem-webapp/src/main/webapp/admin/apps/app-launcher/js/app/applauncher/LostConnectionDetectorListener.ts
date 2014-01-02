module app.launcher {

    export interface LostConnectionDetectorListener {

        onConnectionLost();

        onConnectionRestored();
    }
}
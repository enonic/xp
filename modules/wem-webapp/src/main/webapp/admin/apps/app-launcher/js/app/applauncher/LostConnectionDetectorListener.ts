module app_launcher {

    export interface LostConnectionDetectorListener {

        onConnectionLost();

        onConnectionRestored();
    }
}
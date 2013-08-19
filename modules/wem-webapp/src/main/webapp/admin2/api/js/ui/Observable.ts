module api_ui {

    /**
     *  Base listener
     */
    export interface Listener {

    }

    /**
     *  Observable interface
     */
    export interface Observable {

        addListener(listener:Listener);

        removeListener(listener:Listener);

    }
}

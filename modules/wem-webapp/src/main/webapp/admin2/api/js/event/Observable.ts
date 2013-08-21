module api_event {

    /**
     *  Observable interface
     */
    export interface Observable {

        addListener(listener:Listener);

        removeListener(listener:Listener);

    }
}

module api.event {

    /**
     *  Observable interface
     */
    export interface Observable {

        addListener(listener:Listener);

        removeListener(listener:Listener);

    }
}

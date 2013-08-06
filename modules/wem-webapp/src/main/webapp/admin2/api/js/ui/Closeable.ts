module api_ui {

    export interface Closeable {

        /*
         * Issue closing. Implementations are expected to call canClose if checkCanClose is true.
         */
        close(checkCanClose?:bool);

        /*
         * Whether object can be closed or not.
         */
        canClose():bool;

        /*
         * Override this method if other closing logic is needed.
         */
        closing();
    }
}

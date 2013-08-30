module api_ui {

    export interface Closeable {

        /*
         * Issue closing. Implementations are expected to call canClose if checkCanClose is true.
         */
        close(checkCanClose?:boolean);

        /*
         * Whether object can be closed or not.
         */
        canClose():boolean;

        /*
         * Override this method if other closing logic is needed.
         */
        closing();
    }
}

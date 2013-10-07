module api_handler {

    /**
     * This interface defines the contract between UI components and the actual delete operation.
     */
    export interface DeleteHandler
    {
        /**
         * Handles the actual delete. Some implementations would delegate to a remote http call.
         *
         * @param model model to delete.
         * @param success callback that is called on success.
         * @param failure callback that is called on failure.
         */
        doDelete(model:any, success:() => void, failure:(error:String) => void);
    }

}

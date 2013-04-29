module admin.api.ui {

    /**
     * This class is the base for all delete dialog windows. The delete logic
     * is separated from the view using a delete handler.
     */
    export class DeleteDialog extends BaseDialog {
        /**
         * Set model to delete. This is untyped at this point, but should be typed
         * when generics are available.
         *
         * @param model model object to delete.
         */
        setModel(model:any):void;

        /**
         * Set the delete handler to use. When delete is clicked, the delete handler
         * is called.
         *
         * @param handler delete handler to use.
         */
        setHandler(handler:admin.api.handler.DeleteHandler):void;
    }

}

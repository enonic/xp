module api_ui_dialog {

    export interface UploadDialogUploaderEl {

        stop: () => void;

        reset: () => void;

        onFinishUpload: (fn:(resp:api_rest.Response) => void) => void;

        onError: (fn:(resp:api_rest.Response) => void) => void;

    }

}
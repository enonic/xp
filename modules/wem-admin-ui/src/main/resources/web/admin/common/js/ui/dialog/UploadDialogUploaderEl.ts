module api.ui.dialog {

    export interface UploadDialogUploaderEl {

        stop: () => void;

        reset: () => void;

        onFinishUpload: (fn:(resp:api.rest.Response) => void) => void;

        onError: (fn:(resp:api.rest.RequestError) => void) => void;

    }

}
module api_form_inputtype_content_image {

    export interface UploadDialogListener extends api_event.Listener {

        onImageUploaded: (uploadItem:api_ui.UploadItem) => void;

    }

}
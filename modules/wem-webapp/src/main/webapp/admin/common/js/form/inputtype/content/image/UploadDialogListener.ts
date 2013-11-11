module api_form_inputtype_content_image {

    export interface UploadDialogListener extends api_event.Listener {

        onImageUploaded: (id:string, name:string, mimeType:string) => void;

    }

}
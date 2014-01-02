module api.form.inputtype.content.image {

    export interface UploadDialogListener extends api.event.Listener {

        onImageUploaded: (uploadItem:api.ui.UploadItem) => void;

    }

}
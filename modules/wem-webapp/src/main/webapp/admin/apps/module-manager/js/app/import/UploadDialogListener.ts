module app_import {

    export interface UploadDialogListener extends api_event.Listener {

        onModuleUploaded: (id:string, name:string, mimeType:string) => void;

    }

}
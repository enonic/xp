module app_wizard_form {

    export interface FormItemOccurrenceViewListener extends api_event.Listener{

        onRemoveButtonClicked(toBeRemoved:FormItemOccurrenceView, index:number);
    }
}
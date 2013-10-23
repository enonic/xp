module api_form {

    export interface FormItemOccurrenceViewListener extends api_event.Listener{

        onRemoveButtonClicked(toBeRemoved:FormItemOccurrenceView, index:number);
    }
}
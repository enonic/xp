module api.form {

    export interface FormItemOccurrenceViewListener extends api.event.Listener{

        onRemoveButtonClicked(toBeRemoved:FormItemOccurrenceView, index:number);
    }
}
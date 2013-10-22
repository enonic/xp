module api_form {

    export interface FormItemOccurrencesListener extends api_event.Listener{

        onOccurrenceAdded(occurrence:FormItemOccurrence);

        onOccurrenceRemoved(occurrence:FormItemOccurrence);
    }
}
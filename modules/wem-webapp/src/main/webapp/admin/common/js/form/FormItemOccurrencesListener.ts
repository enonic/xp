module api_form {

    export interface FormItemOccurrencesListener extends api_event.Listener{

        onOccurrenceAdded(occurrence:FormItemOccurrence<FormItemOccurrenceView>);

        onOccurrenceRemoved(occurrence:FormItemOccurrence<FormItemOccurrenceView>);
    }
}
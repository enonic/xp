module api.form {

    export interface FormItemOccurrencesListener extends api.event.Listener{

        onOccurrenceAdded(occurrence:FormItemOccurrence<FormItemOccurrenceView>);

        onOccurrenceRemoved(occurrence:FormItemOccurrence<FormItemOccurrenceView>);
    }
}
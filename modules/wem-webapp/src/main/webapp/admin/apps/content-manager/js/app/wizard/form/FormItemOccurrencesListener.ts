module app_wizard_form {

    export interface FormItemOccurrencesListener extends api_event.Listener{

        onOccurrenceAdded(occurrence:FormItemOccurrence);

        onOccurrenceRemoved(occurrence:FormItemOccurrence);
    }
}
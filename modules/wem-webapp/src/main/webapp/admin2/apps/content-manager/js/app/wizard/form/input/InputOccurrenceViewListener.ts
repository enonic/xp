module app_wizard_form_input {

    export interface InputOccurrenceViewListener extends api_event.Listener{

        onRemoveButtonClicked(toBeRemoved:InputOccurrenceView, index:number);

        onAddButtonClicked(fromOccurrence:InputOccurrenceView);
    }
}
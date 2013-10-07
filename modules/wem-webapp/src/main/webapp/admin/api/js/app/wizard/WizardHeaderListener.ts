module api_app_wizard {

    export interface WizardHeaderListener extends api_event.Listener {

        onPropertyChanged(event:WizardHeaderPropertyChangedEvent);

    }

    export interface WizardHeaderPropertyChangedEvent {

        property:string;

        oldValue:string;

        newValue:string;
    }

}
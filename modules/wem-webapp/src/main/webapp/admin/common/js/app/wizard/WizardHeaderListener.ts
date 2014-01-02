module api.app.wizard {

    export interface WizardHeaderListener extends api.event.Listener {

        onPropertyChanged(event:WizardHeaderPropertyChangedEvent);

    }

    export interface WizardHeaderPropertyChangedEvent {

        property:string;

        oldValue:string;

        newValue:string;
    }

}
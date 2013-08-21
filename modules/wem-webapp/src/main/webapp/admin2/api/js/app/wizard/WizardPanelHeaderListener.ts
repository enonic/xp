module api_app_wizard {

    export interface WizardPanelHeaderListener extends api_event.Listener {

        onDisplayNameChanged?(oldValue:string, newValue:string);

        onNameChanged?(oldValue:string, newValue:string);

    }

}
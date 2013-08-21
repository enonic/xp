module api_app_wizard {

    export interface WizardStepNavigatorListener extends api_event.Listener {

        onStepShown(step:WizardStep);

    }

}
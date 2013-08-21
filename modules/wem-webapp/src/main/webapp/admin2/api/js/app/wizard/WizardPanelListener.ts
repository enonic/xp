module api_app_wizard {

    export interface WizardPanelListener extends WizardPanelHeaderListener {

        onClosed?(wizard:WizardPanel);

    }

}
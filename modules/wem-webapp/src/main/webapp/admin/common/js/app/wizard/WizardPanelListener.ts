module api.app.wizard {

    export interface WizardPanelListener {

        onClosed(wizard:WizardPanel<any>);

    }

}
module app_wizard {

    /**
     * TODO: The upcoming successor of SpaceWizardToolbar, when the Toolbar code is working....
     */
    export class SpaceWizardToolbar2 extends API_ui_toolbar.Toolbar {

        constructor(actions:app_wizard.SpaceWizardActions) {
            super();
            super.addAction(actions.SAVE_SPACE);
            super.addAction(actions.DUPLICATE_SPACE);
            super.addAction(actions.DELETE_SPACE);
            super.addGreedySpacer();
            super.addAction(actions.DELETE_SPACE);
        }
    }
}

module api.ui.panel {

    export class PanelShownEvent {

        private panel: Panel;

        private index: number;

        private previousPanel: Panel;

        constructor(panel: Panel, index: number, previousPanel: Panel) {
            this.panel = panel;
            this.index = index;
            this.previousPanel = previousPanel;
        }

        getPanel(): Panel {
            return this.panel;
        }

        getIndex(): number {
            return this.index;
        }

        getPreviousPanel(): Panel {
            return this.previousPanel;
        }
    }
}
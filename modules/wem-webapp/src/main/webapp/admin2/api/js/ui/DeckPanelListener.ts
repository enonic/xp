module api_ui {

    export interface DeckPanelListener extends api_ui.Listener {

        onPanelShown?(event:PanelShownEvent);

    }

    export interface PanelShownEvent {

        panel:Panel;

        index:number;

        previousPanel:Panel;
    }
}
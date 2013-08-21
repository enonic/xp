module api_ui {

    export interface DeckPanelListener extends api_event.Listener {

        onPanelShown?(event:PanelShownEvent);

    }

    export interface PanelShownEvent {

        panel:Panel;

        index:number;

        previousPanel:Panel;
    }
}
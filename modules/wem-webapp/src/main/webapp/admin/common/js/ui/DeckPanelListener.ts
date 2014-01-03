module api.ui {

    export interface DeckPanelListener extends api.event.Listener {

        onPanelShown(event:PanelShownEvent);

    }

    export interface PanelShownEvent {

        panel:Panel;

        index:number;

        previousPanel:Panel;
    }
}
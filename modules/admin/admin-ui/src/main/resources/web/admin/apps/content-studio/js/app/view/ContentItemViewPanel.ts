import "../../api.ts";
import {ContentItemPreviewPanel} from "./ContentItemPreviewPanel";
import {ContentItemViewToolbar} from "./ContentItemViewToolbar";
import {EditAction} from "./EditAction";
import {DeleteAction} from "./DeleteAction";
import {CloseAction} from "./CloseAction";
import {ContentItemStatisticsPanel} from "./ContentItemStatisticsPanel";
import {Router} from "../Router";
import {ShowPreviewEvent} from "../browse/ShowPreviewEvent";
import {ShowDetailsEvent} from "../browse/ShowDetailsEvent";

export class ContentItemViewPanel extends api.app.view.ItemViewPanel<api.content.ContentSummaryAndCompareStatus> {

    private statisticsPanel: api.app.view.ItemStatisticsPanel<api.content.ContentSummaryAndCompareStatus>;

    private statisticsPanelIndex: number;

    private previewPanel: ContentItemPreviewPanel;

    private previewMode: boolean;

    private previewPanelIndex: number;

    private deckPanel: api.ui.panel.DeckPanel;

    private editAction: api.ui.Action;

    private deleteAction: api.ui.Action;

    private closeAction: api.ui.Action;

    private actions: api.ui.Action[];

    constructor() {
        super();

        this.deckPanel = new api.ui.panel.DeckPanel();

        this.editAction = new EditAction(this);
        this.deleteAction = new DeleteAction(this);
        this.closeAction = new CloseAction(this, true);

        this.actions = [this.editAction, this.deleteAction, this.closeAction];

        let toolbar = new ContentItemViewToolbar({
            editAction: this.editAction,
            deleteAction: this.deleteAction
        });

        this.setToolbar(toolbar);
        this.setPanel(this.deckPanel);

        this.statisticsPanel = new ContentItemStatisticsPanel();
        this.previewPanel = new ContentItemPreviewPanel();

        this.statisticsPanelIndex = this.deckPanel.addPanel(this.statisticsPanel);
        this.previewPanelIndex = this.deckPanel.addPanel(this.previewPanel);

        this.showPreview(false);

        ShowPreviewEvent.on((event) => {
            this.showPreview(true);
        });

        ShowDetailsEvent.on((event) => {
            this.showPreview(false);
        });

        this.onShown((event: api.dom.ElementShownEvent) => {
            if (this.getItem()) {
                Router.setHash("view/" + this.getItem().getModel().getId());
            }
        });
    }

    setItem(item: api.app.view.ViewItem<api.content.ContentSummaryAndCompareStatus>) {
        super.setItem(item);
        this.statisticsPanel.setItem(item);
        this.previewPanel.setItem(item);
    }

    public showPreview(enabled: boolean) {
        this.previewMode = enabled;
        // refresh the view
        if (enabled) {
            this.deckPanel.showPanelByIndex(this.previewPanelIndex);
        } else {
            this.deckPanel.showPanelByIndex(this.statisticsPanelIndex);
        }
    }

    public getCloseAction(): api.ui.Action {
        return this.closeAction;
    }

    getActions(): api.ui.Action[] {
        return this.actions;
    }

}

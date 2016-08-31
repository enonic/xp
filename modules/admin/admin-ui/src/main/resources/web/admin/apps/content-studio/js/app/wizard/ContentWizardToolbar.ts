import "../../api.ts";
import {ContentWizardToolbarPublishControls} from "./ContentWizardToolbarPublishControls";

import CycleButton = api.ui.button.CycleButton;
import TogglerButton = api.ui.button.TogglerButton;

export interface ContentWizardToolbarParams {
    saveAction: api.ui.Action;
    duplicateAction: api.ui.Action;
    deleteAction: api.ui.Action;
    publishAction: api.ui.Action;
    publishTreeAction: api.ui.Action;
    unpublishAction: api.ui.Action;
    previewAction: api.ui.Action;
    showLiveEditAction: api.ui.Action;
    showFormAction: api.ui.Action;
    showSplitEditAction: api.ui.Action;
    publishMobileAction: api.ui.Action;
}

export class ContentWizardToolbar extends api.ui.toolbar.Toolbar {

    private contextWindowToggler: TogglerButton;
    private componentsViewToggler: TogglerButton;
    private cycleViewModeButton: CycleButton;
    private contentWizardToolbarPublishControls: ContentWizardToolbarPublishControls;

    constructor(params: ContentWizardToolbarParams) {
        super("content-wizard-toolbar");
        super.addAction(params.saveAction);
        super.addAction(params.deleteAction);
        super.addAction(params.duplicateAction);
        super.addAction(params.previewAction);
        super.addAction(params.unpublishAction).addClass("unpublish-button");
        super.addGreedySpacer();

        this.cycleViewModeButton = new CycleButton([params.showLiveEditAction, params.showFormAction]);
        this.componentsViewToggler = new TogglerButton("icon-clipboard", "Show Component View");
        this.contextWindowToggler = new TogglerButton("icon-cog", "Show Inspection Panel");

        this.contentWizardToolbarPublishControls = new ContentWizardToolbarPublishControls(
            params.publishAction, params.publishTreeAction, params.unpublishAction, params.publishMobileAction
        );

        super.addElement(this.contentWizardToolbarPublishControls);
        super.addElement(this.componentsViewToggler);
        super.addElement(this.contextWindowToggler);
        super.addElement(this.cycleViewModeButton);
    }

    getCycleViewModeButton(): CycleButton {
        return this.cycleViewModeButton;
    }

    getContextWindowToggler(): TogglerButton {
        return this.contextWindowToggler;
    }

    getComponentsViewToggler(): TogglerButton {
        return this.componentsViewToggler;
    }

    getContentWizardToolbarPublishControls() {
        return this.contentWizardToolbarPublishControls;
    }

}

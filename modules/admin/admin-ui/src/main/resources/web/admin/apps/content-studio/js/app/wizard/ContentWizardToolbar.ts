import '../../api.ts';
import {ContentWizardToolbarPublishControls} from './ContentWizardToolbarPublishControls';

import CycleButton = api.ui.button.CycleButton;
import TogglerButton = api.ui.button.TogglerButton;
import AppIcon = api.app.bar.AppIcon;
import Application = api.app.Application;
import Action = api.ui.Action;

export interface ContentWizardToolbarParams {
    application: Application;
    saveAction: Action;
    duplicateAction: Action;
    deleteAction: Action;
    publishAction: Action;
    publishTreeAction: Action;
    unpublishAction: Action;
    previewAction: Action;
    showLiveEditAction: Action;
    showFormAction: Action;
    showSplitEditAction: Action;
    publishMobileAction: Action;
}

export class ContentWizardToolbar extends api.ui.toolbar.Toolbar {

    private homeButton: AppIcon;
    private contextWindowToggler: TogglerButton;
    private componentsViewToggler: TogglerButton;
    private cycleViewModeButton: CycleButton;
    private contentWizardToolbarPublishControls: ContentWizardToolbarPublishControls;

    constructor(params: ContentWizardToolbarParams) {
        super('content-wizard-toolbar');

        let homeAction = new Action(params.application.getName());
        homeAction.onExecuted((action) => {
            let appId = params.application.getId();
            let tabId;
            if (navigator.userAgent.search('Chrome') > -1) {
                // add tab id for browsers that can focus tabs by id
                tabId = appId;
            }
            window.open(appId + '#/browse', tabId);     // add browse to prevent tab reload because of url mismatch
            return wemQ(null);
        });

        this.homeButton = new AppIcon(params.application, homeAction);
        super.addElement(this.homeButton);

        super.addAction(params.saveAction);
        super.addAction(params.deleteAction);
        super.addAction(params.duplicateAction);
        super.addAction(params.previewAction);
        super.addAction(params.unpublishAction).addClass('unpublish-button');
        super.addGreedySpacer();

        this.cycleViewModeButton = new CycleButton([params.showLiveEditAction, params.showFormAction]);
        this.componentsViewToggler = new TogglerButton('icon-clipboard', 'Show Component View');
        this.contextWindowToggler = new TogglerButton('icon-cog', 'Show Inspection Panel');

        this.contentWizardToolbarPublishControls = new ContentWizardToolbarPublishControls(params);

        super.addElement(this.cycleViewModeButton);
        super.addElement(this.contextWindowToggler);
        super.addElement(this.componentsViewToggler);
        super.addElement(this.contentWizardToolbarPublishControls);
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

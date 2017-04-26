import '../../api.ts';
import {ContentWizardActions} from './action/ContentWizardActions';
import {ContentWizardToolbarPublishControls} from './ContentWizardToolbarPublishControls';

import CycleButton = api.ui.button.CycleButton;
import TogglerButton = api.ui.button.TogglerButton;
import AppIcon = api.app.bar.AppIcon;
import Application = api.app.Application;
import Action = api.ui.Action;

export class ContentWizardToolbar extends api.ui.toolbar.Toolbar {

    private contextWindowToggler: TogglerButton;
    private componentsViewToggler: TogglerButton;
    private cycleViewModeButton: CycleButton;
    private contentWizardToolbarPublishControls: ContentWizardToolbarPublishControls;

    constructor(application: Application, actions: ContentWizardActions) {
        super('content-wizard-toolbar');

        this.addHomeButton(application);
        this.addActionButtons(actions);
        this.addPublishMenuButton(actions);
        this.addTogglerButtons(actions);
    }

    private addHomeButton(application: Application) {
        let homeAction = new Action(application.getName());
        homeAction.onExecuted((action) => {
            let appId = application.getId();
            let tabId;
            if (navigator.userAgent.search('Chrome') > -1) {
                // add tab id for browsers that can focus tabs by id
                tabId = appId;
            }
            window.open(appId + '#/browse', tabId);     // add browse to prevent tab reload because of url mismatch
            return wemQ(null);
        });

        super.addElement(new AppIcon(application, homeAction));
    }

    private addActionButtons(actions: ContentWizardActions) {

        super.addActions([
            actions.getSaveAction(),
            actions.getDeleteAction(),
            actions.getDuplicateAction(),
            actions.getPreviewAction(),
            actions.getUndoPendingDeleteAction()
        ]);

        // Unpublish button will be visible only on mobile resolution
        super.addAction(actions.getUnpublishAction()).addClass('unpublish-button');

        super.addGreedySpacer();
    }

    private addPublishMenuButton(actions: ContentWizardActions) {
        this.contentWizardToolbarPublishControls = new ContentWizardToolbarPublishControls(actions);
        super.addElement(this.contentWizardToolbarPublishControls);
    }

    private addTogglerButtons(actions: ContentWizardActions) {
        this.cycleViewModeButton = new CycleButton([actions.getShowLiveEditAction(), actions.getShowFormAction()]);
        this.componentsViewToggler = new TogglerButton('icon-clipboard', 'Show Component View');
        this.contextWindowToggler = new TogglerButton('icon-cog', 'Show Inspection Panel');

        super.addElement(this.cycleViewModeButton);
        super.addElement(this.contextWindowToggler);
        super.addElement(this.componentsViewToggler);
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

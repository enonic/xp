import '../../../../api.ts';
import {ContextWindow} from './ContextWindow';
import {ShowContentFormEvent} from '../../ShowContentFormEvent';
import {ShowSplitEditEvent} from '../../ShowSplitEditEvent';
import {ShowLiveEditEvent} from '../../ShowLiveEditEvent';
import {ContentWizardPanel} from '../../ContentWizardPanel';

import ComponentView = api.liveedit.ComponentView;
import Component = api.content.page.region.Component;
import PageView = api.liveedit.PageView;
import TogglerButton = api.ui.button.TogglerButton;

export class ContextWindowController {

    private contextWindow: ContextWindow;

    private contextWindowToggler: TogglerButton;

    private componentsViewToggler: TogglerButton;

    private togglerOverriden: boolean = false;

    private contentWizardPanel: ContentWizardPanel;

    constructor(contextWindow: ContextWindow, contentWizardPanel: ContentWizardPanel) {
        this.contextWindow = contextWindow;
        this.contentWizardPanel = contentWizardPanel;
        this.contextWindowToggler = contentWizardPanel.getContextWindowToggler();
        this.componentsViewToggler = contentWizardPanel.getComponentsViewToggler();

        let componentsView = this.contextWindow.getComponentsView();

        this.contextWindowToggler.onClicked((event: MouseEvent) => {
            // set overriden flag when toggle is on by click only
            if (this.contextWindowToggler.isEnabled()) {
                this.togglerOverriden = true;
            }
        });

        this.contextWindowToggler.onActiveChanged((isActive: boolean) => {
            if (isActive) {
                this.contextWindow.slideIn();
            } else {
                this.contextWindow.slideOut();
            }
        });

        this.componentsViewToggler.onActiveChanged((isActive: boolean) => {
            if (!componentsView.getParentElement() && isActive) {
                //append it on click only to be sure that content wizard panel is ready
                let offset = contentWizardPanel.getLivePanel().getEl().getOffsetToParent();
                componentsView.getEl().setOffset(offset);
                contentWizardPanel.appendChild(componentsView);
            }

            componentsView.setVisible(isActive);
        });

        componentsView.onHidden((event: api.dom.ElementHiddenEvent) => {
            this.componentsViewToggler.setActive(false, true);
        });

        let liveEditShownHandler = () => {
            if (this.contextWindow.isLiveFormShown()) {
                this.contextWindowToggler.setEnabled(true);
                this.componentsViewToggler.setEnabled(true);
            }
        };

        let liveEditHiddenHandler = () => {
            this.contextWindowToggler.setEnabled(false);
            this.componentsViewToggler.setEnabled(false);
        };

        ShowLiveEditEvent.on(liveEditShownHandler);
        ShowSplitEditEvent.on(liveEditShownHandler);
        ShowContentFormEvent.on(liveEditHiddenHandler);
    }
}

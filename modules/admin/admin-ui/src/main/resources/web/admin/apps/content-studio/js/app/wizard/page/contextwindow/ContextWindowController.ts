import "../../../../api.ts";

import ComponentView = api.liveedit.ComponentView;
import Component = api.content.page.region.Component;
import PageView = api.liveedit.PageView;
import TogglerButton = api.ui.button.TogglerButton;
import {ContextWindow} from "./ContextWindow";
import {ShowContentFormEvent} from "../../ShowContentFormEvent";
import {ShowSplitEditEvent} from "../../ShowSplitEditEvent";
import {ShowLiveEditEvent} from "../../ShowLiveEditEvent";

export class ContextWindowController {

    private contextWindow: ContextWindow;

    private contextWindowToggler: TogglerButton;

    private componentsViewToggler: TogglerButton;

    private togglerOverriden: boolean = false;

    constructor(contextWindow: ContextWindow, contextWindowToggler: TogglerButton, componentsViewToggler: TogglerButton) {
        this.contextWindow = contextWindow;
        this.contextWindowToggler = contextWindowToggler;
        this.componentsViewToggler = componentsViewToggler;

        var componentsView = this.contextWindow.getComponentsView();

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
            componentsView.setVisible(isActive);
        });

        componentsView.onHidden((event: api.dom.ElementHiddenEvent) => {
            this.componentsViewToggler.setActive(false, true);
        });

        var liveEditShownHandler = () => {
            if (this.contextWindow.isLiveFormShown()) {
                this.contextWindowToggler.setEnabled(true);
                this.componentsViewToggler.setEnabled(true);
            }
        };

        var liveEditHiddenHandler = () => {
            this.contextWindowToggler.setEnabled(false);
            this.componentsViewToggler.setEnabled(false);
        };

        ShowLiveEditEvent.on(liveEditShownHandler);
        ShowSplitEditEvent.on(liveEditShownHandler);
        ShowContentFormEvent.on(liveEditHiddenHandler);
    }
}

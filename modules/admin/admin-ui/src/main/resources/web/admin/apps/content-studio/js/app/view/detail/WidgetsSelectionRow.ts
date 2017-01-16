import "../../../api.ts";
import {WidgetView} from "./WidgetView";
import {DetailsView} from "./DetailsView";
import {InfoWidgetToggleButton} from "./button/InfoWidgetToggleButton";

import Dropdown = api.ui.selector.dropdown.Dropdown;
import DropdownConfig = api.ui.selector.dropdown.DropdownConfig;
import Option = api.ui.selector.Option;
import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;

export class WidgetsSelectionRow extends api.dom.DivEl {

    private detailsView: DetailsView;

    private widgetSelectorDropdown: WidgetSelectorDropdown;
    private infoWidgetToggleButton: InfoWidgetToggleButton;

    constructor(detailsView: DetailsView) {
        super('widgets-selection-row');

        this.detailsView = detailsView;

        this.infoWidgetToggleButton = new InfoWidgetToggleButton(detailsView);

        this.widgetSelectorDropdown = new WidgetSelectorDropdown(detailsView);
        this.widgetSelectorDropdown.addClass('widget-selector');

        this.widgetSelectorDropdown.onOptionSelected((event: OptionSelectedEvent<WidgetViewOption>) => {
            let widgetView = event.getOption().displayValue.getWidgetView();
            widgetView.setActive();
        });

        this.infoWidgetToggleButton.onClicked(() => {
            if (this.widgetSelectorDropdown.isDropdownShown()) {
                this.widgetSelectorDropdown.hideDropdown();
            }
        });

        this.widgetSelectorDropdown.prependChild(this.infoWidgetToggleButton);
        this.appendChild(this.widgetSelectorDropdown);
    }

    updateState(widgetView: WidgetView) {
        if (this.detailsView.isDefaultWidget(widgetView)) {
            this.infoWidgetToggleButton.setActive();
            this.widgetSelectorDropdown.removeClass('non-default-selected');
        } else {
            this.widgetSelectorDropdown.addClass('non-default-selected');
            this.infoWidgetToggleButton.setInactive();
            if (this.widgetSelectorDropdown.getValue() != widgetView.getWidgetName()) {
                this.widgetSelectorDropdown.setValue(widgetView.getWidgetName());
            }
        }
        if (this.widgetSelectorDropdown.getSelectedOption()) {
            this.widgetSelectorDropdown.getSelectedOptionView().getEl().setDisplay('inline-block');
        }
    }

    updateWidgetsDropdown(widgetViews: WidgetView[]) {
        this.widgetSelectorDropdown.removeAllOptions();

        widgetViews.forEach((view: WidgetView) => {

            let option = {
                value: view.getWidgetName(),
                displayValue: new WidgetViewOption(view)
            };

            this.widgetSelectorDropdown.addOption(option);
        });

        if (this.widgetSelectorDropdown.getOptionCount() < 2) {
            this.widgetSelectorDropdown.addClass('single-optioned');
        }

        let visisbleNow = this.isVisible();

        if (visisbleNow) {
            this.setVisible(false);
        }
        this.widgetSelectorDropdown.selectRow(0, true);
        if (visisbleNow) {
            this.setVisible(true);
        }
    }
}

export class WidgetSelectorDropdown extends Dropdown<WidgetViewOption> {

    constructor(detailsView: DetailsView) {
        super('widgetSelector', {
            disableFilter: true,
            skipExpandOnClick: true,
            inputPlaceholderText: ''
        });

        this.onClicked((event) => {
            if (this.isDefaultOptionDisplayValueViewer(event.target)) {
                if (this.getSelectedOption()) {
                    let widgetView = this.getSelectedOption().displayValue.getWidgetView();
                    if (widgetView != detailsView.getActiveWidget()) {
                        widgetView.setActive();
                    }
                    this.hideDropdown();
                }
            }
        });

        api.util.AppHelper.focusInOut(this, () => {
            this.hideDropdown();
        });
    }

    private isDefaultOptionDisplayValueViewer(object: Object) {
        if (object && object['id']) {
            let id = object['id'].toString();
            return id.indexOf('DropdownHandle') < 0 && id.indexOf('InfoWidgetToggleButton') < 0;
        }
        return false;
    }
}

export class WidgetViewOption {

    private widgetView: WidgetView;

    constructor(widgetView: WidgetView) {
        this.widgetView = widgetView;
    }

    getWidgetView(): WidgetView {
        return this.widgetView;
    }

    toString(): string {
        return this.widgetView.getWidgetName();
    }

}

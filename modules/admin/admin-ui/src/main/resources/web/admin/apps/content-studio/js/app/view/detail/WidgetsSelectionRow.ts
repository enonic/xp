import "../../../api.ts";

import Dropdown = api.ui.selector.dropdown.Dropdown;
import DropdownConfig = api.ui.selector.dropdown.DropdownConfig;
import Option = api.ui.selector.Option;
import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;
import {WidgetView} from "./WidgetView";
import {DetailsPanel} from "./DetailsPanel";
import {InfoWidgetToggleButton} from "./button/InfoWidgetToggleButton";

export class WidgetsSelectionRow extends api.dom.DivEl {

    private detailsPanel: DetailsPanel;

    private widgetSelectorDropdown: WidgetSelectorDropdown;
    private infoWidgetToggleButton: InfoWidgetToggleButton;

    constructor(detailsPanel: DetailsPanel) {
        super("widgets-selection-row");

        this.detailsPanel = detailsPanel;

        this.infoWidgetToggleButton = new InfoWidgetToggleButton(detailsPanel);

        this.widgetSelectorDropdown = new WidgetSelectorDropdown(detailsPanel);

        this.widgetSelectorDropdown.addClass("widget-selector");
        this.widgetSelectorDropdown.getInput().getEl().setDisabled(true);
        this.widgetSelectorDropdown.getInput().setPlaceholder("");

        this.widgetSelectorDropdown.onOptionSelected((event: OptionSelectedEvent<WidgetViewOption>) => {
            var widgetView = event.getOption().displayValue.getWidgetView();
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
        if (this.detailsPanel.isDefaultWidget(widgetView)) {
            this.infoWidgetToggleButton.setActive();
            this.widgetSelectorDropdown.removeClass("non-default-selected");
        } else {
            this.widgetSelectorDropdown.addClass("non-default-selected");
            this.infoWidgetToggleButton.setInactive();
            if (this.widgetSelectorDropdown.getValue() != widgetView.getWidgetName()) {
                this.widgetSelectorDropdown.setValue(widgetView.getWidgetName());
            }
        }
        if (this.widgetSelectorDropdown.getSelectedOption()) {
            this.widgetSelectorDropdown.getSelectedOptionView().getEl().setDisplay("inline-block");
        }
    }

    updateWidgetsDropdown(widgetViews: WidgetView[]) {
        this.widgetSelectorDropdown.removeAllOptions();

        widgetViews.forEach((view: WidgetView) => {

            var option = {
                value: view.getWidgetName(),
                displayValue: new WidgetViewOption(view)
            };

            this.widgetSelectorDropdown.addOption(option);
        });

        if (this.widgetSelectorDropdown.getOptionCount() < 2) {
            this.widgetSelectorDropdown.addClass("single-optioned")
        }

        var visisbleNow = this.isVisible();

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

    constructor(detailsPanel: DetailsPanel) {
        super("widgetSelector", <DropdownConfig<WidgetViewOption>>{disableFilter: true, skipExpandOnClick: true});

        this.onClicked((event) => {
            if (this.isDefaultOptionDisplayValueViewer(event.target)) {
                if (this.getSelectedOption()) {
                    var widgetView = this.getSelectedOption().displayValue.getWidgetView();
                    if (widgetView != detailsPanel.getActiveWidget()) {
                        widgetView.setActive();
                    }
                    this.hideDropdown();
                }
            }
        });
    }

    private isDefaultOptionDisplayValueViewer(object: Object) {
        if (object && object["id"]) {
            var id = object["id"].toString();
            return id.indexOf("DropdownHandle") < 0 && id.indexOf("InfoWidgetToggleButton") < 0;
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

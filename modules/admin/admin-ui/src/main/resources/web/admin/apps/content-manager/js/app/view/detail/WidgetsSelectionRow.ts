module app.view.detail {

    import Dropdown = api.ui.selector.dropdown.Dropdown;
    import DropdownConfig = api.ui.selector.dropdown.DropdownConfig;
    import InfoWidgetToggleButton = app.view.detail.button.InfoWidgetToggleButton;
    import Option = api.ui.selector.Option;
    import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;

    export class WidgetsSelectionRow extends api.dom.DivEl {

        private detailsPanel: DetailsPanel;

        private widgetSelectorDropdown: WidgetSelectorDropdown;
        private infoWidgetToggleButton: InfoWidgetToggleButton;

        constructor(detailsPanel: DetailsPanel) {
            super("widgets-selection-row");

            this.detailsPanel = detailsPanel;

            this.infoWidgetToggleButton = new InfoWidgetToggleButton(detailsPanel);

            this.widgetSelectorDropdown = new WidgetSelectorDropdown();

            this.widgetSelectorDropdown.addClass("widget-selector");
            this.widgetSelectorDropdown.getInput().getEl().setDisabled(true);
            this.widgetSelectorDropdown.getInput().setPlaceholder("");

            this.widgetSelectorDropdown.onOptionSelected((event: OptionSelectedEvent<WidgetViewOption>) => {
                var widgetView = event.getOption().displayValue.getWidgetView();
                widgetView.setActive();
            });

            this.appendChild(this.infoWidgetToggleButton);
            this.appendChild(this.widgetSelectorDropdown);
        }

        updateState(widgetView: WidgetView) {
            if (this.detailsPanel.isDefaultWidget(widgetView)) {
                this.infoWidgetToggleButton.setActive();
                this.widgetSelectorDropdown.removeClass("non-default-selected");
            } else {
                this.widgetSelectorDropdown.addClass("non-default-selected");
                this.infoWidgetToggleButton.setInactive();
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

        constructor() {
            super("widgetSelector", <DropdownConfig<WidgetViewOption>>{});

            this.onClicked((event) => {
                if (!this.isDropdownHandle(event.target)) {
                    if (this.getSelectedOption()) {
                        var widgetView = this.getSelectedOption().displayValue.getWidgetView();
                        widgetView.setActive();
                        this.hideDropdown();
                    }
                }
            });
        }

        private isDropdownHandle(object: Object) {
            if (object && object["id"] && object["id"].toString().indexOf("DropdownHandle") > 0) {
                return true;
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
}
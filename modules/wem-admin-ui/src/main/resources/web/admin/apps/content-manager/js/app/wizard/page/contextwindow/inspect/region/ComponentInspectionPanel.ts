module app.wizard.page.contextwindow.inspect.region {

    import Component = api.content.page.region.Component;
    import ComponentName = api.content.page.region.ComponentName;
    import ComponentView = api.liveedit.ComponentView;

    export interface ComponentInspectionPanelConfig {

        iconClass: string;
    }

    export class ComponentInspectionPanel<COMPONENT extends Component> extends app.wizard.page.contextwindow.inspect.BaseInspectionPanel {

        componentSelector: api.ui.selector.combobox.RichComboBox<any>;

        private component: COMPONENT;

        constructor(config: ComponentInspectionPanelConfig) {
            super();

        }

        setComponent(component: COMPONENT) {
            this.component = component;
        }

        setSelectorValue(value: string) {
            if (this.componentSelector) {
                this.componentSelector.clearSelection();
                var option = this.componentSelector.getComboBox().getOptionByValue(value);
                if (option) {
                    this.componentSelector.select(option.displayValue, true);
                    this.componentSelector.selectedOptionsView.show();
                }
            }
        }

        getComponentView(): ComponentView<Component> {
            throw new Error("Must be implemented by inheritors");
        }

        getComponent(): COMPONENT {
            return this.component;
        }
    }
}
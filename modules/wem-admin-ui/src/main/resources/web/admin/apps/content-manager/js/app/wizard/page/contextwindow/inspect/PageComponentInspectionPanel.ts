module app.wizard.page.contextwindow.inspect {

    import PageComponent = api.content.page.PageComponent;
    import ComponentName = api.content.page.ComponentName;
    import PageComponentView = api.liveedit.PageComponentView;

    export interface PageComponentInspectionPanelConfig {

        iconClass: string;

    }

    export class PageComponentInspectionPanel<COMPONENT extends PageComponent> extends BaseInspectionPanel {

        private namesAndIcon: api.app.NamesAndIconView;

        private component: COMPONENT;

        private nameInput: api.ui.text.TextInput;

        constructor(config: PageComponentInspectionPanelConfig) {
            super();

            this.namesAndIcon = new api.app.NamesAndIconView(new api.app.NamesAndIconViewBuilder().
                setSize(api.app.NamesAndIconViewSize.medium)).
                setIconClass(config.iconClass);

            this.appendChild(this.namesAndIcon);

            this.nameInput = new api.ui.text.TextInput('component-name').setValue('');
            var nameLabel = new api.dom.LabelEl("Name", this.nameInput, "component-name-header");
            this.appendChild(nameLabel);

            this.appendChild(this.nameInput);

            this.nameInput.onValueChanged((event: api.ui.ValueChangedEvent) => {
                this.component.setName(new ComponentName(event.getNewValue()));
            });
        }

        setComponent(component: COMPONENT) {

            this.component = component;

            this.namesAndIcon.setMainName(component.getName().toString());
            this.namesAndIcon.setSubName(component.getPath().toString());

            this.nameInput.setValue(component.getName().toString());
        }

        getPageComponentView(): PageComponentView<PageComponent> {
            throw new Error("Must be implemented by inheritors");
        }

        setMainName(value: string) {
            this.namesAndIcon.setMainName(value);
        }

        getComponent(): COMPONENT {
            return this.component;
        }
    }
}
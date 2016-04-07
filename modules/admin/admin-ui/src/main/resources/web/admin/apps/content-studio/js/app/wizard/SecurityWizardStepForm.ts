module app.wizard {

    import AccessControlList = api.security.acl.AccessControlList;
    import AccessControlListView = api.ui.security.acl.AccessControlListView;
    import AccessControlEntryView = api.ui.security.acl.AccessControlEntryView;
    import AccessControlEntry = api.security.acl.AccessControlEntry;
    import Content = api.content.Content;

    import DivEl = api.dom.DivEl;
    import LabelEl = api.dom.LabelEl;
    import Button = api.ui.button.Button;

    export class SecurityWizardStepForm extends api.app.wizard.WizardStepForm {

        private label: LabelEl;
        private inheritance: DivEl;
        private accessListView: AccessControlListView;
        private editLink: Button;

        private content: Content;

        constructor() {
            super("security-wizard-step-form");

            var label = new DivEl("input-label"),
                wrapper = new DivEl("wrapper required");
            this.label = new LabelEl("Permissions");
            wrapper.appendChild(this.label);
            label.appendChild(wrapper);

            this.inheritance = new DivEl(/*"inheritance"*/);

            this.accessListView = new AccessControlListView();
            this.accessListView.setItemsEditable(false);

            this.editLink = new Button("Edit Permissions");
            this.editLink.addClass("edit-permissions");

            this.editLink.onFocus((event) => {
                this.notifyFocused(event);
            });
            this.editLink.onBlur((event) => {
                this.notifyBlurred(event);
            });

            var formView = new DivEl("form-view"),
                inputView = new DivEl("input-view valid"),
                inputTypeView = new DivEl("input-type-view"),
                inputOccurrenceView = new DivEl("input-occurrence-view single-occurrence"),
                inputWrapper = new DivEl("input-wrapper");

            inputWrapper.appendChildren(this.inheritance, this.accessListView, this.editLink);

            inputOccurrenceView.appendChild(inputWrapper);
            inputTypeView.appendChild(inputOccurrenceView);
            inputView.appendChildren(label, inputTypeView);
            formView.appendChild(inputView);

            this.appendChild(formView);

            this.editLink.onClicked(() => {
                if (!!this.content) {
                    new api.content.OpenEditPermissionsDialogEvent(this.content).fire();
                }
            });

            ContentPermissionsAppliedEvent.on((event) => {
                var content = event.getContent();
                if (content.getId() === this.content.getId()) {
                    this.layout(content);
                }
            });
        }

        layout(content: api.content.Content) {

            this.accessListView.clearItems();

            content.getPermissions().getEntries().sort().forEach((entry) => {
                this.accessListView.addItem(entry);

                var entryView = <AccessControlEntryView> this.accessListView.getItemView(entry),
                    selector = entryView.getPermissionSelector();

                // detach onValueChanged events
                entryView.getValueChangedListeners().splice(0);
                entryView.getPermissionSelector().hide();

                entryView.onClicked(() => {
                    var isDisplayed = selector.getEl().getDisplay() !== "block";

                    this.accessListView.getItemViews().forEach((itemView) => {
                        (<AccessControlEntryView>itemView).getPermissionSelector().hide();
                    });

                    if (isDisplayed) {
                        selector.show();
                    }

                });
            });

            var inheritsText = "";
            if (content.isInheritPermissionsEnabled() && content.isRoot() == false) {
                inheritsText = "Inherits permissions from parent";
                this.inheritance.addClass("inheritance");
            } else {
                this.inheritance.removeClass("inheritance");
            }
            this.inheritance.setHtml(inheritsText);

            this.content = content;
        }

        update(content: api.content.Content, unchangedOnly: boolean = true) {
            //TODO: preserve changes
            this.layout(content);
        }

        giveFocus(): boolean {
            return this.accessListView.giveFocus();
        }

    }
}

import '../../api.ts';
import {ContentPermissionsApplyEvent} from './ContentPermissionsApplyEvent';

import AccessControlList = api.security.acl.AccessControlList;
import AccessControlListView = api.ui.security.acl.AccessControlListView;
import AccessControlEntryView = api.ui.security.acl.AccessControlEntryView;
import AccessControlEntry = api.security.acl.AccessControlEntry;
import Content = api.content.Content;

import DivEl = api.dom.DivEl;
import LabelEl = api.dom.LabelEl;
import Button = api.ui.button.Button;
import OpenEditPermissionsDialogEvent = api.content.event.OpenEditPermissionsDialogEvent;
import ContentPath = api.content.ContentPath;

export class SecurityWizardStepForm extends api.app.wizard.WizardStepForm {

    private label: LabelEl;
    private inheritance: DivEl;
    private accessListView: AccessControlListView;
    private editLink: Button;

    private contentId: ContentId;

    private contentPath: ContentPath;

    private displayName: string;

    private permissions: AccessControlList;

    private inheritPermissions: boolean;

    private overwritePermissions: boolean;

    constructor() {
        super('security-wizard-step-form');

        let label = new DivEl('input-label');
        let wrapper = new DivEl('wrapper required');
        this.label = new LabelEl('Permissions');
        wrapper.appendChild(this.label);
        label.appendChild(wrapper);

        this.inheritance = new DivEl(/*"inheritance"*/);

        this.accessListView = new AccessControlListView();
        this.accessListView.setItemsEditable(false);

        this.editLink = new Button('Edit Permissions');
        this.editLink.addClass('edit-permissions');

        this.editLink.onFocus((event) => {
            this.notifyFocused(event);
        });
        this.editLink.onBlur((event) => {
            this.notifyBlurred(event);
        });

        let formView = new DivEl('form-view');
        let inputView = new DivEl('input-view valid');
        let inputTypeView = new DivEl('input-type-view');
        let inputOccurrenceView = new DivEl('input-occurrence-view single-occurrence');
        let inputWrapper = new DivEl('input-wrapper');

        inputWrapper.appendChildren(this.inheritance, this.accessListView, this.editLink);

        inputOccurrenceView.appendChild(inputWrapper);
        inputTypeView.appendChild(inputOccurrenceView);
        inputView.appendChildren(label, inputTypeView);
        formView.appendChild(inputView);

        this.appendChild(formView);

        this.editLink.onClicked(() => {
            if (this.contentId) {
                OpenEditPermissionsDialogEvent.create().setContentId(this.contentId).setContentPath(this.contentPath).setDisplayName(
                    this.displayName).setPermissions(this.permissions).setInheritPermissions(
                    this.inheritPermissions).setOverwritePermissions(this.overwritePermissions).setImmediateApply(false).build().fire();
            }
        });

        ContentPermissionsApplyEvent.on((event) => {
            if (this.contentId.equals(event.getContentId())) {
                this.layoutPermissions(event.getPermissions(), event.isInheritPermissions(), event.isOverwritePermissions());
            }
        });
    }

    private doLayout() {
        this.accessListView.clearItems();

        this.permissions.getEntries().sort().forEach((entry) => {
            this.accessListView.addItem(entry);

            let entryView = <AccessControlEntryView> this.accessListView.getItemView(entry);
            let selector = entryView.getPermissionSelector();

            // detach onValueChanged events
            entryView.getValueChangedListeners().splice(0);
            entryView.getPermissionSelector().hide();

            entryView.onClicked(() => {
                let isDisplayed = selector.getEl().getDisplay() !== 'block';

                this.accessListView.getItemViews().forEach((itemView) => {
                    (<AccessControlEntryView>itemView).getPermissionSelector().hide();
                });

                if (isDisplayed) {
                    selector.show();
                }

            });
        });

        let inheritsText = '';
        if (this.inheritPermissions && this.contentPath.isRoot() == false) {
            inheritsText = 'Inherits permissions from parent';
            this.inheritance.addClass('inheritance');
        } else {
            this.inheritance.removeClass('inheritance');
        }
        this.inheritance.setHtml(inheritsText);
    }

    layoutPermissions(permissions: AccessControlList, isInherit: boolean, isOverwrite: boolean) {

        this.permissions = permissions;
        this.inheritPermissions = isInherit;
        this.overwritePermissions = isOverwrite;

        this.doLayout();
    }

    layout(content: api.content.Content) {

        this.contentId = content.getContentId();
        this.contentPath = content.getPath();
        this.displayName = content.getDisplayName();

        this.layoutPermissions(content.getPermissions(), content.isInheritPermissionsEnabled(), false);
    }

    update(content: api.content.Content, unchangedOnly: boolean = true) {
        //TODO: preserve changes
        this.layout(content);
    }

    apply(builder: api.content.ContentBuilder) {
        builder.setPermissions(this.permissions);
        builder.setInheritPermissionsEnabled(this.inheritPermissions);
        builder.setOverwritePermissionsEnabled(this.overwritePermissions);
    }

    giveFocus(): boolean {
        return this.accessListView.giveFocus();
    }

}

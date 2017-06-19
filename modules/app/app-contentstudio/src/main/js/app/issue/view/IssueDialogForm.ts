import PrincipalComboBox = api.ui.security.PrincipalComboBox;
import TextArea = api.ui.text.TextArea;
import PEl = api.dom.PEl;
import TextInput = api.ui.text.TextInput;
import ContentSummary = api.content.ContentSummary;
import PrincipalLoader = api.security.PrincipalLoader;
import PrincipalType = api.security.PrincipalType;
import FormItemBuilder = api.ui.form.FormItemBuilder;
import Validators = api.ui.form.Validators;
import FormItem = api.ui.form.FormItem;
import {Issue} from '../Issue';
import ValidityChangedEvent = api.ValidityChangedEvent;
import StringHelper = api.util.StringHelper;
import PrincipalKey = api.security.PrincipalKey;
import ContentId = api.content.ContentId;
import UserStoreKey = api.security.UserStoreKey;

export class IssueDialogForm extends api.ui.form.Form {

    private approversSelector: PrincipalComboBox;

    private description: TextArea;

    private contentItemsSelector: api.content.ContentComboBox;

    private descriptionText: PEl;

    private title: TextInput;

    private compactAssigneesView: boolean;

    private contentItemsAddedListeners: {(items: ContentSummary[]): void}[] = [];

    private contentItemsRemovedListeners: {(items: ContentSummary[]): void}[] = [];

    constructor(compactAssigneesView?: boolean) {

        super('issue-dialog-form');

        this.compactAssigneesView = !!compactAssigneesView;

        this.initElements();

        this.initFormView();

    }

    public doRender(): wemQ.Promise<boolean> {
        return super.doRender().then(() => {
            return this.approversSelector.getLoader().load().then(() => {
                return this.contentItemsSelector.getLoader().load().then(() => {
                    this.title.giveFocus();
                    return true;
                });
            });
        });
    }

    show() {
        super.show();
        this.displayValidationErrors(false);
    }

    private initElements() {

        this.title = new TextInput('title');

        this.description = new TextArea('description');
        this.description.addClass('description');

        this.descriptionText = new PEl('description-text');

        const principalLoader = new PrincipalLoader().setAllowedTypes([PrincipalType.USER]).skipPrincipals(
            [PrincipalKey.ofAnonymous(), PrincipalKey.ofUser(UserStoreKey.SYSTEM, 'su')]);

        this.approversSelector = api.ui.security.PrincipalComboBox.create().setLoader(principalLoader).setMaxOccurences(0).setCompactView(
            this.compactAssigneesView).build();

        this.contentItemsSelector = api.content.ContentComboBox.create().setLoader(new api.content.resource.ContentSummaryLoader()).build();

        this.contentItemsSelector.onOptionSelected((option) => {
            this.notifyContentItemsAdded(
                [option.getSelectedOption().getOption().displayValue]);
        });

        this.contentItemsSelector.onOptionDeselected((option) => {
            this.notifyContentItemsRemoved(
                [option.getSelectedOption().getOption().displayValue]);
        });
    }

    private initFormView() {

        const fieldSet: api.ui.form.Fieldset = new api.ui.form.Fieldset();

        const titleFormItem = this.addValidationViewer(
            new FormItemBuilder(this.title).setLabel('Title').setValidator(Validators.required).build());
        fieldSet.add(titleFormItem);

        const descriptionFormItem = this.addValidationViewer(new FormItemBuilder(this.description).setLabel('Description').build());
        fieldSet.add(descriptionFormItem);

        const selectorFormItem = this.addValidationViewer(
            new FormItemBuilder(this.approversSelector).setLabel('Assignees').setValidator(
                Validators.required).build());
        selectorFormItem.addClass('issue-approver-selector');
        fieldSet.add(selectorFormItem);

        fieldSet.appendChild(this.descriptionText);

        const contentItemsFormItem =
            new FormItemBuilder(this.contentItemsSelector).setLabel('Items').build();
        fieldSet.add(contentItemsFormItem);

        this.title.onValueChanged(() => {
            this.validate(true);
        });

        this.approversSelector.onValueChanged(() => {
            this.validate(true);
        });

        this.add(fieldSet);
    }

    public setReadOnly(readOnly: boolean) {
        this.title.setReadOnly(readOnly);
        this.description.setReadOnly(readOnly);
        this.approversSelector.setReadOnly(readOnly);

        const titleFormItem = <FormItem>this.title.getParentElement();
        titleFormItem.setVisible(!readOnly);

        const descFormItem = <FormItem>this.description.getParentElement();
        descFormItem.setVisible(!readOnly);

        this.descriptionText.setVisible(readOnly);

        const selectorFormItem = <FormItem>this.approversSelector.getParentElement();
        selectorFormItem.setLabel(readOnly ? 'Assignees:' : 'Invite users to work on issue');

        const contentItemsFormItem = <FormItem>this.contentItemsSelector.getParentElement();
        contentItemsFormItem.setVisible(!readOnly);
    }

    toggleContentItemsSelector(enabled: boolean) {
        const contentItemsFormItem = <FormItem>this.contentItemsSelector.getParentElement();
        contentItemsFormItem.setVisible(enabled);
    }

    public setIssue(issue: Issue) {
        this.doSetIssue(issue);
    }

    private doSetIssue(issue: Issue) {

        this.title.setValue(issue.getTitle());
        this.description.setValue(issue.getDescription());

        this.descriptionText.setHtml(issue.getDescription());
        this.descriptionText.toggleClass('empty', !issue.getDescription());

        if (this.isRendered()) {
            this.setApprovers(issue.getApprovers());
        } else {
            this.onRendered(() => {
                this.setApprovers(issue.getApprovers());
            });
        }
    }

    public displayValidationErrors(value: boolean) {
        if (value) {
            this.addClass(api.form.FormView.VALIDATION_CLASS);
        } else {
            this.removeClass(api.form.FormView.VALIDATION_CLASS);
        }
    }

    public getTitle(): string {
        return this.title.getValue();
    }

    public getDescription(): string {
        return this.description.getValue();
    }

    public getApprovers(): PrincipalKey[] {
        return this.approversSelector.getSelectedValues().map(value => PrincipalKey.fromString(value));
    }

    public giveFocus(): boolean {
        if (this.title) {
            return this.title.giveFocus();
        }
        return false;
    }

    public reset() {
        this.title.setValue('');
        this.description.setValue('');
        this.descriptionText.setHtml('');
        this.approversSelector.clearCombobox();

        this.contentItemsSelector.clearCombobox();
        this.contentItemsSelector.clearSelection();
        this.toggleContentItemsSelector(true);
    }

    public setContentItems(ids: ContentId[], silent: boolean = false) {
        this.contentItemsSelector.clearSelection();
        ids.forEach((id) => {
            this.contentItemsSelector.selectOptionByValue(id.toString(), silent);
        });
    }

    public selectContentItems(contents: ContentSummary[], silent: boolean = false) {
        if (!contents) {
            return;
        }
        contents.forEach((value) => {
            this.contentItemsSelector.select(value, false, silent);
        });
    }

    public deselectContentItems(contents: ContentSummary[], silent: boolean = false) {
        if (!contents) {
            return;
        }
        contents.forEach((value) => {
            this.contentItemsSelector.deselect(value, silent);
        });
    }

    private addValidationViewer(formItem: FormItem): FormItem {
        let validationRecordingViewer = new api.form.ValidationRecordingViewer();

        formItem.appendChild(validationRecordingViewer);

        formItem.onValidityChanged((event: ValidityChangedEvent) => {
            validationRecordingViewer.setError(formItem.getError());
        });

        return formItem;
    }

    private setApprovers(approvers: PrincipalKey[]) {
        this.approversSelector.clearSelection();

        if (approvers) {
            approvers.forEach((approver) => {
                this.approversSelector.selectOptionByValue(approver.toString());
            });
        }
    }

    onContentItemsAdded(listener: (items: ContentSummary[]) => void) {
        this.contentItemsAddedListeners.push(listener);
    }

    unContentItemsAdded(listener: (items: ContentSummary[]) => void) {
        this.contentItemsAddedListeners = this.contentItemsAddedListeners.filter((current) => {
            return listener !== current;
        });
    }

    private notifyContentItemsAdded(items: ContentSummary[]) {
        this.contentItemsAddedListeners.forEach((listener) => {
            listener(items);
        });
    }

    onContentItemsRemoved(listener: (items: ContentSummary[]) => void) {
        this.contentItemsRemovedListeners.push(listener);
    }

    unContentItemsRemoved(listener: (items: ContentSummary[]) => void) {
        this.contentItemsRemovedListeners = this.contentItemsRemovedListeners.filter((current) => {
            return listener !== current;
        });
    }

    private notifyContentItemsRemoved(items: ContentSummary[]) {
        this.contentItemsRemovedListeners.forEach((listener) => {
            listener(items);
        });
    }
}

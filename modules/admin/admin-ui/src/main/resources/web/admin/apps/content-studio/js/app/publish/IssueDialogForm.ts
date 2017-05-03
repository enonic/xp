import '../../api.ts';
import {Issue} from './Issue';
import PrincipalComboBox = api.ui.security.PrincipalComboBox;
import TextArea = api.ui.text.TextArea;
import TextInput = api.ui.text.TextInput;
import PrincipalType = api.security.PrincipalType;
import FormItemBuilder = api.ui.form.FormItemBuilder;
import Validators = api.ui.form.Validators;
import FormItem = api.ui.form.FormItem;
import ValidityChangedEvent = api.ValidityChangedEvent;
import PrincipalKey = api.security.PrincipalKey;
import PEl = api.dom.PEl;
import StringHelper = api.util.StringHelper;

export class IssueDialogForm extends api.ui.form.Form {

    private selector: PrincipalComboBox;

    private description: TextArea;

    private descriptionText: PEl;

    private title: TextInput;

    constructor() {

        super('issue-dialog-form');

        this.initElements();

        this.initFormView();
    }

    public doRender(): wemQ.Promise<boolean> {
        return super.doRender().then(() => {
            return this.selector.getLoader().load().then(() => {
                this.title.giveFocus();
                return true;
            });
        });
    }

    show() {
        super.show();

        this.displayValidationErrors(false);

        // this.title.giveFocus();
    }

    private initElements() {

        this.title = new TextInput('title');

        this.description = new TextArea('description');
        this.description.addClass('description');

        this.descriptionText = new PEl('description-text');

        const principalLoader = new api.security.PrincipalLoader().setAllowedTypes([PrincipalType.USER]);
        this.selector = api.ui.security.PrincipalComboBox.create().setLoader(principalLoader).setMaxOccurences(0).build();
    }

    private initFormView() {

        const fieldSet: api.ui.form.Fieldset = new api.ui.form.Fieldset();

        const titleFormItem = this.addValidationViewer(
            new FormItemBuilder(this.title).setLabel('Title').setValidator(Validators.required).build());
        fieldSet.add(titleFormItem);

        const descriptionFormItem = this.addValidationViewer(new FormItemBuilder(this.description).setLabel('Description').build());
        fieldSet.add(descriptionFormItem);

        fieldSet.appendChild(this.descriptionText);

        const selectorFormItem = this.addValidationViewer(
            new FormItemBuilder(this.selector).setLabel('Invite users to work on issue').setValidator(Validators.required).build());
        fieldSet.add(selectorFormItem);

        this.title.onValueChanged(() => {
            this.validate(true);
        });

        this.selector.onValueChanged(() => {
            this.validate(true);
        });

        this.add(fieldSet);
    }

    public setReadOnly(readOnly: boolean) {
        this.title.setReadOnly(readOnly);
        this.description.setReadOnly(readOnly);
        this.selector.setReadOnly(readOnly);

        const titleFormItem = <FormItem>this.title.getParentElement();
        titleFormItem.setVisible(!readOnly);

        const descFormItem = <FormItem>this.description.getParentElement();
        descFormItem.setVisible(!readOnly);

        this.descriptionText.setVisible(readOnly && !StringHelper.isBlank(this.descriptionText.getHtml()));

        const selectorFormItem = <FormItem>this.selector.getParentElement();
        selectorFormItem.setLabel(readOnly ? 'Assignees:' : 'Invite users to work on issue');
    }

    public setIssue(issue: Issue) {
        this.doSetIssue(issue);
    }

    private doSetIssue(issue: Issue) {

        this.title.setValue(issue.getTitle());
        this.description.setValue(issue.getDescription());
        this.descriptionText.setHtml(issue.getDescription());

        issue.getApprovers().forEach((approver) => {
            this.selector.selectOptionByValue(approver.toString());
        });
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
        return this.selector.getSelectedValues().map(value => PrincipalKey.fromString(value));
    }

    public giveFocus(): boolean {
        if (this.title) {
            return this.title.giveFocus();
        }
        return false;
    }

    public reset() {
        this.title.setValue('', true);
        this.description.setValue('', true);
        this.descriptionText.setHtml('');
        this.selector.setValue('', true);
    }

    private addValidationViewer(formItem: FormItem): FormItem {
        let validationRecordingViewer = new api.form.ValidationRecordingViewer();

        formItem.appendChild(validationRecordingViewer);

        formItem.onValidityChanged((event: ValidityChangedEvent) => {
            validationRecordingViewer.setError(formItem.getError());
        });

        return formItem;
    }
}

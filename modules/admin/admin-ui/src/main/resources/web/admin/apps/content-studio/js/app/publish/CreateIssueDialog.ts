import '../../api.ts';

import DateTimePickerBuilder = api.ui.time.DateTimePickerBuilder;
import DateTimePicker = api.ui.time.DateTimePicker;
import TextArea = api.ui.text.TextArea;
import UserStoreAccessSelector = api.ui.security.acl.UserStoreAccessSelector;
import PrincipalSelector = api.content.form.inputtype.principalselector.PrincipalSelector;
import PrincipalType = api.security.PrincipalType;
import PrincipalComboBox = api.ui.security.PrincipalComboBox;
import RoleKeys = api.security.RoleKeys;
import FormItemBuilder = api.ui.form.FormItemBuilder;
import TextLine = api.form.inputtype.text.TextLine;
import TextInput = api.ui.text.TextInput;
import CreateIssueRequest = api.issue.resource.CreateIssueRequest;
import Validators = api.ui.form.Validators;
import ValidityChangedEvent = api.ValidityChangedEvent;
import FormItem = api.ui.form.FormItem;
import PrincipalKey = api.security.PrincipalKey;

export class CreateIssueDialog extends api.ui.dialog.ModalDialog {

    private items: ContentId[];

    private selector: PrincipalComboBox;

    private description: TextArea;

    private title: TextInput;

    private confirmButton: api.ui.dialog.DialogButton;

    private form: api.ui.form.Form;

    private onCloseCallback: () => void;

    private onSuccessCallback: () => void;

    constructor() {
        super('Create Issue');

        this.getEl().addClass('create-issue-dialog');

        this.initElements();

        this.initActions();

        this.initFormView();

        this.addCancelButtonToBottom('< Back');

    }

    public setItems(items: ContentId[]) {
        this.items = items;
        (<CreateIssueAction>this.confirmButton.getAction()).updateLabel(this.items ? this.items.length : 0);
    }

    show() {
        this.reset();
        this.displayValidationErrors(false);

        api.dom.Body.get().appendChild(this);
        super.show();
    }

    close() {
        super.close();
        this.remove();
        if (this.onCloseCallback) {
            this.onCloseCallback();
        }
    }

    onClose(onCloseCallback: () => void) {
        this.onCloseCallback = onCloseCallback;
    }

    onSuccess(onSuccessCallback: () => void) {
        this.onSuccessCallback = onSuccessCallback;
    }

    private initElements() {

        this.title = new TextInput('title');

        this.description = new TextArea('description');
        this.description.addClass('description');

        const principalLoader = new api.security.PrincipalLoader().setAllowedTypes([PrincipalType.USER]);

        this.selector = api.ui.security.PrincipalComboBox.create().setLoader(principalLoader).setMaxOccurences(0).build();
    }

    private initFormView() {

        this.form = new api.ui.form.Form();
        const fieldSet = new api.ui.form.Fieldset();

        const titleFormItem = this.addValidationViewer(
            new FormItemBuilder(this.title).setLabel('Title').setValidator(Validators.required).build());

        this.title.onValueChanged(() => {
            this.form.validate(true);
        });

        fieldSet.add(titleFormItem);

        const descriptionFormItem = this.addValidationViewer(new FormItemBuilder(this.description).setLabel('Description').build());
        fieldSet.add(descriptionFormItem);

        const selectorFormItem = this.addValidationViewer(
            new FormItemBuilder(this.selector).setLabel('Invite users to work on issue').setValidator(Validators.required).build());
        fieldSet.add(selectorFormItem);

        this.selector.onValueChanged(() => {
            this.form.validate(true);
            this.centerMyself();
        });

        this.form.add(fieldSet);

        this.appendChildToContentPanel(this.form);
        this.centerMyself();

    }

    protected displayValidationErrors(value: boolean) {
        if (value) {
            this.form.addClass(api.form.FormView.VALIDATION_CLASS);
        } else {
            this.form.removeClass(api.form.FormView.VALIDATION_CLASS);
        }
    }

    private doCreateIssue() {

        const valid = this.form.validate(true).isValid();

        this.displayValidationErrors(!valid);

        if (valid) {
            const createIssueRequest = new CreateIssueRequest()
                .setApprovers(this.selector.getSelectedValues().map(value => PrincipalKey.fromString(value)))
                .setItems(this.items).setDescription(this.description.getValue()).setTitle(this.title.getValue());

            createIssueRequest.sendAndParse().then(() => {
                this.close();
                this.onSuccessCallback();
                api.notify.showSuccess('New issue created successfully');
            }).catch((reason) => {
                if (reason && reason.message) {
                    api.notify.showError(reason.message);
                }
            });
        }
    }

    private reset() {
        this.title.setValue('', true);
        this.description.setValue('', true);
        this.selector.setValue('', true);
    }

    private initActions() {
        const createAction = new CreateIssueAction(this.items);
        createAction.onExecuted(this.doCreateIssue.bind(this));
        this.confirmButton = this.addAction(createAction, true);

    }

    private addValidationViewer(formItem: FormItem): FormItem {
        let validationRecordingViewer = new api.form.ValidationRecordingViewer();

        formItem.appendChild(validationRecordingViewer);

        formItem.onValidityChanged((event: ValidityChangedEvent) => {
            validationRecordingViewer.setError(formItem.getError());
        });

        return formItem;
    }

    protected hasSubDialog(): boolean {
        return false;
    }
}

export class CreateIssueAction extends api.ui.Action {
    constructor(items: ContentId[]) {
        super();
        this.updateLabel(items ? items.length : 0);
        this.setIconClass('create-issue-action');
    }

    public updateLabel(count: number) {
        let label = 'Create Issue... ';
        if (count) {
            label += '(' + count + ')';
        }
        this.setLabel(label);
    }
}

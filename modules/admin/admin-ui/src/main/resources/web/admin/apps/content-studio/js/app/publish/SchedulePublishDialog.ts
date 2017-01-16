import "../../api.ts";

import DateTimePickerBuilder = api.ui.time.DateTimePickerBuilder;
import DateTimePicker = api.ui.time.DateTimePicker;

export class SchedulePublishDialog extends api.ui.dialog.ModalDialog {

    private formView: api.form.FormView;
    private propertySet: api.data.PropertySet;

    private confirmDeleteButton: api.ui.dialog.DialogButton;

    private confirmScheduleAction: api.ui.Action;

    private onCloseCallback: () => void;

    private onScheduleCallback: () => void;

    constructor() {
        super("Scheduled Publishing");

        this.getEl().addClass("schedule-publish-dialog");

        this.addSubtitle();

        this.initConfirmScheduleAction();

        this.initFormView();

        this.addCancelButtonToBottom("Back");
    }

    show() {
        this.resetPublishDates();
        this.formView.displayValidationErrors(false);
        this.confirmScheduleAction.setEnabled(true);
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

    onSchedule(onScheduleCallback: () => void) {
        this.onScheduleCallback = onScheduleCallback;
    }

    private initFormView() {
        let formBuilder = new api.form.FormBuilder().addFormItem(
            new api.form.InputBuilder().setName("from").setInputType(api.content.form.inputtype.publish.PublishFrom.getName()).setLabel(
                "Online from").setHelpText("Offline items will go online at the specified time").setOccurrences(
                new api.form.OccurrencesBuilder().setMinimum(1).setMaximum(1).build()).setInputTypeConfig({}).setMaximizeUIInputWidth(
                true).build()).addFormItem(
            new api.form.InputBuilder().setName("to").setInputType(api.content.form.inputtype.publish.PublishToFuture.getName()).setLabel(
                "Online to").setHelpText("Online items will be taken offline at the specified time").setOccurrences(
                new api.form.OccurrencesBuilder().setMinimum(0).setMaximum(1).build()).setInputTypeConfig({}).setMaximizeUIInputWidth(
                true).build());

        this.propertySet = new api.data.PropertyTree().getRoot();
        this.formView = new api.form.FormView(api.form.FormContext.create().build(), formBuilder.build(), this.propertySet);

        this.formView.layout().then(() => {
            this.formView.onValidityChanged((event: api.form.FormValidityChangedEvent) => {
                this.confirmScheduleAction.setEnabled(event.isValid());
                this.formView.displayValidationErrors(true);
            });
            this.propertySet.onChanged(() => {
                this.formView.validate();
            });
            this.appendChildToContentPanel(this.formView);
            this.centerMyself();

        });
    }

    private addSubtitle() {
        this.appendChildToHeader(
            new api.dom.H6El("schedule-publish-dialog-subtitle").setHtml(
                "NB: Only offline items will be scheduled. All other changes will be published immediately.",
                false));
    }

    private initConfirmScheduleAction() {
        this.confirmScheduleAction = new api.ui.Action("Schedule");

        this.confirmScheduleAction.setIconClass("confirm-schedule-action");
        this.confirmScheduleAction.onExecuted(() => {
            let validationRecording = this.formView.validate();
            if (validationRecording.isValid()) {
                this.close();
                if (this.onScheduleCallback) {
                    this.onScheduleCallback();
                }
            } else {
                this.confirmScheduleAction.setEnabled(false);
                this.formView.displayValidationErrors(true);
            }
        });

        this.confirmDeleteButton = this.addAction(this.confirmScheduleAction, true, true);
    }

    getFromDate(): Date {
        let publishFrom = this.propertySet.getDateTime("from");
        return publishFrom && publishFrom.toDate();
    }

    getToDate(): Date {
        let publishTo = this.propertySet.getDateTime("to");
        return publishTo && publishTo.toDate();
    }

    resetPublishDates() {
        this.propertySet.reset();
        this.formView.update(this.propertySet);
        this.formView.validate();
    }

    protected hasSubDialog(): boolean {
        return true;
    }
}

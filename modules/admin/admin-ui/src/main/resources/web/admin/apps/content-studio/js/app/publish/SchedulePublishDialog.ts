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
        var formBuilder = new api.form.FormBuilder().
            addFormItem(new api.form.InputBuilder().
                setName("from").
                setInputType(api.content.form.inputtype.publish.PublishFrom.getName()).
                setLabel("Publish From").
                setOccurrences(new api.form.OccurrencesBuilder().setMinimum(1).setMaximum(1).build()).
                setInputTypeConfig({}).
                setMaximizeUIInputWidth(true).
                build()).
            addFormItem(new api.form.InputBuilder().
                setName("to").
                setInputType(api.content.form.inputtype.publish.PublishTo.getName()).
                setLabel("Publish To").
                setOccurrences(new api.form.OccurrencesBuilder().setMinimum(0).setMaximum(1).build()).
                setInputTypeConfig({}).
                setMaximizeUIInputWidth(true).
                build());

        this.propertySet = new api.data.PropertyTree().getRoot();
        this.formView = new api.form.FormView(api.form.FormContext.create().build(), formBuilder.build(), this.propertySet);
        this.formView.addClass("display-validation-errors");
        this.formView.layout().then(() => {
            this.appendChildToContentPanel(this.formView);
            this.formView.onValidityChanged((event: api.form.FormValidityChangedEvent) => {
                this.confirmScheduleAction.setEnabled(event.isValid());
            });
            this.confirmScheduleAction.setEnabled(this.formView.isValid());
        });
    }

    private addSubtitle() {
        this.appendChildToHeader(new api.dom.H6El("schedule-publish-dialog-subtitle").
            setHtml("NB: Items with existing publish times will not be affected. " +
                    "Changes to previously published items will be effective immediately.",
            false));
    }

    private initConfirmScheduleAction() {
        this.confirmScheduleAction = new api.ui.Action("Publish");

        this.confirmScheduleAction.setIconClass("confirm-schedule-action");
        this.confirmScheduleAction.onExecuted(() => {
            this.close();
            if (this.onScheduleCallback) {
                this.onScheduleCallback();
            }
        });

        this.confirmDeleteButton = this.addAction(this.confirmScheduleAction, true, true);
    }

    getFromDate(): Date {
        var publishFrom = this.propertySet.getDateTime("from");
        return publishFrom && publishFrom.toDate();
    }

    getToDate(): Date {
        var publishTo = this.propertySet.getDateTime("to");
        return publishTo && publishTo.toDate();
    }

    resetPublishDates() {
        this.propertySet.reset();
        this.propertySet.setLocalDateTime("from", 0, api.util.LocalDateTime.fromDate(new Date()));
        this.formView.update(this.propertySet);
    }

    protected hasSubDialog(): boolean {
        return true;
    }
}
import "../../api.ts";

import DateTimePickerBuilder = api.ui.time.DateTimePickerBuilder;
import DateTimePicker = api.ui.time.DateTimePicker;

export class SchedulePublishDialog extends api.ui.dialog.ModalDialog {

    private formView: api.form.FormView;
    private propertySet: api.data.PropertySet;

    private confirmDeleteButton: api.ui.dialog.DialogButton;

    private confirmScheduleAction: api.ui.Action;

    private fromDate: DateTimePicker;

    private toDate: DateTimePicker;

    private onCloseCallback: () => void;

    private onScheduleCallback: () => void;

    constructor() {
        super("Scheduled Publishing");

        this.getEl().addClass("schedule-publish-dialog");

        this.addSubtitle();

        this.initConfirmScheduleAction();

        this.initScheduleInputs();

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
                setName("publishFrom").
                setInputType(api.content.form.inputtype.time.DateTime.getName()).
                setLabel("Publish From--2").
                setOccurrences(new api.form.OccurrencesBuilder().setMinimum(1).setMaximum(1).build()).
                setInputTypeConfig({}).
                setMaximizeUIInputWidth(true).
                build()).
            addFormItem(new api.form.InputBuilder().
                setName("publishTo").
                setInputType(api.content.form.inputtype.time.DateTime.getName()).
                setLabel("Publish To--").
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
                console.log('event: ' + event);
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

    private initScheduleInputs() {

        this.fromDate = new DateTimePickerBuilder().build();
        this.fromDate.onSelectedDateTimeChanged((event: api.ui.time.SelectedDateChangedEvent) => {
            this.validate();
        });

        this.toDate = new DateTimePickerBuilder().build();
        this.toDate.onSelectedDateTimeChanged((event: api.ui.time.SelectedDateChangedEvent) => {
            this.validate();
        });

        var publishFromLabel = new api.dom.LabelEl("Publish From", this.fromDate, "publish-from-label"),
            publishToLabel = new api.dom.LabelEl("Publish To", this.toDate, "publish-to-label");

        this.appendChildToContentPanel(publishFromLabel);
        this.appendChildToContentPanel(this.fromDate);
        this.appendChildToContentPanel(publishToLabel);
        this.appendChildToContentPanel(this.toDate);
    }

    private validate() {
        var fromDate = this.fromDate.getSelectedDateTime(),
            toDate = this.toDate.getSelectedDateTime();

        if (!fromDate || !this.fromDate.isValid()) {
            this.fromDate.addClass("invalid");
        } else {
            this.fromDate.removeClass("invalid");
        }

        if (toDate && !this.toDate.isValid()) {
            this.toDate.addClass("invalid");
        } else {
            this.toDate.removeClass("invalid");
        }

        // check toDate is before fromDate
        if (fromDate && toDate && toDate < fromDate) {
            this.toDate.addClass("invalid");
        }

        // check toDate is after now
        if (toDate && toDate < new Date()) {
            this.toDate.addClass("invalid");
        }

        this.confirmScheduleAction.setEnabled(!this.fromDate.hasClass("invalid") && !this.toDate.hasClass("invalid"));
    }

    getFromDate(): Date {
        return this.fromDate.getSelectedDateTime();
    }

    getToDate(): Date {
        return this.toDate.getSelectedDateTime();
    }

    resetPublishDates() {
        console.log('reset');
        this.propertySet.reset();
        this.propertySet.setLocalDateTime("publishFrom", 0, api.util.LocalDateTime.fromDate(new Date()));
        this.formView.update(this.propertySet);

        this.resetFromDate();
        this.resetToDate();
    }

    private resetFromDate() {
        this.fromDate.setSelectedDateTime(new Date());
        this.fromDate.removeClass("invalid");
    }

    private resetToDate() {
        this.toDate.setSelectedDateTime(null);
        this.toDate.removeClass("invalid");
    }

    protected hasSubDialog(): boolean {
        return true;
    }
}
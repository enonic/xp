module api.form {

    import PropertyPath = api.data.PropertyPath;
    import Property = api.data.Property;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;
    import PropertyTree = api.data.PropertyTree;
    import PropertySet = api.data.PropertySet;

    /**
     * Creates a UI component representing the given [[Form]] backed by given [[api.data.PropertySet]].
     * Form data is both read from and written to the given [[api.data.PropertySet]] as the user changes the form.
     *
     * When displaying a form for a empty PropertyTree, then FormItemSet's will not be displayed by default.
     * To enable displaying set [[FormContext.showEmptyFormItemSetOccurrences]] to true.
     */
    export class FormView extends api.dom.DivEl {

        private context: FormContext;

        private form: Form;

        private data: PropertySet;

        private formItemViews: FormItemView[] = [];

        private formItemLayer: FormItemLayer;

        private formValidityChangedListeners: {(event: FormValidityChangedEvent):void}[] = [];

        private previousValidationRecording: ValidationRecording;

        private width: number;

        private focusListeners: {(event: FocusEvent):void}[] = [];

        private blurListeners: {(event: FocusEvent):void}[] = [];

        private layoutFinishedListeners: {():void}[] = [];

        public static debug: boolean = false;

        /**
         * @param context the form context.
         * @param form the form to display.
         * @param data the data to back the form with.
         */
        constructor(context: FormContext, form: Form, data: PropertySet) {
            super("form-view");
            this.context = context;
            this.form = form;
            this.data = data;

            this.formItemLayer = new FormItemLayer(context);
        }

        /**
         * Lays out the form.
         */
        public layout(): wemQ.Promise<void> {

            var deferred = wemQ.defer<void>();

            var formItems = this.form.getFormItems();
            var layoutPromise: wemQ.Promise<FormItemView[]> = this.formItemLayer.
                setFormItems(formItems).
                setParentElement(this).
                layout(this.data);

            layoutPromise.then((formItemViews: FormItemView[]) => {

                this.formItemViews = formItemViews;
                api.util.assert(this.formItemViews.length == formItems.length,
                    "Not all FormItemView-s was created. Expected " + formItems.length + ", was: " + formItemViews.length);

                deferred.resolve(null);

                this.formItemViews.forEach((formItemView: FormItemView) => {

                    formItemView.onFocus((event: FocusEvent) => {
                        this.notifyFocused(event);
                    });

                    formItemView.onBlur((event: FocusEvent) => {
                        this.notifyBlurred(event);
                    });

                    formItemView.onValidityChanged((event: RecordingValidityChangedEvent) => {
                        if (!this.previousValidationRecording) {
                            this.previousValidationRecording = event.getRecording();
                            this.notifyValidityChanged(new FormValidityChangedEvent(this.previousValidationRecording,
                                event.isInputValueBroken()));
                        }
                        else {
                            if (event.isValid()) {
                                this.previousValidationRecording.removeByPath(event.getOrigin());
                            }
                            else {
                                this.previousValidationRecording.flatten(event.getRecording());
                            }

                            this.notifyValidityChanged(new FormValidityChangedEvent(this.previousValidationRecording,
                                event.isInputValueBroken()));

                        }
                    });

                    formItemView.onEditContentRequest((content: api.content.ContentSummary) => {
                        var summaryAndStatus = api.content.ContentSummaryAndCompareStatus.fromContentSummary(content);
                        new api.content.event.EditContentEvent([summaryAndStatus]).fire();
                    })
                });

                api.dom.WindowDOM.get().onResized((event: UIEvent) => this.checkSizeChanges(), this);
                this.onShown(() => this.checkSizeChanges());

            }).catch((reason: any) => {
                api.DefaultErrorHandler.handle(reason);
            }).done(() => {
                this.notifyLayoutFinished();
            });

            return deferred.promise;
        }

        public update(propertySet: PropertySet, unchangedOnly?: boolean): wemQ.Promise<void> {
            if (FormView.debug) {
                console.debug('FormView.update' + (unchangedOnly ? ' (unchanged only)' : ''), this, propertySet);
            }
            return this.formItemLayer.update(propertySet, unchangedOnly);
        }

        private checkSizeChanges() {
            if (this.isVisible() && this.isSizeChanged()) {
                this.preserveCurrentSize();
                this.broadcastFormSizeChanged();
            }
        }

        private preserveCurrentSize() {
            this.width = this.getEl().getWidth();
        }

        private isSizeChanged(): boolean {
            return this.width != this.getEl().getWidth();
        }

        private broadcastFormSizeChanged() {
            this.formItemViews.forEach((formItemView: FormItemView) => {
                formItemView.broadcastFormSizeChanged();
            });
        }

        public hasValidUserInput(): boolean {

            var result = true;
            this.formItemViews.forEach((formItemView: FormItemView) => {
                if (!formItemView.hasValidUserInput()) {
                    result = false;
                }
            });

            return result;
        }

        public validate(silent?: boolean, forceNotify: boolean = false): ValidationRecording {

            var recording: ValidationRecording = new ValidationRecording();
            this.formItemViews.forEach((formItemView: FormItemView) => {
                recording.flatten(formItemView.validate(silent));
            });

            if (!silent && (recording.validityChanged(this.previousValidationRecording) || forceNotify)) {
                this.notifyValidityChanged(new FormValidityChangedEvent(recording));
            }

            this.previousValidationRecording = recording;
            return recording;
        }

        public isValid(): boolean {
            if (!this.previousValidationRecording) {
                this.previousValidationRecording = this.validate(true);
            }
            return this.previousValidationRecording.isValid();
        }

        public displayValidationErrors(value: boolean) {
            if (value) {
                this.addClass("display-validation-errors");
            } else {
                this.removeClass("display-validation-errors");
            }
            for (var i = 0; i < this.formItemViews.length; i++) {
                this.formItemViews[i].displayValidationErrors(value);
            }
        }

        getData(): PropertySet {
            return this.data;
        }

        giveFocus(): boolean {
            var focusGiven = false;
            if (this.formItemViews.length > 0) {
                for (var i = 0; i < this.formItemViews.length; i++) {
                    if (this.formItemViews[i].giveFocus()) {
                        focusGiven = true;
                        break;
                    }
                }
            }
            return focusGiven;
        }

        onValidityChanged(listener: (event: FormValidityChangedEvent)=>void) {
            this.formValidityChangedListeners.push(listener);
        }

        unValidityChanged(listener: (event: FormValidityChangedEvent)=>void) {
            this.formValidityChangedListeners =
                this.formValidityChangedListeners.filter((currentListener: (event: FormValidityChangedEvent)=>void)=> {
                    return listener != currentListener;
                });
        }

        private notifyValidityChanged(event: FormValidityChangedEvent) {
            //console.log("FormView.validityChanged");
            //if (event.getRecording().isValid()) {
            //    console.log(" valid: ");
            //}
            //else {
            //    console.log(" invalid: ");
            //    event.getRecording().print();
            //}

            this.formValidityChangedListeners.forEach((listener: (event: FormValidityChangedEvent)=>void)=> {
                listener.call(this, event);
            });
        }

        onFocus(listener: (event: FocusEvent) => void) {
            this.focusListeners.push(listener);
        }

        unFocus(listener: (event: FocusEvent) => void) {
            this.focusListeners = this.focusListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        onBlur(listener: (event: FocusEvent) => void) {
            this.blurListeners.push(listener);
        }

        unBlur(listener: (event: FocusEvent) => void) {
            this.blurListeners = this.blurListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        onLayoutFinished(listener: () => void) {
            this.layoutFinishedListeners.push(listener);
        }

        unLayoutFinished(listener: () => void) {
            this.layoutFinishedListeners = this.layoutFinishedListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        private notifyFocused(event: FocusEvent) {
            this.focusListeners.forEach((listener) => {
                listener(event);
            })
        }

        private notifyBlurred(event: FocusEvent) {
            this.blurListeners.forEach((listener) => {
                listener(event);
            })
        }

        private notifyLayoutFinished() {
            this.layoutFinishedListeners.forEach((listener) => {
                listener();
            })
        }
    }
}
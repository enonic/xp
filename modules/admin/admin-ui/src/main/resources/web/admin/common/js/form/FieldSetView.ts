module api.form {

    import PropertySet = api.data.PropertySet;

    export interface FieldSetViewConfig {

        context: FormContext;

        fieldSet: FieldSet;

        parent: FormItemSetOccurrenceView;

        dataSet?: PropertySet;
    }

    export class FieldSetView extends LayoutView {

        private fieldSet: FieldSet;

        private propertySet: PropertySet;

        private formItemViews: FormItemView[] = [];

        private formItemLayer: FormItemLayer;

        constructor(config: FieldSetViewConfig) {
            super(<LayoutViewConfig>{
                context: config.context,
                layout: config.fieldSet,
                parent: config.parent,
                className: "field-set-view"
            });

            this.formItemLayer = new FormItemLayer(config.context);

            this.fieldSet = config.fieldSet;
            this.propertySet = config.dataSet;
        }

        broadcastFormSizeChanged() {
            this.formItemViews.forEach((formItemView: FormItemView) => {
                formItemView.broadcastFormSizeChanged();
            });
        }

        public layout(): wemQ.Promise<void> {

            return this.doLayout();
        }

        getFormItemViews(): FormItemView[] {
            return this.formItemViews;
        }

        private doLayout(): wemQ.Promise<void> {

            var deferred = wemQ.defer<void>();

            var label = new FieldSetLabel(this.fieldSet);
            this.appendChild(label);

            var wrappingDiv = new api.dom.DivEl("field-set-container");
            this.appendChild(wrappingDiv);

            var layoutPromise: wemQ.Promise<FormItemView[]> = this.formItemLayer.
                setFormItems(this.fieldSet.getFormItems()).
                setParentElement(wrappingDiv).
                setParent(this.getParent()).
                layout(this.propertySet);
            layoutPromise.then((formItemViews: FormItemView[]) => {
                this.formItemViews = formItemViews;

                deferred.resolve(null);
            }).catch((reason: any) => {
                var fieldSetValue = this.fieldSet ? this.fieldSet.toFieldSetJson() : {};
                console.error('Could not render FieldSet view: ' + reason + '\r\n FieldSet value:', JSON.stringify(fieldSetValue));
                api.DefaultErrorHandler.handle(reason);
            }).done();

            return deferred.promise;
        }

        public update(propertySet: PropertySet, unchangedOnly?: boolean): wemQ.Promise<void> {
            if (InputView.debug) {
                console.debug('FieldSetView.update' + (unchangedOnly ? ' ( unchanged only)' : ''), propertySet);
            }
            this.propertySet = propertySet;

            return this.formItemLayer.update(propertySet, unchangedOnly);
        }

        onEditContentRequest(listener: (content: api.content.ContentSummary) => void) {
            super.onEditContentRequest(listener);
            this.formItemViews.forEach((formItemView: FormItemView) => {
                formItemView.onEditContentRequest(listener);
            });
        }

        unEditContentRequest(listener: (content: api.content.ContentSummary) => void) {
            super.unEditContentRequest(listener);
            this.formItemViews.forEach((formItemView: FormItemView) => {
                formItemView.unEditContentRequest(listener);
            });
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

        public displayValidationErrors(value: boolean) {
            this.formItemViews.forEach((view: FormItemView) => {
                view.displayValidationErrors(value);
            });
        }

        hasValidUserInput(): boolean {

            var result = true;
            this.formItemViews.forEach((formItemView: FormItemView) => {
                if (!formItemView.hasValidUserInput()) {
                    result = false;
                }
            });

            return result;
        }


        validate(silent: boolean = true): ValidationRecording {

            var recording = new ValidationRecording();
            this.formItemViews.forEach((formItemView: FormItemView)=> {
                recording.flatten(formItemView.validate(silent));
            });

            return recording;
        }

        onValidityChanged(listener: (event: RecordingValidityChangedEvent)=>void) {

            this.formItemViews.forEach((formItemView: FormItemView)=> {
                formItemView.onValidityChanged(listener);
            });
        }

        unValidityChanged(listener: (event: RecordingValidityChangedEvent)=>void) {
            this.formItemViews.forEach((formItemView: FormItemView)=> {
                formItemView.unValidityChanged(listener);
            });
        }

        onFocus(listener: (event: FocusEvent) => void) {
            this.formItemViews.forEach((formItemView) => {
                formItemView.onFocus(listener);
            });
        }

        unFocus(listener: (event: FocusEvent) => void) {
            this.formItemViews.forEach((formItemView) => {
                formItemView.unFocus(listener);
            });
        }

        onBlur(listener: (event: FocusEvent) => void) {
            this.formItemViews.forEach((formItemView) => {
                formItemView.onBlur(listener);
            });
        }

        unBlur(listener: (event: FocusEvent) => void) {
            this.formItemViews.forEach((formItemView) => {
                formItemView.unBlur(listener);
            });
        }
    }
}
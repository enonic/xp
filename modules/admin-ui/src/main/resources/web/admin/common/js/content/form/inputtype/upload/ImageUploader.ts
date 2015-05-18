module api.content.form.inputtype.upload {

    import Property = api.data.Property;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;

    export class ImageUploader extends api.form.inputtype.support.BaseInputTypeSingleOccurrence<any,string> {

        private imageUploader: api.content.ImageUploader;
        private property: Property;
        private previousValidationRecording: api.form.inputtype.InputValidationRecording;

        constructor(config: api.content.form.inputtype.ContentInputTypeViewContext<any>) {
            super(config, "image");
            var input = config.input;

            this.imageUploader = new api.content.ImageUploader(<api.content.ImageUploaderConfig>{
                params: {
                    content: config.contentId.toString()
                },
                operation: api.content.MediaUploaderOperation.update,
                name: input.getName(),
                skipWizardEvents: false,
                maximumOccurrences: 1
            });

            this.appendChild(this.imageUploader);
        }

        getContext(): api.content.form.inputtype.ContentInputTypeViewContext<any> {
            return <api.content.form.inputtype.ContentInputTypeViewContext<any>>super.getContext();
        }

        getValueType(): ValueType {
            return ValueTypes.STRING;
        }

        newInitialValue(): Value {
            return ValueTypes.STRING.newNullValue();
        }

        layoutProperty(input: api.form.Input, property: Property): wemQ.Promise<void> {

            this.input = input;
            this.property = property;

            if (property.hasNonNullValue()) {
                //TODO: should we pass Content.getId() instead of ContentId in property to spare this request ?
                new api.content.GetContentByIdRequest(this.getContext().contentId).
                    sendAndParse().
                    then((content: api.content.Content) => {

                        this.imageUploader.setValue(content.getId());
                    }).catch((reason: any) => {
                        api.DefaultErrorHandler.handle(reason);
                    }).done();
            }

            this.imageUploader.onFileUploaded((event: api.ui.uploader.FileUploadedEvent<api.content.Content>) => {
                var value = this.imageUploader.getMediaValue(event.getUploadItem().getModel());

                switch (property.getType()) {
                case ValueTypes.DATA:
                    property.getPropertySet().setProperty('attachment', 0, value);
                    break;
                case ValueTypes.STRING:
                    property.setValue(value);
                    break;
                }
            });

            this.imageUploader.onUploadReset(() => {
                switch (property.getType()) {
                case ValueTypes.DATA:
                    var set = property.getPropertySet();
                    set.setProperty('attachment', 0, ValueTypes.STRING.newNullValue());
                    set.setDoubleByPath('focalPoint.x', 0.5);
                    set.setDoubleByPath('focalPoint.y', 0.5);
                    break;
                case ValueTypes.STRING:
                    property.setValue(ValueTypes.STRING.newNullValue());
                    break;
                }
            });

            this.imageUploader.onFocalEditModeChanged((edit: boolean, position: {x: number; y: number}) => {
                this.validate(false);
                if (!edit && ValueTypes.DATA.equals(property.getType())) {
                    var set = property.getPropertySet();
                    set.setDoubleByPath('focalPoint.x', position.x);
                    set.setDoubleByPath('focalPoint.y', position.y);
                }
            });

            return wemQ<void>(null);
        }

        validate(silent: boolean = true): api.form.inputtype.InputValidationRecording {
            var recording = new api.form.inputtype.InputValidationRecording();
            var propertyValue = this.property.getValue();

            if (this.imageUploader.isFocalEditMode()) {
                recording.setBreaksMinimumOccurrences(true);
            }
            if (propertyValue.isNull() && this.input.getOccurrences().getMinimum() > 0) {
                recording.setBreaksMinimumOccurrences(true);
            }
            if (!silent) {
                if (recording.validityChanged(this.previousValidationRecording)) {
                    this.notifyValidityChanged(new api.form.inputtype.InputValidityChangedEvent(recording, this.input.getName()));
                }
            }
            this.previousValidationRecording = recording;
            return recording;
        }

        onFocus(listener: (event: FocusEvent) => void) {
            this.imageUploader.onFocus(listener);
        }

        unFocus(listener: (event: FocusEvent) => void) {
            this.imageUploader.unFocus(listener);
        }

        onBlur(listener: (event: FocusEvent) => void) {
            this.imageUploader.onBlur(listener);
        }

        unBlur(listener: (event: FocusEvent) => void) {
            this.imageUploader.unBlur(listener);
        }

    }

    api.form.inputtype.InputTypeManager.register(new api.Class("ImageUploader", ImageUploader));
}
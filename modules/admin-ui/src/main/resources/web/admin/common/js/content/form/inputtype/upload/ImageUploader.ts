module api.content.form.inputtype.upload {

    import Property = api.data.Property;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;
    import Point = api.ui.image.Point;
    import Rect = api.ui.image.Rect;

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
                maximumOccurrences: 1,
                scaleWidth: true,
                hideDropZone: true,
                showReset: false
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
                        var metaData = content.getContentData().getProperty('metadata');
                        if (metaData && ValueTypes.DATA.equals(metaData.getType())) {
                            var width = metaData.getPropertySet().getProperty('imageWidth');
                            var height = metaData.getPropertySet().getProperty('imageHeight');
                            this.imageUploader.setOriginalDimensions(width ? width.getString() : '0', height ? height.getString() : '0');
                        }
                        this.imageUploader.setValue(content.getId());
                        var focalPoint = this.getFocalPoint(content);
                        if (focalPoint) {
                            this.imageUploader.setFocalPoint(focalPoint.x, focalPoint.y);
                        }
                    }).catch((reason: any) => {
                        api.DefaultErrorHandler.handle(reason);
                    }).done();
            }

            this.imageUploader.onFileUploaded((event: api.ui.uploader.FileUploadedEvent<api.content.Content>) => {
                var value = this.imageUploader.getMediaValue(event.getUploadItem().getModel());

                switch (property.getType()) {
                case ValueTypes.DATA:
                    // update the attachment name, and reset the focal point data
                    var set = property.getPropertySet();
                    set.setProperty('attachment', 0, value);
                    set.removeProperty('focalPoint', 0);
                    break;
                case ValueTypes.STRING:
                    property.setValue(value);
                    break;
                }
            });

            this.imageUploader.onUploadReset(() => {
                switch (property.getType()) {
                case ValueTypes.DATA:
                    // reset both attachment name and focal point data
                    var set = property.getPropertySet();
                    set.setProperty('attachment', 0, ValueTypes.STRING.newNullValue());
                    set.removeProperty('focalPoint', 0);
                    break;
                case ValueTypes.STRING:
                    property.setValue(ValueTypes.STRING.newNullValue());
                    break;
                }
            });

            this.imageUploader.onCropEditModeChanged((edit: boolean, crop: Rect) => {
                this.validate(false);
                this.toggleClass('standout', edit);
            });

            this.imageUploader.onFocalPointEditModeChanged((edit: boolean, position: Point) => {
                this.validate(false);
                this.toggleClass('standout', edit);

                if (!edit && position) {
                    var tree;
                    switch (property.getType()) {
                    case ValueTypes.DATA:
                        tree = property.getPropertySet();
                        break;
                    case ValueTypes.STRING:
                        // save in new format always no matter what was the format originally
                        tree = new api.data.PropertyTree();
                        tree.setString('attachment', 0, property.getString());
                        var propertyParent = property.getParent();
                        var propertyName = property.getName();
                        // remove old string property and set the new property set
                        propertyParent.removeProperty(propertyName, 0);
                        propertyParent.setPropertySet(propertyName, 0, tree.getRoot());
                        // update local property reference
                        property = propertyParent.getProperty(propertyName);
                        break;
                    }

                    tree.setDoubleByPath('focalPoint.x', position.x);
                    tree.setDoubleByPath('focalPoint.y', position.y);
                }
            });

            return wemQ<void>(null);
        }

        private getFocalPoint(content: Content): {x: number; y: number} {
            var mediaProperty = content.getContentData().getProperty('media');
            if (!mediaProperty || !ValueTypes.DATA.equals(mediaProperty.getType())) {
                return null;
            }

            var focalProperty = mediaProperty.getPropertySet().getProperty('focalPoint');
            if (!focalProperty || !ValueTypes.DATA.equals(focalProperty.getType())) {
                return null;
            }

            var focalSet = focalProperty.getPropertySet(),
                x = focalSet.getDouble('x'),
                y = focalSet.getDouble('y');

            if (!x || !y) {
                return null;
            }

            return {
                x: x,
                y: y
            }
        }

        validate(silent: boolean = true): api.form.inputtype.InputValidationRecording {
            var recording = new api.form.inputtype.InputValidationRecording();
            var propertyValue = this.property.getValue();

            if (this.imageUploader.isFocalPointEditMode() || this.imageUploader.isCropEditMode()) {
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
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
                        this.imageUploader.setOriginalDimensions(content);
                        this.imageUploader.setValue(content.getId());

                        this.configEditorsProperties(content);

                    }).catch((reason: any) => {
                        api.DefaultErrorHandler.handle(reason);
                    }).done();
            }

            this.imageUploader.onFileUploaded((event: api.ui.uploader.FileUploadedEvent<api.content.Content>) => {
                var content = event.getUploadItem().getModel(),
                    value = this.imageUploader.getMediaValue(content);

                this.imageUploader.setOriginalDimensions(content);

                switch (this.property.getType()) {
                case ValueTypes.DATA:
                    // update the attachment name, and reset the focal point data
                    var set = this.property.getPropertySet();
                    set.setProperty('attachment', 0, value);
                    set.removeProperty('focalPoint', 0);
                    break;
                case ValueTypes.STRING:
                    this.property.setValue(value);
                    break;
                }
            });

            this.imageUploader.onUploadReset(() => {
                switch (this.property.getType()) {
                case ValueTypes.DATA:
                    // reset both attachment name and focal point data
                    var set = this.property.getPropertySet();
                    set.setProperty('attachment', 0, ValueTypes.STRING.newNullValue());
                    set.removeProperty('focalPoint', 0);
                    break;
                case ValueTypes.STRING:
                    this.property.setValue(ValueTypes.STRING.newNullValue());
                    break;
                }
            });

            this.imageUploader.onCropEditModeChanged((edit: boolean, crop: Rect, zoom: Rect) => {
                this.validate(false);
                this.toggleClass('standout', edit);

                if (!edit && crop) {
                    var container = this.getPropertyContainer(this.property);
                    if (container) {
                        container.setDoubleByPath('cropPosition.x', crop.x);
                        container.setDoubleByPath('cropPosition.y', crop.y);
                        container.setDoubleByPath('cropPosition.x2', crop.x2);
                        container.setDoubleByPath('cropPosition.y2', crop.y2);

                        container.setDoubleByPath('zoomPosition.x', zoom.x);
                        container.setDoubleByPath('zoomPosition.y', zoom.y);
                        container.setDoubleByPath('zoomPosition.x2', zoom.x2);
                        container.setDoubleByPath('zoomPosition.y2', zoom.y2);
                    }
                }
            });

            this.imageUploader.onFocalPointEditModeChanged((edit: boolean, position: Point) => {
                this.validate(false);
                this.toggleClass('standout', edit);

                if (!edit && position) {
                    var container = this.getPropertyContainer(this.property);
                    if (container) {
                        container.setDoubleByPath('focalPoint.x', position.x);
                        container.setDoubleByPath('focalPoint.y', position.y);
                    }
                }
            });

            return wemQ<void>(null);
        }

        private getPropertyContainer(property: Property) {
            var container;
            switch (property.getType()) {
            case ValueTypes.DATA:
                container = property.getPropertySet();
                break;
            case ValueTypes.STRING:
                // save in new format always no matter what was the format originally
                container = new api.data.PropertyTree();
                container.setString('attachment', 0, property.getString());
                var propertyParent = property.getParent();
                var propertyName = property.getName();
                // remove old string property and set the new property set
                propertyParent.removeProperty(propertyName, 0);
                propertyParent.setPropertySet(propertyName, 0, container.getRoot());
                // update local property reference
                this.property = propertyParent.getProperty(propertyName);
                break;
            }
            return container;
        }

        private getFocalPoint(content: Content): Point {
            var focalProperty = this.getMediaProperty(content, 'focalPoint');

            if (!focalProperty) {
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

        private getRectFromProperty(content: Content, propertyName: string): Rect {
            var property = this.getMediaProperty(content, propertyName);

            if (!property) {
                return null;
            }

            var cropPositionSet = property.getPropertySet(),
                x = cropPositionSet.getDouble('x'),
                y = cropPositionSet.getDouble('y'),
                x2 = cropPositionSet.getDouble('x2'),
                y2 = cropPositionSet.getDouble('y2');

            return {
                x: x,
                y: y,
                x2: x2,
                y2: y2
            };
        }

        private getMediaProperty(content: Content, propertyName: string) {
            var mediaProperty = content.getProperty('media');
            if (!mediaProperty || !ValueTypes.DATA.equals(mediaProperty.getType())) {
                return null;
            }

            var resultProperty = mediaProperty.getPropertySet().getProperty(propertyName);
            if (!resultProperty || !ValueTypes.DATA.equals(resultProperty.getType())) {
                return null;
            }
            return resultProperty;
        }

        private configEditorsProperties(content: Content) {
            var focalPoint = this.getFocalPoint(content);
            if (focalPoint) {
                this.imageUploader.setFocalPoint(focalPoint.x, focalPoint.y);
            }

            var cropPosition = this.getRectFromProperty(content, 'cropPosition');
            if (cropPosition) {
                this.imageUploader.setCrop(cropPosition);
            }

            var zoomPosition = this.getRectFromProperty(content, 'zoomPosition');
            if (zoomPosition) {
                this.imageUploader.setZoom(zoomPosition);
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
module api.content.form.inputtype.upload {

    import Property = api.data.Property;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;
    import Point = api.ui.image.Point;
    import Rect = api.ui.image.Rect;

    export class ImageUploader extends api.form.inputtype.support.BaseInputTypeSingleOccurrence<string> {

        private imageUploader: api.content.ImageUploaderEl;
        private previousValidationRecording: api.form.inputtype.InputValidationRecording;

        constructor(config: api.content.form.inputtype.ContentInputTypeViewContext) {
            super(config);
            var input = config.input;

            this.imageUploader = new api.content.ImageUploaderEl(<api.content.ImageUploaderElConfig>{
                params: {
                    content: config.contentId.toString()
                },
                operation: api.content.MediaUploaderElOperation.update,
                name: input.getName(),
                skipWizardEvents: false,
                maximumOccurrences: 1,
                scaleWidth: true,
                hideDropZone: true,
                showReset: false
            });

            this.appendChild(this.imageUploader);
        }

        getContext(): api.content.form.inputtype.ContentInputTypeViewContext {
            return <api.content.form.inputtype.ContentInputTypeViewContext>super.getContext();
        }

        getValueType(): ValueType {
            return ValueTypes.STRING;
        }

        newInitialValue(): Value {
            return ValueTypes.STRING.newNullValue();
        }

        layoutProperty(input: api.form.Input, property: Property): wemQ.Promise<void> {
            if (!ValueTypes.STRING.equals(property.getType()) && !ValueTypes.DATA.equals(property.getType())) {
                property.convertValueType(ValueTypes.STRING);
            }

            this.input = input;

            this.imageUploader.onFileUploaded((event: api.ui.uploader.FileUploadedEvent<api.content.Content>) => {
                var content = event.getUploadItem().getModel(),
                    value = this.imageUploader.getMediaValue(content);

                this.imageUploader.setOriginalDimensions(content);

                this.saveToProperty(value);
            });

            this.imageUploader.onUploadReset(() => {
                this.saveToProperty(null);
            });

            this.imageUploader.onEditModeChanged((edit: boolean, crop: Rect, zoom: Rect, focus: Point) => {
                this.validate(false);

                if (!edit && crop) {
                    this.saveEditDataToProperty(crop, zoom, focus);
                }
            });

            this.imageUploader.onCropAutoPositionedChanged((auto) => {
                if (auto) {
                    this.saveEditDataToProperty({x: 0, y: 0, x2: 1, y2: 1}, {x: 0, y: 0, x2: 1, y2: 1}, null);
                }
            });

            this.imageUploader.onFocusAutoPositionedChanged((auto) => {
                if (auto) {
                    this.saveEditDataToProperty(null, null, {x: 0.5, y: 0.5});
                }
            });

            return property.hasNonNullValue() ? this.updateProperty(property) : wemQ<void>(null);
        }


        protected saveToProperty(value: api.data.Value) {
            this.ignorePropertyChange = true;
            var property = this.getProperty();
            switch (property.getType()) {
            case ValueTypes.DATA:
                // update the attachment name, and reset the focal point data
                var set = property.getPropertySet();
                set.setProperty('attachment', 0, value);
                set.removeProperty('focalPoint', 0);
                set.removeProperty('cropPosition', 0);
                set.removeProperty('zoomPosition', 0);

                break;
            case ValueTypes.STRING:
                property.setValue(value);
                break;
            }
            this.validate();
            this.ignorePropertyChange = false;
        }

        updateProperty(property: api.data.Property, unchangedOnly?: boolean): Q.Promise<void> {
            if ((!unchangedOnly || !this.imageUploader.isDirty()) && this.getContext().contentId) {

                return new api.content.GetContentByIdRequest(this.getContext().contentId).
                    sendAndParse().
                    then((content: api.content.Content) => {

                        this.imageUploader.setOriginalDimensions(content);
                        this.imageUploader.setValue(content.getId(), false, true);

                        this.configEditorsProperties(content);

                    }).catch((reason: any) => {
                        api.DefaultErrorHandler.handle(reason);
                    });
            }
            return wemQ<void>(null);
        }

        private saveEditDataToProperty(crop: Rect, zoom: Rect, focus: Point) {
            var container = this.getPropertyContainer(this.getProperty());

            if (container) {
                if (crop) {
                    container.setDoubleByPath('cropPosition.left', crop.x);
                    container.setDoubleByPath('cropPosition.top', crop.y);
                    container.setDoubleByPath('cropPosition.right', crop.x2);
                    container.setDoubleByPath('cropPosition.bottom', crop.y2);
                    container.setDoubleByPath('cropPosition.zoom', zoom.x2 - zoom.x);
                }

                if (zoom) {
                    container.setDoubleByPath('zoomPosition.left', zoom.x);
                    container.setDoubleByPath('zoomPosition.top', zoom.y);
                    container.setDoubleByPath('zoomPosition.right', zoom.x2);
                    container.setDoubleByPath('zoomPosition.bottom', zoom.y2);
                }

                if (focus) {
                    container.setDoubleByPath('focalPoint.x', focus.x);
                    container.setDoubleByPath('focalPoint.y', focus.y);
                }
            }
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
                var newProperty = propertyParent.setPropertySet(propertyName, 0, container.getRoot());
                // update local property reference
                this.registerProperty(newProperty);
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
                x = cropPositionSet.getDouble('left'),
                y = cropPositionSet.getDouble('top'),
                x2 = cropPositionSet.getDouble('right'),
                y2 = cropPositionSet.getDouble('bottom');

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
            var propertyValue = this.getProperty().getValue();

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
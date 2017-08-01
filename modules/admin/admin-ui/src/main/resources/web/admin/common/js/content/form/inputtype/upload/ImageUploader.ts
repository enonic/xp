module api.content.form.inputtype.upload {

    import Property = api.data.Property;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;
    import Point = api.ui.image.Point;
    import Rect = api.ui.image.Rect;

    export class ImageUploader
        extends api.form.inputtype.support.BaseInputTypeSingleOccurrence<string> {

        private imageUploader: api.content.image.ImageUploaderEl;
        private previousValidationRecording: api.form.inputtype.InputValidationRecording;

        constructor(config: api.content.form.inputtype.ContentInputTypeViewContext) {
            super(config);
            this.initUploader(config);
            this.addClass('image-uploader-input');
        }

        private initUploader(config: api.content.form.inputtype.ContentInputTypeViewContext) {
            this.imageUploader = new api.content.image.ImageUploaderEl({
                params: {
                    content: config.content.getContentId().toString()
                },
                operation: api.ui.uploader.MediaUploaderElOperation.update,
                name: config.input.getName(),
                maximumOccurrences: 1,
                hideDefaultDropZone: true
            });

            this.imageUploader.getUploadButton().hide();
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

            this.imageUploader.onUploadStarted(() => this.imageUploader.getUploadButton().hide());

            this.imageUploader.onFileUploaded((event: api.ui.uploader.FileUploadedEvent<api.content.Content>) => {
                let content = event.getUploadItem().getModel();
                let value = this.imageUploader.getMediaValue(content);

                this.imageUploader.setOriginalDimensions(content);

                this.saveToProperty(value);
                api.notify.showFeedback(content.getDisplayName() + ' saved');
            });

            this.imageUploader.onUploadReset(() => {
                this.saveToProperty(this.newInitialValue());
                this.imageUploader.getUploadButton().show();
            });

            this.imageUploader.onUploadFailed(() => {
                this.saveToProperty(this.newInitialValue());
                this.imageUploader.getUploadButton().show();
                this.imageUploader.setProgressVisible(false);
            });

            api.content.image.ImageErrorEvent.on((event: api.content.image.ImageErrorEvent) => {
                if (this.getContext().content.getContentId().equals(event.getContentId())) {
                    this.imageUploader.getUploadButton().show();
                    this.imageUploader.setProgressVisible(false);
                }
            });

            this.imageUploader.onEditModeChanged((edit: boolean, crop: Rect, zoom: Rect, focus: Point) => {
                this.validate(edit);

                if (!edit && crop) {
                    this.saveEditDataToProperty(crop, zoom, focus);
                }
            });

            this.imageUploader.onCropAutoPositionedChanged(auto => {
                if (auto) {
                    this.saveEditDataToProperty({x: 0, y: 0, x2: 1, y2: 1}, {x: 0, y: 0, x2: 1, y2: 1}, null);
                }
            });

            this.imageUploader.onFocusAutoPositionedChanged(auto => {
                if (auto) {
                    this.saveEditDataToProperty(null, null, {x: 0.5, y: 0.5});
                }
            });

            this.imageUploader.onOrientationChanged(orientation => {
                this.writeOrientation(<Content>this.getContext().content, orientation);
            });

            return property.hasNonNullValue() ? this.updateProperty(property) : wemQ<void>(null);
        }

        protected saveToProperty(value: api.data.Value) {
            this.ignorePropertyChange = true;
            let property = this.getProperty();
            switch (property.getType()) {
            case ValueTypes.DATA:
                // update the attachment name, and reset the focal point data
                let set = property.getPropertySet();
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
            if ((!unchangedOnly || !this.imageUploader.isDirty()) && this.getContext().content.getContentId()) {

                return new api.content.resource.GetContentByIdRequest(this.getContext().content.getContentId())
                    .sendAndParse().then((content: api.content.Content) => {

                        this.imageUploader.setOriginalDimensions(content);
                        this.imageUploader.setValue(content.getId(), false, false);

                        this.configEditorsProperties(content);

                    }).catch((reason: any) => {
                        api.DefaultErrorHandler.handle(reason);
                    });
            }
            return wemQ<void>(null);
        }

        reset() {
            this.imageUploader.resetBaseValues();
        }

        private saveEditDataToProperty(crop: Rect, zoom: Rect, focus: Point) {
            let container = this.getPropertyContainer(this.getProperty());

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
            let container;
            switch (property.getType()) {
            case ValueTypes.DATA:
                container = property.getPropertySet();
                break;
            case ValueTypes.STRING:
                // save in new format always no matter what was the format originally
                container = new api.data.PropertyTree();
                container.setString('attachment', 0, property.getString());
                let propertyParent = property.getParent();
                let propertyName = property.getName();
                // remove old string property and set the new property set
                propertyParent.removeProperty(propertyName, 0);
                let newProperty = propertyParent.setPropertySet(propertyName, 0, container.getRoot());
                // update local property reference
                this.registerProperty(newProperty);
                break;
            }
            return container;
        }

        private getFocalPoint(content: Content): Point {
            let focalProperty = this.getMediaProperty(content, 'focalPoint');

            if (!focalProperty) {
                return null;
            }

            let focalSet = focalProperty.getPropertySet();
            let x = focalSet.getDouble('x');
            let y = focalSet.getDouble('y');

            if (!x || !y) {
                return null;
            }

            return {
                x: x,
                y: y
            };
        }

        private getRectFromProperty(content: Content, propertyName: string): Rect {
            let property = this.getMediaProperty(content, propertyName);

            if (!property) {
                return null;
            }

            let cropPositionSet = property.getPropertySet();
            let x = cropPositionSet.getDouble('left');
            let y = cropPositionSet.getDouble('top');
            let x2 = cropPositionSet.getDouble('right');
            let y2 = cropPositionSet.getDouble('bottom');

            return {x, y, x2, y2};
        }

        private writeOrientation(content: Content, orientation: number) {
            const property = this.getMetaProperty(content, 'orientation');
            if (property) {
                property.setValue(new Value(String(orientation), ValueTypes.STRING));
            }
        }

        private readOrientation(content: Content): number {
            const property = this.getMetaProperty(content, 'orientation');
            return property && property.getLong() || 0;
        }

        private getMetaProperty(content: Content, propertyName: string) {
            const extra = content.getAllExtraData();
            for (let i = 0; i < extra.length; i++) {
                const metaProperty = extra[i].getData().getProperty(propertyName);
                if (metaProperty) {
                    return metaProperty;
                }
            }
        }

        private getMediaProperty(content: Content, propertyName: string) {
            let mediaProperty = content.getProperty('media');
            if (!mediaProperty || !ValueTypes.DATA.equals(mediaProperty.getType())) {
                return null;
            }

            let resultProperty = mediaProperty.getPropertySet().getProperty(propertyName);
            if (!resultProperty || !ValueTypes.DATA.equals(resultProperty.getType())) {
                return null;
            }
            return resultProperty;
        }

        private configEditorsProperties(content: Content) {
            let focalPoint = this.getFocalPoint(content);
            this.imageUploader.setFocalPoint(focalPoint);

            let cropPosition = this.getRectFromProperty(content, 'cropPosition');
            this.imageUploader.setCrop(cropPosition);

            let zoomPosition = this.getRectFromProperty(content, 'zoomPosition');
            this.imageUploader.setZoom(zoomPosition);

            const orientation = this.readOrientation(content);
            if (orientation) {
                this.imageUploader.setOrientation(orientation);
            }
        }

        validate(silent: boolean = true): api.form.inputtype.InputValidationRecording {
            let recording = new api.form.inputtype.InputValidationRecording();
            let propertyValue = this.getProperty().getValue();

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

    api.form.inputtype.InputTypeManager.register(new api.Class('ImageUploader', ImageUploader));
}

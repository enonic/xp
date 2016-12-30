module ImageUploaderElSpec {

    import ImageUploaderEl = api.content.image.ImageUploaderEl;
    import Spy = jasmine.Spy;
    import Rect = api.ui.image.Rect;
    import MaskContentWizardPanelEvent = api.app.wizard.MaskContentWizardPanelEvent;

    describe("api.content.image.ImageUploaderEl", () => {

        let imageUploaderEl: ImageUploaderEl;

        beforeEach(() => {
            imageUploaderEl = createUploader();
        });

        describe("constructor", () => {

            it("allowTypes is initialized by default", () => {
                expect(imageUploaderEl.getAllowedTypes()[0].title).toEqual('Image files');
                expect(imageUploaderEl.getAllowedTypes()[0].extensions).toEqual('jpg,jpeg,gif,png,svg');
            });

            it("selfIsDropzone is enabled", () => {
                expect(imageUploaderEl["config"].selfIsDropzone).toBeTruthy();
            });

            it("has default css class", () => {
                expect(imageUploaderEl.hasClass("image-uploader-el")).toBeTruthy();
            });

        });

        describe("image editor listeners", () => {

            let editor;

            beforeEach(() => {
                api.dom.Body.get().appendChild(imageUploaderEl);
                editor = imageUploaderEl.createResultItem("testItem");
            });

            afterEach(() => {
                editor.remove();
                api.dom.Body.get().removeChild(imageUploaderEl);
            });

            it("to be defined", () => {
                expect(editor).toBeDefined();
            });

            describe("after image has been loaded", () => {

                let shaderVisibilitySpy, showFileDialogSpy;

                beforeEach(() => {
                    shaderVisibilitySpy = spyOn(editor, "onShaderVisibilityChanged").and.callThrough();
                    editor.getImage().notifyLoaded();
                });

                it("shader visibility listener should be set", () => {
                    expect(shaderVisibilitySpy).toHaveBeenCalled();
                });

                describe("and edit mode has been changed", () => {

                    let notifyEditModeSpy, addClassSpy, removeClassSpy: Spy;
                    beforeEach(() => {
                        notifyEditModeSpy = spyOn(imageUploaderEl, "notifyEditModeChanged").and.callThrough();
                        addClassSpy = spyOn(editor, "addClass").and.callThrough();
                        removeClassSpy = spyOn(editor, "removeClass").and.callThrough();
                    });

                    it("edit mode listener should be called", () => {
                        editor.notifyEditModeChanged();
                        expect(notifyEditModeSpy).toHaveBeenCalled();
                    });

                    it("standout class should be added on edit", () => {
                        editor.notifyEditModeChanged(true);
                        expect(addClassSpy).toHaveBeenCalled();
                    });

                    it("standout class should be removed if not edit", () => {
                        editor.notifyEditModeChanged(false);
                        expect(removeClassSpy).toHaveBeenCalled();
                    });
                });

                describe("and shader visibility has been changed", () => {

                    beforeEach(() => {
                        spyOn(api.app.wizard.MaskContentWizardPanelEvent.prototype, "fire");
                        editor.notifyShaderVisibilityChanged(true);
                    });

                    it("MaskContentWizardPanelEvent event should be fired", () => {
                        expect(api.app.wizard.MaskContentWizardPanelEvent.prototype.fire).toHaveBeenCalled();
                    });
                });

                describe("and upload button has been clicked", () => {

                    beforeEach(() => {
                        showFileDialogSpy = spyOn(imageUploaderEl, 'showFileSelectionDialog').and.callThrough();
                        editor.getUploadButton().getHTMLElement().click();
                    });

                    it("show file dialog should be shown", () => {
                        expect(showFileDialogSpy).toHaveBeenCalled();
                    });
                });
            });

            describe("if image loading throws an error", () => {

                let showErrorSpy: Spy;

                beforeEach(() => {
                    showErrorSpy = spyOn(api.notify, 'showError');
                    editor.notifyImageError();
                });

                it("error should be displayed", () => {
                    expect(showErrorSpy).toHaveBeenCalled();
                });
            });

            describe("getValue()", () => {

                beforeEach(() => {
                    imageUploaderEl.setValue("testItem1");
                });

                it("should return last set value", () => {
                    expect(imageUploaderEl.getValue()).toEqual("testItem1");
                });
            });

            describe("refreshExistingItem()", () => {

                let refreshSpy: Spy;

                beforeEach(() => {
                    let src = jasmine.createSpyObj("src", ['indexOf']);
                    src.indexOf.and.returnValue(1);

                    spyOn(editor, "getSrc").and.returnValue(src);
                    refreshSpy = spyOn(imageUploaderEl, "refreshExistingItem").and.callThrough();

                    imageUploaderEl.setValue("testValue");
                });

                it("should be called for existing value item", () => {
                    expect(refreshSpy).toHaveBeenCalled();
                });
            });
        });

        describe("initialized", () => {

            let editorSpyObj, editorSpy;

            beforeEach((done) => {
                imageUploaderEl.getEl().setWidthPx(0);
                api.dom.Body.get().appendChild(imageUploaderEl);

                editorSpyObj = jasmine.createSpyObj('fakeEditor',
                    ['remove', 'hasClass', 'getImage', 'isElementInsideButtonsContainer', 'setFocusPosition', 'resetFocusPosition',
                        'setCropPosition', 'resetCropPosition',
                        'setZoomPosition', 'resetZoomPosition', 'isFocusEditMode', 'isCropEditMode']);

                editorSpy = spyOn(imageUploaderEl, "createImageEditor").and.returnValue(editorSpyObj);

                imageUploaderEl.createResultItem("testItem");

                imageUploaderEl.onShown(() => {
                    done();
                });

                imageUploaderEl.show();
            });

            afterEach(() => {
                api.dom.Body.get().removeChild(imageUploaderEl);
            });

            describe("public methods", () => {

                describe("setOriginalDimensions()", () => {

                    let sizeSpy: Spy;
                    let content, contentData, metadata;

                    beforeEach(() => {
                        content = jasmine.createSpyObj("fakeContent", ["getContentData", "getAllExtraData"]);
                        contentData = jasmine.createSpyObj("fakeContentData", ["getProperty"]);
                        metadata = jasmine.createSpyObj("fakeMetadata", ["getType", "getPropertySet"]);
                    });

                    describe("from metadata", () => {
                        beforeEach(() => {

                            let metadataPropertySet = jasmine.createSpyObj("fakeMetadataPropertySet", ["getProperty"]);
                            let metadataProperty = jasmine.createSpyObj("fakeMetadataProperty", ["getString"]);

                            content.getContentData.and.returnValue(contentData);
                            contentData.getProperty.and.returnValue(metadata);

                            metadata.getType.and.returnValue(api.data.ValueTypes.DATA);
                            metadata.getPropertySet.and.returnValue(metadataPropertySet);
                            metadataPropertySet.getProperty.and.returnValue(metadataProperty);

                            metadataProperty.getString.and.returnValue("111");

                            sizeSpy = spyOn(imageUploaderEl, "getSizeValue").and.callThrough();
                            imageUploaderEl.setOriginalDimensions(content);
                        });

                        it("original values is set to height and width", () => {
                            expect(sizeSpy.calls.count()).toEqual(2);
                        });

                        it("original values is set from metadata", () => {
                            expect(sizeSpy.calls.first().returnValue).toEqual(111);
                        });

                        describe("new editors", () => {

                            let proportionalHeightSpy: Spy;
                            let newItem;

                            beforeEach(() => {
                                editorSpy.and.callThrough();

                                proportionalHeightSpy = spyOn(imageUploaderEl, "getProportionalHeight").and.callThrough();
                                newItem = imageUploaderEl.createResultItem("newTestItem");
                            });

                            afterEach(() => {
                                newItem.remove();
                            })

                            it("should use original height values", () => {
                                expect(proportionalHeightSpy).toHaveBeenCalled();
                            });
                        });


                    });

                    describe("from extradata", () => {
                        beforeEach(() => {

                            content.getContentData.and.returnValue(contentData);
                            contentData.getProperty.and.returnValue(null);

                            let extraData = jasmine.createSpyObj("fakeExtraData", ["getData"]);
                            let data = jasmine.createSpyObj("fakeData", ["getProperty"]);
                            let property = jasmine.createSpyObj("fakeProperty", ["getValue"]);
                            let value = jasmine.createSpyObj("fakeValue", ["getString"]);

                            content.getAllExtraData.and.returnValue([extraData]);
                            extraData.getData.and.returnValue(data);
                            data.getProperty.and.returnValue(property);
                            property.getValue.and.returnValue(value);
                            value.getString.and.returnValue("222");

                            sizeSpy = spyOn(imageUploaderEl, "getSizeValue").and.callThrough();
                            imageUploaderEl.setOriginalDimensions(content);
                        });

                        it("original values is set to height and width", () => {
                            expect(sizeSpy.calls.count()).toEqual(2);
                        });

                        it("original values is set from extradata", () => {
                            expect(sizeSpy.calls.first().returnValue).toEqual(222);
                        });

                    });


                });

                describe("setFocalPoint()", () => {

                    let focalPoint;

                    beforeEach(() => {
                        focalPoint = {x: 1, y: 2};
                    });

                    it("editor set focal point", () => {
                        imageUploaderEl.setFocalPoint(focalPoint);
                        expect(editorSpyObj.setFocusPosition).toHaveBeenCalledWith(1, 2);
                    });

                    it("empty point should invoke editor's reset", () => {
                        imageUploaderEl.setFocalPoint(null);
                        expect(editorSpyObj.resetFocusPosition).toHaveBeenCalled();
                    });
                });

                describe("setCrop()", () => {

                    let crop: Rect;

                    beforeEach(() => {
                        crop = {x: 1, y: 2, x2: 3, y2: 4};
                    });

                    it("editor should set crop", () => {
                        imageUploaderEl.setCrop(crop);
                        expect(editorSpyObj.setCropPosition).toHaveBeenCalledWith(1, 2, 3, 4);
                    });

                    it("empty crop should invoke editor's reset", () => {
                        imageUploaderEl.setCrop(null);
                        expect(editorSpyObj.resetCropPosition).toHaveBeenCalled();
                    });
                });

                describe("setZoom()", () => {

                    let zoom: Rect;

                    beforeEach(() => {
                        zoom = {x: 1, y: 2, x2: 3, y2: 4};
                    });

                    it("editor should set zoom", () => {
                        imageUploaderEl.setZoom(zoom);
                        expect(editorSpyObj.setZoomPosition).toHaveBeenCalledWith(1, 2, 3, 4);
                    });

                    it("empty zoom should invoke editor's reset", () => {
                        imageUploaderEl.setZoom(null);
                        expect(editorSpyObj.resetZoomPosition).toHaveBeenCalled();
                    });
                });

                describe("isFocalPointEditMode()", () => {

                    it("focalPoint edit mode should be enabled if editor in focus edit mode", () => {
                        editorSpyObj.isFocusEditMode.and.returnValue(true);
                        expect(imageUploaderEl.isFocalPointEditMode()).toBeTruthy();
                    });

                    it("focalPoint edit mode should be disabled if editor not in focus edit mode", () => {
                        editorSpyObj.isFocusEditMode.and.returnValue(false);
                        expect(imageUploaderEl.isFocalPointEditMode()).toBeFalsy();
                    });
                });

                describe("isCropEditMode()", () => {

                    it("crop edit mode should be enabled if editor in crop edit mode", () => {
                        editorSpyObj.isCropEditMode.and.returnValue(true);
                        expect(imageUploaderEl.isCropEditMode()).toBeTruthy();
                    });

                    it("crop edit mode should be disabled if editor not in crop edit mode", () => {
                        editorSpyObj.isCropEditMode.and.returnValue(false);
                        expect(imageUploaderEl.isCropEditMode()).toBeFalsy();
                    });
                });
            });

            describe("event listeners", () => {

                describe("onShown", () => {

                    it("max width equals to parent width", () => {
                        expect(imageUploaderEl.getEl().getMaxWidth()).toEqual(imageUploaderEl.getParentElement().getEl().getWidth());
                    });
                });

                describe("onUploadStarted", () => {

                    it("image editors are not empty before event handled", () => {
                        expect(imageUploaderEl['imageEditors'].length).toEqual(1);
                    });

                    it("image editors are removed on event handling", () => {
                        imageUploaderEl['notifyFileUploadStarted']("value");
                        expect(imageUploaderEl['imageEditors'].length).toEqual(0);
                    });
                });

                describe("onClicked()", () => {
                    let toggleSelectedSpy;

                    beforeEach(() => {
                        toggleSelectedSpy = spyOn(imageUploaderEl, "toggleSelected");
                        imageUploaderEl.getHTMLElement().click();
                    });

                    it("toggle selected should be called with target editor", () => {
                        expect(toggleSelectedSpy).toHaveBeenCalledWith(editorSpyObj);
                    });

                });
            });
        });
    });


    function createUploader(): ImageUploaderEl {
        return new ImageUploaderEl({
            params: {
                parent: "parentId"
            },
            operation: api.ui.uploader.MediaUploaderElOperation.create,
            name: 'upload-dialog',
            showCancel: false,
            showResult: false,
            deferred: true
        });
    }
}
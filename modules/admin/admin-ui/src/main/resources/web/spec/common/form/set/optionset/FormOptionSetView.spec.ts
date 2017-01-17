module FormOptionSetViewSpec {

    import FormOptionSet = api.form.FormOptionSet;
    import FormOptionSetJson = api.form.json.FormOptionSetJson;
    import FormOptionSetOptionJson = api.form.json.FormOptionSetOptionJson;
    import FormOptionSetView = api.form.FormOptionSetView;
    import FormOptionSetViewConfig = api.form.FormOptionSetViewConfig;
    import PropertySet = api.data.PropertySet;
    import FormContext = api.form.FormContext;
    import ValidationRecording = api.form.ValidationRecording;
    import FormItem = api.form.FormItem;
    import FormOptionSetOccurrenceView = api.form.FormOptionSetOccurrenceView;
    import FormItemPath = api.form.FormItemPath;
    import RecordingValidityChangedEvent = api.form.RecordingValidityChangedEvent;
    import FormOptionSetOccurrences = api.form.FormOptionSetOccurrences;
    import Button = api.ui.button.Button;
    import AEl = api.dom.AEl;
    import CallInfo = jasmine.CallInfo;

    describe('api.form.FormOptionSetView', function () {

        let optionSet: FormOptionSet;
        let optionSetViewConfig: FormOptionSetViewConfig;
        let optionSetView: FormOptionSetView;

        beforeEach(function () {
            optionSet = FormOptionSetSpec.createOptionSet(FormOptionSetSpec.getOptionSetJsonWithOptions());
            optionSetViewConfig = getFormOptionSetViewConfig(optionSet, getPropertySet());
            optionSetView = new FormOptionSetView(optionSetViewConfig);
        });

        describe('constructor', function () {

            it('should be defined', function () {
                expect(optionSetView).toBeDefined();
            });

            it('should have help text', function () {
                expect(optionSetView.hasHelpText()).toBeTruthy();
            });

            it('should have form option set', function () {
                expect(optionSetView['formSet']).toEqual(optionSetViewConfig.formOptionSet);
            });

            it('should have parent data set', function () {
                expect(optionSetView['parentDataSet']).toEqual(optionSetViewConfig.parentDataSet);
            });

            it('should have even class when first level', function () {
                expect(optionSetView.hasClass('even')).toBeTruthy();
            });

            it('should have odd class when fourth level', function () {
                spyOn(optionSet, 'getPath').and.returnValue(FormItemPath.fromString('path.to.some.view'));
                let optionSetView2 = new FormOptionSetView(optionSetViewConfig);

                expect(optionSetView2.hasClass('odd')).toBeTruthy();
            });

        });

        describe('layout()', function () {
            let initOccurrencesSpy;

            beforeEach(function () {
                initOccurrencesSpy = spyOn(api.form.FormOptionSetView.prototype, 'initOccurrences').and.callThrough();
                spyOn(optionSetView, 'validate').and.stub();
            });

            describe('default behaviour', function () {
                let addButtonSpy;
                let collapseButtonSpy;

                beforeEach(function (done: DoneFn) {
                    addButtonSpy = spyOn(optionSetView, 'makeAddButton').and.callThrough();
                    collapseButtonSpy = spyOn(optionSetView, 'makeCollapseButton').and.callThrough();
                    spyOn(api.form.FormSetOccurrences.prototype, 'layout').and.returnValue(wemQ<void>(null));

                    optionSetView.layout().then(function () {
                        done();
                    });
                });

                it('should create a container for occurrence views and append it to DOM', function () {
                    expect(optionSetView.getEl().getElementsByClassName('occurrence-views-container').length).toEqual(1);
                });

                it('should create form option set occurrences', function () {
                    expect(initOccurrencesSpy.calls.mostRecent().returnValue).toBeDefined();
                });

                it('should perform layout of the option set occurrences with validation', function () {
                    expect(api.form.FormSetOccurrences.prototype.layout).toHaveBeenCalledWith(true);
                });

                it('should create add button for occurrences', function () {
                    expect(addButtonSpy).toHaveBeenCalled();
                });

                it('should create collapse button for occurrences', function () {
                    expect(collapseButtonSpy).toHaveBeenCalled();
                });

                it('should run validation', function () {
                    expect(optionSetView.validate).toHaveBeenCalled();
                });

            });

            describe('when layout is called without validation', function () {

                beforeEach(function (done: DoneFn) {
                    spyOn(api.form.FormSetOccurrences.prototype, 'layout').and.returnValue(wemQ<void>(null));

                    optionSetView.layout(false).then(function () {
                        done();
                    });
                });

                it('should perform layout of the option set occurrences without validation', function () {
                    expect(api.form.FormSetOccurrences.prototype.layout).toHaveBeenCalledWith(false);
                });

                it('should NOT run validation', function () {
                    expect(optionSetView.validate).not.toHaveBeenCalled();
                });
            });

            describe('occurrences events', function () {
                let handleValiditySpy;

                beforeEach(function (done: DoneFn) {
                    spyOn(optionSetView, 'refresh').and.stub();
                    handleValiditySpy = spyOn(optionSetView, 'handleFormSetOccurrenceViewValidityChanged').and.stub();
                    spyOn(optionSetView, 'notifyEditContentRequested').and.callThrough();
                    // need actual layout to pass edit content request event
                    spyOn(api.form.FormSetOccurrences.prototype, 'layout').and.callThrough();

                    optionSetView.layout(false).then(function () {
                        done();
                    });
                });

                it('should listen to occurrence added', function () {
                    initOccurrencesSpy.calls.mostRecent().returnValue['notifyOccurrenceAdded'](null, null);
                    expect(optionSetView.refresh).toHaveBeenCalled();
                });

                it('should listen to occurrence rendered', function () {
                    initOccurrencesSpy.calls.mostRecent().returnValue['notifyOccurrenceRendered'](null, null, false);
                    expect(optionSetView.validate).toHaveBeenCalled();
                });

                it('should listen to occurrence removed', function () {
                    initOccurrencesSpy.calls.mostRecent().returnValue['notifyOccurrenceRemoved'](null, null);
                    expect(optionSetView.refresh).toHaveBeenCalled();
                });

                it('should listen to validity changed event', function () {
                    let views = initOccurrencesSpy.calls.mostRecent().returnValue.getOccurrenceViews();
                    views[0]['notifyValidityChanged'](null);
                    expect(handleValiditySpy).toHaveBeenCalled();
                });

                it('should listen to edit content request', function () {
                    let views = initOccurrencesSpy.calls.mostRecent().returnValue.getOccurrenceViews();
                    views[0].getFormItemViews()[0]['notifyEditContentRequested'](null);
                    expect(optionSetView.notifyEditContentRequested).toHaveBeenCalled();
                });

            });

            describe('buttons interaction', function () {
                let addSpy;
                let collapseSpy;
                let showSpy;

                beforeEach(function (done: DoneFn) {
                    addSpy = spyOn(optionSetView, 'makeAddButton').and.callThrough();
                    collapseSpy = spyOn(optionSetView, 'makeCollapseButton').and.callThrough();
                    showSpy = spyOn(api.form.FormSetOccurrences.prototype, 'showOccurrences').and.callThrough();

                    optionSetView.layout(false).then(function () {
                        done();
                    });
                });

                it('should add occurrence on add button click', function () {
                    let createSpy = spyOn(api.form.FormSetOccurrences.prototype, 'createAndAddOccurrence');

                    let button: Button = addSpy.calls.mostRecent().returnValue;
                    button.getHTMLElement().click();

                    expect(createSpy).toHaveBeenCalled();
                });

                it('should collapse occurrence on collapse link click', function () {
                    spyOn(api.form.FormSetOccurrences.prototype, 'isCollapsed').and.returnValues(false);

                    let link: AEl = collapseSpy.calls.mostRecent().returnValue;
                    link.getHTMLElement().click();

                    expect(link.getHtml()).toEqual('Expand');
                    expect(showSpy).toHaveBeenCalledWith(false);
                });

                it('should expand occurrence on expand button click', function () {
                    spyOn(api.form.FormSetOccurrences.prototype, 'isCollapsed').and.returnValues(true);

                    let link: AEl = collapseSpy.calls.mostRecent().returnValue;
                    link.getHTMLElement().click();

                    expect(link.getHtml()).toEqual('Collapse');
                    expect(showSpy).toHaveBeenCalledWith(true);
                });
            });
        });

        describe('validate()', function () {
            let occurrenceValidateSpy;

            beforeEach(function () {
                occurrenceValidateSpy = spyOn(api.form.FormOptionSetOccurrenceView.prototype, 'validate').and.callThrough();
            });

            it('should throw an exception if not laid out yet', function () {
                expect(optionSetView.validate).toThrowError("Can't validate before layout is done");
            });

            describe('after layout was done', function () {
                let renderValidationErrorsSpy;
                let notifyValidityChangedSpy;
                let initOccurrencesSpy;

                beforeEach(function (done: DoneFn) {
                    initOccurrencesSpy = spyOn(api.form.FormOptionSetView.prototype, 'initOccurrences').and.callThrough();

                    optionSetView.layout(false).then(function () {
                        done();
                    });

                    renderValidationErrorsSpy = spyOn(optionSetView, 'renderValidationErrors').and.callThrough();
                    notifyValidityChangedSpy = spyOn(optionSetView, 'notifyValidityChanged').and.callThrough();
                });

                describe('default behavior', function () {
                    let recording: ValidationRecording;

                    beforeEach(function () {
                        recording = optionSetView.validate();
                    });

                    it('should return ValidationRecording', function () {
                        expect(recording).toBeDefined();
                    });

                    it('should call validate on every FormSetOccurrenceView', function () {
                        expect(occurrenceValidateSpy).toHaveBeenCalledWith(true);
                    });

                    it('should have called renderValidationErrors', function () {
                        expect(renderValidationErrorsSpy).toHaveBeenCalled();
                    });

                    it('should NOT have called notifyValidityChanged', function () {
                        expect(notifyValidityChangedSpy).not.toHaveBeenCalled();
                    });
                });

                describe('not silent validate', function () {
                    let recording;

                    beforeEach(function () {
                        recording = optionSetView.validate(false);
                    });

                    it('should call validate(false) on every FormSetOccurrenceView', function () {
                        expect(occurrenceValidateSpy).toHaveBeenCalledWith(false);
                    });

                    it('should have called notifyValidityChanged', function () {
                        expect(notifyValidityChangedSpy).toHaveBeenCalled();
                    });
                });

                describe('validate with exclusions', function () {
                    let recording;
                    let excludedOccurrenceView;

                    beforeEach(function (done: DoneFn) {
                        let occurrences = initOccurrencesSpy.calls.mostRecent().returnValue;

                        occurrences.createAndAddOccurrence(1, false).then((addedView) => {
                            excludedOccurrenceView = addedView;
                            recording = optionSetView.validate(false, excludedOccurrenceView);
                            done();
                        });
                    });

                    it('should call validate on every FormSetOccurrenceView except excluded', function () {
                        expect(occurrenceValidateSpy.calls.all().every((info: CallInfo) => {
                            return info.object !== excludedOccurrenceView;
                        })).toBeTruthy(true);
                    });
                });
            });
        });

        describe('update()', function () {

            it('should become invalid after setting invalid data', function (done: DoneFn) {

                optionSetView.layout(false).then(function () {

                    let recording = optionSetView.validate();
                    expect(recording.isValid()).toBeTruthy('ValidationRecording should be valid');

                    optionSetView.update(getPropertySet(false)).then(function () {

                        recording = optionSetView.validate();
                        expect(recording.isValid()).toBeFalsy('ValidationRecording should\'ve become invalid');

                        done();
                    });
                });
            });

            it('should become valid after setting valid data', function (done: DoneFn) {

                optionSetView = new FormOptionSetView(getFormOptionSetViewConfig(optionSet, getPropertySet(false)));
                optionSetView.layout(false).then(function () {

                    let recording = optionSetView.validate();
                    expect(recording.isValid()).toBeFalsy('ValidationRecording should be invalid');

                    optionSetView.update(getPropertySet()).then(function () {

                        recording = optionSetView.validate();
                        expect(recording.isValid()).toBeTruthy('ValidationRecording should\'ve become valid');

                        done();
                    });
                });
            });
        });
    });

    export function getFormOptionSetViewConfig(optionSet: FormOptionSet, dataSet: PropertySet): FormOptionSetViewConfig {
        return {
            context: getFormContext(),
            formOptionSet: optionSet,
            parent: undefined,
            parentDataSet: dataSet
        };
    }

    export function getFormContext(): FormContext {
        return FormContext.create().setShowEmptyFormItemSetOccurrences(true).build();
    }

    export function getPropertySet(valid: boolean = true): PropertySet {
        let tree = new api.data.PropertyTree();
        let set = tree.addPropertySet('optionSet');

        let optionSet1 = set.addPropertySet('option1');
        optionSet1.addString('input1', 'Option 1 value from data');

        let optionSet2 = set.addPropertySet('option2');
        let itemSet1 = optionSet2.addPropertySet('itemSet1');
        if (valid) {
            itemSet1.addString('input2-1', 'Option 2 value from data');
            itemSet1.addBoolean('input2-2', true);
        }

        return tree.getRoot();
    }
}

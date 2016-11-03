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

    describe("api.form.FormOptionSetView", function () {

        let optionSet: FormOptionSet;
        let optionSetViewConfig: FormOptionSetViewConfig;
        let optionSetView: FormOptionSetView;

        beforeEach(function () {
            optionSet = FormOptionSetSpec.createOptionSet(FormOptionSetSpec.getOptionSetJsonWithOptions());
            optionSetViewConfig = getFormOptionSetViewConfig(optionSet, getPropertySet());
            optionSetView = new FormOptionSetView(optionSetViewConfig);
        });

        describe("constructor", function () {

            it('should be defined', function () {
                expect(optionSetView).toBeDefined();
            });

            it('should have help text', function () {
                expect(optionSetView.hasHelpText()).toBeTruthy();
            });

            it('should have form option set', function () {
                expect(optionSetView['formOptionSet']).toEqual(optionSetViewConfig.formOptionSet);
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
            var formOptionSetOccurrencesConstructor, formOptionSetOccurrences;

            beforeEach(function (done) {
                formOptionSetOccurrencesConstructor = api.form.FormOptionSetOccurrences;

                spyOn(api.form, 'FormOptionSetOccurrences').and.callFake(function (config) {
                    formOptionSetOccurrences = new formOptionSetOccurrencesConstructor(config);
                    spyOn(formOptionSetOccurrences, "layout").and.returnValue(wemQ<void>(null));
                    return formOptionSetOccurrences;
                });

                spyOn(optionSetView, "validate");
                done();
            });

            describe('default behaviour', function () {
                var addButtonSpy, collapseButtonSpy;

                beforeEach(function (done) {

                    addButtonSpy = spyOn(optionSetView, "makeAddButton").and.callThrough();
                    collapseButtonSpy = spyOn(optionSetView, "makeCollapseButton").and.callThrough();

                    optionSetView.layout().then(function () {
                        done();
                    });
                });

                it('should create a container for occurrence views and append it to DOM', function () {
                    expect(optionSetView.getEl().getElementsByClassName("occurrence-views-container").length).toEqual(1);
                });

                it('should create form option set occurrences', function () {
                    expect(api.form.FormOptionSetOccurrences).toHaveBeenCalled();
                });

                it('should perform layout of the option set occurrences with validation', function () {
                    expect(formOptionSetOccurrences.layout).toHaveBeenCalledWith(true);
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

                beforeEach(function (done) {
                    optionSetView.layout(false).then(function () {
                        done();
                    });
                });

                it('should perform layout of the option set occurrences without validation', function () {
                    expect(formOptionSetOccurrences.layout).toHaveBeenCalledWith(false);
                });

                it('should NOT run validation', function () {

                    expect(optionSetView.validate).not.toHaveBeenCalled();
                });
            });
        });

        describe("validate()", function () {

            beforeEach(function () {
                spyOn(optionSetView, 'renderValidationErrors').and.callThrough();
                spyOn(optionSetView, 'notifyValidityChanged').and.callThrough();
            });

            it('should render validation errors on validate() but not notify validity changed listeners', function (done) {

                optionSetView.layout().then(function () {

                    optionSetView.validate();

                    expect(optionSetView['renderValidationErrors']).toHaveBeenCalledTimes(2);
                    expect(optionSetView['notifyValidityChanged']).toHaveBeenCalledTimes(0);

                    done();
                });
            });

            it('should render validation errors and notify validity changed on validate(false)', function (done) {

                optionSetView.layout(false).then(function () {

                    optionSetView.validate(false);

                    expect(optionSetView['renderValidationErrors']).toHaveBeenCalledTimes(1);
                    expect(optionSetView['notifyValidityChanged']).toHaveBeenCalledTimes(1);

                    done();
                });
            });

            it('should return ValidationRecording when validated', function (done) {
                optionSetView.layout(false).then(function () {

                    let recording: ValidationRecording = optionSetView.validate(false);

                    expect(recording).toBeTruthy('validate() should return ValidationRecording');
                    expect(recording.isValid()).toBeTruthy('ValidationRecording should be valid');

                    done();
                });
            });

        });

        describe('update()', function () {

            it('should become invalid after setting invalid data', function (done) {

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

            it('should become valid after setting valid data', function (done) {

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
            })
        })
    });

    export function getFormOptionSetViewConfig(optionSet: FormOptionSet, dataSet: PropertySet): FormOptionSetViewConfig {
        return {
            context: getFormContext(),
            formOptionSet: optionSet,
            parent: undefined,
            parentDataSet: dataSet
        }
    }

    export function getFormContext(): FormContext {
        return FormContext.create().setShowEmptyFormItemSetOccurrences(true).build();
    }

    export function getPropertySet(valid: boolean = true): PropertySet {
        var tree = new api.data.PropertyTree();
        var set = tree.addPropertySet('optionSet');

        var optionSet1 = set.addPropertySet("option1");
        optionSet1.addString("input1", "Option 1 value from data");

        var optionSet2 = set.addPropertySet("option2");
        var itemSet1 = optionSet2.addPropertySet('itemSet1');
        if (valid) {
            itemSet1.addString("input2-1", "Option 2 value from data");
            itemSet1.addBoolean("input2-2", true);
        }

        return tree.getRoot();
    }
}
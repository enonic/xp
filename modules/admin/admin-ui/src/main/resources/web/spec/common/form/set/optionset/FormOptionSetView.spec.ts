module FormOptionSetViewSpec {

    import FormOptionSet = api.form.FormOptionSet;
    import FormOptionSetJson = api.form.json.FormOptionSetJson;
    import FormOptionSetOptionJson = api.form.json.FormOptionSetOptionJson;
    import FormOptionSetView = api.form.FormOptionSetView;
    import FormOptionSetViewConfig = api.form.FormOptionSetViewConfig;
    import PropertySet = api.data.PropertySet;
    import FormContext = api.form.FormContext;
    import ValidationRecording = api.form.ValidationRecording;

    describe("api.form.FormOptionSetView", function () {

        let optionSet: FormOptionSet;
        let optionSetView: FormOptionSetView;

        beforeEach(function () {
            optionSet = FormOptionSetSpec.createOptionSet(FormOptionSetSpec.getOptionSetJsonWithOptions());
            optionSetView = createOptionSetView(optionSet, getPropertySet());

            spyOn(optionSetView, 'renderValidationErrors').and.callThrough();
            spyOn(optionSetView, 'notifyValidityChanged').and.callThrough();
        });

        afterEach(function () {
            optionSet = null;
            optionSetView = null;
        });

        describe("constructor", function () {

            it('should have help text', function (done) {
                optionSetView.layout().then(function () {
                    expect(optionSetView.hasHelpText()).toBeTruthy();
                    done();
                });
            });

        });

        describe('layout()', function () {

            it('should render validation errors on layout() but not notify validity changed listeners', function (done) {

                optionSetView.layout().then(function () {
                    expect(optionSetView['renderValidationErrors']).toHaveBeenCalledTimes(1);
                    expect(optionSetView['notifyValidityChanged']).toHaveBeenCalledTimes(0);

                    done();
                });
            });

            it('should not render validation errors nor notify validity changed on layout(false)', function (done) {

                optionSetView.layout(false).then(function () {
                    expect(optionSetView['renderValidationErrors']).toHaveBeenCalledTimes(0);
                    expect(optionSetView['notifyValidityChanged']).toHaveBeenCalledTimes(0);

                    done();
                });
            });
        });

        describe("validate()", function () {

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

                optionSetView = createOptionSetView(optionSet, getPropertySet(false));
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

    export function createOptionSetView(optionSet: FormOptionSet, dataSet: PropertySet): FormOptionSetView {
        return new FormOptionSetView(getFormOptionSetViewConfig(optionSet, dataSet));
    }

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
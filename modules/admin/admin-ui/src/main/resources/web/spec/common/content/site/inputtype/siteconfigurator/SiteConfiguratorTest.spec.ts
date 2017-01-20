import Input = api.form.Input;
import Site = api.content.site.Site;
import ContentFormContext = api.content.form.ContentFormContext;
import SiteConfigurator = api.content.site.inputtype.siteconfigurator.SiteConfigurator;
import SiteConfigProvider = api.content.site.inputtype.siteconfigurator.SiteConfigProvider;
import SiteConfiguratorComboBox = api.content.site.inputtype.siteconfigurator.SiteConfiguratorComboBox;
import SiteConfiguratorSelectedOptionsView = api.content.site.inputtype.siteconfigurator.SiteConfiguratorSelectedOptionsView;
import SelectedOptionEvent = api.ui.selector.combobox.SelectedOptionEvent;
import ContentTypeName = api.schema.content.ContentTypeName;
import InputJson = api.form.json.InputJson;
import ContentJson = api.content.json.ContentJson;
import ApplicationJson = api.application.json.ApplicationJson;
import ContentInputTypeViewContext = api.content.form.inputtype.ContentInputTypeViewContext;
import ContentPath = api.content.ContentPath;
import BaseInputTypeManagingAdd = api.form.inputtype.support.BaseInputTypeManagingAdd;
import FormView = api.form.FormView;
import FormValidityChangedEvent = api.form.FormValidityChangedEvent;

describe('api.content.site.inputtype.siteconfigurator.SiteConfigurator', () => {

    let globalInput: Input;
    let globalSite: Site;
    let globalFormContext: ContentFormContext;
    let globalConfigurator: SiteConfigurator;

    beforeEach(() => {
        globalInput = createInput();
        globalSite = createSite();
        globalFormContext = createFormContext(globalSite);
        globalConfigurator = createSiteConfigurator(globalInput, globalSite, globalFormContext);
    });

    describe('constructor', () => {

        it('should correctly initialize form context', function () {
            expect(globalFormContext).toEqual(globalConfigurator['globalFormContext']);
        });

        describe('should correctly initialize input type context:', function () {

            it('input', function () {
                expect(globalInput).toEqual(globalConfigurator['context'].input);
            });

            it('site', function () {
                expect(globalSite).toEqual(globalConfigurator['context'].site);
            });

            it('formContext', function () {
                expect(globalFormContext).toEqual(globalConfigurator['context'].formContext);
            });
        });
    });

    describe('what happens after layout', () => {

        let createComboBoxSpy;
        let parentSpy;
        let appendChildSpy;
        let providerSpy;

        let combobox;
        let provider;

        beforeEach((done) => {
            provider = new SiteConfigProvider(globalSite.getContentData().getPropertyArray('siteConfig'));

            createComboBoxSpy = spyOn(globalConfigurator, 'createComboBox').and.callThrough();
            appendChildSpy = spyOn(globalConfigurator, 'appendChild').and.callThrough();
            parentSpy = spyOn(BaseInputTypeManagingAdd.prototype, 'layout').and.callThrough();

            providerSpy = spyOn(api.content.site.inputtype.siteconfigurator, 'SiteConfigProvider')
                .and.returnValue(provider);

            globalConfigurator.layout(globalInput, globalSite.getContentData().getPropertyArray('siteConfig')).then(()=> {
                combobox = createComboBoxSpy.calls.mostRecent().returnValue;
                done();
            });
        });

        it('layout of the parent class is called', () => {
            expect(parentSpy).toHaveBeenCalledWith(globalInput, globalSite.getContentData().getPropertyArray('siteConfig'));
        });

        it('siteConfigProvider is initialized', () => {
            expect(providerSpy).toHaveBeenCalled();
        });

        it('combobox is added', () => {
            expect(appendChildSpy).toHaveBeenCalledWith(combobox);
        });
    });

    describe('test public methods', () => {

        let combobox;
        let createComboBoxSpy;

        beforeEach((done) => {
            createComboBoxSpy = spyOn(globalConfigurator, 'createComboBox').and.callThrough();

            globalConfigurator.layout(globalInput, globalSite.getContentData().getPropertyArray('siteConfig')).then(()=> {
                combobox = createComboBoxSpy.calls.mostRecent().returnValue;
                done();
            });
        });

        describe('reset()', () => {
            let resetSpy;

            beforeEach(() => {
                resetSpy = spyOn(combobox, 'resetBaseValues');
                globalConfigurator.reset();
            });

            it('combobox resetBaseValues method is called', () => {
                expect(resetSpy).toHaveBeenCalled();
            });
        });

        describe('update()', () => {

            let newSet: PropertySet;

            beforeEach(()=> {
                let propertyArray = globalSite.getContentData().getPropertyArray('siteConfig');
                spyOn(combobox.getComboBox(), 'doWhenLoaded').and.stub();

                newSet = propertyArray.addSet();
            });

            it('combobox value changed listener driggered by data update', (done) => {
                combobox.onValueChanged((e) => {
                    expect(e.getNewValue()).toBe('com.enonic.app.test;');
                    done();
                });
                newSet.addString('v', 'test');
            });
        });

        describe('displayValidationErrors()', () => {

            let displayValidationErrorsSpy;

            beforeEach(() => {
                let selectedOption = combobox.getSelectedOptionsView().createSelectedOption(
                    <api.ui.selector.Option<Application>>{displayValue: createApplication(), value: 'com.enonic.app.test'}
                );

                let formView = new FormView(null, null, null);

                spyOn(combobox, 'getSelectedOptionViews').and.returnValue([selectedOption.getOptionView()]);
                spyOn(selectedOption.getOptionView(), 'getFormView').and.returnValue(formView);

                displayValidationErrorsSpy = spyOn(formView, 'displayValidationErrors');

                globalConfigurator.displayValidationErrors(null);
            });

            it('formView displayValidationErrors method is called', () => {
                expect(displayValidationErrorsSpy).toHaveBeenCalled();
            });

        });
        describe('getValueType()', () => {

            it("value type should be 'PropertySet'", () => {
                expect(globalConfigurator.getValueType() == ValueTypes.DATA).toBeTruthy();
            });

        });
        describe('newInitialValue()', () => {

            it('initial value should be null', () => {
                expect(globalConfigurator.newInitialValue()).toBeNull();
            });

        });

        describe('giveFocus()', () => {
            let focusSpy;

            beforeEach(() => {
                focusSpy = spyOn(combobox, 'giveFocus');
                spyOn(combobox, 'maximumOccurrencesReached');
            });

            describe('if maximum occurrences reached ', () => {
                beforeEach(() => {
                    combobox.maximumOccurrencesReached.and.returnValue(true);
                    globalConfigurator.giveFocus();
                });

                it('should not focus the combobox', () => {
                    expect(focusSpy).not.toHaveBeenCalled();
                });

            });

            describe('if maximum occurrences is not reached ', () => {
                beforeEach(() => {
                    combobox.maximumOccurrencesReached.and.returnValue(false);
                    globalConfigurator.giveFocus();
                });

                it('should focus the combobox', () => {
                    expect(focusSpy).toHaveBeenCalled();
                });
            });
        });

        describe('validate()', () => {
            let recording;
            let selectedOption;
            let result;

            beforeEach(() => {
                selectedOption = combobox.getSelectedOptionsView().createSelectedOption(
                    <api.ui.selector.Option<Application>>{displayValue: createApplication(), value: 'com.enonic.app.test'}
                );

                recording = new api.form.ValidationRecording();

                let formView = new FormView(null, null, null);

                spyOn(formView, 'validate').and.returnValue(recording);
                spyOn(selectedOption.getOptionView(), 'getFormView').and.returnValue(formView);

                spyOn(combobox, 'getSelectedOptionViews').and.returnValue([selectedOption.getOptionView()]);
            });

            describe('validation is not passed', () => {

                beforeEach(() => {
                    spyOn(recording, 'isMinimumOccurrencesValid').and.returnValue(false);
                    spyOn(recording, 'isMaximumOccurrencesValid').and.returnValue(false);

                    result = globalConfigurator.validate();
                });

                it('minimum occurrences breached', () => {
                    expect(result.isMinimumOccurrencesBreached()).toBe(true);
                });

                it('maximum occurrences breached', () => {
                    expect(result.isMaximumOccurrencesBreached()).toBe(true);
                });

                it('result is not valid', () => {
                    expect(result.isValid()).toBeFalsy();
                });
            });

            describe('validation is passed', () => {

                beforeEach(() => {
                    spyOn(recording, 'isMaximumOccurrencesValid').and.returnValue(true);
                    spyOn(recording, 'isMinimumOccurrencesValid').and.returnValue(true);

                    result = globalConfigurator.validate();
                });

                it('result is valid', () => {
                    expect(result.isValid()).toBeTruthy();
                });
            });
        });

    });

    describe('test event listeners', () => {

        let combobox;
        let selectedOption;
        let handlerSpy;
        let event: SelectedOptionEvent<any>;
        let validationSpy;

        beforeEach((done) => {
            let createComboBoxSpy = spyOn(globalConfigurator, 'createComboBox').and.callThrough();

            globalConfigurator.layout(globalInput, globalSite.getContentData().getPropertyArray('siteConfig')).then(() => {
                combobox = createComboBoxSpy.calls.mostRecent().returnValue;

                selectedOption = combobox.getSelectedOptionsView().createSelectedOption(
                    <api.ui.selector.Option<Application>>{displayValue: createApplication(), value: 'com.enonic.app.test'}
                );

                handlerSpy = spyOn(globalConfigurator, 'saveToSet').and.callThrough();
                validationSpy = spyOn(globalConfigurator, 'validate');
                event = new SelectedOptionEvent(selectedOption, -1);

                done();
            });
        });

        describe('when config has changed', () => {
            let fakePropertyArrayObj;
            let fakePropertySetObj;
            let fakePropertyObj;

            beforeEach(() => {
                fakePropertySetObj = jasmine.createSpyObj('fakePropertySet', ['setStringByPath', 'setPropertySetByPath']);
                fakePropertyObj = jasmine.createSpyObj('fakeProperty', ['getPropertySet']);

                fakePropertyArrayObj = jasmine.createSpyObj('fakePropertyArray', ['get', 'addSet']);
                fakePropertyArrayObj.get.and.returnValue(fakePropertyObj);

                spyOn(globalConfigurator, 'getPropertyArray').and.returnValue(fakePropertyArrayObj);
            });

            describe("if property set doesn't exist", () => {
                beforeEach(() => {
                    fakePropertyArrayObj.addSet.and.returnValue(fakePropertySetObj);
                    fakePropertyObj.getPropertySet.and.returnValue(null);

                    combobox.getSelectedOptionsView().notifyOptionSelected(event);
                });

                it('should create a new property set', () => {
                    expect(fakePropertyArrayObj.addSet).toHaveBeenCalled();
                });

                it('should update properties of the new property set', () => {
                    expect(fakePropertySetObj.setStringByPath).toHaveBeenCalled();
                    expect(fakePropertySetObj.setPropertySetByPath).toHaveBeenCalled();
                });
            });

            describe('if property set exists', () => {
                beforeEach(() => {
                    fakePropertyObj.getPropertySet.and.returnValue(fakePropertySetObj);

                    combobox.getSelectedOptionsView().notifyOptionSelected(event);
                });

                it('should NOT create a new property set', () => {
                    expect(fakePropertyArrayObj.addSet).not.toHaveBeenCalled();
                });

                it('should update properties of the existing property set', () => {
                    expect(fakePropertySetObj.setStringByPath).toHaveBeenCalled();
                    expect(fakePropertySetObj.setPropertySetByPath).toHaveBeenCalled();
                });
            });
        });

        describe('when the form is displayed', () => {

            let formView;

            beforeEach(() => {
                formView = new FormView(null, null, null);

                combobox.getSelectedOptionsView().notifySiteConfigFormDisplayed(selectedOption.getOption().displayValue.getApplicationKey(),
                    formView);
            });

            it('should run validation', () => {
                expect(validationSpy).toHaveBeenCalled();
            });

            it('should run validation every time form validity changes', () => {
                validationSpy.calls.reset();

                formView.notifyValidityChanged(new FormValidityChangedEvent());
                formView.notifyValidityChanged(new FormValidityChangedEvent());
                formView.notifyValidityChanged(new FormValidityChangedEvent());

                expect(validationSpy.calls.count()).toEqual(3);
            });
        });

        describe('when an option is selected', () => {

            beforeEach(() => {
                combobox.getSelectedOptionsView().notifyOptionSelected(event);
            });

            it('should save config in the property set', () => {
                expect(handlerSpy).toHaveBeenCalledWith(selectedOption.getOptionView().getSiteConfig(), selectedOption.getIndex());
            });

            it('should run validation', () => {
                expect(validationSpy).toHaveBeenCalled();
            });

        });

        describe('when an option is moved', () => {
            beforeEach(() => {
                combobox.getSelectedOptionsView().notifyOptionMoved(selectedOption);
            });

            it('should save config in the property set', () => {
                expect(handlerSpy).toHaveBeenCalledWith(selectedOption.getOptionView().getSiteConfig(), selectedOption.getIndex());
            });

            it('should run validation', () => {
                expect(validationSpy).toHaveBeenCalled();
            });

        });

        describe('when an option is deselected', () => {
            let fakePropertyArrayObj;

            beforeEach(() => {
                fakePropertyArrayObj = jasmine.createSpyObj('eventHandler', ['remove']);
                spyOn(globalConfigurator, 'getPropertyArray').and.returnValue(fakePropertyArrayObj);

                combobox.getSelectedOptionsView().notifyOptionDeselected(selectedOption);
            });

            it('should remove option from the property set', () => {
                expect(fakePropertyArrayObj.remove).toHaveBeenCalledWith(selectedOption.getIndex());
            });

            it('should run validation', () => {
                expect(validationSpy).toHaveBeenCalled();
            });
        });
    });

    function createFormContext(site: Site): ContentFormContext {
        return ContentFormContext.create()
            .setSite(site)
            .setParentContent(null)
            .setPersistedContent(site)
            .setContentTypeName(ContentTypeName.SITE)
            .build();
    }

    function createInput(): Input {
        return Input.fromJson(<InputJson>{
            name: 'siteConfig',
            customText: null,
            helpText: 'Configure applications used by this site',
            immutable: false,
            indexed: false,
            label: 'Applications',
            occurrences: {minimum: 0, maximum: 0},
            validationRegexp: null,
            inputType: '{name:\"SiteConfigurator\",custom:false,refString:\"SiteConfigurator\"}',
            config: {},
            maximizeUIInputWidth: true

        });
    }

    function createSite(): Site {
        return <Site>Site.fromJson(<ContentJson>
            {
                id: 'd8555d1d-88a6-45c7-8756-6600ed3cb0c8',
                name: 'test',
                displayName: 'test',
                path: '/test',
                attachments: [],
                childOrder: {
                    orderExpressions: []
                }
                ,
                contentState: 'DEFAULT',
                createdTime: '2016-10-24T15:03:47.914Z',
                creator: 'user:system:su',
                data: [{name: 'description', type: 'String', values: [{v: 'Site'}]}, {
                    name: 'siteConfig',
                    type: 'PropertySet',
                    values: [{
                        set: [{name: 'applicationKey', type: 'String', values: [{v: 'com.enonic.app.test'}]}, {
                            name: 'config',
                            type: 'PropertySet',
                            values: [{set: []}]
                        }]
                    }]
                }],
                deletable: true,
                editable: true,
                hasChildren: true,
                iconUrl: '',
                inheritPermissions: true,
                isPage: false,
                isRoot: true,
                isValid: true,
                language: null,
                meta: [],
                modifiedTime: '',
                modifier: 'user:system:su',
                owner: 'user:system:su',
                page: null,
                permissions: [],
                thumbnail: null,
                type: 'portal:site',
                requireValid: false,
                publish: {
                    from: '2016-10-24T15:03:47.914Z',
                    to: '2017-10-24T15:03:47.914Z'
                }
            }
        );
    }

    function createApplication(): Application {
        return Application.fromJson(<ApplicationJson>{
            authConfig: null,
            config: {
                formItems: []
            },
            deletable: false,
            description: 'desc',
            displayName: 'App',
            editable: false,
            iconUrl: '',
            key: 'com.enonic.app.test',
            local: true,
            maxSystemVersion: '7.0.0',
            metaSteps: [],
            minSystemVersion: '6.0.0',
            modifiedTime: '',
            state: 'started',
            url: null,
            vendorName: 'Enonic AS',
            vendorUrl: 'http://enonic.com',
            version: '',
            info: null,
            applicationDependencies: null,
            contentTypeDependencies: null,
            createdTime: null,
            id: ''
        });
    }

    function createSiteConfigurator(input: Input, site: Site, formContext: ContentFormContext): SiteConfigurator {
        let config: ContentInputTypeViewContext = <ContentInputTypeViewContext> {
            formContext: formContext,
            input: input,
            inputConfig: {},
            parentDataPath: PropertyPath.ROOT,
            site: site,
            content: site,
            contentPath: ContentPath.fromString('/test'),
            parentContentPath: ContentPath.ROOT
        };

        return new SiteConfigurator(config);
    }

});

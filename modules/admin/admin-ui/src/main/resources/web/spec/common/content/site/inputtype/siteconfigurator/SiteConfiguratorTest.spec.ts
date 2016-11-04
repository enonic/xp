import Input = api.form.Input;
import Site = api.content.site.Site;
import ContentFormContext = api.content.form.ContentFormContext;
import SiteConfigurator = api.content.site.inputtype.siteconfigurator.SiteConfigurator;
import Application = api.application.Application;
import SiteConfigProvider = api.content.site.inputtype.siteconfigurator.SiteConfigProvider;
import SiteConfiguratorComboBox = api.content.site.inputtype.siteconfigurator.SiteConfiguratorComboBox;
import SiteConfiguratorSelectedOptionsView = api.content.site.inputtype.siteconfigurator.SiteConfiguratorSelectedOptionsView;
import ObjectHelper = api.ObjectHelper;
import SelectedOptionEvent = api.ui.selector.combobox.SelectedOptionEvent;
import ValueTypes = api.data.ValueTypes;
import ContentTypeName = api.schema.content.ContentTypeName;
import InputJson = api.form.json.InputJson;
import ContentJson = api.content.json.ContentJson;
import ApplicationJson = api.application.json.ApplicationJson;
import ContentInputTypeViewContext = api.content.form.inputtype.ContentInputTypeViewContext;
import PropertyPath = api.data.PropertyPath;
import ContentPath = api.content.ContentPath;
import BaseInputTypeManagingAdd = api.form.inputtype.support.BaseInputTypeManagingAdd;
import FormView = api.form.FormView;

describe("api.content.site.inputtype.siteconfigurator.SiteConfigurator", () => {

    var input: Input, site: Site, formContext: ContentFormContext, configurator: SiteConfigurator;

    beforeEach(() => {
        input = createInput();
        site = createSite();
        formContext = createFormContext(site);
        configurator = createSiteConfigurator(input, site, formContext);
    });

    describe("constructor", () => {

        it("should correctly initialize form context", function () {
            expect(configurator["formContext"]).toEqual(formContext);
        });

        describe("should correctly initialize input type context:", function () {

            it("input", function () {
                expect(configurator["context"].input).toEqual(input);
            });

            it("site", function () {
                expect(configurator["context"].site).toEqual(site);
            });

            it("formContext", function () {
                expect(configurator["context"].formContext).toEqual(formContext);
            });
        });
    });


    describe("what happens after layout", () => {

        var createComboBoxSpy, parentSpy, appendChildSpy, providerSpy;

        var combobox, provider;

        beforeEach((done) => {
            provider = new SiteConfigProvider(site.getContentData().getPropertyArray("siteConfig"));

            createComboBoxSpy = spyOn(configurator, "createComboBox").and.callThrough();
            appendChildSpy = spyOn(configurator, "appendChild").and.callThrough();
            parentSpy = spyOn(BaseInputTypeManagingAdd.prototype, "layout").and.callThrough();

            providerSpy = spyOn(api.content.site.inputtype.siteconfigurator, "SiteConfigProvider")
                .and.returnValue(provider);

            configurator.layout(input, site.getContentData().getPropertyArray("siteConfig")).then(()=> {
                combobox = createComboBoxSpy.calls.mostRecent().returnValue;
                done();
            });
        });

        it('layout of the parent class is called', () => {
            expect(parentSpy).toHaveBeenCalledWith(input, site.getContentData().getPropertyArray("siteConfig"));
        });

        it('siteConfigProvider is initialized', () => {
            expect(providerSpy).toHaveBeenCalled();
        });

        it('combobox is added', () => {
            expect(appendChildSpy).toHaveBeenCalledWith(combobox);
        });

        describe("test public methods", () => {

            it("reset()", () => {
                let resetSpy = spyOn(combobox, "resetBaseValues");
                configurator.reset();
                expect(resetSpy).toHaveBeenCalled();
            });

            it("update()", (done) => {

                var propertyArray = site.getContentData().getPropertyArray("siteConfig");

                combobox.onValueChanged((a) => {
                    expect(a.getNewValue()).toBe("com.enonic.app.test;");
                    done();
                });

                spyOn(combobox.getComboBox(), 'doWhenLoaded').and.stub();

                var newSet = propertyArray.addSet();
                newSet.addString("v", "test");
            });

            it("displayValidationErrors()",() => {
                let selectedOption = combobox.getSelectedOptionsView().createSelectedOption(
                    <api.ui.selector.Option<Application>>{displayValue: createApplication(), value: "com.enonic.app.test"}
                );

                let formView = new FormView(null,null,null);
                spyOn(combobox, 'getSelectedOptionViews').and.returnValue([selectedOption.getOptionView()]);
                spyOn(selectedOption.getOptionView(), "getFormView").and.returnValue(formView);
                let displayValidationErrorsSpy = spyOn(formView, "displayValidationErrors");


                configurator.displayValidationErrors(null);
                expect(displayValidationErrorsSpy).toHaveBeenCalled();
            });

            it("getValueType()", () => {
                expect(configurator.getValueType() == ValueTypes.DATA).toBeTruthy();
            });

            it("newInitialValue()", () => {
                expect(configurator.newInitialValue()).toBeNull();
            });

            describe("giveFocus()", () => {
                let focusSpy;

                beforeEach(() => {
                   focusSpy = spyOn(combobox, "giveFocus");
                });

                it("combobox is focused",() => {
                    spyOn(combobox, "maximumOccurrencesReached").and.returnValue(false);
                    configurator.giveFocus();

                    expect(focusSpy).toHaveBeenCalled();
                });

                it("validation check is failed",() => {
                    spyOn(combobox, "maximumOccurrencesReached").and.returnValue(true);

                    let result = configurator.giveFocus();

                    expect(result).toBe(false);
                    expect(focusSpy).not.toHaveBeenCalled();
                });
            });


            describe("validate()", () => {
                let recording, selectedOption;

                beforeEach(() => {
                    selectedOption = combobox.getSelectedOptionsView().createSelectedOption(
                        <api.ui.selector.Option<Application>>{displayValue: createApplication(), value: "com.enonic.app.test"}
                    );

                    recording = new api.form.ValidationRecording();

                    let formView = new FormView(null,null,null);

                    spyOn(formView, "validate").and.returnValue(recording);
                    spyOn(selectedOption.getOptionView(), "getFormView").and.returnValue(formView);

                    spyOn(combobox, 'getSelectedOptionViews').and.returnValue([selectedOption.getOptionView()]);
                });


                it("minimum occurrences breached",() => {
                    spyOn(recording, "isMinimumOccurrencesValid").and.returnValue(false);

                    var result = configurator.validate();
                    expect(result.isMinimumOccurrencesBreached()).toBe(true);
                    expect(result.isValid()).toBe(false);
                });

                it("maximum occurrences breached",() => {
                    spyOn(recording, "isMaximumOccurrencesValid").and.returnValue(false);

                    var result = configurator.validate();
                    expect(result.isMaximumOccurrencesBreached()).toBe(true);
                    expect(result.isValid()).toBe(false);
                });

                it("valid",() => {
                    spyOn(recording, "isMaximumOccurrencesValid").and.returnValue(true);
                    spyOn(recording, "isMinimumOccurrencesValid").and.returnValue(true);

                    var result = configurator.validate();
                    expect(result.isValid()).toBe(true);
                });

            });



        });

        describe("test event listeners", () => {

            var selectedOption;

            beforeEach((done) => {
                configurator.layout(input, site.getContentData().getPropertyArray("siteConfig")).then(() => {
                    selectedOption = combobox.getSelectedOptionsView().createSelectedOption(
                        <api.ui.selector.Option<Application>>{displayValue: createApplication(), value: "com.enonic.app.test"}
                    );
                    done();
                });
            });

            it("option selected listener is called", () => {

                var handlerSpy = spyOn(configurator, "saveToSet");

                var event = new SelectedOptionEvent(selectedOption, -1);

                combobox.getSelectedOptionsView().notifyOptionSelected(event);

                expect(handlerSpy).toHaveBeenCalledWith(selectedOption.getOptionView().getSiteConfig(), selectedOption.getIndex());
            });

            it("option moved listener is called", (done) => {

                var handlerSpy = spyOn(configurator, "saveToSet");

                combobox.getSelectedOptionsView().notifyOptionMoved(selectedOption);

                expect(handlerSpy).toHaveBeenCalledWith(selectedOption.getOptionView().getSiteConfig(), 0);
                done();
            });

            it("option deselected listener is called", (done) => {

                var handlerSpy = spyOn(configurator["propertyArray"], "remove");

                combobox.getSelectedOptionsView().notifyOptionDeselected(selectedOption);

                expect(handlerSpy).toHaveBeenCalledWith(selectedOption.getIndex());
                done();

            });

        });
    });

    function createFormContext(site: Site): ContentFormContext {
        return ContentFormContext.create().setSite(site).setParentContent(null).setPersistedContent(site).setContentTypeName(
            ContentTypeName.SITE).build();
    }

    function createInput(): Input {
        return Input.fromJson(<InputJson>{
            name: "siteConfig",
            customText: null,
            helpText: "Configure applications used by this site",
            immutable: false,
            indexed: false,
            label: "Applications",
            occurrences: {minimum: 0, maximum: 0},
            validationRegexp: null,
            inputType: "{name:\"SiteConfigurator\",custom:false,refString:\"SiteConfigurator\"}",
            config: {},
            maximizeUIInputWidth: true

        })
    }

    function createSite(): Site {
        return <Site>Site.fromJson(<ContentJson>
            {
                id: "d8555d1d-88a6-45c7-8756-6600ed3cb0c8",
                name: "test",
                displayName: "test",
                path: "/test",
                attachments: [],
                childOrder: {
                    orderExpressions: []
                }
                ,
                contentState: "DEFAULT",
                createdTime: "2016-10-24T15:03:47.914Z",
                creator: "user:system:su",
                data: [{"name": "description", "type": "String", "values": [{"v": "Site"}]}, {
                    "name": "siteConfig",
                    "type": "PropertySet",
                    "values": [{
                        "set": [{"name": "applicationKey", "type": "String", "values": [{"v": "com.enonic.app.test"}]}, {
                            "name": "config",
                            "type": "PropertySet",
                            "values": [{"set": []}]
                        }]
                    }]
                }],
                deletable: true,
                editable: true,
                hasChildren: true,
                iconUrl: "",
                inheritPermissions: true,
                isPage: false,
                isRoot: true,
                isValid: true,
                language: null,
                meta: [],
                modifiedTime: "",
                modifier: "user:system:su",
                owner: "user:system:su",
                page: null,
                permissions: [],
                thumbnail: null,
                type: "portal:site",
                requireValid: false

            }
        )
    }

    function createApplication(): Application {
        return Application.fromJson(<ApplicationJson>{
            "authConfig": null,
            "config": {
                "formItems": []
            },
            "deletable": false,
            "description": "desc",
            "displayName": "App",
            "editable": false,
            "iconUrl": "",
            "key": "com.enonic.app.test",
            "local": true,
            "maxSystemVersion": "7.0.0",
            "metaSteps": [],
            "minSystemVersion": "6.0.0",
            "modifiedTime": "",
            "state": "started",
            "url": null,
            "vendorName": "Enonic AS",
            "vendorUrl": "http://enonic.com",
            "version": "",
            "info": null,
            "applicationDependencies": null,
            "contentTypeDependencies": null,
            "createdTime": null,
            "id": ""
        });
    }

    function createSiteConfigurator(input: Input, site: Site, formContext: ContentFormContext): SiteConfigurator {
        var config: ContentInputTypeViewContext = <ContentInputTypeViewContext> {
            formContext: formContext,
            input: input,
            inputConfig: {},
            parentDataPath: PropertyPath.ROOT,
            site: site,
            content: site,
            contentPath: ContentPath.fromString("/test"),
            parentContentPath: ContentPath.ROOT
        }

        return new SiteConfigurator(config);
    }

})
;
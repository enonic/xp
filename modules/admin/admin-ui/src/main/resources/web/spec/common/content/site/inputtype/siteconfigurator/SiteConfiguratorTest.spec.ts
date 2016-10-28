import Input = api.form.Input;
import Site = api.content.site.Site;
import ContentFormContext = api.content.form.ContentFormContext;
import SiteConfigurator = api.content.site.inputtype.siteconfigurator.SiteConfigurator;
import Application = api.application.Application;
import SiteConfigProvider = api.content.site.inputtype.siteconfigurator.SiteConfigProvider;
import SiteConfiguratorComboBox = api.content.site.inputtype.siteconfigurator.SiteConfiguratorComboBox;
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

describe("api.content.site.inputtype.siteconfigurator.SiteConfigurator", function () {

    var input: Input, site: Site, formContext: ContentFormContext, configurator: SiteConfigurator;

    beforeEach(() => {
        input = createInput();
        site = createSite();
        formContext = createFormContext(site);
        configurator = createSiteConfigurator(input, site, formContext);
    });

    describe("test layout process", function () {

        var selectedOption;

        beforeEach((done) => {
            configurator.layout(input, site.getContentData().getPropertyArray("siteConfig")).then(() => {
                selectedOption = configurator["comboBox"]["siteConfiguratorSelectedOptionsView"].createSelectedOption(
                    <api.ui.selector.Option<Application>>{displayValue: createApplication(), value: "com.enonic.app.test"}
                );
                done();
            });
        });

        it("combobox created correctly", function (done) {

            var combobox = new SiteConfiguratorComboBox(input.getOccurrences().getMaximum() ||
                                                        0, new SiteConfigProvider(site.getContentData().getPropertyArray(
                "siteConfig")), this.formContext,
                "com.enonic.app.test");

            expect(ObjectHelper.stringEquals(combobox.getName(), configurator["comboBox"].getName())).toBeTruthy();
            done();

        });

        it("option selected listener is called", function () {

            var comboboxSpy = spyOn(configurator, "saveToSet");

            var event = new SelectedOptionEvent(selectedOption, -1);

            configurator["comboBox"].getComboBox()["selectedOptionsView"]["notifyOptionSelected"](event);

            expect(comboboxSpy).toHaveBeenCalledWith(selectedOption.getOptionView().getSiteConfig(), selectedOption.getIndex());
        });

        it("option moved listener is called", function (done) {

            var comboboxSpy = spyOn(configurator, "saveToSet");

            configurator["comboBox"].getComboBox()["selectedOptionsView"]["notifyOptionMoved"](selectedOption);

            expect(comboboxSpy).toHaveBeenCalledWith(selectedOption.getOptionView().getSiteConfig(), 0);
            done();

        });

        it("option deselected listener is called", function (done) {

            var comboboxSpy = spyOn(configurator["propertyArray"], "remove");

            configurator["comboBox"].getComboBox()["selectedOptionsView"]["notifyOptionDeselected"](selectedOption);

            expect(comboboxSpy).toHaveBeenCalledWith(selectedOption.getIndex());
            done();

        });

    });

    it("getValueType method returns right value type", function () {
        expect(configurator.getValueType() == ValueTypes.DATA).toBeTruthy();
    });

    it("newInitialValue method returns right value", function () {
        expect(configurator.newInitialValue()).toBeNull();
    });

    it("resetBaseValues method resets combobox origin and old values", function (done) {
        configurator.layout(input, site.getContentData().getPropertyArray("siteConfig")).then(() => {
            configurator["comboBox"].setValue("testValue");
            configurator.reset();

            expect(configurator["comboBox"]["originalValue"]).toBe(configurator["comboBox"].getValue());
            expect(configurator["comboBox"]["oldValue"]).toBe(configurator["comboBox"].getValue());
            done();
        });
    });

    it("configurator data changing updates combobox data ", function (done) {

        configurator.layout(input, site.getContentData().getPropertyArray("siteConfig")).then(() => {

            var propertyArray = site.getContentData().getPropertyArray("siteConfig");

            configurator["comboBox"].onValueChanged((a) => {
                expect(a.getOldValue()).toBe("");
                expect(a.getNewValue()).toBe("com.enonic.app.features;");
                done();
            });

            var newSet = propertyArray.addSet();
            newSet.addString("v", "test");
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
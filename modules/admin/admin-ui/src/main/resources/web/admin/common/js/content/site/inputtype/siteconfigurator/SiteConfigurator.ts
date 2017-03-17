module api.content.site.inputtype.siteconfigurator {

    import PropertyTree = api.data.PropertyTree;
    import Property = api.data.Property;
    import PropertyArray = api.data.PropertyArray;
    import PropertySet = api.data.PropertySet;
    import FormView = api.form.FormView;
    import FormValidityChangedEvent = api.form.FormValidityChangedEvent;
    import Value = api.data.Value;
    import ValueType = api.data.ValueType;
    import ValueTypes = api.data.ValueTypes;
    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import Application = api.application.Application;
    import SiteConfig = api.content.site.SiteConfig;
    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;
    import SelectedOptionEvent = api.ui.selector.combobox.SelectedOptionEvent;
    import FocusSwitchEvent = api.ui.FocusSwitchEvent;
    import Promise = Q.Promise;
    import ApplicationKey = api.application.ApplicationKey;
    import ApplicationEvent = api.application.ApplicationEvent;
    import ApplicationEventType = api.application.ApplicationEventType;
    import PrincipalKey = api.security.PrincipalKey;

    export class SiteConfigurator extends api.form.inputtype.support.BaseInputTypeManagingAdd<Application> {

        private context: api.form.inputtype.InputTypeViewContext;

        private readOnly: boolean;

        private comboBox: SiteConfiguratorComboBox;

        private siteConfigProvider: SiteConfigProvider;

        private formContext: api.content.form.ContentFormContext;

        private readOnlyPromise: Promise<void>;

        constructor(config: api.content.form.inputtype.ContentInputTypeViewContext) {
            super('site-configurator');
            this.context = config;
            this.formContext = config.formContext;

            this.readOnlyPromise =
                new api.security.auth.IsAuthenticatedRequest().sendAndParse().then((loginResult: api.security.auth.LoginResult) => {
                    this.readOnly = !loginResult.getPrincipals().some(function (principal: PrincipalKey) {
                        return principal.equals(api.security.RoleKeys.ADMIN) || principal.equals(api.security.RoleKeys.CMS_ADMIN);
                    });
                });
        }

        getValueType(): ValueType {
            return ValueTypes.DATA;
        }

        newInitialValue(): Value {
            return null;
        }

        layout(input: api.form.Input, propertyArray: PropertyArray): wemQ.Promise<void> {

            super.layout(input, propertyArray);

            let deferred = wemQ.defer<void>();

            this.siteConfigProvider = new SiteConfigProvider(propertyArray);
            // ignore changes made to property by siteConfigProvider
            this.siteConfigProvider.onBeforePropertyChanged(() => this.ignorePropertyChange = true);
            this.siteConfigProvider.onAfterPropertyChanged(() => this.ignorePropertyChange = false);

            this.comboBox = this.createComboBox(input, this.siteConfigProvider);
            if (this.readOnlyPromise.isFulfilled()) {
                this.comboBox.setReadOnly(this.readOnly);
            } else {
                this.readOnlyPromise.then(() => {
                    this.comboBox.setReadOnly(this.readOnly);
                });
            }

            this.appendChild(this.comboBox);

            this.comboBox.render().then(() => {
                this.setLayoutInProgress(false);
                deferred.resolve(null);
            });
            return deferred.promise;
        }

        update(propertyArray: api.data.PropertyArray, unchangedOnly?: boolean): Q.Promise<void> {
            return super.update(propertyArray, unchangedOnly).then(() => {
                this.siteConfigProvider.setPropertyArray(propertyArray);

                if (!unchangedOnly || !this.comboBox.isDirty()) {
                    this.comboBox.setValue(this.getValueFromPropertyArray(propertyArray));
                }
                return null;
            });
        }

        reset() {
            this.comboBox.resetBaseValues();
        }

        private saveToSet(siteConfig: SiteConfig, index: number) {

            let propertySet = this.getPropertyArray().get(index).getPropertySet();
            if (!propertySet) {
                propertySet = this.getPropertyArray().addSet();
            }

            let config = siteConfig.getConfig();
            let appKey = siteConfig.getApplicationKey();

            propertySet.setStringByPath('applicationKey', appKey.toString());
            propertySet.setPropertySetByPath('config', config);
        }

        protected getValueFromPropertyArray(propertyArray: api.data.PropertyArray): string {
            return propertyArray.getProperties().map((property) => {
                if (property.hasNonNullValue()) {
                    let siteConfig = SiteConfig.create().fromData(property.getPropertySet()).build();
                    return siteConfig.getApplicationKey().toString();
                }
            }).join(';');
        }

        private createComboBox(input: api.form.Input, siteConfigProvider: SiteConfigProvider): SiteConfiguratorComboBox {

            const value = this.getValueFromPropertyArray(this.getPropertyArray());
            const siteConfigFormsToDisplay = value.split(';');
            const maximum = input.getOccurrences().getMaximum() || 0;
            const comboBox = new SiteConfiguratorComboBox(maximum, siteConfigProvider, this.formContext, value);

            const forcedValidate = () => {
                this.ignorePropertyChange = false;
                this.validate(false);
            };
            const saveAndForceValidate = (selectedOption: SelectedOption<Application>) => {
                const view: SiteConfiguratorSelectedOptionView = <SiteConfiguratorSelectedOptionView>selectedOption.getOptionView();
                this.saveToSet(view.getSiteConfig(), selectedOption.getIndex());
                forcedValidate();
            };

            comboBox.onOptionDeselected((event: SelectedOptionEvent<Application>) => {
                this.ignorePropertyChange = true;

                this.getPropertyArray().remove(event.getSelectedOption().getIndex());

                forcedValidate();
            });

            comboBox.onOptionSelected((event: SelectedOptionEvent<Application>) => {
                this.fireFocusSwitchEvent(event);

                this.ignorePropertyChange = true;

                const selectedOption = event.getSelectedOption();
                const key = selectedOption.getOption().displayValue.getApplicationKey();
                if (key) {
                    saveAndForceValidate(selectedOption);
                }
            });

            comboBox.onOptionMoved((selectedOption: SelectedOption<Application>) => {
                this.ignorePropertyChange = true;

                saveAndForceValidate(selectedOption);
            });

            comboBox.onSiteConfigFormDisplayed((applicationKey: ApplicationKey, formView: FormView) => {
                let indexToRemove = siteConfigFormsToDisplay.indexOf(applicationKey.toString());
                if (indexToRemove !== -1) {
                    siteConfigFormsToDisplay.splice(indexToRemove, 1);
                }

                formView.onValidityChanged((event: FormValidityChangedEvent) => {
                    this.validate(false);
                });

                this.validate(false);
            });

            let handleAppEvent = (view: SiteConfiguratorSelectedOptionView, hasUninstalledClass: boolean, hasStoppedClass) => {
                if (view) {
                    view.toggleClass('stopped', hasStoppedClass);
                    view.toggleClass('uninstalled', hasUninstalledClass);
                }
            };

            ApplicationEvent.on((event: ApplicationEvent) => {
                if (ApplicationEventType.STOPPED === event.getEventType()) {
                    handleAppEvent(this.getMatchedOption(comboBox, event), false, true);
                } else if (ApplicationEventType.STARTED === event.getEventType()) {
                    let view = this.getMatchedOption(comboBox, event);
                    handleAppEvent(view, false, false);
                    if (view && !!view.getOption().empty) {
                        view.removeClass('empty');
                    }
                } else if (ApplicationEventType.UNINSTALLED === event.getEventType()) {
                    handleAppEvent(this.getMatchedOption(comboBox, event), true, false);
                }
            });

            return comboBox;
        }

        private getMatchedOption(combobox: SiteConfiguratorComboBox, event: ApplicationEvent): SiteConfiguratorSelectedOptionView {
            let result;
            combobox.getSelectedOptionViews().some((view: SiteConfiguratorSelectedOptionView) => {
                if (view.getApplication() && view.getApplication().getApplicationKey().equals(event.getApplicationKey())) {
                    result = view;
                    return true;
                }
            });
            return result;
        }

        displayValidationErrors(value: boolean) {
            this.comboBox.getSelectedOptionViews().forEach((view: SiteConfiguratorSelectedOptionView) => {
                view.getFormView().displayValidationErrors(value);
            });
        }

        protected getNumberOfValids(): number {
            return this.comboBox.countSelected();
        }

        validate(silent: boolean = true): api.form.inputtype.InputValidationRecording {
            let recording = new api.form.inputtype.InputValidationRecording();

            this.comboBox.getSelectedOptionViews().forEach((view: SiteConfiguratorSelectedOptionView) => {

                let validationRecording = view.getFormView().validate(true);
                if (!validationRecording.isMinimumOccurrencesValid()) {
                    recording.setBreaksMinimumOccurrences(true);
                }
                if (!validationRecording.isMaximumOccurrencesValid()) {
                    recording.setBreaksMaximumOccurrences(true);
                }
            });

            return super.validate(silent, recording);
        }

        giveFocus(): boolean {
            if (this.comboBox.maximumOccurrencesReached()) {
                return false;
            }
            return this.comboBox.giveFocus();
        }

    }

    api.form.inputtype.InputTypeManager.register(new api.Class('SiteConfigurator', SiteConfigurator));
}

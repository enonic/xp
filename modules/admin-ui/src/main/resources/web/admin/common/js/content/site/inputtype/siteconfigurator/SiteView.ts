module api.content.site.inputtype.siteconfigurator {

    import AEl = api.dom.AEl;
    import PropertyTree = api.data.PropertyTree;
    import PropertySet = api.data.PropertySet;
    import Option = api.ui.selector.Option;
    import FormView = api.form.FormView;
    import FormContextBuilder = api.form.FormContextBuilder;
    import Application = api.application.Application;
    import ApplicationKey = api.application.ApplicationKey;
    import SiteConfig = api.content.site.SiteConfig;
    import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;
    import LoadedDataEvent = api.util.loader.event.LoadedDataEvent;

    export class SiteView extends api.dom.DivEl {

        private application: Application;

        private formView: FormView;

        private siteConfig: SiteConfig;

        private removeClickedListeners: {(event: MouseEvent): void;}[];

        private collapseClickedListeners: {(event: MouseEvent): void;}[];

        private siteConfigFormDisplayedListeners: {(applicationKey: ApplicationKey) : void}[] = [];

        constructor(application: Application, siteConfig: SiteConfig, formContext: api.content.form.ContentFormContext) {
            super("site-view");
            debugger;

            this.removeClickedListeners = [];
            this.collapseClickedListeners = [];

            this.application = application;
            this.siteConfig = siteConfig;

            var header = new api.dom.DivEl('header');

            var namesAndIconView = new api.app.NamesAndIconView(new api.app.NamesAndIconViewBuilder().
                setSize(api.app.NamesAndIconViewSize.large)).
                setMainName(this.application.getDisplayName()).
                setSubName(this.application.getName() + "-" + this.application.getVersion()).
                setIconClass("icon-xlarge icon-puzzle");

            header.appendChild(namesAndIconView);

            var removeButton = new api.dom.AEl("remove-button icon-close");
            removeButton.onClicked((event: MouseEvent) => {
                this.notifyRemoveClicked(event);
            });
            header.appendChild(removeButton);

            this.appendChild(header);

            this.formView = this.createFormView(formContext);

            if (!this.siteConfig.getConfig().isEmpty()) {

                header.appendChild(this.createCollapseButton());

                this.appendChild(this.formView);
            }

        }

        private createCollapseButton(): api.dom.AEl {
            var collapseButton = new api.dom.AEl('collapse-button');
            collapseButton.setHtml('Collapse');

            collapseButton.onClicked((event: MouseEvent) => {
                var isFormVisible = this.formView.isVisible();

                collapseButton.setHtml(isFormVisible ? 'Expand' : 'Collapse');
                this.toggleClass('collapsed', isFormVisible);
                this.formView.setVisible(!isFormVisible);

                this.notifyCollapseClicked(event);
            });

            return collapseButton;
        }

        private createFormView(formContext: api.content.form.ContentFormContext): FormView {
            var formView = new FormView(formContext, this.application.getForm(), this.siteConfig.getConfig());
            formView.addClass("site-form");
            formView.layout().then(() => {
                this.notifySiteConfigFormDisplayed(this.application.getApplicationKey());
                formView.onEditContentRequest((content: api.content.ContentSummary) => {
                    new api.content.EditContentEvent([content]).fire();
                });
            }).catch((reason: any) => {
                api.DefaultErrorHandler.handle(reason);
            }).done();

            return formView;
        }

        getApplication(): Application {
            return this.application;
        }

        getSiteConfig(): SiteConfig {
            return this.siteConfig;
        }

        getFormView(): FormView {
            return this.formView;
        }

        onRemoveClicked(listener: (event: MouseEvent) => void) {
            this.removeClickedListeners.push(listener);
        }

        unRemoveClicked(listener: (event: MouseEvent) => void) {
            this.removeClickedListeners = this.removeClickedListeners.filter((curr) => {
                return listener != curr;
            })
        }

        private notifyRemoveClicked(event: MouseEvent) {
            this.removeClickedListeners.forEach((listener) => {
                listener(event);
            })
        }

        onCollapseClicked(listener: (event: MouseEvent) => void) {
            this.collapseClickedListeners.push(listener);
        }

        unCollapseClicked(listener: (event: MouseEvent) => void) {
            this.collapseClickedListeners = this.collapseClickedListeners.filter((curr) => {
                return listener != curr;
            })
        }

        private notifyCollapseClicked(event: MouseEvent) {
            this.collapseClickedListeners.forEach((listener) => {
                listener(event);
            })
        }

        onSiteConfigFormDisplayed(listener: {(applicationKey: ApplicationKey): void;}) {
            this.siteConfigFormDisplayedListeners.push(listener);
        }

        unSiteConfigFormDisplayed(listener: {(applicationKey: ApplicationKey): void;}) {
            this.siteConfigFormDisplayedListeners =
            this.siteConfigFormDisplayedListeners.filter((curr) => (curr != listener));
        }

        private notifySiteConfigFormDisplayed(applicationKey: ApplicationKey) {
            this.siteConfigFormDisplayedListeners.forEach((listener) => listener(applicationKey));
        }
    }
}
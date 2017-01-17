module api.util.htmlarea.dialog {

    import MacroDescriptor = api.macro.MacroDescriptor;
    import MacroPreview = api.macro.MacroPreview;
    import FormView = api.form.FormView;
    import DockedPanel = api.ui.panel.DockedPanel;
    import Panel = api.ui.panel.Panel;

    import PropertySet = api.data.PropertySet;

    export class MacroDockedPanel extends DockedPanel {

        private static CONFIGURATION_TAB_NAME: string = 'Configuration';
        private static PREVIEW_TAB_NAME: string = 'Preview';
        private static MACRO_FORM_INCOMPLETE_MES: string = 'Macro configuration is not complete';
        private static PREVIEW_LOAD_ERROR_MESSAGE: string = 'An error occurred while loading preview';

        private configPanel: Panel;
        private previewPanel: Panel;

        private content: api.content.ContentSummary;
        private macroDescriptor: MacroDescriptor;
        private previewResolved: boolean = false;
        private macroPreview: MacroPreview;
        private data: PropertySet;
        private previewPanelLoadMask: api.ui.mask.LoadMask;
        private configPanelLoadMask: api.ui.mask.LoadMask;

        private formValueChangedHandler: () => void;

        private panelRenderedListeners: {(): void}[] = [];

        constructor() {
            super();

            this.addItem(MacroDockedPanel.CONFIGURATION_TAB_NAME, true, this.createConfigurationPanel());
            this.addItem(MacroDockedPanel.PREVIEW_TAB_NAME, true, this.createPreviewPanel());

            this.previewPanelLoadMask = new api.ui.mask.LoadMask(this.previewPanel);
            this.configPanelLoadMask = new api.ui.mask.LoadMask(this.configPanel);
            this.appendChild(this.previewPanelLoadMask);
            this.appendChild(this.configPanelLoadMask);

            this.handleConfigPanelShowEvent();
            this.handlePreviewPanelShowEvent();

            this.formValueChangedHandler = () => {
                this.previewResolved = false;
            };
        }

        public setContent(content: api.content.ContentSummary) {
            this.content = content;
        }

        private createConfigurationPanel(): Panel {
            return this.configPanel = new Panel('macro-config-panel');
        }

        private createPreviewPanel(): Panel {
            return this.previewPanel = new Panel('macro-preview-panel');
        }

        private handlePreviewPanelShowEvent() {
            this.previewPanel.onShown(() => {
                if (this.validateMacroForm()) {
                    if (!!this.macroDescriptor && !this.previewResolved) {
                        this.previewPanel.removeChildren();
                        this.previewPanelLoadMask.show();
                        this.fetchPreview().then((macroPreview: MacroPreview) => {
                            this.previewResolved = true;
                            this.macroPreview = macroPreview;
                            this.renderPreview(macroPreview);
                        }).catch((reason: any) => {
                            api.DefaultErrorHandler.handle(reason);
                            this.renderPreviewWithMessage(MacroDockedPanel.PREVIEW_LOAD_ERROR_MESSAGE);
                        }).finally(() => {
                            this.previewPanelLoadMask.hide();
                        });
                    } else {
                        this.notifyPanelRendered();
                    }
                } else {
                    this.renderPreviewWithMessage(MacroDockedPanel.MACRO_FORM_INCOMPLETE_MES);
                }
            });
        }

        private handleConfigPanelShowEvent() {
            this.configPanel.onShown(() => {
                this.notifyPanelRendered();
            });
        }

        private fetchPreview(): wemQ.Promise<MacroPreview> {
            return new api.macro.resource.GetPreviewRequest(
                new api.data.PropertyTree(this.data),
                this.macroDescriptor.getKey(),
                this.content.getPath()).
                sendAndParse();
        }

        private fetchMacroString(): wemQ.Promise<string> {
            return new api.macro.resource.GetPreviewStringRequest(new api.data.PropertyTree(this.data),
                this.macroDescriptor.getKey()).sendAndParse();
        }

        public getMacroPreviewString(): wemQ.Promise<string> {
            let deferred = wemQ.defer<string>();
            if (this.previewResolved) {
                deferred.resolve(this.macroPreview.getMacroString());
            } else {
                this.configPanelLoadMask.show();
                this.fetchMacroString().then((macroString: string) => {
                    deferred.resolve(macroString);
                }).catch((reason: any) => {
                    deferred.reject(reason);
                }).finally(() => {
                    this.configPanelLoadMask.hide();
                });
            }

            return deferred.promise;
        }

        private renderPreviewWithMessage(message: string) {
            this.previewPanel.removeChildren();
            let appendMe = new api.dom.DivEl('preview-message');
            appendMe.setHtml(message);
            this.previewPanel.appendChild(appendMe);
        }

        private renderPreview(macroPreview: MacroPreview) {
            // render in iframe if there are scripts to be included for preview rendering
            if (macroPreview.getPageContributions().hasAtLeastOneScript()) {
                this.previewPanel.appendChild(this.makePreviewFrame(macroPreview));
            } else {
                let appendMe = new api.dom.DivEl('preview-content');
                appendMe.setHtml(macroPreview.getHtml(), false);
                this.previewPanel.appendChild(appendMe);
                this.notifyPanelRendered();
            }
        }

        private makePreviewFrame(macroPreview: MacroPreview): MacroPreviewFrame {
            const previewFrame = new MacroPreviewFrame(macroPreview);
            const previewFrameRenderedHandler: () => void = () => {
                    this.notifyPanelRendered();
                };

            previewFrame.onPreviewRendered(previewFrameRenderedHandler);
            previewFrame.onRemoved(() => {
                previewFrame.unPreviewRendered(previewFrameRenderedHandler);
            });

            return previewFrame;
        }

        public validateMacroForm(): boolean {
            let isValid = true;
            let form = <FormView>(this.configPanel.getFirstChild());

            if (form) {
                isValid = form.validate(false).isValid();
                form.displayValidationErrors(!isValid);
            }
            return isValid;
        }

        public setMacroDescriptor(macroDescriptor: MacroDescriptor) {
            this.macroDescriptor = macroDescriptor;
            this.previewResolved = false;

            this.initPropertySetForDescriptor();
            this.showDescriptorConfigView(macroDescriptor);
        }

        private showDescriptorConfigView(macroDescriptor: MacroDescriptor) {
            this.selectPanel(this.configPanel);

            if (!!macroDescriptor) {
                let formView: FormView = new FormView(api.content.form.ContentFormContext.
                        create().
                        setPersistedContent(<api.content.Content>this.content).
                        build(),
                    macroDescriptor.getForm(), this.data);

                this.renderConfigView(formView);
            }
        }

        private initPropertySetForDescriptor() {
            if (!!this.data) {
                this.data.unChanged(this.formValueChangedHandler);
            }
            this.data = new PropertySet();
            this.data.onChanged(this.formValueChangedHandler);
        }

        private renderConfigView(formView: FormView) {
            this.configPanel.removeChildren();

            formView.layout().then(() => {
                this.configPanel.appendChild(formView);
                api.ui.responsive.ResponsiveManager.fireResizeEvent();
            });
        }

        onPanelRendered(listener: () => void) {
            this.panelRenderedListeners.push(listener);
        }

        unPanelRendered(listener: () => void) {
            this.panelRenderedListeners = this.panelRenderedListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        private notifyPanelRendered() {
            this.panelRenderedListeners.forEach((listener) => {
                listener();
            });
        }
    }

    export class MacroPreviewFrame extends api.dom.IFrameEl {

        private id: string = 'macro-preview-frame-id';

        private macroPreview: MacroPreview;

        private debouncedResizeHandler: () => void = api.util.AppHelper.debounce(() => {
            this.adjustFrameHeight();
        }, 500, false);

        private previewRenderedListeners: {(): void}[] = [];

        constructor(macroPreview: MacroPreview) {
            super('preview-iframe');
            this.setId(this.id);
            this.macroPreview = macroPreview;

            this.initFrameContent(macroPreview);
        }

        private initFrameContent(macroPreview: MacroPreview) {
            this.onLoaded(() => {

                let doc = this.getHTMLElement()['contentWindow'] || this.getHTMLElement()['contentDocument'];
                if (doc.document) {
                    doc = doc.document;
                }

                doc.open();
                doc.write(this.makeContentForPreviewFrame(macroPreview));
                doc.close();

                if (this.isYoutubePreview()) {
                    doc.body.style.marginRight = 4;
                }

                this.debouncedResizeHandler();
                this.adjustFrameHeightOnContentsUpdate();
            });
        }

        private isYoutubePreview(): boolean {
            return this.macroPreview.getMacroString().indexOf('[youtube') == 0;
        }

        private isInstagramPreview(): boolean {
            return this.macroPreview.getMacroString().indexOf('[instagram') == 0;
        }

        private adjustFrameHeightOnContentsUpdate() {
            let frameWindow = this.getHTMLElement()['contentWindow'];
            if (frameWindow) {
                let observer = new MutationObserver(this.debouncedResizeHandler);
                let config = {attributes: true, childList: true, characterData: true};

                observer.observe(frameWindow.document.body, config);
            }
        }

        private adjustFrameHeight() {
            try {
                let frameWindow = this.getHTMLElement()['contentWindow'] || this.getHTMLElement()['contentDocument'];
                let scrollHeight = frameWindow.document.body.scrollHeight;
                let maxFrameHeight = this.getMaxFrameHeight();
                this.getEl().setHeightPx(scrollHeight > 150
                    ? scrollHeight > maxFrameHeight ? maxFrameHeight : scrollHeight + (this.isInstagramPreview() ? 18 : 0)
                    : wemjq('#' + this.id).contents().find('body').outerHeight());
                this.notifyPreviewRendered();
            } catch (error) { /* empty*/ }
        }

        private getMaxFrameHeight(): number {
            return wemjq(window).height() - 250;
        }

        private makeContentForPreviewFrame(macroPreview: MacroPreview): string {
            let result = '';
            macroPreview.getPageContributions().getHeadBegin().forEach(script => result += script);
            macroPreview.getPageContributions().getHeadEnd().forEach(script => result += script);
            macroPreview.getPageContributions().getBodyBegin().forEach(script => result += script);
            result += macroPreview.getHtml();
            macroPreview.getPageContributions().getBodyEnd().forEach(script => result += script);
            return result;
        }

        onPreviewRendered(listener: () => void) {
            this.previewRenderedListeners.push(listener);
        }

        unPreviewRendered(listener: () => void) {
            this.previewRenderedListeners = this.previewRenderedListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        private notifyPreviewRendered() {
            this.previewRenderedListeners.forEach((listener) => {
                listener();
            });
        }
    }
}

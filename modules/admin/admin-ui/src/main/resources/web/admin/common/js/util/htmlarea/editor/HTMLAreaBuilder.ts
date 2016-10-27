module api.util.htmlarea.editor {

    import CreateHtmlAreaDialogEvent = api.util.htmlarea.dialog.CreateHtmlAreaDialogEvent;
    import ApplicationKey = api.application.ApplicationKey

    export class HTMLAreaBuilder {

        private content: api.content.ContentSummary; // used for image dialog
        private contentPath: api.content.ContentPath; // used for macro dialog
        private applicationKeys: ApplicationKey[]; // used for macro dialog

        private assetsUri: string;
        private selector: string;
        private focusHandler: (e) => void;
        private blurHandler: (e) => void;
        private keydownHandler: (e) => void;
        private keyupHandler: (e) => void;
        private nodeChangeHandler: (e) => void;
        private createDialogListeners: {(event: CreateHtmlAreaDialogEvent): void}[] = [];
        private inline: boolean = false;
        private fixedToolbarContainer: string;
        private convertUrls: boolean = false;
        private hasActiveDialog: boolean = false;
        private customToolConfig: any;

        private tools: string = "styleselect | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | charmap anchor image macro link unlink | table | pastetext";

        setAssetsUri(assetsUri: string): HTMLAreaBuilder {
            this.assetsUri = assetsUri;
            return this;
        }

        setSelector(selector: string): HTMLAreaBuilder {
            this.selector = selector;
            return this;
        }

        onCreateDialog(listener: (event: CreateHtmlAreaDialogEvent) => void) {
            this.createDialogListeners.push(listener);
            return this;
        }

        unCreateDialog(listener: (event: CreateHtmlAreaDialogEvent) => void) {
            this.createDialogListeners = this.createDialogListeners.filter((curr) => {
                return curr !== listener;
            });
            return this;
        }

        private notifyCreateDialog(event: CreateHtmlAreaDialogEvent) {
            this.createDialogListeners.forEach((listener) => {
                listener(event);
            })
        }

        setFocusHandler(focusHandler: (e) => void): HTMLAreaBuilder {
            this.focusHandler = focusHandler;
            return this;
        }

        setBlurHandler(blurHandler: (e) => void): HTMLAreaBuilder {
            this.blurHandler = blurHandler;
            return this;
        }

        setKeydownHandler(keydownHandler: (e) => void): HTMLAreaBuilder {
            this.keydownHandler = keydownHandler;
            return this;
        }

        setKeyupHandler(keyupHandler: (e) => void): HTMLAreaBuilder {
            this.keyupHandler = keyupHandler;
            return this;
        }

        setNodeChangeHandler(nodeChangeHandler: (e) => void): HTMLAreaBuilder {
            this.nodeChangeHandler = nodeChangeHandler;
            return this;
        }

        setInline(inline: boolean): HTMLAreaBuilder {
            this.inline = inline;
            return this;
        }

        setFixedToolbarContainer(fixedToolbarContainer: string): HTMLAreaBuilder {
            this.fixedToolbarContainer = fixedToolbarContainer;
            return this;
        }

        setContent(content: api.content.ContentSummary): HTMLAreaBuilder {
            this.content = content;
            return this;
        }

        setContentPath(contentPath: api.content.ContentPath): HTMLAreaBuilder {
            this.contentPath = contentPath;
            return this;
        }

        setConvertUrls(convertUrls: boolean): HTMLAreaBuilder {
            this.convertUrls = convertUrls;
            return this;
        }

        setApplicationKeys(applicationKeys: ApplicationKey[]): HTMLAreaBuilder {
            this.applicationKeys = applicationKeys;
            return this;
        }

        private excludeTools(tools: any[]) {
            var strTools = this.tools;
            tools.forEach((toolStr: any) => {
                toolStr.value.split(" ").forEach((tool: string) => {
                    if (tool == "*") {
                        strTools = "";
                    }
                    else {
                        strTools = strTools.replace(tool, "");
                    }
                });
            });
            this.tools = strTools;
        }

        private includeTools(tools: any[]) {
            tools.forEach((tool: any) => {
                this.includeTool(tool.value);
            });
        }

        private includeTool(tool: string) {
            this.tools += " " + tool;
        }

        setTools(tools: any): HTMLAreaBuilder {
            this.customToolConfig = tools;

            if (tools["exclude"] && tools["exclude"] instanceof Array) {
                this.excludeTools(tools["exclude"]);
            }
            if (tools["include"] && tools["include"] instanceof Array) {
                this.includeTools(tools["include"]);
            }

            return this;
        }

        private checkRequiredFieldsAreSet() {
            if (!this.assetsUri || !this.selector || !this.content) {
                throw new Error("some required fields are missing for tinymce editor");
            }
        }

        public createEditor(): wemQ.Promise<HtmlAreaEditor> {
            this.checkRequiredFieldsAreSet();

            if (this.inline && !this.isToolExcluded("code")) {
                this.includeTool("code");
            }

            var deferred = wemQ.defer<HtmlAreaEditor>();

            tinymce.init({
                selector: this.selector,
                document_base_url: this.assetsUri + '/common/lib/tinymce/',
                skin_url: this.assetsUri + '/common/lib/tinymce/skins/lightgray',
                content_css: this.assetsUri + '/common/styles/api/util/htmlarea/html-editor.css',
                theme_url: 'modern',
                inline: this.inline,
                fixed_toolbar_container: this.fixedToolbarContainer,
                convert_urls: this.convertUrls,

                toolbar: [
                    this.tools
                ],

                formats: {
                    alignleft: [
                        {
                            selector: 'img,figure,p,h1,h2,h3,h4,h5,h6,td,th,tr,div,ul,ol,li',
                            styles: {textAlign: 'left'},
                            inline: 'span'
                        },
                        {selector: 'table', collapsed: false, styles: {'float': 'left'}}
                    ],
                    aligncenter: [
                        {
                            selector: 'img,figure,p,h1,h2,h3,h4,h5,h6,td,th,tr,div,ul,ol,li',
                            styles: {textAlign: 'center'},
                            inline: 'span'
                        },
                        {selector: 'table', collapsed: false, styles: {marginLeft: 'auto', marginRight: 'auto'}}
                    ],
                    alignright: [
                        {
                            selector: 'img,figure,p,h1,h2,h3,h4,h5,h6,td,th,tr,div,ul,ol,li',
                            styles: {textAlign: 'right'},
                            inline: 'span'
                        },
                        {selector: 'table', collapsed: false, styles: {'float': 'right'}}
                    ],
                    alignjustify: [
                        {
                            selector: 'img,figure,p,h1,h2,h3,h4,h5,h6,td,th,tr,div,ul,ol,li',
                            styles: {textAlign: 'justify'},
                            inline: 'span'
                        }
                    ]
                },
                menubar: false,
                statusbar: true,
                paste_as_text: true,
                browser_spellcheck: true,
                verify_html: false,
                verify_css_classes: false,

                plugins: ['directionality', 'hr', 'preview', 'searchreplace', 'textcolor', 'visualchars', 'visualblocks', 
                            'autoresize', 'table', 'fullscreen', 'charmap', 'code', 'paste'],
                external_plugins: {
                    "link": this.assetsUri + "/common/js/util/htmlarea/plugins/link.js",
                    "anchor": this.assetsUri + "/common/js/util/htmlarea/plugins/anchor.js",
                    "image": this.assetsUri + "/common/js/util/htmlarea/plugins/image.js",
                    "macro": this.assetsUri + "/common/js/util/htmlarea/plugins/macro.js"
                },
                object_resizing: "table",
                autoresize_min_height: 100,
                autoresize_bottom_margin: 0,

                setup: (editor) => {
                    editor.addCommand("openLinkDialog", this.notifyLinkDialog, this);
                    editor.addCommand("openAnchorDialog", this.notifyAnchorDialog, this);
                    editor.addCommand("openImageDialog", this.notifyImageDialog, this) ;
                    editor.addCommand("openMacroDialog", this.notifyMacroDialog, this);
                    editor.on('NodeChange', (e) => {
                        if (!!this.nodeChangeHandler) {
                            this.nodeChangeHandler(e);
                        }
                    });
                    editor.on('keyup', (e) => {
                        if (!!this.keyupHandler) {
                            this.keyupHandler(e);
                        }
                    });
                    editor.on('focus', (e) => {
                        if (!!this.focusHandler) {
                            this.focusHandler(e);
                        }
                    });
                    editor.on('blur', (e) => {
                        if (this.hasActiveDialog) {
                            e.stopImmediatePropagation();
                            this.hasActiveDialog = false;
                        }
                        if (!!this.blurHandler) {
                            this.blurHandler(e);
                        }
                    });
                    editor.on('keydown', (e) => {
                        if (e.keyCode == 9 && !e.altKey && !e.ctrlKey) { // tab pressed
                            editor.execCommand(e.shiftKey ? 'Outdent' : 'Indent');
                            e.preventDefault();
                        }
                        else if (e.keyCode == 46 || e.keyCode == 8) { // DELETE
                            var selectedNode = editor.selection.getRng().startContainer;
                            if (/^(FIGURE)$/.test(selectedNode.nodeName)) {
                                var previousEl = selectedNode.previousSibling;
                                e.preventDefault();
                                selectedNode.remove();
                                if (previousEl) {
                                    editor.selection.setNode(previousEl);
                                }
                                else {
                                    editor.focus();
                                }
                            }
                        }

                        if (!!this.keydownHandler) {
                            this.keydownHandler(e);
                        }
                    });

                    var dragParentElement;
                    editor.on('dragstart', (e) => {
                        dragParentElement = e.target.parentElement || e.target.parentNode;
                    });

                    editor.on('drop', (e) => {
                        if (dragParentElement) {
                            // prevent browser from handling the drop
                            e.preventDefault();

                            e.target.appendChild(dragParentElement);
                            dragParentElement = undefined;
                        }
                    });

                },
                init_instance_callback: (editor) => {
                    deferred.resolve(editor);
                }
            });
            return deferred.promise;
        }

        private notifyLinkDialog(config) {
            let event = CreateHtmlAreaDialogEvent.create().
                setConfig(config).
                setType(api.util.htmlarea.dialog.HtmlAreaDialogType.LINK).
                setContent(this.content).
                build();
            this.publishCreateDialogEvent(event);
        }

        private notifyImageDialog(config) {
            let event = CreateHtmlAreaDialogEvent.create().
                setConfig(config).setType(api.util.htmlarea.dialog.HtmlAreaDialogType.IMAGE).setContent(this.content).
                build();
            this.publishCreateDialogEvent(event);
        }

        private notifyAnchorDialog(config) {
            let event = CreateHtmlAreaDialogEvent.create().
                setConfig(config).
                setType(api.util.htmlarea.dialog.HtmlAreaDialogType.ANCHOR).
                build();
            this.publishCreateDialogEvent(event);
        }

        private notifyMacroDialog(config) {
            let event = CreateHtmlAreaDialogEvent.create().
                setConfig(config).
                setType(api.util.htmlarea.dialog.HtmlAreaDialogType.MACRO).setContentPath(this.contentPath).setApplicationKeys(
                this.applicationKeys).
                setType(api.util.htmlarea.dialog.HtmlAreaDialogType.MACRO).
                setContent(this.content).
                setApplicationKeys(this.applicationKeys).
                build();
            this.publishCreateDialogEvent(event);
        }

        private publishCreateDialogEvent(event: CreateHtmlAreaDialogEvent) {
            this.hasActiveDialog = true;
            this.notifyCreateDialog(event);
            event.fire();
        }

        private isToolExcluded(tool: string): boolean {
            if (!this.customToolConfig || !this.customToolConfig["exclude"]) {
                return false;
            }
            return this.customToolConfig["exclude"].indexOf(tool) > -1;
        }
    }
}

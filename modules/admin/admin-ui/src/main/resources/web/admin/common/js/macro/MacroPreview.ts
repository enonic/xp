module api.macro {

    export class MacroPreview implements api.Equitable {

        private html: string;

        private macroString: string;

        private pageContributions: PageContributions;

        constructor(builder: MacroPreviewBuilder) {
            this.html = builder.html;
            this.macroString = builder.macroString;
            this.pageContributions = builder.pageContributions;
        }

        getHtml(): string {
            return this.html;
        }

        getMacroString(): string {
            return this.macroString;
        }

        getPageContributions(): PageContributions {
            return this.pageContributions;
        }

        static create(): MacroPreviewBuilder {
            return new MacroPreviewBuilder();
        }

        equals(o: api.Equitable): boolean {
            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, MacroPreview)) {
                return false;
            }

            let other = <MacroPreview>o;

            if (this.html != other.html) {
                return false;
            }

            if (this.macroString != other.macroString) {
                return false;
            }

            if (!api.ObjectHelper.equals(this.pageContributions, other.pageContributions)) {
                return false;
            }

            return true;
        }
    }

    export class MacroPreviewBuilder {

        html: string;

        macroString: string;

        pageContributions: PageContributions;

        fromJson(json: api.macro.resource.MacroPreviewJson) {
            this.html = json.html;
            this.macroString = json.macro;
            this.pageContributions = PageContributions.create().fromJson(json.pageContributions).build();
            return this;
        }

        setHtml(html: string): MacroPreviewBuilder {
            this.html = html;
            return this;
        }

        setMacroString(macroString: string): MacroPreviewBuilder {
            this.macroString = macroString;
            return this;
        }

        setPageContributions(pageContributions: PageContributions): MacroPreviewBuilder {
            this.pageContributions = pageContributions;
            return this;
        }

        build(): MacroPreview {
            return new MacroPreview(this);
        }
    }
}

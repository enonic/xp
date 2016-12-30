module api.form {

    /**
     * A parent for [[FormItemSet]] and [[FormOptionSet]].
     */
    export class FormSet extends FormItem {

        private label: string;

        private occurrences: Occurrences;

        private helpText: string;

        private helpTextIsOn: boolean = false;

        constructor(formSetJson: api.form.json.FormSetJson) {
            super(formSetJson.name);
            this.label = formSetJson.label;
            this.occurrences = Occurrences.fromJson(formSetJson.occurrences);
            this.helpText = formSetJson.helpText;
        }

        getLabel(): string {
            return this.label;
        }

        getHelpText(): string {
            return this.helpText;
        }

        getOccurrences(): Occurrences {
            return this.occurrences;
        }

        isHelpTextOn(): boolean {
            return this.helpTextIsOn;
        }

        toggleHelpText(show?: boolean) {
            this.helpTextIsOn = show;
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, FormSet)) {
                return false;
            }

            if (!super.equals(o)) {
                return false;
            }

            let other = <FormSet>o;

            if (!api.ObjectHelper.stringEquals(this.label, other.label)) {
                return false;
            }

            if (!api.ObjectHelper.equals(this.occurrences, other.occurrences)) {
                return false;
            }

            if (!api.ObjectHelper.stringEquals(this.helpText, other.helpText)) {
                return false;
            }

            return true;
        }

    }
}
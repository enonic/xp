module app.wizard {

    import Content = api.content.Content;
    import ComboBox = api.ui.selector.combobox.ComboBox;
    import FormItemBuilder = api.ui.form.FormItemBuilder;
    import FormItem = api.ui.form.FormItem;
    import PrincipalComboBox = api.ui.security.PrincipalComboBox;

    export class ContentSettingsModel implements api.Equitable {

        private propertyChangedListeners: {(event: api.PropertyChangedEvent):void}[] = [];

        private owner: string;
        private language: string;

        public static PROPERTY_OWNER: string = 'owner';
        public static PROPERTY_LANG: string = 'language';

        constructor(content: Content) {
            //this.language = content.getLanguage();
            this.owner = content.getOwner();
        }

        getOwner(): string {
            return this.owner;
        }

        setOwner(owner: string, silent?: boolean): ContentSettingsModel {
            if (!silent) {
                this.notifyPropertyChanged(new api.PropertyChangedEvent(ContentSettingsModel.PROPERTY_OWNER, this.owner, owner));
            }
            this.owner = owner;
            return this;
        }

        getLanguage(): string {
            return this.language;
        }

        setLanguage(lang: string, silent?: boolean): ContentSettingsModel {
            if (!silent) {
                this.notifyPropertyChanged(new api.PropertyChangedEvent(ContentSettingsModel.PROPERTY_LANG, this.language, lang));
            }
            this.language = lang;
            return this;
        }

        onPropertyChanged(listener: {(event: api.PropertyChangedEvent): void;}) {
            this.propertyChangedListeners.push(listener);
        }

        unPropertyChanged(listener: {(event: api.PropertyChangedEvent): void;}) {
            this.propertyChangedListeners =
            this.propertyChangedListeners.filter((curr) => (curr != listener));
        }

        private notifyPropertyChanged(event: api.PropertyChangedEvent) {
            this.propertyChangedListeners.forEach((listener) => listener(event));
        }

        equals(other: api.Equitable): boolean {
            if (!api.ObjectHelper.iFrameSafeInstanceOf(other, ContentSettingsModel)) {
                return false;
            } else {
                var otherModel = <ContentSettingsModel> other;
                return otherModel.owner == this.owner && otherModel.language == this.language;
            }
        }

        apply(builder: api.content.ContentBuilder) {
            builder.owner = this.owner;
            //builder.language = this.language;
        }

    }
}

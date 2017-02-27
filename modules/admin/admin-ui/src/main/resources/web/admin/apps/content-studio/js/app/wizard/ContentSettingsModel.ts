import '../../api.ts';

import Content = api.content.Content;

export class ContentSettingsModel implements api.Equitable {

    private propertyChangedListeners: {(event: api.PropertyChangedEvent):void}[] = [];

    private owner: api.security.PrincipalKey;
    private language: string;

    public static PROPERTY_OWNER: string = 'owner';
    public static PROPERTY_LANG: string = 'language';

    constructor(content: Content) {
        this.language = content.getLanguage();
        this.owner = content.getOwner();
    }

    getOwner(): api.security.PrincipalKey {
        return this.owner;
    }

    setOwner(owner: api.security.PrincipalKey, silent?: boolean): ContentSettingsModel {
        if (!silent) {
            let event = new api.PropertyChangedEvent(ContentSettingsModel.PROPERTY_OWNER, this.owner, owner);
            this.notifyPropertyChanged(event);
        }
        this.owner = owner;
        return this;
    }

    getLanguage(): string {
        return this.language;
    }

    setLanguage(lang: string, silent?: boolean): ContentSettingsModel {
        if (!silent) {
            let event = new api.PropertyChangedEvent(ContentSettingsModel.PROPERTY_LANG, this.language, lang);
            this.notifyPropertyChanged(event);
        }
        this.language = lang;
        return this;
    }

    onPropertyChanged(listener: {(event: api.PropertyChangedEvent): void;}) {
        this.propertyChangedListeners.push(listener);
    }

    unPropertyChanged(listener: {(event: api.PropertyChangedEvent): void;}) {
        this.propertyChangedListeners =
            this.propertyChangedListeners.filter((curr) => (curr !== listener));
    }

    private notifyPropertyChanged(event: api.PropertyChangedEvent) {
        this.propertyChangedListeners.forEach((listener) => listener(event));
    }

    equals(other: api.Equitable): boolean {
        if (!api.ObjectHelper.iFrameSafeInstanceOf(other, ContentSettingsModel)) {
            return false;
        } else {
            let otherModel = <ContentSettingsModel> other;
            return otherModel.owner === this.owner && otherModel.language === this.language;
        }
    }

    apply(builder: api.content.ContentBuilder) {
        builder.owner = this.owner;
        builder.language = this.language;
    }

}

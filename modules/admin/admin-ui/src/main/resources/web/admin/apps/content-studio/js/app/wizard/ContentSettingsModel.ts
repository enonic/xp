import "../../api.ts";

import Content = api.content.Content;
import ComboBox = api.ui.selector.combobox.ComboBox;
import FormItemBuilder = api.ui.form.FormItemBuilder;
import FormItem = api.ui.form.FormItem;

export class ContentSettingsModel implements api.Equitable {

    private propertyChangedListeners: {(event: api.PropertyChangedEvent):void}[] = [];

    private owner: api.security.PrincipalKey;
    private language: string;
    private publishFromDate: Date;
    private publishToDate: Date;

    public static PROPERTY_OWNER: string = 'owner';
    public static PROPERTY_LANG: string = 'language';
    public static PROPERTY_PUBLISH_FROM: string = 'publishFromDate';
    public static PROPERTY_PUBLISH_TO: string = 'publishToDate';

    constructor(content: Content) {
        this.language = content.getLanguage();
        this.owner = content.getOwner();
        this.publishFromDate = content.getPublishFromDate();
        this.publishToDate = content.getPublishToDate();
    }

    getOwner(): api.security.PrincipalKey {
        return this.owner;
    }

    setOwner(owner: api.security.PrincipalKey, silent?: boolean): ContentSettingsModel {
        if (!silent) {
            var event = new api.PropertyChangedEvent(ContentSettingsModel.PROPERTY_OWNER, this.owner, owner);
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
            var event = new api.PropertyChangedEvent(ContentSettingsModel.PROPERTY_LANG, this.language, lang);
            this.notifyPropertyChanged(event);
        }
        this.language = lang;
        return this;
    }

    setPublishFrom(publishFrom: Date, silent?: boolean): ContentSettingsModel {
        if (!silent) {
            var event = new api.PropertyChangedEvent(ContentSettingsModel.PROPERTY_PUBLISH_FROM, this.publishFromDate, publishFrom);
            this.notifyPropertyChanged(event);
        }
        this.publishFromDate = publishFrom;
        return this;
    }

    setPublishTo(publishTo: Date, silent?: boolean): ContentSettingsModel {
        if (!silent) {
            var event = new api.PropertyChangedEvent(ContentSettingsModel.PROPERTY_PUBLISH_TO, this.publishToDate, publishTo);
            this.notifyPropertyChanged(event);
        }
        this.publishToDate = publishTo;
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
            return otherModel.owner == this.owner && otherModel.language == this.language
                   && otherModel.publishFromDate == this.publishFromDate && otherModel.publishToDate == this.publishToDate;
        }
    }

    apply(builder: api.content.ContentBuilder) {
        builder.owner = this.owner;
        builder.language = this.language;
        builder.publishFromDate = this.publishFromDate;
        builder.publishToDate = this.publishToDate;
    }

}

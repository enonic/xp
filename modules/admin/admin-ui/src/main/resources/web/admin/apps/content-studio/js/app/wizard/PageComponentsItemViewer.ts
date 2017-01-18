import "../../api.ts";

import ItemView = api.liveedit.ItemView;
import ComponentView = api.liveedit.ItemView;
import ItemType = api.liveedit.ItemType;
import PageView = api.liveedit.PageView;
import PageItemType = api.liveedit.PageItemType;
import Content = api.content.Content;
import TextItemType = api.liveedit.text.TextItemType;
import FragmentItemType = api.liveedit.fragment.FragmentItemType;
import TextComponentView = api.liveedit.text.TextComponentView;
import FragmentComponentView = api.liveedit.fragment.FragmentComponentView;
import TextComponent = api.content.page.region.TextComponent;
import TextComponentViewer = api.liveedit.text.TextComponentViewer;

export class PageComponentsItemViewer extends api.ui.NamesAndIconViewer<ItemView> {

    private content: Content;

    constructor(content: Content) {
        super('page-components-item-viewer');

        this.content = content;
    }

    resolveDisplayName(object: ItemView): string {
        if (api.ObjectHelper.iFrameSafeInstanceOf(object.getType(), TextItemType)) {
            let textView = <TextComponentView> object;
            let textComponent = <TextComponent>textView.getComponent();
            let viewer = <TextComponentViewer>object.getViewer()
            return viewer.resolveDisplayName(textComponent, textView);
        } else if (api.ObjectHelper.iFrameSafeInstanceOf(object.getType(), FragmentItemType)) {
            let fragmentView = <FragmentComponentView> object;
            let fragmentComponent = fragmentView.getFragmentRootComponent();
            if (fragmentComponent && api.ObjectHelper.iFrameSafeInstanceOf(fragmentComponent, TextComponent)) {
                return this.extractTextFromTextComponent(<TextComponent>fragmentComponent) || fragmentComponent.getName().toString();
            }
            return fragmentView.getFragmentDisplayName();
        }

        return object.getName();
    }

    resolveSubName(object: ItemView, relativePath: boolean = false): string {
        if (api.ObjectHelper.iFrameSafeInstanceOf(object.getType(), FragmentItemType)) {
            let fragmentView = <FragmentComponentView> object;
            let fragmentComponent = fragmentView.getFragmentRootComponent();
            if (fragmentComponent) {
                return fragmentComponent.getType().getShortName();
            }
        }

        return object.getType() ? object.getType().getShortName() : "";
    }

    resolveIconUrl(object: ItemView): string {
        if (PageItemType.get().equals(object.getType())) {
            return object.getIconUrl(this.content);
        }
        return null;
    }

    resolveIconClass(object: ItemView): string {
        return object.getIconClass();
    }

    private extractTextFromTextComponent(textComponent: TextComponent): string {
        var tmp = document.createElement("DIV");
        tmp.innerHTML = textComponent.getText() || "";
        return (tmp.textContent || tmp.innerText || "").trim();
    }
}

import '../../../../../api.ts';
import {Insertable} from './Insertable';
import i18n = api.util.i18n;

export class Insertables {

    private static IMAGE: Insertable = new Insertable().setName('image').setDisplayName(i18n('field.image')).setDescription(
        i18n('field.image.help')).setIconCls('image');

    private static PART: Insertable = new Insertable().setName('part').setDisplayName(i18n('field.part')).setDescription(
        i18n('field.part.help')).setIconCls('part');

    private static LAYOUT: Insertable = new Insertable().setName('layout').setDisplayName(i18n('field.layout')).setDescription(
        i18n('field.layout.help')).setIconCls('layout');

    private static TEXT: Insertable = new Insertable().setName('text').setDisplayName(i18n('field.text')).setDescription(
        i18n('field.text.help')).setIconCls('text');

    private static FRAGMENT: Insertable = new Insertable().setName('fragment').setDisplayName(i18n('field.fragment')).setDescription(
        i18n('field.fragment.help')).setIconCls('fragment');

    public static ALL: Insertable[] = [Insertables.IMAGE, Insertables.PART, Insertables.LAYOUT,
        Insertables.TEXT, Insertables.FRAGMENT];

    public static ALLOWED_IN_FRAGMENT: Insertable[] = [Insertables.IMAGE, Insertables.PART, Insertables.TEXT, Insertables.FRAGMENT];
}

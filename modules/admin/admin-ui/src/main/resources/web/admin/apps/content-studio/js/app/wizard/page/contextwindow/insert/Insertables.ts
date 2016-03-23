module app.wizard.page.contextwindow.insert {

    export class Insertables {

        private static IMAGE: Insertable = new Insertable().
            setName("image").
            setDisplayName("Image").
            setDescription("Upload or use existing images").
            setIconCls("image");

        private static PART: Insertable = new Insertable().
            setName("part").
            setDisplayName("Part").
            setDescription("Advanced components").
            setIconCls("part");

        private static LAYOUT: Insertable = new Insertable().
            setName("layout").
            setDisplayName("Layout").
            setDescription("Customize page layout").
            setIconCls("layout");

        private static TEXT: Insertable = new Insertable().
            setName("text").
            setDisplayName("Text").
            setDescription("Write directly on the page").
            setIconCls("text");

        private static FRAGMENT: Insertable = new Insertable().
            setName("fragment").
            setDisplayName("Fragment").
            setDescription("Reusable components").
            setIconCls("fragment");

        public static ALL: Insertable[] = [Insertables.IMAGE, Insertables.PART, Insertables.LAYOUT,
            Insertables.TEXT, Insertables.FRAGMENT];

        public static ALLOWED_IN_FRAGMENT: Insertable[] = [Insertables.IMAGE, Insertables.PART, Insertables.TEXT, Insertables.FRAGMENT];
    }
}

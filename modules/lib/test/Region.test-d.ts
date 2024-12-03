import type {
    // Component,
    FragmentComponent,
    LayoutComponent,
    LayoutRegion,
    PageComponent,
    PageRegion,
    PartComponent,
    // PartDescriptor,
    TextComponent,
} from '../core/index';
// import type * as admin from '../lib-admin/src/main/resources/lib/xp/admin';
import {
    expectAssignable,
    expectNotAssignable,
} from 'tsd';


declare global {
    interface XpLayoutMap {
        'com.enonic.app.myapp:mylayout': {
            string: string;
        };
    }
    interface XpPageMap {
        'com.enonic.app.myapp:mypage': {
            string: string;
        };
    }
    interface XpPartMap {
        'com.enonic.app.myapp:mypart': {
            mycheckbox: boolean;
        };
        'com.enonic.app.myapp:myanotherpart': {
            long: number;
        };
    }
}

const textComponent: TextComponent = {
    text: '<p>Hellow World</p>',
    path: '/main/0',
    type: 'text',
};

const fragmentComponent: FragmentComponent = {
    // This points to FragmentContent which can contain TextComponent, PartComponent or LayoutComponent.
    fragment: '66504e55-e1a7-4927-a5ae-5952cb66664d',
    path: '/main/0',
    type: 'fragment',
};

const genericPartComponent: PartComponent = {
    config: {},
    descriptor: 'a:b',
    type: 'part',
};
const myPartComponent: PartComponent = {
    config: {
        mycheckbox: true,
    },
    descriptor: 'com.enonic.app.myapp:mypart',
    type: 'part',
};

const genericLayoutComponent: LayoutComponent = {
    config: {},
    descriptor: 'a:b',
    regions: {},
    type: 'layout',
};
const myLayoutComponent: LayoutComponent = {
    config: {
        string: 'string',
    },
    descriptor: 'com.enonic.app.myapp:mylayout',
    regions: {},
    type: 'layout',
};

// Needs other PR merged
// const genericPageComponentAutomaticTemplate: PageComponent = {};
// const genericPageComponentSpecificTempalte: PageComponent = {
//     path: '/',
//     // template: '0e1eb842-dc40-48f0-b3c1-3be86210e384',
//     type: 'page',
// };
const genericPageComponentCustomized: PageComponent = {
    config: {},
    descriptor: 'a:b',
    path: '/',
    regions: {
        main: {
            components: [],
            name: 'main',
        },
    },
    type: 'page',
};

expectAssignable<PageRegion>({
    name: 'main',
    components: [
        textComponent,
        fragmentComponent,
        genericPartComponent,
        myPartComponent,
        myLayoutComponent,
    ],
});
expectAssignable<LayoutRegion>({
    name: 'main',
    components: [
        textComponent,
        fragmentComponent,
        genericPartComponent,
        myPartComponent,
    ],
});

expectNotAssignable<PageRegion>({
    name: 'left',
    components: [
        genericPageComponentCustomized, // Should NOT be able to put a page component in a page region
    ],
});

expectNotAssignable<PageComponent>({
    config: {
        wrong: 'wrong', // Should not be able to add a config property that is not defined in XpPageMap.
    },
    descriptor: 'com.enonic.app.myapp:mypage',
    regions: {
        main: {
            components: [],
            name: 'main',
        },
    },
    type: 'page',
});

expectNotAssignable<LayoutRegion>({
    name: 'left',
    components: [
        genericLayoutComponent, // Should NOT be able to put a generic layout component in a layout region
    ],
});
expectNotAssignable<LayoutRegion>({
    name: 'left',
    components: [
        myLayoutComponent, // Should NOT be able to put a specific layout component in a layout region
    ],
});
expectNotAssignable<LayoutRegion>({
    name: 'left',
    components: [
        genericPageComponentCustomized, // Should NOT be able to put a page component in a layout region
    ],
});

expectNotAssignable<LayoutComponent>({
    config: {
        wrong: 'wrong', // Should not be able to add a config property that is not defined in XpLayoutMap.
    },
    descriptor: 'com.enonic.app.myapp:mylayout',
    regions: {},
    type: 'layout',
});

expectNotAssignable<PartComponent>({
    config: {
        wrong: 'wrong', // Should not be able to add a config property that is not defined in XpPartMap.
    },
    descriptor: 'com.enonic.app.myapp:mypart',
    type: 'part',
});

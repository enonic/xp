module api.ui.responsive {
    export class ResponsiveRanges {
        public static _0_240: ResponsiveRange = new ResponsiveRange(0, 240);     // none (necessary for valid check)
        public static _240_360: ResponsiveRange = new ResponsiveRange(240, 360);     // mobile vertical
        public static _360_540: ResponsiveRange = new ResponsiveRange(360, 540);     // mobile horizontal
        public static _540_720: ResponsiveRange = new ResponsiveRange(540, 720);     // Phablet
        public static _720_960: ResponsiveRange = new ResponsiveRange(720, 960);     // Tablet vertical
        public static _960_1200: ResponsiveRange = new ResponsiveRange(960, 1200);     // Tablet horizontal
        public static _1200_1380: ResponsiveRange = new ResponsiveRange(1200, 1380);     // 13"
        public static _1380_1620: ResponsiveRange = new ResponsiveRange(1380, 1620);     // 15"
        public static _1620_1920: ResponsiveRange = new ResponsiveRange(1620, 1920);     // TV
        public static _1920_UP: ResponsiveRange = new ResponsiveRange(1920, Infinity); // Monitor
    }
}
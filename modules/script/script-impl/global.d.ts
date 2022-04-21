/*
This project don't include XP libraries definitions and need these interfaces to properly
map imports via require or __non_webpack_require__.

Warning: all of these interfaces are empty. If you want type definitions for various libraries,
you need to add types for there libraries, where those interfaces are redefined.
*/
interface XpEventLibrary {}

interface XpLibraries {
    '/lib/xp/event': XpEventLibrary;
}

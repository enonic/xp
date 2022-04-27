/**
 * This project don't include XP libraries definitions and need this interface
 * to properly map imports via require or __non_webpack_require__.
 *
 * Warning: this interface is empty. If you want type definitions for various
 * libraries, you need to redefine this interface in your library and add a new
 * property with library path.
 *
 * @example
 * interface XpLibraries {
 *     '/lib/xp/event': typeof import('./event'),
 * }
*/
interface XpLibraries {}

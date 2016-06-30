/*
 gulpfile.js in Babel format (using ES6/ES2015).
 Tasks are split into the several files in `tasks` folder.
 */

import requireDir from "require-dir";
import CONFIG from "./gulp/config";

requireDir(CONFIG.gulpTasks, {recurse: true});
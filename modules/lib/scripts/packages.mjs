import {existsSync, readdirSync, readFileSync} from 'node:fs';
import {dirname, join} from 'node:path';
import {fileURLToPath} from 'node:url';

export const libDir = join(dirname(fileURLToPath(import.meta.url)), '..');

export const xpPackageDir = join(libDir, 'xp', 'build', 'npm');

export function readJson(file) {
    return JSON.parse(readFileSync(file, 'utf8'));
}

// Staged dirs of every package the build publishes, except the `xp` meta package itself.
export function memberPackageDirs() {
    const libDirs = readdirSync(libDir)
        .filter(name => name.startsWith('lib-') && existsSync(join(libDir, name, 'build.gradle')))
        .sort()
        .map(name => join(libDir, name, 'build', 'npm'));
    const dirs = [join(libDir, 'build', 'npm'), join(libDir, 'core', 'build', 'npm'), ...libDirs];

    const missing = dirs.filter(dir => !existsSync(join(dir, 'package.json')));
    if (missing.length > 0) {
        throw new Error(`Packages are not staged, run prepareAllToPublish first:\n${missing.join('\n')}`);
    }
    return dirs;
}

export function readMembers() {
    return memberPackageDirs().map(dir => ({dir, pkg: readJson(join(dir, 'package.json'))}));
}

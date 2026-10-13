import {copyFileSync, mkdirSync, rmSync, writeFileSync} from 'node:fs';
import {join} from 'node:path';

import {libDir, readJson, readMembers, xpPackageDir} from './packages.mjs';

const members = readMembers().map(({pkg}) => pkg);

const versions = [...new Set(members.map(pkg => pkg.version))];
if (versions.length !== 1) {
    throw new Error(`Staged packages have different versions: ${versions.join(', ')}`);
}
const [version] = versions;

const dependencies = Object.fromEntries(members.map(pkg => [pkg.name, version]));

const pkg = Object.assign(readJson(join(libDir, 'package.template.json')), {
    name: '@enonic-types/xp',
    version,
    description: 'Type definitions for Enonic XP global objects and all XP libraries.',
    types: 'index.d.ts',
    files: ['index.d.ts'],
    keywords: ['enonic', 'enonic-xp', 'xp', 'types', 'typescript'],
    dependencies,
});

// `global` and the libs are referenced rather than imported, so a single `types` entry
// in a consumer's tsconfig pulls every lib's `XpLibraries` registration into the program.
const references = members
    .map(({name}) => name)
    .filter(name => name !== '@enonic-types/core')
    .map(name => `/// <reference types="${name}" />`);

rmSync(xpPackageDir, {recursive: true, force: true});
mkdirSync(xpPackageDir, {recursive: true});
writeFileSync(join(xpPackageDir, 'package.json'), `${JSON.stringify(pkg, null, 4)}\n`);
writeFileSync(join(xpPackageDir, 'index.d.ts'), `${references.join('\n')}\n`);
copyFileSync(join(libDir, 'xp', 'README.md'), join(xpPackageDir, 'README.md'));

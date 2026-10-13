import {copyFileSync, mkdirSync, readdirSync, readFileSync, rmSync, writeFileSync} from 'node:fs';
import {basename, dirname, join, relative} from 'node:path';

import {libDir, memberPackageDirs, xpPackageDir} from './packages.mjs';

const version = process.argv[2];
if (!version) {
    throw new Error('Usage: node prepare-packages.mjs <version>');
}

const repoDir = join(libDir, '..', '..');
const typePackageReference = /(?:from\s+|import\s*\(?\s*|require\(\s*|reference\s+types=)['"](@enonic-types\/[^'"/]+)/g;

function describe(sourceName) {
    switch (sourceName) {
    case 'lib':
        return {name: 'global', types: 'global.d.ts', description: 'Global variables and functions type definition.', keywords: ['global', 'common']};
    case 'core':
        return {name: 'core', types: 'index.d.ts', description: 'Shared type definition for core libraries.', keywords: ['core', 'shared']};
    case 'xp':
        return {name: 'xp', types: 'index.d.ts', description: 'Type definitions for Enonic XP global objects and all XP libraries.', keywords: ['xp']};
    default: {
        const shortName = sourceName.slice('lib-'.length);
        return {name: sourceName, types: `${shortName}.d.ts`, description: `Type definitions for ${sourceName}.`, keywords: [sourceName, shortName]};
    }
    }
}

function writeManifest(dir, dependencyNames) {
    const sourceDir = dirname(dirname(dir));
    const {name, types, description, keywords} = describe(basename(sourceDir));
    const declarations = readdirSync(dir).filter(file => file.endsWith('.d.ts')).sort();
    if (!declarations.includes(types)) {
        throw new Error(`${dir} has no ${types}`);
    }
    const manifest = {
        name: `@enonic-types/${name}`,
        version,
        description,
        types,
        files: declarations,
        repository: {
            type: 'git',
            url: 'git+https://github.com/enonic/xp.git',
            directory: relative(repoDir, sourceDir),
        },
        keywords: ['enonic', 'enonic-xp', ...keywords, 'types', 'typescript'],
        contributors: ['Mikita Taukachou <edloidas@gmail.com> (https://edloidas.github.io/)'],
        license: 'Apache-2.0',
        bugs: {url: 'https://github.com/enonic/xp/issues'},
        homepage: 'https://github.com/enonic/xp/tree/master#readme',
        dependencies: Object.fromEntries(dependencyNames.map(dependency => [dependency, version])),
        publishConfig: {access: 'public'},
    };
    writeFileSync(join(dir, 'package.json'), `${JSON.stringify(manifest, null, 4)}\n`);
    return manifest.name;
}

function usedTypePackages(dir, ownName) {
    const used = readdirSync(dir)
        .filter(file => file.endsWith('.d.ts'))
        .flatMap(file => [...readFileSync(join(dir, file), 'utf8').matchAll(typePackageReference)].map(([, name]) => name));
    return [...new Set(used)].filter(name => name !== ownName).sort();
}

const memberNames = memberPackageDirs().map(dir => {
    const ownName = `@enonic-types/${describe(basename(dirname(dirname(dir)))).name}`;
    return writeManifest(dir, usedTypePackages(dir, ownName));
});

// `global` and the libs are referenced rather than imported, so a single `types` entry
// in a consumer's tsconfig pulls every lib's `XpLibraries` registration into the program.
const references = memberNames
    .filter(name => name !== '@enonic-types/core')
    .map(name => `/// <reference types="${name}" />`);

rmSync(xpPackageDir, {recursive: true, force: true});
mkdirSync(xpPackageDir, {recursive: true});
writeFileSync(join(xpPackageDir, 'index.d.ts'), `${references.join('\n')}\n`);
copyFileSync(join(libDir, 'xp', 'README.md'), join(xpPackageDir, 'README.md'));
writeManifest(xpPackageDir, memberNames);

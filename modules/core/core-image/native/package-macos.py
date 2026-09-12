#!/usr/bin/env python3
"""Assemble pinned conda-forge ARM64 binaries without installing or running Conda.

Build-only: Python 3 and 7-Zip. Preserve Mach-O bytes/signatures and relative loader
paths; materialize archive links so JDK ZIP extraction also works on Windows.
"""
import concurrent.futures
import hashlib
import io
import json
from pathlib import Path
import posixpath
import shutil
import struct
import subprocess
import sys
import tarfile
import tempfile
import urllib.request
import zipfile


def download(package, cache):
    path = cache / package['url'].rsplit('/', 1)[1]
    if not path.exists() or hashlib.sha256(path.read_bytes()).hexdigest() != package['sha256']:
        with urllib.request.urlopen(package['url'], timeout=120) as response:
            data = response.read()
        if hashlib.sha256(data).hexdigest() != package['sha256']:
            raise ValueError('Checksum mismatch: ' + package['name'])
        path.write_bytes(data)
    return package, path


def tar_streams(path, seven_zip):
    if path.name.endswith('.tar.bz2'):
        yield path.read_bytes(), 'r:bz2'
    else:
        with zipfile.ZipFile(path) as archive:
            for name in archive.namelist():
                if name.endswith('.tar.zst'):
                    with tempfile.TemporaryDirectory() as directory:
                        compressed = Path(directory) / 'package.tar.zst'
                        compressed.write_bytes(archive.read(name))
                        command = ['zstd', '-d', '-c', str(compressed)] if shutil.which('zstd') else [seven_zip, 'x', '-so', str(compressed)]
                        result = subprocess.run(command,
                                                check=True, capture_output=True, timeout=120)
                        yield result.stdout, 'r:'


def safe_name(name):
    name = posixpath.normpath(name)
    if name.startswith('/') or name == '..' or name.startswith('../') or '\\' in name:
        raise ValueError('Unsafe archive path: ' + name)
    return name


def dependencies(data):
    # Native ARM64 Mach-O, little endian. Packages are already ad-hoc signed.
    if data[:4] != b'\xcf\xfa\xed\xfe':
        return []
    if struct.unpack_from('<I', data, 4)[0] != 0x0100000c:
        raise ValueError('Expected ARM64 Mach-O')
    count = struct.unpack_from('<I', data, 16)[0]
    position = 32
    result = []
    for _ in range(count):
        command, size = struct.unpack_from('<II', data, position)
        if size < 8 or position + size > len(data):
            raise ValueError('Invalid Mach-O command')
        if command in (0xc, 0x80000018, 0x8000001f, 0x80000023, 0x20):
            offset = struct.unpack_from('<I', data, position + 8)[0]
            result.append(data[position + offset:position + size].split(b'\0', 1)[0].decode())
        position += size
    return result


def package(manifest_path, target, seven_zip, cache):
    manifest = json.loads(manifest_path.read_text())
    cache.mkdir(parents=True, exist_ok=True)
    files, links, owners = {}, {}, {}
    with concurrent.futures.ThreadPoolExecutor(max_workers=6) as executor:
        downloads = list(executor.map(lambda p: download(p, cache), manifest['packages']))
    for item, path in downloads:
        for data, mode in tar_streams(path, seven_zip):
            with tarfile.open(fileobj=io.BytesIO(data), mode=mode) as archive:
                for member in archive:
                    name = safe_name(member.name)
                    if name.startswith('info/licenses/'):
                        name = 'licenses/' + item['name'] + '/' + name[len('info/licenses/'):]
                    if not (name == 'bin/magick' or name.startswith(('lib/', 'etc/ImageMagick', 'licenses/'))):
                        continue
                    if member.issym() or member.islnk():
                        links[name] = safe_name(posixpath.join(posixpath.dirname(name), member.linkname)
                                                if member.issym() else member.linkname)
                    elif member.isfile():
                        files[name] = archive.extractfile(member).read()
                    owners[name] = item['name']
    def contents(name, seen=None):
        seen = set() if seen is None else seen
        if name in seen:
            raise ValueError('Archive link cycle: ' + name)
        seen.add(name)
        return contents(links[name], seen) if name in links else files[name]

    # Include the executable and actual library closure, plus dynamically loaded HEIF codecs.
    selected = set()
    pending = ['bin/magick'] + [n for n in files if n.startswith('lib/libheif/') and n.endswith(('.so', '.dylib'))]
    while pending:
        name = pending.pop()
        if name in selected:
            continue
        selected.add(name)
        for dependency in dependencies(contents(name)):
            if dependency.startswith(('/usr/lib/', '/System/Library/')):
                continue
            if dependency.startswith('@rpath/'):
                resolved = 'lib/' + dependency[len('@rpath/'):]
            elif dependency.startswith('@loader_path/'):
                resolved = safe_name(posixpath.join(posixpath.dirname(name), dependency[len('@loader_path/'):]))
            else:
                raise ValueError('Non-relocatable dependency in ' + name + ': ' + dependency)
            pending.append(resolved)
    included_packages = {owners[n] for n in selected}
    selected.update(n for n in files if n.startswith('etc/ImageMagick') or
                    n.startswith('licenses/') and owners[n] in included_packages)
    target.parent.mkdir(parents=True, exist_ok=True)
    with zipfile.ZipFile(target, 'w', compression=zipfile.ZIP_DEFLATED, compresslevel=9) as archive:
        for name in sorted(selected):
            entry = zipfile.ZipInfo(name, (1980, 1, 1, 0, 0, 0))
            entry.compress_type = zipfile.ZIP_DEFLATED
            entry.external_attr = 0o100644 << 16
            archive.writestr(entry, contents(name))
        metadata = zipfile.ZipInfo('packages.json', (1980, 1, 1, 0, 0, 0))
        metadata.compress_type = zipfile.ZIP_DEFLATED
        archive.writestr(metadata, json.dumps([p for p in manifest['packages'] if p['name'] in included_packages], indent=2))
    print('Bundled macOS ARM64 ImageMagick:', len(selected), 'files,', target.stat().st_size, 'bytes')


if __name__ == '__main__':
    package(Path(sys.argv[1]), Path(sys.argv[2]), sys.argv[3], Path(sys.argv[4]))

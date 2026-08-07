"""bedrock-pack/ を .mcpack にまとめる。

出力した custommobs-test.mcpack を Geyser の packs/ 直下に置いて Velocity を再起動する。
packs/ のサブフォルダは読まれないので、必ず直下に置くこと。

クライアントにパックを再取得させるには manifest.json の header.version と
modules[].version を上げること。上げないと古いパックがキャッシュから使われる。
"""

import hashlib
import os
import zipfile

SRC = os.path.dirname(os.path.abspath(__file__))
OUT = os.path.join(SRC, 'custommobs-test.mcpack')
SKIP = {'build-mcpack.py', 'custommobs-test.mcpack', 'README.md'}

if os.path.exists(OUT):
    os.remove(OUT)

with zipfile.ZipFile(OUT, 'w', zipfile.ZIP_DEFLATED) as z:
    for root, _, files in os.walk(SRC):
        for f in files:
            if f in SKIP:
                continue
            p = os.path.join(root, f)
            z.write(p, os.path.relpath(p, SRC).replace(os.sep, '/'))

print('\n'.join(zipfile.ZipFile(OUT).namelist()))
print('sha1', hashlib.sha1(open(OUT, 'rb').read()).hexdigest())

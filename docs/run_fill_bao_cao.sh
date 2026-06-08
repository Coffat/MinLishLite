#!/usr/bin/env bash
# Tạo venv (nếu chưa có) và điền nội dung BaoCaoKetQuaDoAn.docx
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
VENV="$ROOT/.venv-docx"
PYTHON="${VENV}/bin/python3"

cd "$ROOT"

if [[ ! -x "$PYTHON" ]]; then
  echo "Tạo virtualenv tại .venv-docx ..."
  python3 -m venv "$VENV"
fi

"$PYTHON" -m pip install -q python-docx
"$PYTHON" docs/fill_bao_cao.py
echo "Xong. Mở file: $ROOT/docs/BaoCaoKetQuaDoAn.docx"

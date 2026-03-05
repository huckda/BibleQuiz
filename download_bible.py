#!/usr/bin/env python3
"""Downloads the full NKJV Bible from bolls.life, cleans the text, and saves
it as a gzipped JSON file for bundling in the Android and iOS apps."""

import urllib.request
import json
import gzip
import time
import re

BASE_URL = "https://bolls.life"
HTML_TAG = re.compile(r"<[^>]*>")

def clean_text(text):
    text = HTML_TAG.sub("", text)
    text = text.replace("&nbsp;", " ")
    text = text.replace("&amp;", "&")
    text = text.replace("&lt;", "<")
    text = text.replace("&gt;", ">")
    text = text.replace("&quot;", '"')
    text = text.replace("&#39;", "'")
    return text.strip()

def fetch(url, retries=5):
    for attempt in range(retries):
        try:
            req = urllib.request.Request(url, headers={"User-Agent": "BeSpecificApp/1.0"})
            with urllib.request.urlopen(req, timeout=30) as r:
                return json.loads(r.read().decode("utf-8"))
        except Exception as e:
            if attempt == retries - 1:
                raise
            wait = 2 ** attempt
            print(f"\n  Retry {attempt+1} after {wait}s ({e})")
            time.sleep(wait)

print("Fetching book list...")
books_raw = fetch(f"{BASE_URL}/get-books/NKJV/")
books = [{"bookId": b["bookid"], "name": b["name"], "chapters": b["chapters"]}
         for b in books_raw]
print(f"  {len(books)} books, {sum(b['chapters'] for b in books)} total chapters")

all_verses = []
total_chapters = sum(b["chapters"] for b in books)
done = 0

for book in books:
    for chapter in range(1, book["chapters"] + 1):
        url = f"{BASE_URL}/get-text/NKJV/{book['bookId']}/{chapter}/"
        verses_raw = fetch(url)
        for v in verses_raw:
            all_verses.append({
                "pk":      v["pk"],
                "bookId":  book["bookId"],
                "chapter": chapter,
                "verse":   v["verse"],
                "text":    clean_text(v["text"])
            })
        done += 1
        pct = done * 100 // total_chapters
        print(f"\r  {done}/{total_chapters} chapters  ({pct}%)  {book['name']} {chapter}   ",
              end="", flush=True)

print(f"\n  {len(all_verses):,} verses downloaded")

data = {"books": books, "verses": all_verses}
raw = json.dumps(data, ensure_ascii=False, separators=(",", ":")).encode("utf-8")
compressed = gzip.compress(raw, compresslevel=9)

out = "bible_nkjv.json.gz"
with open(out, "wb") as f:
    f.write(compressed)

print(f"Saved {out}:  {len(compressed):,} bytes compressed  ({len(raw):,} raw)")

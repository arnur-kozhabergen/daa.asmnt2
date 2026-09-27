import csv
import math
from pathlib import Path

files = {name: list(csv.DictReader(open(f"results/{name}.csv", newline="")))
         for name in ("access", "search", "changes", "heap")}
panels = [
    ("Random access", "access", "element_accesses", [("DynamicArray", "#2563eb", lambda r: r["structure"] == "DynamicArray"), ("LinkedList", "#ea580c", lambda r: r["structure"] == "LinkedList")]),
    ("Search", "search", "comparisons", [("DynamicArray", "#2563eb", lambda r: r["structure"] == "DynamicArray"), ("LinkedList", "#ea580c", lambda r: r["structure"] == "LinkedList")]),
    ("Insertion", "changes", "movements_or_accesses", [("Array front", "#2563eb", lambda r: r["structure"] == "DynamicArray" and r["operation"] == "insert_front"), ("Array middle", "#60a5fa", lambda r: r["structure"] == "DynamicArray" and r["operation"] == "insert_middle"), ("List front", "#ea580c", lambda r: r["structure"] == "LinkedList" and r["operation"] == "insert_front"), ("List middle", "#fdba74", lambda r: r["structure"] == "LinkedList" and r["operation"] == "insert_middle")]),
    ("Priority processing", "heap", "comparisons", [("Insert", "#2563eb", lambda r: r["operation"] == "insert"), ("Extract", "#ea580c", lambda r: r["operation"] == "extract")]),
]

for filename, title, field in [("time.svg", "Execution time vs n", "average_ms"), ("operations.svg", "Operations vs n", None)]:
    svg = ['<svg xmlns="http://www.w3.org/2000/svg" width="1100" height="700" viewBox="0 0 1100 700">',
           '<rect width="1100" height="700" fill="#f8fafc"/>',
           f'<text x="55" y="48" font-family="Arial" font-size="28" font-weight="bold" fill="#0f172a">{title}</text>',
           '<text x="55" y="72" font-family="Arial" font-size="14" fill="#475569">Time: five-run mean. Counts: per run. Vertical scale is logarithmic.</text>']
    for panel, (heading, file, metric, series) in enumerate(panels):
        left = 55 + panel % 2 * 540
        top = 105 + panel // 2 * 290
        column = field or metric
        maximum = max(float(row[column]) for row in files[file])
        svg += [f'<rect x="{left}" y="{top}" width="500" height="260" rx="12" fill="white" stroke="#e2e8f0"/>',
                f'<text x="{left + 24}" y="{top + 32}" font-family="Arial" font-size="19" font-weight="bold" fill="#0f172a">{heading}</text>']
        for y in (top + 65, top + 125, top + 185):
            svg.append(f'<line x1="{left + 55}" y1="{y}" x2="{left + 460}" y2="{y}" stroke="#e2e8f0"/>')
        svg += [f'<text x="{left + 48}" y="{top + 69}" text-anchor="end" font-family="Arial" font-size="11" fill="#64748b">{maximum:.1f}</text>',
                f'<text x="{left + 48}" y="{top + 129}" text-anchor="end" font-family="Arial" font-size="11" fill="#64748b">{math.sqrt(maximum + 1) - 1:.1f}</text>',
                f'<text x="{left + 48}" y="{top + 189}" text-anchor="end" font-family="Arial" font-size="11" fill="#64748b">0</text>']
        for i, n in enumerate((100, 1000, 10000, 100000)):
            svg.append(f'<text x="{left + 70 + i * 128}" y="{top + 208}" text-anchor="middle" font-family="Arial" font-size="12" fill="#475569">{n:,}</text>')
        for label, color, select in series:
            data = sorted((row for row in files[file] if select(row)), key=lambda row: int(row["n"]))
            points = " ".join(f'{left + 70 + i * 128},{top + 185 - math.log10(float(row[column]) + 1) / math.log10(maximum + 1) * 120:.1f}' for i, row in enumerate(data))
            svg.append(f'<polyline points="{points}" fill="none" stroke="{color}" stroke-width="3"/>')
        for i, (label, color, _) in enumerate(series):
            x = left + 25 + (i % 2) * 230
            y = top + 231 + (i // 2) * 17
            svg += [f'<line x1="{x}" y1="{y - 4}" x2="{x + 18}" y2="{y - 4}" stroke="{color}" stroke-width="3"/>',
                    f'<text x="{x + 24}" y="{y}" font-family="Arial" font-size="12" fill="#334155">{label}</text>']
    svg.append('</svg>')
    Path("results", filename).write_text("\n".join(svg), encoding="utf-8")

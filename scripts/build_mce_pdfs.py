#!/usr/bin/env python3
# -*- coding: utf-8 -*-
import json, os, re, sys
from pathlib import Path
from xml.sax.saxutils import escape

from reportlab.lib import colors
from reportlab.lib.enums import TA_CENTER, TA_LEFT
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.units import mm
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from reportlab.platypus import (
    SimpleDocTemplate, Paragraph, Spacer, PageBreak, KeepTogether, Table, TableStyle
)

ROOT = Path(__file__).resolve().parents[1]
MASTER = ROOT / "MCE_8_Truong_24_De_Thi_Thu_Master.json"
OUT_DIR = ROOT / "pdf_output"
OUT_DIR.mkdir(exist_ok=True)

FONT_REG_CANDIDATES = [
    "/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf",
    "/usr/share/fonts/truetype/liberation2/LiberationSans-Regular.ttf",
]
FONT_BOLD_CANDIDATES = [
    "/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf",
    "/usr/share/fonts/truetype/liberation2/LiberationSans-Bold.ttf",
]

def first_existing(paths):
    for p in paths:
        if os.path.exists(p):
            return p
    raise FileNotFoundError("Không tìm thấy font Unicode trong runner.")

pdfmetrics.registerFont(TTFont("MCE", first_existing(FONT_REG_CANDIDATES)))
pdfmetrics.registerFont(TTFont("MCE-Bold", first_existing(FONT_BOLD_CANDIDATES)))

NAVY = colors.HexColor("#0B2D5C")
GOLD = colors.HexColor("#D5A23E")
LIGHT = colors.HexColor("#EDF3FA")
PALE = colors.HexColor("#FFF6DD")
TEXT = colors.HexColor("#1F2A44")
MUTED = colors.HexColor("#5A677A")

styles = getSampleStyleSheet()
styles.add(ParagraphStyle(
    name="MCECover", parent=styles["Title"], fontName="MCE-Bold",
    fontSize=22, leading=28, alignment=TA_CENTER, textColor=NAVY, spaceAfter=10
))
styles.add(ParagraphStyle(
    name="MCESubtitle", parent=styles["Normal"], fontName="MCE",
    fontSize=11.5, leading=17, alignment=TA_CENTER, textColor=MUTED, spaceAfter=12
))
styles.add(ParagraphStyle(
    name="MCESection", parent=styles["Heading1"], fontName="MCE-Bold",
    fontSize=15, leading=20, textColor=NAVY, spaceBefore=4, spaceAfter=8
))
styles.add(ParagraphStyle(
    name="MCESchool", parent=styles["Heading2"], fontName="MCE-Bold",
    fontSize=13, leading=18, textColor=colors.HexColor("#9A6411"), spaceBefore=4, spaceAfter=7
))
styles.add(ParagraphStyle(
    name="MCEBody", parent=styles["BodyText"], fontName="MCE",
    fontSize=9.3, leading=13.3, textColor=TEXT, spaceAfter=4
))
styles.add(ParagraphStyle(
    name="MCESmall", parent=styles["BodyText"], fontName="MCE",
    fontSize=8.3, leading=11.5, textColor=MUTED, spaceAfter=4
))
styles.add(ParagraphStyle(
    name="MCENote", parent=styles["BodyText"], fontName="MCE",
    fontSize=9, leading=13, textColor=colors.HexColor("#72500B"),
    backColor=PALE, borderColor=GOLD, borderWidth=0.7,
    borderPadding=6, spaceBefore=4, spaceAfter=8
))
styles.add(ParagraphStyle(
    name="MCEAnswer", parent=styles["BodyText"], fontName="MCE",
    fontSize=9.1, leading=13.2, textColor=TEXT, spaceAfter=4
))

SCHOOL_ORDER = [
    "THCS Thanh Xuân", "THCS Cầu Giấy", "Nguyễn Tất Thành",
    "THCS Nam Từ Liêm", "Lương Thế Vinh", "THCS Ngoại ngữ",
    "THCS Lê Lợi", "Archimedes"
]
SUBJECT_ORDER = ["Toán", "Tiếng Anh", "Tiếng Việt"]

def clean_text(s):
    s = str(s or "")
    s = re.sub(r"<PARSED TEXT FOR PAGE:[^>]+>", "", s, flags=re.I)
    s = re.sub(r"No readable content was found in this file for the requested mode\.", "", s, flags=re.I)
    s = s.replace("\r\n", "\n").replace("\r", "\n")
    s = re.sub(r"\n{4,}", "\n\n\n", s)
    return s.strip()

def ptxt(s):
    return escape(str(s or "")).replace("\n", "<br/>")

def text_flow(text, style_name="MCEBody"):
    text = clean_text(text)
    if not text:
        return [Paragraph("(Không có nội dung)", styles["MCESmall"])]
    out = []
    # Split into manageable blocks on blank lines, then sub-split overly long blocks.
    blocks = re.split(r"\n\s*\n", text)
    for block in blocks:
        block = block.strip()
        if not block:
            continue
        lines = block.split("\n")
        chunk = []
        chars = 0
        for line in lines:
            if chars + len(line) > 2800 and chunk:
                out.append(Paragraph(ptxt("\n".join(chunk)), styles[style_name]))
                chunk, chars = [], 0
            chunk.append(line)
            chars += len(line) + 1
        if chunk:
            out.append(Paragraph(ptxt("\n".join(chunk)), styles[style_name]))
    return out

def footer(canvas, doc):
    canvas.saveState()
    w, h = A4
    canvas.setStrokeColor(colors.HexColor("#D9E1EC"))
    canvas.line(15*mm, 12*mm, w-15*mm, 12*mm)
    canvas.setFont("MCE", 7.5)
    canvas.setFillColor(MUTED)
    canvas.drawString(15*mm, 7.2*mm, "MCE TOP SCHOOL • Tài liệu luyện tập/tham khảo • Không phải đề thi chính thức")
    canvas.drawRightString(w-15*mm, 7.2*mm, f"Trang {doc.page}")
    canvas.restoreState()

def metadata_box(rec):
    data = [
        ["Trường", rec.get("school",""), "Môn", rec.get("subject","")],
        ["Tuần", str(rec.get("week","")), "Mã", rec.get("code","")],
        ["Format", rec.get("format_summary",""), "Nguồn", rec.get("source_file","")],
    ]
    t = Table(data, colWidths=[18*mm, 58*mm, 16*mm, 82*mm])
    t.setStyle(TableStyle([
        ("FONTNAME",(0,0),(-1,-1),"MCE"),
        ("FONTNAME",(0,0),(0,-1),"MCE-Bold"),
        ("FONTNAME",(2,0),(2,-1),"MCE-Bold"),
        ("FONTSIZE",(0,0),(-1,-1),7.8),
        ("LEADING",(0,0),(-1,-1),10.5),
        ("TEXTCOLOR",(0,0),(-1,-1),TEXT),
        ("BACKGROUND",(0,0),(-1,-1),LIGHT),
        ("GRID",(0,0),(-1,-1),0.35,colors.HexColor("#CAD5E4")),
        ("VALIGN",(0,0),(-1,-1),"TOP"),
        ("LEFTPADDING",(0,0),(-1,-1),4),
        ("RIGHTPADDING",(0,0),(-1,-1),4),
        ("TOPPADDING",(0,0),(-1,-1),4),
        ("BOTTOMPADDING",(0,0),(-1,-1),4),
    ]))
    return t

def cover(title, subtitle):
    story = [
        Spacer(1, 35*mm),
        Paragraph(title, styles["MCECover"]),
        Spacer(1, 4*mm),
        Paragraph(subtitle, styles["MCESubtitle"]),
        Spacer(1, 8*mm),
        Paragraph(
            "Bộ 8 trường: THCS Thanh Xuân • THCS Cầu Giấy • Nguyễn Tất Thành • "
            "THCS Nam Từ Liêm • Lương Thế Vinh • THCS Ngoại ngữ • THCS Lê Lợi • Archimedes",
            styles["MCENote"]
        ),
        Spacer(1, 5*mm),
        Paragraph(
            "Tài liệu dùng để kiểm tra và chuẩn bị chương trình thi thử hàng tuần của MCE Top School. "
            "Các đề là tài liệu luyện tập/tham khảo, không phải đề thi chính thức của nhà trường.",
            styles["MCEBody"]
        ),
        PageBreak()
    ]
    return story

def exam_story(records):
    story = []
    for idx, rec in enumerate(records, 1):
        story.append(Paragraph(f"TUẦN {rec.get('week')} • {rec.get('school')} • {rec.get('subject')}", styles["MCESchool"]))
        story.append(Paragraph("ĐỀ THI THỬ SỐ 01", styles["MCESection"]))
        story.append(metadata_box(rec))
        story.append(Spacer(1, 4*mm))
        if rec.get("matrix_text"):
            story.append(Paragraph("Ma trận / format", styles["MCESection"]))
            story.extend(text_flow(rec.get("matrix_text"), "MCESmall"))
        story.append(Paragraph("Đề thi", styles["MCESection"]))
        story.extend(text_flow(rec.get("exam_text"), "MCEBody"))
        if idx != len(records):
            story.append(PageBreak())
    return story

def answer_story(records):
    story = []
    for idx, rec in enumerate(records, 1):
        story.append(Paragraph(f"TUẦN {rec.get('week')} • {rec.get('school')} • {rec.get('subject')}", styles["MCESchool"]))
        story.append(Paragraph("ĐÁP ÁN / LỜI GIẢI / HƯỚNG DẪN CHẤM", styles["MCESection"]))
        story.append(metadata_box(rec))
        story.append(Spacer(1, 4*mm))
        if rec.get("validation_note"):
            story.append(Paragraph(ptxt(rec.get("validation_note")), styles["MCENote"]))
        story.extend(text_flow(rec.get("answer_and_solution_text"), "MCEAnswer"))
        if idx != len(records):
            story.append(PageBreak())
    return story

def build_pdf(path, story):
    doc = SimpleDocTemplate(
        str(path), pagesize=A4,
        rightMargin=15*mm, leftMargin=15*mm, topMargin=15*mm, bottomMargin=18*mm,
        title=path.stem, author="MCE Top School"
    )
    doc.build(story, onFirstPage=footer, onLaterPages=footer)

def main():
    data = json.loads(MASTER.read_text(encoding="utf-8"))
    records = data["records"]
    records.sort(key=lambda r: (
        SCHOOL_ORDER.index(r["school"]) if r["school"] in SCHOOL_ORDER else 99,
        SUBJECT_ORDER.index(r["subject"]) if r["subject"] in SUBJECT_ORDER else 99
    ))
    assert len(records) == 24, f"Expected 24 records, got {len(records)}"
    assert all(clean_text(r.get("exam_text")) for r in records)
    assert all(clean_text(r.get("answer_and_solution_text")) for r in records)

    de = OUT_DIR / "MCE_8_Truong_24_De_Thi_Thu_DE.pdf"
    da = OUT_DIR / "MCE_8_Truong_24_De_Thi_Thu_DAP_AN.pdf"
    gop = OUT_DIR / "MCE_8_Truong_24_De_Thi_Thu_DE_Truoc_DAP_AN_Sau.pdf"

    build_pdf(de, cover("MCE TOP SCHOOL – 24 ĐỀ THI THỬ", "PHẦN ĐỀ • 8 trường × 3 môn") + exam_story(records))
    build_pdf(da, cover("MCE TOP SCHOOL – ĐÁP ÁN 24 ĐỀ THI THỬ", "ĐÁP ÁN • LỜI GIẢI • HƯỚNG DẪN CHẤM") + answer_story(records))
    build_pdf(gop, cover("MCE TOP SCHOOL – 24 ĐỀ THI THỬ", "ĐỀ TRƯỚC • ĐÁP ÁN SAU") + exam_story(records) + [PageBreak()] + cover("PHẦN ĐÁP ÁN", "Đáp án, lời giải và hướng dẫn chấm của 24 đề")[:-1] + answer_story(records))

    for p in (de, da, gop):
        if not p.exists() or p.stat().st_size < 10000:
            raise RuntimeError(f"PDF output invalid: {p}")
        print(f"{p.name}: {p.stat().st_size} bytes")

if __name__ == "__main__":
    main()

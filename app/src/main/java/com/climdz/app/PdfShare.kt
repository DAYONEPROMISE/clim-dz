package com.climdz.app

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.climdz.app.data.Intervention
import java.io.File

/** Builds a simple intervention fiche PDF with the platform API (no dependency) and shares it. */
fun shareInterventionPdf(ctx: Context, item: Intervention, checklist: List<String>) {
    val doc = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).build()
    val page = doc.startPage(pageInfo)
    val c = page.canvas
    val title = Paint().apply { textSize = 20f; isFakeBoldText = true }
    val head = Paint().apply { textSize = 14f; isFakeBoldText = true }
    val body = Paint().apply { textSize = 12f }
    var y = 60f
    fun line(p: Paint, s: String) {
        c.drawText(s.take(90), 40f, y, p)
        y += 24f
    }
    line(title, "ClimDZ - Fiche d'intervention")
    y += 8f
    line(body, "Client: ${item.clientName}")
    line(body, "Piece: ${item.roomLabel}  Surface: ${item.surface} m2  Hauteur: ${item.height} m")
    line(body, "Exposition: ${item.exposure}  Isolation: ${item.insulation}")
    line(body, "BTU calcules: ${item.btu}  Recommande: ${item.recommended} BTU (${"%.2f".format(item.kw)} kW)")
    y += 8f
    line(head, "Check-list maintenance:")
    val checked = item.checked.split(",").mapNotFound { it.toIntOrNull() }.toSet()
    checklist.forEachIndexed { idx, label ->
        val mark = if (idx in checked) "[x]" else "[ ]"
        line(body, "$mark $label")
    }
    if (item.notes.isNotBlank()) {
        y += 8f
        line(head, "Notes:")
        line(body, item.notes)
    }
    doc.finishPage(page)
    val file = File(ctx.cacheDir, "fiche-${item.id}.pdf")
    file.outputStream().use { doc.writeTo(it) }
    doc.close()
    val uri = FileProvider.getUriForFile(ctx, "com.climdz.app.fileprovider", file)
    val send = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    ctx.startActivity(Intent.createChooser(send, "Partager PDF"))
}

private fun <T, R> Iterable<T>.mapNotFound(f: (T) -> R?): List<R> = mapNotNull(f)

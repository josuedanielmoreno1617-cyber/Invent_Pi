package com.example.utils

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.Product
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfExporter {
    fun exportToPdfAndShare(
        context: Context,
        products: List<Product>,
        currencySymbol: String
    ) {
        if (products.isEmpty()) {
            Toast.makeText(context, "No hay productos para exportar", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val document = PdfDocument()
            val pageWidth = 595 // A4 width in points
            val pageHeight = 842 // A4 height in points
            var pageNumber = 1

            // Calculations
            val totalInvestment = products.sumOf { it.quantity * it.buyPrice }
            val expectedRevenue = products.sumOf { it.quantity * it.sellPrice }
            val expectedProfit = expectedRevenue - totalInvestment

            // Paint styles
            val paintTitle = Paint().apply {
                color = Color.parseColor("#0A1F38")
                textSize = 20f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }

            val paintSubtitle = Paint().apply {
                color = Color.parseColor("#5A6B82")
                textSize = 10f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                isAntiAlias = true
            }

            val paintHeader = Paint().apply {
                color = Color.parseColor("#FFFFFF")
                textSize = 10f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }

            val paintBodyText = Paint().apply {
                color = Color.parseColor("#2A3B50")
                textSize = 9f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                isAntiAlias = true
            }

            val paintProfitPositive = Paint().apply {
                color = Color.parseColor("#2E7D32") // Green
                textSize = 9f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }

            val paintProfitNegative = Paint().apply {
                color = Color.parseColor("#C62828") // Red
                textSize = 9f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }

            val paintBgHeader = Paint().apply {
                color = Color.parseColor("#112B4A")
                style = Paint.Style.FILL
            }

            val paintBgRowEven = Paint().apply {
                color = Color.parseColor("#F5F7FA")
                style = Paint.Style.FILL
            }

            val paintBgRowOdd = Paint().apply {
                color = Color.parseColor("#FFFFFF")
                style = Paint.Style.FILL
            }

            val paintLine = Paint().apply {
                color = Color.parseColor("#E0E4EC")
                style = Paint.Style.STROKE
                strokeWidth = 1f
            }

            // Create first page
            var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            var page = document.startPage(pageInfo)
            var canvas = page.canvas

            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val dateStr = dateFormat.format(Date())

            // 1. Draw PDF Header
            canvas.drawText("AI INVENTARIO", 36f, 50f, paintTitle)
            canvas.drawText("Reporte de Inventario y Análisis Financiero", 36f, 68f, paintSubtitle)
            canvas.drawText("Fecha de generación: $dateStr", 36f, 82f, paintSubtitle)

            // Draw a separator line
            canvas.drawLine(36f, 95f, (pageWidth - 36).toFloat(), 95f, paintLine)

            // 2. Summary Card
            // Background card
            val cardPaintBg = Paint().apply {
                color = Color.parseColor("#F0F4F8")
                style = Paint.Style.FILL
            }
            canvas.drawRoundRect(36f, 110f, (pageWidth - 36).toFloat(), 185f, 8f, 8f, cardPaintBg)

            // Content inside Summary Card
            val paintCardTitle = Paint().apply {
                color = Color.parseColor("#112B4A")
                textSize = 11f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }
            val paintCardValueLabel = Paint().apply {
                color = Color.parseColor("#5A6B82")
                textSize = 9f
                isAntiAlias = true
            }
            val paintCardValue = Paint().apply {
                color = Color.parseColor("#112B4A")
                textSize = 12f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }

            canvas.drawText("RESUMEN GENERAL DEL INVENTARIO", 50f, 130f, paintCardTitle)

            // Left column inside card
            canvas.drawText("Inversión Total:", 50f, 152f, paintCardValueLabel)
            canvas.drawText("$currencySymbol${String.format(Locale.US, "%.2f", totalInvestment)}", 50f, 168f, paintCardValue)

            // Middle column inside card
            canvas.drawText("Ingresos Esperados:", 190f, 152f, paintCardValueLabel)
            canvas.drawText("$currencySymbol${String.format(Locale.US, "%.2f", expectedRevenue)}", 190f, 168f, paintCardValue)

            // Right column inside card
            canvas.drawText("Beneficio Estimado:", 350f, 152f, paintCardValueLabel)
            val profitPaint = if (expectedProfit >= 0) {
                Paint(paintCardValue).apply { color = Color.parseColor("#2E7D32") }
            } else {
                Paint(paintCardValue).apply { color = Color.parseColor("#C62828") }
            }
            val sign = if (expectedProfit >= 0) "+" else ""
            canvas.drawText("$sign$currencySymbol${String.format(Locale.US, "%.2f", expectedProfit)}", 350f, 168f, profitPaint)

            // Stock Count
            canvas.drawText("Total de Ítems: ${products.sumOf { it.quantity }} (${products.size} referencias)", 350f, 130f, paintSubtitle)

            // 3. Table of Products Title
            val paintTableSectionTitle = Paint().apply {
                color = Color.parseColor("#0A1F38")
                textSize = 12f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }
            canvas.drawText("DETALLE DE PRODUCTOS", 36f, 215f, paintTableSectionTitle)

            // Define Table Headers
            fun drawTableHeader(canvas: Canvas, y: Float) {
                // Table header background
                canvas.drawRect(36f, y - 15f, (pageWidth - 36).toFloat(), y + 8f, paintBgHeader)

                canvas.drawText("Producto", 46f, y, paintHeader)
                canvas.drawText("Cant.", 220f, y, paintHeader)
                canvas.drawText("P. Compra", 290f, y, paintHeader)
                canvas.drawText("P. Venta", 370f, y, paintHeader)
                canvas.drawText("Beneficio", 460f, y, paintHeader)
            }

            var currentY = 240f
            drawTableHeader(canvas, currentY)
            currentY += 20f

            // 4. Draw Rows
            products.forEachIndexed { idx, product ->
                // Check if we need a new page
                if (currentY > 780f) {
                    // Draw Footer on old page
                    drawFooter(canvas, pageNumber, dateStr, pageWidth)
                    document.finishPage(page)

                    pageNumber++
                    pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                    page = document.startPage(pageInfo)
                    canvas = page.canvas

                    // New Page Header (shorter)
                    canvas.drawText("AI INVENTARIO - Reporte Continuación", 36f, 40f, paintSubtitle)
                    canvas.drawLine(36f, 48f, (pageWidth - 36).toFloat(), 48f, paintLine)

                    currentY = 70f
                    drawTableHeader(canvas, currentY)
                    currentY += 20f
                }

                // Row background striping
                val bgPaint = if (idx % 2 == 0) paintBgRowEven else paintBgRowOdd
                canvas.drawRect(36f, currentY - 14f, (pageWidth - 36).toFloat(), currentY + 6f, bgPaint)

                // Row contents
                // Truncate name if it's too long
                var displayName = product.name
                if (displayName.length > 25) {
                    displayName = displayName.take(22) + "..."
                }

                canvas.drawText(displayName, 46f, currentY, paintBodyText)
                canvas.drawText(product.quantity.toString(), 220f, currentY, paintBodyText)
                canvas.drawText("$currencySymbol${String.format(Locale.US, "%.2f", product.buyPrice)}", 290f, currentY, paintBodyText)
                canvas.drawText("$currencySymbol${String.format(Locale.US, "%.2f", product.sellPrice)}", 370f, currentY, paintBodyText)

                val itemProfit = (product.sellPrice - product.buyPrice) * product.quantity
                val itemProfitColor = if (itemProfit >= 0) paintProfitPositive else paintProfitNegative
                val itemSign = if (itemProfit >= 0) "+" else ""
                canvas.drawText("$itemSign$currencySymbol${String.format(Locale.US, "%.2f", itemProfit)}", 460f, currentY, itemProfitColor)

                // Bottom line border for row
                canvas.drawLine(36f, currentY + 6f, (pageWidth - 36).toFloat(), currentY + 6f, paintLine)

                currentY += 20f
            }

            // Draw final footer
            drawFooter(canvas, pageNumber, dateStr, pageWidth)
            document.finishPage(page)

            // Save PDF to cache directory for sharing
            val cacheDir = context.cacheDir
            val pdfFile = File(cacheDir, "Reporte_Inventario_AI.pdf")
            val outputStream = FileOutputStream(pdfFile)
            document.writeTo(outputStream)
            document.close()
            outputStream.flush()
            outputStream.close()

            // Share Intent using FileProvider
            val authority = "${context.packageName}.fileprovider"
            val fileUri: Uri = FileProvider.getUriForFile(context, authority, pdfFile)

            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, fileUri)
                type = "application/pdf"
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(shareIntent, "Exportar Reporte PDF")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error al generar PDF: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun drawFooter(canvas: Canvas, pageNum: Int, dateStr: String, pageWidth: Int) {
        val paintFooter = Paint().apply {
            color = Color.parseColor("#8A9BB2")
            textSize = 8f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
        }
        val paintLine = Paint().apply {
            color = Color.parseColor("#E0E4EC")
            style = Paint.Style.STROKE
            strokeWidth = 0.5f
        }

        canvas.drawLine(36f, 805f, (pageWidth - 36).toFloat(), 805f, paintLine)
        canvas.drawText("AI Inventario - Reporte de Desempeño Financiero", 36f, 818f, paintFooter)
        canvas.drawText("Pág. $pageNum", (pageWidth - 70).toFloat(), 818f, paintFooter)
    }
}

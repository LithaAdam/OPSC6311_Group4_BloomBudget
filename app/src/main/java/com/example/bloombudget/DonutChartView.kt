package com.example.bloombudget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class DonutChartView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    data class ChartSegment(
        val name: String,
        val amount: Double,
        val percentage: Double,
        val color: Int
    )

    private var segments: List<ChartSegment> = emptyList()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.BUTT
    }
    private val rectF = RectF()

    fun setData(newSegments: List<ChartSegment>) {
        segments = newSegments
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (segments.isEmpty()) return

        val width = width.toFloat()
        val height = height.toFloat()
        val size = if (width < height) width else height
        if (size <= 0) return

        val strokeWidth = size * 0.15f
        paint.strokeWidth = strokeWidth

        val margin = strokeWidth / 2f
        rectF.set(margin, margin, size - margin, size - margin)

        var startAngle = -90f
        for (segment in segments) {
            val sweepAngle = (segment.percentage.toFloat() / 100f) * 360f
            paint.color = segment.color
            canvas.drawArc(rectF, startAngle, sweepAngle, false, paint)
            startAngle += sweepAngle
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredSize = (220 * resources.displayMetrics.density).toInt()
        val width = resolveSize(desiredSize, widthMeasureSpec)
        val height = resolveSize(desiredSize, heightMeasureSpec)
        val size = if (width < height) width else height
        setMeasuredDimension(size, size)
    }
}

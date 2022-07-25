package br.com.customview.fancontroller.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import br.com.customview.fancontroller.R
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin


private enum class FanSpeed(val label: Int){

    OFF(R.string.fan_off),
    LOW(R.string.fan_low),
    MEDIUM(R.string.fan_medium),
    HIGH(R.string.fan_high);

    fun next() = when (this){
        OFF -> LOW
        LOW -> MEDIUM
        MEDIUM -> HIGH
        HIGH -> OFF
    }
}

private const val RADIUS_OFFSET_LABEL = 120
private const val RADIUS_OFFSET_INDICATOR = 60

class DialView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): View(context, attrs, defStyleAttr) {

    private var radius = 0.0f
    private var fanSpeed = FanSpeed.OFF

    private val pointPosition: PointF = PointF(0.0f, 0.0f)

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 20.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }

    init {
        isClickable = true //habilita o onclick na view
    }

    override fun performClick(): Boolean {
        if(super.performClick()) return true

        fanSpeed = fanSpeed.next()
        contentDescription = resources.getString(fanSpeed.label)

        invalidate() //redesenha a tela
        return true

    }

    //metodo chamado toda vez que a view precisa redesenhar, aqui e calculado as posicoes dimensoes
    //tudo relacionado a dimensao e o desenho personalizado da view
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = (min(w, h) / 2.0 * 0.8).toFloat()
    }

    //funcao de extensao para o PointF, aqui é calculado as coordenadas X e Y para o rotulo
    //do texto que sera renderizado na tela
    private fun PointF.computeXYForSpeed(pos: FanSpeed, radius: Float){
        val startAngle = Math.PI * (9 / 8.0)
        val angle = startAngle + pos.ordinal * (Math.PI / 4)
        x = (radius * cos(angle).toFloat() + width) / 2
        y = (radius * sin(angle).toFloat() + height) / 2
    }

    //metodo responsavel por desenhar a view
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //adiciona a regra para mudar de cor
        paint.color = if (fanSpeed == FanSpeed.OFF ) Color.GRAY else Color.GREEN
        //desenha um circulo com as medidas da View (width e height)
        canvas?.drawCircle( (width/2).toFloat(), (height/2).toFloat(), radius, paint)

        //desenha um circulo menor
        val markerRadius = radius + RADIUS_OFFSET_INDICATOR
        //calcula a posicao
        pointPosition.computeXYForSpeed(fanSpeed, markerRadius)
        paint.color = Color.BLACK
        canvas?.drawCircle(pointPosition.x, pointPosition.y, radius/12, paint)

        //etiquetas de velocidade do ventisilva
        val labelRadius = radius + RADIUS_OFFSET_LABEL
        for(i in FanSpeed.values()){
            Log.i("canvas", "desenhando a posicao $i")
            pointPosition.computeXYForSpeed(i , labelRadius)
            val label = resources.getString(i.label)
            Log.i("canvas", "valor de canvas ${canvas}")
            canvas?.drawText(label, pointPosition.x, pointPosition.y, paint)
        }
    }

}
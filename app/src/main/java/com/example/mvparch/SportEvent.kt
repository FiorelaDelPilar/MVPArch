package com.example.mvparch

sealed class SportEvent {
    //las clases selladas se usan para representar una jerarquía de clases restringidas
    //es una forma de controlar la herencia
    //se conocen en tiempo de compilación
    //no se pueden crear nuevas clases que intenten heredar después;esto se parece un poco a las clases enum
    //pero con la diferencia que las clases selladas si pueden tener múltiples instancias de una subclase

    data class ResultSuccess(
        val sportKey: Int,
        val sportName: String,
        val results: List<String>?,
        val isWarning: Boolean = false
    ) : SportEvent() {
        fun getImgRes(): Int = when (sportKey) {
            1 -> R.drawable.ic_soccer
            2 -> R.drawable.ic_weight_lifter
            3 -> R.drawable.ic_gymnastics
            4 -> R.drawable.ic_water_polo
            5 -> R.drawable.ic_baseball_bat
            6 -> R.drawable.ic_rugby
            7 -> R.drawable.ic_tennis_ball
            else -> R.drawable.ic_timer
        }
    }

    data class ResultError(
        val code: Int,
        val msg: String
    ) : SportEvent()

    data object AdEvent : SportEvent()

    data object SaveEvent : SportEvent()
    data object ClosedAdEvent : SportEvent()
}
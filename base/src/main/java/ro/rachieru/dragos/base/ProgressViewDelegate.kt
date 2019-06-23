package ro.rachieru.dragos.base

/**
 * Upit
 *
 * @author Dragos
 * @since 15.06.2019
 */
interface ProgressViewDelegate : ViewDelegate {
    fun showProgress()
    fun hideProgress()
}
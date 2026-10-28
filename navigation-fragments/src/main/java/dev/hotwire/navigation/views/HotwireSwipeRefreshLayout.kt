package dev.hotwire.navigation.views

import android.content.Context
import android.util.AttributeSet
import androidx.core.view.children
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import dev.hotwire.core.turbo.webview.HotwireWebView

internal class HotwireSwipeRefreshLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
        SwipeRefreshLayout(context, attrs) {

    init {
        disableCustomDrawingOrder()
    }

    /**
     * SwipeRefreshLayout only measures and lays out the first child it found (its
     * target), even after that child is removed. Measure and lay out the attached
     * WebView as well, so a new WebView instance (such as one that replaces a WebView
     * whose render process is gone) isn't left with a 0x0 size.
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        webView?.measure(
            MeasureSpec.makeMeasureSpec(measuredWidth - paddingLeft - paddingRight, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(measuredHeight - paddingTop - paddingBottom, MeasureSpec.EXACTLY)
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        webView?.layout(paddingLeft, paddingTop, measuredWidth - paddingRight, measuredHeight - paddingBottom)
    }

    override fun canChildScrollUp(): Boolean {
        val webView = children.firstOrNull() as? HotwireWebView

        return if (webView != null) {
            webView.scrollY > 0 || webView.elementTouchPreventsPullsToRefresh
        } else {
            false
        }
    }

    private val webView: HotwireWebView?
        get() = children.filterIsInstance<HotwireWebView>().firstOrNull()

    /**
     * Disable custom child drawing order. This fixes a crash while using a
     * stylus that dispatches hover events when the WebView is being removed.
     * This doesn't have any unintended consequences, since the WebView is the
     * only possible child of this view.
     */
    private fun disableCustomDrawingOrder() {
        isChildrenDrawingOrderEnabled = false
    }
}

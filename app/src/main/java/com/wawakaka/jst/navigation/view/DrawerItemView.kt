package com.wawakaka.jst.navigation.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.wawakaka.jst.R
import com.wawakaka.jst.base.view.ViewUtils
import kotlinx.android.synthetic.main.navigation_drawer_item.view.*

/**
 * Created by wawakaka on 11/15/2017.
 */
class DrawerItemView : RelativeLayout {
    private var itemTitle: String = ""
    private var itemIconDrawable: Drawable? = null
    private var itemShowNewIcon: Boolean = false

    constructor(context: Context) : super(context) {
        initViews()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initAttrs(attrs)
        initViews()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initAttrs(attrs)
        initViews()
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        initAttrs(attrs)
        initViews()
    }

    private fun initAttrs(attrs: AttributeSet) {
        val typedArray = context.theme.obtainStyledAttributes(
            attrs, R.styleable.DrawerItemView, 0, 0
        )

        itemTitle = typedArray.getString(R.styleable.DrawerItemView_drawerItemTitle)
        itemIconDrawable = typedArray.getDrawable(R.styleable.DrawerItemView_drawerItemIcon)

        typedArray.recycle()
    }

    private fun initViews() {
        val layoutInflater = ViewUtils.getLayoutInflater(context)
        layoutInflater?.inflate(R.layout.navigation_drawer_item, this, true)

        drawer_title.text = itemTitle
        drawer_item_icon.setImageDrawable(itemIconDrawable)
    }

}